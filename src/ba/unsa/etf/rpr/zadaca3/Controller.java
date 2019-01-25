package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public TableView tabelaVlasnici;
    public TableColumn idColumn;
    public TableColumn imeColumn;
    public TableColumn prezimeColumn;
    public TableColumn jmbgColumn;
    public TableView tabelaVozila;
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

        tabelaVozila.setItems(listaVozila);
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
            stage.setOnCloseRequest(event-> {
                tabelaVlasnici.setItems(dao.getVlasnici());
            });
            stage.setOnHiding(event-> {
                tabelaVlasnici.setItems(dao.getVlasnici());
            });
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeVlasnik(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        //alert.setTitle("Brisanje vlasnika");
        alert.setHeaderText("Brisanje vlasnika");
        alert.setContentText("Da li zelite obrisati vlasnika?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            if (tabelaVlasnici.getSelectionModel().getSelectedItems().size() == 0) {
                Alert alertNovi = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Greška!");
                alert.setContentText("Nije moguće brisati!");

                alert.showAndWait();
                return;
            }
            Vlasnik vlasnik = (Vlasnik) tabelaVlasnici.getSelectionModel().getSelectedItem();
            try {
                dao.obrisiVlasnika(vlasnik);
            }
            catch (Exception ex) {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                alertError.setHeaderText("Brisanje vlasnika");
                alertError.setContentText(ex.getMessage());
                alertError.showAndWait();
                return;
            }
        } else {
            // ... user chose CANCEL or closed the dialog
        }
        tabelaVlasnici.setItems(dao.getVlasnici());
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
            stage.setOnCloseRequest(event-> {
                tabelaVlasnici.setItems(dao.getVlasnici());
            });
            stage.setOnHiding(event-> {
                tabelaVlasnici.setItems(dao.getVlasnici());
            });
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
            stage.setOnCloseRequest(event-> {
                tabelaVozila.setItems(dao.getVozila());
            });
            stage.setOnHiding(event-> {
                tabelaVozila.setItems(dao.getVozila());
            });
            stage.show();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeVozilo(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        //alert.setTitle("Brisanje vlasnika");
        alert.setHeaderText("Brisanje bozila");
        alert.setContentText("Da li zelite obrisati vozilo?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            if (tabelaVozila.getSelectionModel().getSelectedItems().size() == 0) {
                Alert alertNovi = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Greška!");
                alert.setContentText("Nije moguće brisati!");

                alert.showAndWait();
                return;
            }
            Vozilo vozilo = (Vozilo) tabelaVozila.getSelectionModel().getSelectedItem();
            dao.obrisiVozilo(vozilo);
        } else {
            // ... user chose CANCEL or closed the dialog
        }
        tabelaVlasnici.setItems(dao.getVlasnici());
        tabelaVozila.setItems(dao.getVozila());
    }

    public void editVozilo(ActionEvent actionEvent) {
        if (tabelaVozila.getSelectionModel().getSelectedItems() == null)
            return;
        Vozilo vozilo = (Vozilo) tabelaVozila.getSelectionModel().getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader();
            VoziloController voziloController = new VoziloController(dao, vozilo);
            loader.setController(voziloController);
            loader.setLocation(getClass().getResource("/fxml/vozilo.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Vozilo");
            stage.setScene(scene);
            stage.setOnCloseRequest(event-> {
                tabelaVozila.setItems(dao.getVozila());
            });
            stage.setOnHiding(event-> {
                tabelaVozila.setItems(dao.getVozila());
            });
            stage.show();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
