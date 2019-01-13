package ba.unsa.etf.rpr.zadaca3;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class VoziloController {

    private VozilaDAO dao;
    private Vozilo trenutnoVozilo = null;

    public TextField modelField;
    public TextField brojSasijeField;
    public TextField brojTablicaField;
    public ComboBox<Proizvodjac> proizvodjacCombo;
    public ComboBox<Vlasnik> vlasnikCombo;

    @FXML
    public Button okButton;
    @FXML
    public Button cancelButton;

    public SimpleStringProperty modelProperty;
    public SimpleStringProperty brojSasijeProperty;
    public SimpleStringProperty brojTablicaProperty;
    public SimpleObjectProperty<Proizvodjac> proizvodjacProperty;
    public SimpleObjectProperty<Vlasnik> vlasnikProperty;

    public VoziloController(VozilaDAO dao, Vozilo vozilo) {
        this.dao = dao;
        this.trenutnoVozilo = vozilo;
        modelProperty = new SimpleStringProperty("");
        brojSasijeProperty = new SimpleStringProperty("");
        brojTablicaProperty = new SimpleStringProperty("");
        proizvodjacProperty = new SimpleObjectProperty<>();
        vlasnikProperty = new SimpleObjectProperty<>();
    }

    @FXML
    public void initialize() {
        proizvodjacCombo.setItems(dao.getProizvodjaci());
        vlasnikCombo.setItems(dao.getVlasnici());

        proizvodjacCombo.setConverter(new StringConverter<Proizvodjac>() {
            @Override
            public String toString(Proizvodjac proizvodjac) {
                return proizvodjac == null ? "" : proizvodjac.getNaziv();
            }

            @Override
            public Proizvodjac fromString(String s) {
                Proizvodjac p = new Proizvodjac(0, s);
                return p;
            }
        });

        vlasnikCombo.setConverter(new StringConverter<Vlasnik>() {
            @Override
            public String toString(Vlasnik vlasnik) {
                return vlasnik == null ? "" : vlasnik.getPrezime() + " " + vlasnik.getIme();
            }

            @Override
            public Vlasnik fromString(String s) {
                return null;
            }
        });

        inicijalizirajDataBinding();
        dodajListenere();
        if (trenutnoVozilo != null) {
            popuniFormu();
        }
    }

    private void inicijalizirajDataBinding() {
        modelField.textProperty().bindBidirectional(modelProperty);
        brojSasijeField.textProperty().bindBidirectional(brojSasijeProperty);
        brojTablicaField.textProperty().bindBidirectional(brojTablicaProperty);
        proizvodjacCombo.valueProperty().bindBidirectional(proizvodjacProperty);
        vlasnikCombo.valueProperty().bindBidirectional(vlasnikProperty);
    }

    private void dodajListenere() {
        modelField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                modelField.getStyleClass().removeAll("poljeNijeIspravno");
                modelField.getStyleClass().add("poljeIspravno");
            }
            else {
                modelField.getStyleClass().removeAll("poljeIspravno");
                modelField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        brojSasijeField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                brojSasijeField.getStyleClass().removeAll("poljeNijeIspravno");
                brojSasijeField.getStyleClass().add("poljeIspravno");
            }
            else {
                brojSasijeField.getStyleClass().removeAll("poljeIspravno");
                brojSasijeField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        brojTablicaField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.validirajTablice(n)) {
                brojTablicaField.getStyleClass().removeAll("poljeNijeIspravno");
                brojTablicaField.getStyleClass().add("poljeIspravno");
            }
            else {
                brojTablicaField.getStyleClass().removeAll("poljeIspravno");
                brojTablicaField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        proizvodjacCombo.getEditor().textProperty().addListener((observableValue, s, n) -> {
            Proizvodjac m = new Proizvodjac(0, n);
            proizvodjacProperty.set(m);
            proizvodjacCombo.setValue(m);
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                proizvodjacCombo.getStyleClass().removeAll("poljeNijeIspravno");
                proizvodjacCombo.getStyleClass().add("poljeIspravno");
            }
            else {
                proizvodjacCombo.getStyleClass().removeAll("poljeIspravno");
                proizvodjacCombo.getStyleClass().add("poljeNijeIspravno");
            }
        });
    }

    private void popuniFormu() {
        modelProperty.set(trenutnoVozilo.getModel());
        brojSasijeProperty.set(trenutnoVozilo.getBrojSasije());
        brojTablicaProperty.set(trenutnoVozilo.getBrojTablica());
        vlasnikCombo.setValue(trenutnoVozilo.getVlasnik());
        proizvodjacCombo.setValue(trenutnoVozilo.getProizvodjac());
    }



    public void potvrdiFormuBtn(ActionEvent actionEvent) {
        if (daLiJeValidnaForma()) {
            boolean dodavanje = trenutnoVozilo == null;
            if (trenutnoVozilo == null) {
                trenutnoVozilo = new Vozilo();
            }

            trenutnoVozilo.setModel(modelProperty.get());
            trenutnoVozilo.setBrojSasije(brojSasijeProperty.get());
            trenutnoVozilo.setBrojTablica(brojTablicaProperty.get());

            if (trenutnoVozilo.getVlasnik() == null)
                trenutnoVozilo.setVlasnik(vlasnikCombo.getValue());

            if (trenutnoVozilo.getProizvodjac() == null) {
                Proizvodjac p = new Proizvodjac(0, proizvodjacCombo.getValue().getNaziv());
                trenutnoVozilo.setProizvodjac(p);
            }
            else if (trenutnoVozilo.getProizvodjac().getNaziv() != proizvodjacCombo.getEditor().getText()) {
                Proizvodjac p = new Proizvodjac(0, proizvodjacCombo.getValue().getNaziv());
                trenutnoVozilo.setProizvodjac(p);            }

            if (dodavanje)
                dao.dodajVozilo(trenutnoVozilo);
            else
                dao.promijeniVozilo(trenutnoVozilo);
            Stage stage = (Stage) okButton.getScene().getWindow();
            // do what you have to do
            stage.close();
        }
        else {
            postaviFlagove();
        }
    }

    public void prekiniFormuBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean daLiJeValidnaForma() {
        return ValidatorskaKlasa.daLiJeValidanString(modelProperty.get()) &&
                ValidatorskaKlasa.daLiJeValidanString(brojTablicaProperty.get()) &&
                ValidatorskaKlasa.daLiJeValidanString(brojSasijeProperty.get()) &&
                ValidatorskaKlasa.validirajTablice(brojTablicaProperty.get());
    }

    private void postaviFlagove() {
        if (ValidatorskaKlasa.daLiJeValidanString(modelField.getText())) {
            modelField.getStyleClass().removeAll("poljeNijeIspravno");
            modelField.getStyleClass().add("poljeIspravno");
        }
        else {
            modelField.getStyleClass().removeAll("poljeIspravno");
            modelField.getStyleClass().add("poljeNijeIspravno");
        }

        if (ValidatorskaKlasa.daLiJeValidanString(brojTablicaField.getText())) {
            brojTablicaField.getStyleClass().removeAll("poljeNijeIspravno");
            brojTablicaField.getStyleClass().add("poljeIspravno");
        }
        else {
            brojTablicaField.getStyleClass().removeAll("poljeIspravno");
            brojTablicaField.getStyleClass().add("poljeNijeIspravno");
        }

        if (ValidatorskaKlasa.daLiJeValidanString(brojSasijeField.getText())) {
            brojSasijeField.getStyleClass().removeAll("poljeNijeIspravno");
            brojSasijeField.getStyleClass().add("poljeIspravno");
        }
        else {
            brojSasijeField.getStyleClass().removeAll("poljeIspravno");
            brojSasijeField.getStyleClass().add("poljeNijeIspravno");
        }

        if (ValidatorskaKlasa.validirajTablice(brojTablicaField.getText())) {
            brojTablicaField.getStyleClass().removeAll("poljeNijeIspravno");
            brojTablicaField.getStyleClass().add("poljeIspravno");
        }
        else {
            brojTablicaField.getStyleClass().removeAll("poljeIspravno");
            brojTablicaField.getStyleClass().add("poljeNijeIspravno");
        }
    }

}
