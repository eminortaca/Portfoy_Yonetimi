package com.emin.portfoy.controller;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;
import com.emin.portfoy.service.KriptoServisi;
import com.emin.portfoy.service.PortfoyService;
import javafx.application.Application;
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
    private ObservableList<Varlik> varlikListesi;

    private Label lblToplamMaliyet;
    private Label lblToplamDeger;
    private Label lblKarZarar;

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

        // 4. Üst Buton Paneli (Fiyat Güncelle Butonu)
        Button btnGuncelle = new Button("Piyasa Fiyatlarını Güncelle");
        btnGuncelle.setStyle("-fx-background-color: #2b5797; -fx-text-fill: white; -fx-font-weight: bold;");
        btnGuncelle.setOnAction(e -> {
            portfoyService.piyasaFiyatlariniGuncelle();
            verileriYenile();
        });

        HBox ustPanel = new HBox(btnGuncelle);
        ustPanel.setPadding(new Insets(10));
        ustPanel.setAlignment(Pos.CENTER_RIGHT);

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
    }

    private void tabloOlustur() {
        TableColumn<Varlik, String> colSembol = new TableColumn<>("Sembol");
        colSembol.setCellValueFactory(new PropertyValueFactory<>("sembol"));
        colSembol.setPrefWidth(120);

        TableColumn<Varlik, Double> colMiktar = new TableColumn<>("Miktar");
        colMiktar.setCellValueFactory(new PropertyValueFactory<>("miktar"));
        colMiktar.setPrefWidth(100);

        TableColumn<Varlik, Double> colMaliyet = new TableColumn<>("Ort. Maliyet");
        colMaliyet.setCellValueFactory(new PropertyValueFactory<>("ortalamaMaliyet"));
        colMaliyet.setPrefWidth(130);

        TableColumn<Varlik, Double> colGuncelFiyat = new TableColumn<>("Güncel Fiyat");
        colGuncelFiyat.setCellValueFactory(new PropertyValueFactory<>("guncelFiyat"));
        colGuncelFiyat.setPrefWidth(130);

        TableColumn<Varlik, Double> colToplamDeger = new TableColumn<>("Toplam Değer");
        colToplamDeger.setCellValueFactory(new PropertyValueFactory<>("toplamDeger"));
        colToplamDeger.setPrefWidth(130);

        tablo.getColumns().addAll(colSembol, colMiktar, colMaliyet, colGuncelFiyat, colToplamDeger);
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

    public static void main(String[] args) {
        launch(args);
    }
}