package ba.unsa.etf.rpr.zadaca3;

public class Mjesto {

    private Integer id;
    private String naziv;
    private String postanskiBroj;

    public Mjesto(Integer id, String naziv, String postanskiBroj) {
        this.id = id;
        this.naziv = naziv;
        this.postanskiBroj = postanskiBroj;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
