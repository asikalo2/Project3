package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VozilaDAOXMLTest {
    VozilaDAOXML dao = null;

    @Test
    void initDb() {
        if (dao != null) dao.close();

        try {
            initFile("mjesta.xml");
            initFile("proizvodjaci.xml");
            initFile("vlasnici.xml");
            initFile("vozila.xml");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Ne mogu kreirati datoteku");
        }

        dao = new VozilaDAOXML();
    }

    private void initFile(String file) throws IOException {
        File dbfile = new File(file);
        ClassLoader classLoader = getClass().getClassLoader();
        File srcfile = new File(classLoader.getResource("xml/" + file).getFile());
        dbfile.delete();
        Files.copy(srcfile.toPath(), dbfile.toPath());
    }

    @Test
    void getVlasnici() {
        initDb();
        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        assertEquals(1, vlasnici.size());
        assertEquals("Meho", vlasnici.get(0).getIme());
        assertEquals("Mehaga", vlasnici.get(0).getImeRoditelja());
        assertEquals(LocalDate.of(1970,1,2), vlasnici.get(0).getDatumRodjenja());
        assertEquals("Sarajevo", vlasnici.get(0).getMjestoRodjenja().getNaziv());
        assertEquals("Zmaja od Bosne bb", vlasnici.get(0).getAdresaPrebivalista());
        assertEquals("Sarajevo", vlasnici.get(0).getMjestoPrebivalista().getNaziv());
    }

    @Test
    void getVozila() {
        initDb();
        ObservableList<Vozilo> vozila = dao.getVozila();
        assertEquals(1, vozila.size());
        assertEquals("Volkswagen", vozila.get(0).getProizvodjac().getNaziv());
        assertEquals("Golf", vozila.get(0).getModel());
        assertEquals("A12-O-123", vozila.get(0).getBrojTablica());
        assertEquals("Mehic", vozila.get(0).getVlasnik().getPrezime());
    }

    @Test
    void getMjesta() {
        initDb();
        ObservableList<Mjesto> mjesta = dao.getMjesta();
        assertEquals(2, mjesta.size());
        assertEquals("71000", mjesta.get(0).getPostanskiBroj());
        assertEquals("Tuzla", mjesta.get(1).getNaziv());
    }

    @Test
    void getProizvodjaci() {
        initDb();
        ObservableList<Proizvodjac> proizvodjaci = dao.getProizvodjaci();
        assertEquals(3, proizvodjaci.size());
        // Ovo će vratiti abecedno
        assertEquals("Ford", proizvodjaci.get(0).getNaziv());
        assertEquals("Renault", proizvodjaci.get(1).getNaziv());
    }

    @Test
    void dodajVlasnika() {
        initDb();

        Mjesto sarajevo = new Mjesto(1, "Sarajevo", "71000");
        Mjesto zenica = new Mjesto(0, "Zenica", "73000");
        Vlasnik vlasnik = new Vlasnik(0, "Test", "Testovic", "Te", LocalDate.now(), sarajevo,
                "Prva ulica 1", zenica, "1234567890");
        dao.dodajVlasnika(vlasnik);

        // Provjera dodavanja
        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        // Broj vlasnika je sada 2
        assertEquals(2, vlasnici.size());
        // Na indeksu 1 je Test Testovic
        assertEquals("Testovic", vlasnici.get(1).getPrezime());
        // Novi vlasnik je dobio id 2
        assertEquals(2, vlasnici.get(1).getId());
        // jmbg
        assertEquals("1234567890", vlasnici.get(1).getJmbg());
        // Mjesto rođenja je Sarajevo
        assertEquals("Sarajevo", vlasnici.get(1).getMjestoRodjenja().getNaziv());
        // Ovo mjesto je postojeće mjesto u bazi (ima id 1)
        assertEquals(1, vlasnici.get(1).getMjestoRodjenja().getId());
        // Mjesto prebivališta je Zenica
        assertEquals("Zenica", vlasnici.get(1).getMjestoPrebivalista().getNaziv());
        // Zenica je dodata u bazu podataka pod ID-om 3
        assertEquals(3, vlasnici.get(1).getMjestoPrebivalista().getId());

        // Provjeravamo da li je Zenica zaista dodata u mjesta
        ObservableList<Mjesto> mjesta = dao.getMjesta();
        assertEquals(3, mjesta.size());
        assertEquals(3, mjesta.get(2).getId());
        assertEquals("Zenica", mjesta.get(2).getNaziv());
    }

    @Test
    void promijeniVlasnika() {
        initDb();

        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        Vlasnik vlasnik = vlasnici.get(0);
        vlasnik.setJmbg("1234567890");
        Mjesto zenica = new Mjesto(0, "Zenica", "73000");
        vlasnik.setMjestoRodjenja(zenica);

        dao.promijeniVlasnika(vlasnik);

        // Provjeravamo da li je postojeći promijenjen
        ObservableList<Vlasnik> vlasnici2 = dao.getVlasnici();
        assertEquals(1, vlasnici2.size());
        assertEquals("1234567890", vlasnici2.get(0).getJmbg());
        assertEquals("Meho", vlasnici.get(0).getIme());
        assertEquals("Zenica", vlasnici.get(0).getMjestoRodjenja().getNaziv());

        // Provjeravamo da li je Zenica zaista dodata u mjesta
        ObservableList<Mjesto> mjesta = dao.getMjesta();
        assertEquals(3, mjesta.size());
        assertEquals(3, mjesta.get(2).getId());
        assertEquals("Zenica", mjesta.get(2).getNaziv());
    }

    @Test
    void obrisiVlasnika() {
        initDb();

        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        Vlasnik mehoMehic = vlasnici.get(0);

        // Ne može se obrisati jer ima vozilo!
        assertThrows(IllegalArgumentException.class, () -> dao.obrisiVlasnika(mehoMehic));

        // Dodajemo novog vlasnika
        Mjesto sarajevo = new Mjesto(1, "Sarajevo", "71000");
        Mjesto zenica = new Mjesto(0, "Zenica", "73000");
        Vlasnik testTestovic = new Vlasnik(0, "Test", "Testović", "Te", LocalDate.now(), sarajevo,
                "Prva ulica 1", zenica, "1234567890");
        dao.dodajVlasnika(testTestovic);

        // Detaljna provjera dodavanja bi trebala biti urađena u testu dodajVlasnika()
        ObservableList<Vlasnik> vlasnici2 = dao.getVlasnici();
        assertEquals(2, vlasnici2.size());
        testTestovic = vlasnici2.get(1); // osiguravamo da je id ispravan

        // Postavljamo Testa Testovića za vlasnika vozila 1
        ObservableList<Vozilo> vozila = dao.getVozila();
        Vozilo vozilo = vozila.get(0);
        vozilo.setVlasnik(testTestovic);
        dao.promijeniVozilo(vozilo);

        // Brišemo Mehu Mehića
        dao.obrisiVlasnika(mehoMehic);

        ObservableList<Vlasnik> vlasnici3 = dao.getVlasnici();
        assertEquals(1, vlasnici3.size());
        assertEquals("Testović", vlasnici3.get(0).getPrezime());
        assertEquals(2, vlasnici3.get(0).getId());

        // Da li je vlasnik vozila stvarno promijenjen?
        ObservableList<Vozilo> vozila2 = dao.getVozila();
        assertEquals("Test", vozila2.get(0).getVlasnik().getIme());
    }

    @Test
    void dodajVozilo() {
        initDb();

        Proizvodjac renault = new Proizvodjac(2, "Renault");
        Mjesto sarajevo = new Mjesto(1, "Sarajevo", "71000");
        Vlasnik vlasnik = new Vlasnik(2, "Test", "Testovic", "Te", LocalDate.now(), sarajevo,
                "Prva ulica 1", sarajevo, "1234567890");
        Vozilo vozilo = new Vozilo(0, renault, "Megane", "98765", "E12-K-987", vlasnik);

        // Ne može se dodati jer je vlasnik nepostojeći
        assertThrows(IllegalArgumentException.class, () -> dao.dodajVozilo(vozilo));

        // Ovaj vlasnik je identičan onom u bazi
        Vlasnik mehoMehic = new Vlasnik(1, "Meho", "Mehic", "Mehaga",
                LocalDate.of(1970,2,1), sarajevo, "Zmaja od Bosne bb",
                sarajevo, "123453452345");
        vozilo.setVlasnik(mehoMehic);
        dao.dodajVozilo(vozilo);

        ObservableList<Vozilo> vozila = dao.getVozila();
        assertEquals(2, vozila.size());
        assertEquals("Renault", vozila.get(1).getProizvodjac().getNaziv());
        assertEquals("Megane", vozila.get(1).getModel());
        assertEquals("E12-K-987", vozila.get(1).getBrojTablica());
        assertEquals("Meho", vozila.get(1).getVlasnik().getIme());
    }

    @Test
    void dodajVozilo2() {
        initDb();

        // Testiramo da li će se dodati novi proizvođač
        Proizvodjac hyundai = new Proizvodjac(0, "Hyundai");
        Mjesto sarajevo = new Mjesto(1, "Sarajevo", "71000");
        Vlasnik mehoMehic = new Vlasnik(1, "Meho", "Mehic", "Mehaga",
                LocalDate.of(1970,2,1), sarajevo, "Zmaja od Bosne bb",
                sarajevo, "123453452345");
        Vozilo vozilo = new Vozilo(0, hyundai, "i30", "98765",
                "E12-K-987", mehoMehic);

        dao.dodajVozilo(vozilo);

        ObservableList<Vozilo> vozila = dao.getVozila();
        assertEquals(2, vozila.size());
        assertEquals("Hyundai", vozila.get(1).getProizvodjac().getNaziv());

        // Da li se hyundai zaista dodao u listu proizvođača?
        ObservableList<Proizvodjac> proizvodjaci = dao.getProizvodjaci();
        assertEquals(4, proizvodjaci.size());
        // Ovo će vratiti abecedno, tako da će Hyundai biti na indeksu 1 (poslije Ford a prije Renault)
        assertEquals("Hyundai", proizvodjaci.get(1).getNaziv());
        // Trebao bi dobiti Id 4
        assertEquals(4, proizvodjaci.get(1).getId());
    }

    @Test
    void promijeniVozilo() {
        initDb();

        ObservableList<Vozilo> vozila = dao.getVozila();
        Vozilo vozilo = vozila.get(0);
        Proizvodjac hyundai = new Proizvodjac(0, "Hyundai");
        vozilo.setProizvodjac(hyundai);
        vozilo.setModel("i30");
        dao.promijeniVozilo(vozilo);

        ObservableList<Vozilo> vozila2 = dao.getVozila();
        assertEquals(1, vozila2.size());
        assertEquals("Hyundai", vozila.get(0).getProizvodjac().getNaziv());
        assertEquals("i30", vozila.get(0).getModel());

        // Da li se hyundai zaista dodao u listu proizvođača?
        ObservableList<Proizvodjac> proizvodjaci = dao.getProizvodjaci();
        assertEquals(4, proizvodjaci.size());
        // Ovo će vratiti abecedno, tako da će Hyundai biti na indeksu 1 (poslije Ford a prije Renault)
        assertEquals("Hyundai", proizvodjaci.get(1).getNaziv());
        // Trebao bi dobiti Id 4
        assertEquals(4, proizvodjaci.get(1).getId());
    }

    @Test
    void obrisiVozilo() {
        initDb();

        ObservableList<Vozilo> vozila = dao.getVozila();
        Vozilo vozilo = vozila.get(0);
        dao.obrisiVozilo(vozilo);

        ObservableList<Vozilo> vozila2 = dao.getVozila();
        assertEquals(0, vozila2.size());
    }
}