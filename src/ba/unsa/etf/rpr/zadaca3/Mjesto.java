package ba.unsa.etf.rpr.zadaca3;

public class Mjesto {

    private int id;
    private String naziv;
    private String postanskiBroj;

    public Mjesto() {
        this.id = 0;
        this.naziv = "";
        this.postanskiBroj = "";
    }

    public Mjesto(int id, String naziv, String postanskiBroj) {
        this.id = id;
        this.naziv = naziv;
        this.postanskiBroj = postanskiBroj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getPostanskiBroj() {
        return postanskiBroj;
    }

    public void setPostanskiBroj(String postanskiBroj) {
        this.postanskiBroj = postanskiBroj;
    }

    @Override
    public String toString() {
        return this.getNaziv();
    }
}
