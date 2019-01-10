package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VozilaDAOXML implements VozilaDAO {

    private void ispisiVlasniciXML(ArrayList<Vlasnik> lista) {
        XMLEncoder encoder=null;
        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getClass().getClassLoader().getResource(
                    "vlasnici.xml").getFile())));
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        encoder.writeObject(lista);
        encoder.close();
    }

    @Override
    public ObservableList<Vlasnik> getVlasnici() {
        try {
            FileInputStream fis = new FileInputStream(getClass().getClassLoader().getResource(
                    "xml/vlasnici.xml").getFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            XMLDecoder xmlDecoder = new XMLDecoder(bis);
            ArrayList<Vlasnik> vlasnici = (ArrayList<Vlasnik>) xmlDecoder.readObject();
            return FXCollections.observableArrayList(vlasnici);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Vozilo> getVozila() {
        try {
            FileInputStream fis = new FileInputStream(getClass().getClassLoader().getResource(
                    "xml/vozila.xml").getFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            XMLDecoder xmlDecoder = new XMLDecoder(bis);
            ArrayList<Vozilo> vozila = (ArrayList<Vozilo>) xmlDecoder.readObject();
            return FXCollections.observableArrayList(vozila);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Mjesto> getMjesta() {
        try {
            FileInputStream fis = new FileInputStream(getClass().getClassLoader().getResource(
                    "xml/mjesta.xml").getFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            XMLDecoder xmlDecoder = new XMLDecoder(bis);
            ArrayList<Mjesto> mjesta = (ArrayList<Mjesto>) xmlDecoder.readObject();
            xmlDecoder.close();
            return FXCollections.observableArrayList(mjesta);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Proizvodjac> getProizvodjaci() {
        try {
            FileInputStream fis = new FileInputStream(getClass().getClassLoader().getResource(
                    "xml/proizvodjaci.xml").getFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            XMLDecoder xmlDecoder = new XMLDecoder(bis);
            ArrayList<Proizvodjac> proizvodjaci = (ArrayList<Proizvodjac>) xmlDecoder.readObject();
            xmlDecoder.close();
            return FXCollections.observableArrayList(proizvodjaci);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void dodajVlasnika(Vlasnik vlasnik) {
        ObservableList<Vlasnik> vlasnici = getVlasnici();
        if (vlasnik.getMjestoRodjenja().getId() == 0) {
            dodajMjesto(vlasnik.getMjestoRodjenja());
        }
        if (vlasnik.getId() == 0) {
            int maxId = vlasnici.stream()
                    .max(Comparator.comparing(Vlasnik::getId)).get().getId();
            vlasnik.setId(maxId+1);
        }
        if (vlasnik.getMjestoRodjenja().getId() == 0) {
            int id = dodajMjesto(vlasnik.getMjestoRodjenja());
            vlasnik.getMjestoRodjenja().setId(id);
        }
        if (vlasnik.getMjestoPrebivalista().getId() == 0) {
            int id = dodajMjesto(vlasnik.getMjestoPrebivalista());
            vlasnik.getMjestoPrebivalista().setId(id);
        }
        vlasnici.add(vlasnik);
        ArrayList<Vlasnik> temp = new ArrayList<Vlasnik>(vlasnici);
        ispisiVlasniciXML(temp);
    }

    private int dodajMjesto(Mjesto mjesto) {
        ObservableList<Mjesto> mjesta = getMjesta();
        int maxId = 0;
        if (mjesto.getId() == 0) {
            maxId = mjesta.stream()
                    .max(Comparator.comparing(Mjesto::getId)).get().getId();
            mjesto.setId(maxId+1);
        }
        mjesta.add(mjesto);
        ArrayList<Mjesto> temp = new ArrayList<Mjesto>(mjesta);
        ispisiMjestaXML(temp);
        return maxId+1;
    }

    private void ispisiMjestaXML(ArrayList<Mjesto> lista) {
        XMLEncoder encoder=null;
        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getClass().getClassLoader().getResource(
                    "xml/mjesta.xml").getFile())));
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        encoder.writeObject(lista);
        encoder.close();
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
