package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    }

    public void postaviXML(ActionEvent actionEvent) {
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
}
