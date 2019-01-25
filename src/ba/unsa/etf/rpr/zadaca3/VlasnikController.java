package ba.unsa.etf.rpr.zadaca3;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

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
    public TextField adresaField;
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

    PostanskiBrojValidator validator;

    @FXML
    public Button cancelButton;
    @FXML
    public Button okButton;

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
        validator = new PostanskiBrojValidator("");
    }

    @FXML
    public void initialize() {
        adresaMjesto.setItems(dao.getMjesta());
        mjestoRodjenja.setItems(dao.getMjesta());
        datumField.setConverter(new StringConverter<LocalDate>() {
            String pattern = "M/d/yyyy";
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

        adresaMjesto.setConverter(new StringConverter<Mjesto>() {
            @Override
            public String toString(Mjesto mjesto) {
                return mjesto == null ? "" : mjesto.getNaziv();
            }

            @Override
            public Mjesto fromString(String s) {
                Mjesto m = new Mjesto(0, s, "");
                return m;
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
            if (trenutniVlasnik != null && staro.length() != 0 && novo != trenutniVlasnik.getMjestoPrebivalista().getNaziv()) {
                postanskiBrojField.setDisable(false);
            }
        });

        imeField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                imeField.getStyleClass().removeAll("poljeNijeIspravno");
                imeField.getStyleClass().add("poljeIspravno");
            }
            else {
                imeField.getStyleClass().removeAll("poljeIspravno");
                imeField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        prezimeField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                prezimeField.getStyleClass().removeAll("poljeNijeIspravno");
                prezimeField.getStyleClass().add("poljeIspravno");
            }
            else {
                prezimeField.getStyleClass().removeAll("poljeIspravno");
                prezimeField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        imeRoditeljaField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                imeRoditeljaField.getStyleClass().removeAll("poljeNijeIspravno");
                imeRoditeljaField.getStyleClass().add("poljeIspravno");
            }
            else {
                imeRoditeljaField.getStyleClass().removeAll("poljeIspravno");
                imeRoditeljaField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        adresaField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                adresaField.getStyleClass().removeAll("poljeNijeIspravno");
                adresaField.getStyleClass().add("poljeIspravno");
            }
            else {
                adresaField.getStyleClass().removeAll("poljeIspravno");
                adresaField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        adresaMjesto.getSelectionModel().selectedItemProperty().addListener((observableValue, staroMjesto, novoMjesto) -> {
            if (novoMjesto.getPostanskiBroj() != "") {
                postanskiBrojProperty.set(novoMjesto.getPostanskiBroj());
                postanskiBrojField.setDisable(false);
                if (ValidatorskaKlasa.daLiJeValidanString(postanskiBrojField.getText())) {
                    postanskiBrojField.getStyleClass().removeAll("poljeNijeIspravno");
                    postanskiBrojField.getStyleClass().add("poljeIspravno");
                } else {
                    postanskiBrojField.getStyleClass().removeAll("poljeIspravno");
                    postanskiBrojField.getStyleClass().add("poljeNijeIspravno");
                }
            }
        });

        adresaMjesto.valueProperty().addListener((observableValue, mjesto, n) -> {
            adresaMjesto.setValue(n);
        });

        datumField.getEditor().textProperty().addListener((observableValue, s, n) -> {
            //System.out.println(n); System.out.println(ValidatorskaKlasa.daLiJeValidanDatum(n));
            if (ValidatorskaKlasa.daLiJeValidanDatum(n)) {
                datumField.getStyleClass().removeAll("poljeNijeIspravno");
                datumField.getStyleClass().add("poljeIspravno");
            }
            else {
                datumField.getStyleClass().removeAll("poljeIspravno");
                datumField.getStyleClass().add("poljeNijeIspravno");
            }

        });

        jmbgField.textProperty().addListener((observableValue, s, t1) -> {
            if (ValidatorskaKlasa.daLiJeIspravanJMBG(jmbgField.getText()) &&
                ValidatorskaKlasa.validirajJmbgDatum(jmbgField.getText(), datumField.getValue())) {
                datumField.getStyleClass().removeAll("poljeNijeIspravno");
                datumField.getStyleClass().add("poljeIspravno");
                jmbgField.getStyleClass().removeAll("poljeNijeIspravno");
                jmbgField.getStyleClass().add("poljeIspravno");
            }
            else {
                datumField.getStyleClass().removeAll("poljeIspravno");
                datumField.getStyleClass().add("poljeNijeIspravno");
                jmbgField.getStyleClass().removeAll("poljeIspravno");
                jmbgField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        adresaMjesto.getEditor().textProperty().addListener((observableValue, s, n) -> {
            Mjesto m = new Mjesto(0, n, "");
            adresaMjestoProperty.set(m);
            adresaMjesto.setValue(m);
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                postanskiBrojField.setDisable(false);
                adresaMjesto.getStyleClass().removeAll("poljeNijeIspravno");
                adresaMjesto.getStyleClass().add("poljeIspravno");
            }
            else {
                postanskiBrojField.setDisable(true);
                adresaMjesto.getStyleClass().removeAll("poljeIspravno");
                adresaMjesto.getStyleClass().add("poljeNijeIspravno");
            }
        });

        mjestoRodjenja.getEditor().textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                mjestoRodjenja.getStyleClass().removeAll("poljeNijeIspravno");
                mjestoRodjenja.getStyleClass().add("poljeIspravno");
            }
            else {
                mjestoRodjenja.getStyleClass().removeAll("poljeIspravno");
                mjestoRodjenja.getStyleClass().add("poljeNijeIspravno");
            }
        });

        postanskiBrojField.textProperty().addListener((observableValue, s, n) -> {
            if (ValidatorskaKlasa.daLiJeValidanString(n)) {
                postanskiBrojField.getStyleClass().removeAll("poljeNijeIspravno");
                postanskiBrojField.getStyleClass().add("poljeIspravno");
            } else {
                postanskiBrojField.getStyleClass().removeAll("poljeIspravno");
                postanskiBrojField.getStyleClass().add("poljeNijeIspravno");
            }
        });

        postanskiBrojField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (aBoolean && !t1) {
                    validator.setBroj(postanskiBrojField.getText());

                    Task<Boolean> task = new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            //System.out.println("calling");
                            return validator.provjeriPostanskiBroj(postanskiBrojField.getText());
                        }
                    };

                    task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent) {
                            Boolean value = task.getValue();
                            //System.out.println(value);
                            if (value) {
                                //System.out.println("test 1");
                                postanskiBrojField.getStyleClass().removeAll("poljeNijeIspravno");
                                postanskiBrojField.getStyleClass().add("poljeIspravno");
                            } else {
                                postanskiBrojField.getStyleClass().removeAll("poljeIspravno");
                                postanskiBrojField.getStyleClass().add("poljeNijeIspravno");
                            }
                        }
                    });

                    new Thread(task).start();

                }
            }
        });

    }

    private void inicijalizirajDataBinding() {
        imeField.textProperty().bindBidirectional(imeProperty);
        prezimeField.textProperty().bindBidirectional(prezimeProperty);
        imeRoditeljaField.textProperty().bindBidirectional(imeRoditeljaProperty);
        adresaField.textProperty().bindBidirectional(adresaPrebivalistaProperty);
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
        adresaMjesto.setValue(trenutniVlasnik.getMjestoPrebivalista());
        mjestoRodjenjaProperty.set(trenutniVlasnik.getMjestoRodjenja());
    }

    public void potvrdiFormuBtn(ActionEvent actionEvent) {
        if (daLiJeValidnaForma()) {
            boolean dodavanje = trenutniVlasnik == null;
            if (trenutniVlasnik == null) {
                trenutniVlasnik = new Vlasnik();
            }
            trenutniVlasnik.setIme(imeProperty.get());
            trenutniVlasnik.setPrezime(prezimeProperty.get());
            trenutniVlasnik.setImeRoditelja(imeRoditeljaProperty.get());
            trenutniVlasnik.setJmbg(jmbgProperty.get());
            trenutniVlasnik.setDatumRodjenja(datumProperty.get());

            if (trenutniVlasnik.getMjestoPrebivalista() == null)
                trenutniVlasnik.setMjestoPrebivalista(adresaMjesto.getValue());

            if (adresaMjesto.getEditor().getText() != trenutniVlasnik.getMjestoPrebivalista().getNaziv() ||
                postanskiBrojProperty.get() != trenutniVlasnik.getMjestoPrebivalista().getPostanskiBroj()) {
                Mjesto m = new Mjesto(0, adresaMjesto.getEditor().getText(), postanskiBrojProperty.get());
                trenutniVlasnik.setMjestoPrebivalista(m);
            }
            trenutniVlasnik.setMjestoRodjenja(mjestoRodjenja.getValue());
            trenutniVlasnik.setAdresaPrebivalista(adresaPrebivalistaProperty.get());
            if (dodavanje)
                dao.dodajVlasnika(trenutniVlasnik);
            else
                dao.promijeniVlasnika(trenutniVlasnik);
            Stage stage = (Stage) okButton.getScene().getWindow();
            // do what you have to do
            stage.close();
        }
        else {
            postaviFlagove();
        }
    }

    private void postaviFlagove() {
        if (ValidatorskaKlasa.daLiJeValidanString(imeField.getText())) {
            imeField.getStyleClass().removeAll("poljeNijeIspravno");
            imeField.getStyleClass().add("poljeIspravno");
        }
        else {
            imeField.getStyleClass().removeAll("poljeIspravno");
            imeField.getStyleClass().add("poljeNijeIspravno");
        }

        if (ValidatorskaKlasa.daLiJeValidanString(postanskiBrojField.getText())) {
            postanskiBrojField.getStyleClass().removeAll("poljeNijeIspravno");
            postanskiBrojField.getStyleClass().add("poljeIspravno");
        }
        else {
            postanskiBrojField.getStyleClass().removeAll("poljeIspravno");
            postanskiBrojField.getStyleClass().add("poljeNijeIspravno");
        }
    }

    private boolean daLiJeValidnaForma() {
        return ValidatorskaKlasa.daLiJeValidanString(imeProperty.get()) &&
                ValidatorskaKlasa.daLiJeValidanString(prezimeProperty.get()) &&
                ValidatorskaKlasa.daLiJeValidanString(imeRoditeljaProperty.get()) &&
                ValidatorskaKlasa.daLiJeValidanString(adresaPrebivalistaProperty.get()) &&
                ValidatorskaKlasa.daLiJeIspravanJMBG(jmbgProperty.get()) &&
                ValidatorskaKlasa.daLiJeValidanDatum(datumField.getEditor().getText());

    }

    public void prekiniFormuBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }



}
