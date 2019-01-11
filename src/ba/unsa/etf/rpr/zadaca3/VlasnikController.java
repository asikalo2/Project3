package ba.unsa.etf.rpr.zadaca3;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VlasnikController {

    private VozilaDAO dao;
    private Vlasnik trenutniVlasnik = null;

    public ComboBox<Mjesto> adresaMjesto;
    public ComboBox<Mjesto> mjestoRodjenja;
    public TextField imeField;
    public TextField prezimeField;
    public TextField imeRoditeljaField;
    public TextField adresaPrebivalistaField;
    public TextField jmbgField;
    public DatePicker datumField;
    public TextField postanskiBrojField;

    public SimpleStringProperty imeProperty;
    public SimpleStringProperty prezimeProperty;
    public SimpleStringProperty imeRoditeljaProperty;
    public SimpleStringProperty adresaPrebivalistaProperty;
    public SimpleStringProperty jmbgProperty;
    public SimpleStringProperty postanskiBrojProperty;
    public SimpleObjectProperty<LocalDate> datumProperty;
    public SimpleObjectProperty<Mjesto> adresaMjestoProperty;
    public SimpleObjectProperty<Mjesto> mjestoRodjenjaProperty;

    public VlasnikController(VozilaDAO dao, Vlasnik vlasnik) {
        this.dao = dao;
        this.trenutniVlasnik = vlasnik;
        imeProperty = new SimpleStringProperty("");
        prezimeProperty = new SimpleStringProperty("");
        imeRoditeljaProperty = new SimpleStringProperty("");
        adresaPrebivalistaProperty = new SimpleStringProperty("");
        jmbgProperty = new SimpleStringProperty("");
        postanskiBrojProperty = new SimpleStringProperty("");
        datumProperty = new SimpleObjectProperty<LocalDate>();
        adresaMjestoProperty = new SimpleObjectProperty<Mjesto>();
        mjestoRodjenjaProperty = new SimpleObjectProperty<Mjesto>();
    }

    @FXML
    public void initialize() {
        adresaMjesto.setItems(dao.getMjesta());
        mjestoRodjenja.setItems(dao.getMjesta());
        datumField.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd.MM.yyyy.";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        postanskiBrojField.setDisable(true);
        inicijalizirajDataBinding();
        dodajListenere();
        if (trenutniVlasnik != null) {
            popuniFormu();
        }
    }

    private void dodajListenere() {
        adresaMjesto.getEditor().textProperty().addListener((observableValue, staro, novo) -> {
            System.out.println(staro);
            System.out.println(novo);
            if (staro.length() != 0 && novo != trenutniVlasnik.getMjestoPrebivalista().getNaziv()) {
                postanskiBrojField.setDisable(false);
            }
        });
    }

    private void inicijalizirajDataBinding() {
        imeField.textProperty().bindBidirectional(imeProperty);
        prezimeField.textProperty().bindBidirectional(prezimeProperty);
        imeRoditeljaField.textProperty().bindBidirectional(imeRoditeljaProperty);
        adresaPrebivalistaField.textProperty().bindBidirectional(adresaPrebivalistaProperty);
        jmbgField.textProperty().bindBidirectional(jmbgProperty);
        postanskiBrojField.textProperty().bindBidirectional(postanskiBrojProperty);
        datumField.valueProperty().bindBidirectional(datumProperty);
        adresaMjesto.valueProperty().bindBidirectional(adresaMjestoProperty);
        mjestoRodjenja.valueProperty().bindBidirectional(mjestoRodjenjaProperty);
    }

    private void popuniFormu() {
        imeProperty.set(trenutniVlasnik.getIme());
        prezimeProperty.set(trenutniVlasnik.getPrezime());
        imeRoditeljaProperty.set(trenutniVlasnik.getImeRoditelja());
        adresaPrebivalistaProperty.set(trenutniVlasnik.getAdresaPrebivalista());
        jmbgProperty.set(trenutniVlasnik.getJmbg());
        postanskiBrojProperty.set(trenutniVlasnik.getMjestoPrebivalista().getPostanskiBroj());
        datumProperty.set(trenutniVlasnik.getDatumRodjenja());
        adresaMjestoProperty.set(trenutniVlasnik.getMjestoPrebivalista());
        mjestoRodjenjaProperty.set(trenutniVlasnik.getMjestoRodjenja());
    }

}
