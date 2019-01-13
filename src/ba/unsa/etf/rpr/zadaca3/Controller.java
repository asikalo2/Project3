package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public TableView tabelaVlasnici;
    public TableColumn idColumn;
    public TableColumn imeColumn;
    public TableColumn prezimeColumn;
    public TableColumn jmbgColumn;
    public TableView tabelaVozilo;
    public TableColumn idColumn1;
    public TableColumn nazivProizvodjacaColumn1;
    public TableColumn nazivModela1;
    public TableColumn brojRegColumn1;
    public TableColumn brojSasijeColumn1;
    private VozilaDAO dao;

    public void postaviSQLite(ActionEvent actionEvent) {
        dao = new VozilaDAOBaza();
        napuniTabeluVlasnici();
        napuniTabeluVozila();
    }

    public void postaviXML(ActionEvent actionEvent) {
        dao = new VozilaDAOXML();
        napuniTabeluVlasnici();
        napuniTabeluVozila();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dao = new VozilaDAOBaza();
        napuniTabeluVlasnici();
        napuniTabeluVozila();
    }

    private void napuniTabeluVozila() {
        ObservableList<Vozilo> listaVozila = dao.getVozila();

        idColumn1.setCellValueFactory(new PropertyValueFactory("id"));
        nazivProizvodjacaColumn1.setCellValueFactory(new PropertyValueFactory("nazivProizvodjaca"));
        nazivModela1.setCellValueFactory(new PropertyValueFactory("model"));
        brojSasijeColumn1.setCellValueFactory(new PropertyValueFactory("brojSasije"));
        brojRegColumn1.setCellValueFactory(new PropertyValueFactory("brojTablica"));

        tabelaVozilo.setItems(listaVozila);
    }

    private void napuniTabeluVlasnici() {
        ObservableList<Vlasnik> listaVlasnika = dao.getVlasnici();

        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        imeColumn.setCellValueFactory(new PropertyValueFactory("ime"));
        prezimeColumn.setCellValueFactory(new PropertyValueFactory("prezime"));
        jmbgColumn.setCellValueFactory(new PropertyValueFactory("jmbg"));

        tabelaVlasnici.setItems(listaVlasnika);
    }

    public void addVlasnik(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            VlasnikController vlasnikController = new VlasnikController(dao, null);
            loader.setController(vlasnikController);
            loader.setLocation(getClass().getResource("/fxml/vlasnik.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Vlasnik");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeVlasnik(ActionEvent actionEvent) {
    }

    public void editVlasnik(ActionEvent actionEvent) {
        if (tabelaVlasnici.getSelectionModel().getSelectedItems() == null)
            return;
        Vlasnik vlasnik = (Vlasnik) tabelaVlasnici.getSelectionModel().getSelectedItem();
        System.out.println(vlasnik.getIme());

        try {
            FXMLLoader loader = new FXMLLoader();
            VlasnikController vlasnikController = new VlasnikController(dao, vlasnik);
            loader.setController(vlasnikController);
            loader.setLocation(getClass().getResource("/fxml/vlasnik.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Vlasnik");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void addVozilo(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            VoziloController voziloController = new VoziloController(dao, null);
            loader.setController(voziloController);
            loader.setLocation(getClass().getResource("/fxml/vozilo.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Vozilo");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeVozilo(ActionEvent actionEvent) {
    }

    public void editVozilo(ActionEvent actionEvent) {
        if (tabelaVozilo.getSelectionModel().getSelectedItems() == null)
            return;
        Vozilo vozilo = (Vozilo) tabelaVozilo.getSelectionModel().getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader();
            VoziloController voziloController = new VoziloController(dao, vozilo);
            loader.setController(voziloController);
            loader.setLocation(getClass().getResource("/fxml/vozilo.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Vozilo");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
