package com.emin.portfoy.ui;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private PortfoyService portfoyService;
    private TableView<Varlik> tablo;
    private Label lblToplamMaliyet;
    private Label lblToplamDeger;
    private Label lblKarZarar;

    @Override
    public void start(Stage primaryStage) {
        portfoyService = new PortfoyService(new VarlikRepository(), new KriptoServisi());

        primaryStage.setTitle("Portföy Yönetim Sistemi");

        tablo = new TableView<>();
        tabloOlustur();

        HBox ozetPaneli = ozetPaneliOlustur();

        // Ekleme formunu oluşturuyoruz
        HBox eklemePaneli = eklemePaneliOlustur();

        Label lblBilgi = new Label("Piyasa fiyatları her 3 saniyede bir otomatik güncellenmektedir.");
        lblBilgi.setStyle("-fx-font-style: italic; -fx-text-fill: #666666;");

        // Üst kısmı dikey olarak grupluyoruz: Önce form, altında bilgi yazısı
        VBox ustPanel = new VBox(10, eklemePaneli, lblBilgi);
        ustPanel.setPadding(new Insets(10));
        ustPanel.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(ustPanel);
        root.setCenter(tablo);
        root.setBottom(ozetPaneli);

        verileriYenile();

        Scene scene = new Scene(root, 750, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        portfoyService.otomatikGuncellemeyiBaslat(() -> Platform.runLater(this::verileriYenile));
    }

    private HBox eklemePaneliOlustur() {
        TextField txtSembol = new TextField();
        txtSembol.setPromptText("Sembol (Örn: SOL)");
        txtSembol.setPrefWidth(120);

        TextField txtMiktar = new TextField();
        txtMiktar.setPromptText("Miktar (Örn: 2.5)");
        txtMiktar.setPrefWidth(120);

        TextField txtMaliyet = new TextField();
        txtMaliyet.setPromptText("Ort. Maliyet ($)");
        txtMaliyet.setPrefWidth(120);

        Button btnEkle = new Button("Varlık Ekle");
        btnEkle.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

        btnEkle.setOnAction(e -> {
            try {
                // 1. Arayüz sadece kutulardaki ham metni alır
                String sembolTxt = txtSembol.getText();
                String miktarTxt = txtMiktar.getText();
                String maliyetTxt = txtMaliyet.getText();

                // 2. Her şeyi (doğrulama, obje oluşturma) servise paslar
                portfoyService.yeniVarlikIsleVeEkle(sembolTxt, miktarTxt, maliyetTxt);

                // 3. Servis hata fırlatmazsa işlem başarılı demektir, kutuları temizle ve UI yenile
                txtSembol.clear();
                txtMiktar.clear();
                txtMaliyet.clear();
                verileriYenile();

            } catch (IllegalArgumentException ex) {
                // 4. Servis "Bu veri hatalı" derse, sadece servisin gönderdiği mesajı ekrana basar.
                uyariGoster("İşlem Başarısız", ex.getMessage());
            }
        });

        HBox hbox = new HBox(15, txtSembol, txtMiktar, txtMaliyet, btnEkle);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    private void uyariGoster(String baslik, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    // TEMİZLENDİ: Sütun eklerken çıkan genel uyarıyı Java'ya yoksaymasını söyledik
    @SuppressWarnings("unchecked")
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
        tablo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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
        ObservableList<Varlik> varlikListesi = FXCollections.observableArrayList(portfoyService.tumVarliklar());
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

    @Override
    public void stop() throws Exception {
        super.stop();
        portfoyService.otomatikGuncellemeyiDurdur();
        com.emin.portfoy.repository.DatabaseManager.baglantiyiKapat();
    }

    public static void main(String[] args) {
        launch(args);
    }
}