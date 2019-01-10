package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class VozilaDAOXML implements VozilaDAO {

    @Override
    public ObservableList<Vlasnik> getVlasnici() {
        try {
            FileInputStream fis = new FileInputStream("vlasnici.xml");
            BufferedInputStream bis = new BufferedInputStream(fis);
            XMLDecoder xmlDecoder = new XMLDecoder(bis);
            return null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Vozilo> getVozila() {
        return null;
    }

    @Override
    public ObservableList<Mjesto> getMjesta() {
        return null;
    }

    @Override
    public ObservableList<Proizvodjac> getProizvodjaci() {
        try {
            FileInputStream fis = new FileInputStream(getClass().getClassLoader().getResource(
                    "xml/proizvodjaci.xml").getFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            XMLDecoder xmlDecoder = new XMLDecoder(bis);
            ArrayList proizvodjaci = (ArrayList) xmlDecoder.readObject();
            xmlDecoder.close();
            return null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void dodajVlasnika(Vlasnik vlasnik) {

    }

    @Override
    public void promijeniVlasnika(Vlasnik vlasnik) {

    }

    @Override
    public void obrisiVlasnika(Vlasnik vlasnik) {

    }

    @Override
    public void dodajVozilo(Vozilo vozilo) {

    }

    @Override
    public void promijeniVozilo(Vozilo vozilo) {

    }

    @Override
    public void obrisiVozilo(Vozilo vozilo) {

    }

    @Override
    public void close() {


    }
}
