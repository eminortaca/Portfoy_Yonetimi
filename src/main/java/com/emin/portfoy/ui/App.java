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

        Label lblBilgi = new Label("Piyasa fiyatları her 3 saniyede bir otomatik güncellenmektedir.");
        lblBilgi.setStyle("-fx-font-style: italic; -fx-text-fill: #666666;");
        HBox ustPanel = new HBox(lblBilgi);
        ustPanel.setPadding(new Insets(10));
        ustPanel.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(ustPanel);
        root.setCenter(tablo);
        root.setBottom(ozetPaneli);

        verileriYenile();

        Scene scene = new Scene(root, 750, 450);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Zamanlayıcı metodunu silip, servise "başla" dedik.
        portfoyService.otomatikGuncellemeyiBaslat(() -> Platform.runLater(this::verileriYenile));
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

        tablo.getColumns().add(colSembol);
        tablo.getColumns().add(colMiktar);
        tablo.getColumns().add(colMaliyet);
        tablo.getColumns().add(colGuncelFiyat);
        tablo.getColumns().add(colToplamDeger);
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

    // DEĞİŞEN DİĞER KISIM: Kapanırken servise "durdur" dedik.
    @Override
    public void stop() throws Exception {
        super.stop();
        portfoyService.otomatikGuncellemeyiDurdur();
    }

    public static void main(String[] args) {
        launch(args);
    }
}