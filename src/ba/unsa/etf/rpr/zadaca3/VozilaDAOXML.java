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
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream("vlasnici.xml")));
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
            FileInputStream fis = new FileInputStream("vlasnici.xml");
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
            FileInputStream fis = new FileInputStream("vozila.xml");
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
            FileInputStream fis = new FileInputStream("mjesta.xml");
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
            FileInputStream fis = new FileInputStream("proizvodjaci.xml");
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
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream("mjesta.xml")));
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        encoder.writeObject(lista);
        encoder.close();
    }

    @Override
    public void promijeniVlasnika(Vlasnik vlasnik) {
        ObservableList<Vlasnik> vlasnici = getVlasnici();
        if (vlasnik.getMjestoRodjenja().getId() == 0) {
            int id = dodajMjesto(vlasnik.getMjestoRodjenja());
            vlasnik.getMjestoRodjenja().setId(id);
        }
        if (vlasnik.getMjestoPrebivalista().getId() == 0) {
            int id = dodajMjesto(vlasnik.getMjestoPrebivalista());
            vlasnik.getMjestoPrebivalista().setId(id);
        }
        for (Vlasnik v: vlasnici) {
            if (v.getId() == vlasnik.getId())
                vlasnici.set(vlasnici.indexOf(v), vlasnik);
        }
        ArrayList<Vlasnik> temp = new ArrayList<Vlasnik>(vlasnici);
        ispisiVlasniciXML(temp);

    }

    @Override
    public void obrisiVlasnika(Vlasnik vlasnik) {
        if (daLiPosjedujeVozilo(vlasnik)) {
            throw new IllegalArgumentException("Vlasnik posjeduje vozilo!");
        }
        ObservableList<Vlasnik> vlasnici = getVlasnici();
        ArrayList<Vlasnik> temp = new ArrayList<Vlasnik>(vlasnici);
        int i = 0;
        for (Vlasnik v : temp) {
            if(v.getId() == vlasnik.getId()) {
                temp.remove(i);
                break;
            }
            i++;
        }
        ispisiVlasniciXML(temp);


    }

    private boolean daLiPosjedujeVozilo(Vlasnik vlasnik) {
        ObservableList<Vozilo> vozila = getVozila();
        for(Vozilo v : vozila) {
            if (v.getVlasnik().getId() == vlasnik.getId())
                return true;
        }
        return false;
    }

    @Override
    public void dodajVozilo(Vozilo vozilo) {
        ObservableList<Vozilo> vozila = getVozila();
        if (!daLiPostojiVlasnik(vozilo)) {
            throw new IllegalArgumentException("Ne postoji vlasnik!");
        }
        if (vozilo.getId() == 0) {
            int maxId = vozila.stream()
                    .max(Comparator.comparing(Vozilo::getId)).get().getId();
            vozilo.setId(maxId+1);
        }
        ObservableList<Proizvodjac> proizvodjaci = getProizvodjaci();
        if (vozilo.getProizvodjac().getId() == 0) {
            int maxId = proizvodjaci.stream()
                    .max(Comparator.comparing(Proizvodjac::getId)).get().getId();
            vozilo.getProizvodjac().setId(maxId+1);
            proizvodjaci.add(vozilo.getProizvodjac());
            ArrayList<Proizvodjac> temp = new ArrayList<Proizvodjac>(proizvodjaci);
            ispisiProizvodjaceXML(temp);
        }
        vozila.add(vozilo);
        ArrayList<Vozilo> temp = new ArrayList<Vozilo>(vozila);
        ispisiVozilaXML(temp);

    }

    private void ispisiProizvodjaceXML(ArrayList<Proizvodjac> lista) {
        XMLEncoder encoder=null;
        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream("proizvodjaci.xml")));
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        encoder.writeObject(lista);
        encoder.close();
    }


    private void ispisiVozilaXML(ArrayList<Vozilo> lista) {
        XMLEncoder encoder=null;
        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream("vozila.xml")));
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        encoder.writeObject(lista);
        encoder.close();
    }

    private boolean daLiPostojiVlasnik(Vozilo vozilo) {
        ObservableList<Vlasnik> vlasnici = getVlasnici();
        for (Vlasnik v : vlasnici) {
            if (vozilo.getVlasnik().getId() == v.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void promijeniVozilo(Vozilo vozilo) {
        ObservableList<Vozilo> vozila = getVozila();
        ObservableList<Proizvodjac> proizvodjaci = getProizvodjaci();
        if (vozilo.getProizvodjac().getId() == 0) {
            int maxId = proizvodjaci.stream()
                    .max(Comparator.comparing(Proizvodjac::getId)).get().getId();
            vozilo.getProizvodjac().setId(maxId+1);
            proizvodjaci.add(vozilo.getProizvodjac());
            ArrayList<Proizvodjac> temp = new ArrayList<Proizvodjac>(proizvodjaci);
            ispisiProizvodjaceXML(temp);
        }
        for (Vozilo v: vozila) {
            if (v.getId() == vozilo.getId())
                vozila.set(vozila.indexOf(v), vozilo);
        }
        //vozila.add(vozilo);
        ArrayList<Vozilo> temp = new ArrayList<Vozilo>(vozila);
        ispisiVozilaXML(temp);

    }

    @Override
    public void obrisiVozilo(Vozilo vozilo) {
        ObservableList<Vozilo> vozila = getVozila();
        ArrayList<Vozilo> temp = new ArrayList<Vozilo>(vozila);
        int i = 0;
        for (Vozilo v : temp) {
            if(v.getId() == vozilo.getId()) {
                temp.remove(i);
                break;
            }
            i++;
        }
        ispisiVozilaXML(temp);
    }

    @Override
    public void close() {
        File dbfile = new File("mjesta.xml");
        dbfile.delete();
        dbfile = new File("proizvodjaci.xml");
        dbfile.delete();

        dbfile = new File("vlasnici.xml");
        dbfile.delete();

        dbfile = new File("vozila.xml");
        dbfile.delete();
    }
}
