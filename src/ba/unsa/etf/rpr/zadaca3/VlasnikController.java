package ba.unsa.etf.rpr.zadaca3;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;

import javax.swing.plaf.synth.SynthTextAreaUI;

public class VlasnikController {

    public ComboBox<Mjesto> adresaMjesto;
    public ComboBox<Mjesto> mjestoRodjenja;

    public VlasnikController(VozilaDAO dao, Vlasnik vlasnik) {
        System.out.println(dao.getMjesta());
        adresaMjesto.setItems(dao.getMjesta());

        System.out.println(vlasnik.getIme());

    }

}
