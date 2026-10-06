package com.emin.portfoy.ui; // Paketi 'ui' olarak güncellediğimizi varsayarak değiştirdik

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;
import com.emin.portfoy.service.KriptoServisi;
import com.emin.portfoy.service.PortfoyService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    private PortfoyService portfoyService;
    private TableView<Varlik> tablo;
    private ObservableList<Varlik> varlikListesi;

    private Label lblToplamMaliyet;
    private Label lblToplamDeger;
    private Label lblKarZarar;

    // Otomatik güncelleme için zamanlayıcı (Background Worker)
    private ScheduledExecutorService zamanlayici;

    @Override
    public void start(Stage primaryStage) {
        // 1. Servisleri ve Veritabanını Başlat
        portfoyService = new PortfoyService(new VarlikRepository(), new KriptoServisi());

        primaryStage.setTitle("Portföy Yönetim Sistemi");

        // 2. Tabloyu Hazırla
        tablo = new TableView<>();
        tabloOlustur();

        // 3. Alt Özet Paneli (Toplam Değerler)
        HBox ozetPaneli = ozetPaneliOlustur();

        // 4. Üst Panel (Butonu kaldırıp yerine bilgi mesajı koyduk)
        Label lblBilgi = new Label("Piyasa fiyatları her 3 saniyede bir otomatik güncellenmektedir.");
        lblBilgi.setStyle("-fx-font-style: italic; -fx-text-fill: #666666;");
        HBox ustPanel = new HBox(lblBilgi);
        ustPanel.setPadding(new Insets(10));
        ustPanel.setAlignment(Pos.CENTER);

        // 5. Ana Düzen (BorderPane)
        BorderPane root = new BorderPane();
        root.setTop(ustPanel);
        root.setCenter(tablo);
        root.setBottom(ozetPaneli);

        // Veritabanındaki ilk verileri yükle
        verileriYenile();

        Scene scene = new Scene(root, 750, 450);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 6. Uygulama ekranda göründüğü an otomatik döngüyü başlat
        otomatikGuncellemeyiBaslat();
    }

    private void tabloOlustur() {
        TableColumn<Varlik, String> colSembol = new TableColumn<>("Sembol");
        colSembol.setCellValueFactory(new PropertyValueFactory<>("sembol"));

        TableColumn<Varlik, Double> colMiktar = new TableColumn<>("Miktar");
        colMiktar.setCellValueFactory(new PropertyValueFactory<>("miktar"));

        TableColumn<Varlik, Double> colMaliyet = new TableColumn<>("Ort. Maliyet");
        colMaliyet.setCellValueFactory(new PropertyValueFactory<>("ortalamaMaliyet"));

        TableColumn<Varlik, Double> colGuncelFiyat = new TableColumn<>("Güncel Fiyat");
        colGuncelFiyat.setCellValueFactory(new PropertyValueFactory<>("guncelFiyat"));

        TableColumn<Varlik, Double> colToplamDeger = new TableColumn<>("Toplam Değer");
        colToplamDeger.setCellValueFactory(new PropertyValueFactory<>("toplamDeger"));

        tablo.getColumns().addAll(colSembol, colMiktar, colMaliyet, colGuncelFiyat, colToplamDeger);

        // Boş gri sütunu yok eder, sütunları pencere genişliğine eşit ve orantılı yayar:
        tablo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox ozetPaneliOlustur() {
        lblToplamMaliyet = new Label();
        lblToplamDeger = new Label();
        lblKarZarar = new Label();

        lblToplamMaliyet.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblToplamDeger.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblKarZarar.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        HBox hbox = new HBox(30, lblToplamMaliyet, lblToplamDeger, lblKarZarar);
        hbox.setPadding(new Insets(15));
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dcdcdc;");
        return hbox;
    }

    private void verileriYenile() {
        varlikListesi = FXCollections.observableArrayList(portfoyService.tumVarliklar());
        tablo.setItems(varlikListesi);

        lblToplamMaliyet.setText(String.format("Toplam Maliyet: %.2f", portfoyService.toplamMaliyet()));
        lblToplamDeger.setText(String.format("Toplam Değer: %.2f", portfoyService.toplamDeger()));

        double karZarar = portfoyService.toplamKarZarar();
        lblKarZarar.setText(String.format("Kâr/Zarar: %.2f", karZarar));
        if (karZarar >= 0) {
            lblKarZarar.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            lblKarZarar.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }

    private void otomatikGuncellemeyiBaslat() {
        // Sadece bu iş için arka planda 1 adet işçi oluşturuyoruz
        zamanlayici = Executors.newSingleThreadScheduledExecutor();

        // İşçiye görevini ve süresini veriyoruz
        zamanlayici.scheduleAtFixedRate(() -> {

            // 1. ADIM: Arka planda Binance'e gidip fiyatları çek (UI donmaz)
            portfoyService.piyasaFiyatlariniGuncelle();

            // 2. ADIM: Fiyatlar geldiğinde, arayüzü güncellemek için ana UI Thread'e haber ver
            Platform.runLater(() -> {
                verileriYenile(); // Tablodaki rakamlar yenilenir
            });

        }, 0, 3, TimeUnit.SECONDS);
        // 0: Hemen başla, 3: Her 3 saniyede bir tekrarla
    }

    // Kullanıcı pencereyi (X) çarpıdan kapattığında arka plandaki döngüyü tamamen durdur
    @Override
    public void stop() throws Exception {
        super.stop();
        if (zamanlayici != null && !zamanlayici.isShutdown()) {
            zamanlayici.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}