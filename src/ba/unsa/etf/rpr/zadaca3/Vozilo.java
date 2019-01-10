package ba.unsa.etf.rpr.zadaca3;

public class Vozilo {

    private int id;
    private Proizvodjac proizvodjac;
    private String model;
    private String brojSasije;
    private String brojTablica;
    private Vlasnik vlasnik;

    public Vozilo(int id, Proizvodjac proizvodjac, String model, String brojSasije, String brojTablica, Vlasnik vlasnik) {
        this.id = id;
        this.proizvodjac = proizvodjac;
        this.model = model;
        this.brojSasije = brojSasije;
        this.brojTablica = brojTablica;
        this.vlasnik = vlasnik;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Proizvodjac getProizvodjac() {
        return proizvodjac;
    }

    public void setProizvodjac(Proizvodjac proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrojSasije() {
        return brojSasije;
    }

    public void setBrojSasije(String brojSasije) {
        this.brojSasije = brojSasije;
    }

    public String getBrojTablica() {
        return brojTablica;
    }

    public void setBrojTablica(String brojTablica) {
        this.brojTablica = brojTablica;
    }

    public Vlasnik getVlasnik() {
        return vlasnik;
    }

    public void setVlasnik(Vlasnik vlasnik) {
        this.vlasnik = vlasnik;
    }
}
