package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.ObservableList;

public interface VozilaDAO {

    ObservableList<Vlasnik> getVlasnici();
    ObservableList<Vozilo> getVozila();
    ObservableList<Mjesto> getMjesta();
    ObservableList<Proizvodjac> getProizvodjaci();
    void dodajVlasnika(Vlasnik vlasnik);
    void promijeniVlasnika(Vlasnik vlasnik);
    void obrisiVlasnika(Vlasnik vlasnik);
    void dodajVozilo(Vozilo vozilo);
    void promijeniVozilo(Vozilo vozilo);
    void obrisiVozilo(Vozilo vozilo);
    void close();

}
