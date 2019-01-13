package ba.unsa.etf.rpr.zadaca3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class VozilaDAOBaza implements VozilaDAO {

    private static VozilaDAOBaza instance = null;

    private static Connection conn;

    public static Connection getConn() {
        return conn;
    }

    public VozilaDAOBaza() {
        conn = null;

        try {
            String url = "jdbc:sqlite:vozila.db";
            conn = DriverManager.getConnection(url);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void initialize() {
        instance = new VozilaDAOBaza();
    }

    public static VozilaDAOBaza getInstance() {
        if (instance == null) initialize();
        return instance;
    }

    public static void removeInstance() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        instance = null;
    }


    @Override
    public ObservableList<Vlasnik> getVlasnici() {
        ArrayList<Vlasnik> res = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from (select * from vlasnik join mjesto on " +
                    "vlasnik.mjesto_prebivalista=mjesto.id)\n" +
                    "join mjesto on mjesto_rodjenja=mjesto.id;\n");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Mjesto mjestoRodjenja = new Mjesto(rs.getInt(13), rs.getString(14), rs.getString(15));
                Mjesto mjestoPrebivalista = new Mjesto(rs.getInt(10), rs.getString(11), rs.getString(12));
                LocalDate datumRodjenja = Instant.ofEpochSecond(Long.valueOf(
                        rs.getString(5))).atZone(ZoneId.systemDefault()).toLocalDate();
                Vlasnik vlasnik = new Vlasnik(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), datumRodjenja, mjestoRodjenja, rs.getString(7),
                        mjestoPrebivalista, rs.getString(9));
                res.add(vlasnik);
            }
            return FXCollections.observableArrayList(res);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Vozilo> getVozila() {
        ArrayList<Vozilo> res = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from (select * from vozilo join proizvodjac, vlasnik, mjesto on \n" +
                    "vozilo.proizvodjac=proizvodjac.id and \n" +
                    "vozilo.vlasnik=vlasnik.id and \n" +
                    "vlasnik.mjesto_rodjenja=mjesto.id) join mjesto on\n" +
                    "mjesto_prebivalista=mjesto.id;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Proizvodjac p = new Proizvodjac(rs.getInt(2), rs.getString(8));
                Mjesto mjestoRodjenja = new Mjesto(rs.getInt(18), rs.getString(19), rs.getString(20));
                Mjesto mjestoPrebivalista = new Mjesto(rs.getInt(21), rs.getString(22), rs.getString(23));
                LocalDate datumRodjenja = Instant.ofEpochMilli(Integer.valueOf(
                        rs.getString(13))).atZone(ZoneId.systemDefault()).toLocalDate();
                Vlasnik vlasnik = new Vlasnik(rs.getInt(9), rs.getString(10),
                        rs.getString(11), rs.getString(12), datumRodjenja, mjestoRodjenja, rs.getString(15),
                        mjestoPrebivalista, rs.getString(17));
                Vozilo vozilo = new Vozilo(rs.getInt(1), p, rs.getString(3), rs.getString(4),
                        rs.getString(5), vlasnik);
                res.add(vozilo);
            }
            return FXCollections.observableArrayList(res);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Mjesto> getMjesta() {
        ArrayList<Mjesto> res = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, naziv, postanski_broj FROM mjesto " +
                    "ORDER BY naziv ASC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Mjesto m = new Mjesto(rs.getInt(1), rs.getString(2), rs.getString(3));
                res.add(m);
            }
            return FXCollections.observableArrayList(res);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Proizvodjac> getProizvodjaci() {
        ArrayList<Proizvodjac> res = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM proizvodjac " +
                    "ORDER BY naziv DESC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Proizvodjac m = new Proizvodjac(rs.getInt(1), rs.getString(2));
                res.add(m);
            }
            return FXCollections.observableArrayList(res);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private int dajNajveciIdVozila() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT max(id) from vozilo");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private int dajNajveciIdVlasnika() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT max(id) from vlasnik");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private int dajNajveciIdMjesta() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT max(id) from mjesto");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private int dajNajveciIdProizvodjac() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT max(id) from proizvodjac");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private void dodajMjesto(Mjesto mjesto) {
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO mjesto(id, naziv, postanski_broj) VALUES(?,?,?)");
            stmt.setInt(1, mjesto.getId());
            stmt.setString(2, mjesto.getNaziv());
            stmt.setString(3, mjesto.getPostanskiBroj());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void dodajVlasnika(Vlasnik vlasnik) {
        if (vlasnik.getMjestoRodjenja().getId() == 0) {
            vlasnik.getMjestoRodjenja().setId(dajNajveciIdMjesta() + 1);
            dodajMjesto(vlasnik.getMjestoRodjenja());
        }
        if (vlasnik.getMjestoPrebivalista().getId() == 0) {
            vlasnik.getMjestoPrebivalista().setId(dajNajveciIdMjesta() + 1);
            dodajMjesto(vlasnik.getMjestoPrebivalista());
        }
        vlasnik.setId(dajNajveciIdVlasnika()+1);
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO vlasnik(id, ime, prezime, " +
                    "ime_roditelja, datum_rodjenja, mjesto_rodjenja, adresa_prebivalista, mjesto_prebivalista," +
                    "jmbg) VALUES(?,?,?,?,?,?,?,?,?)");
            stmt.setInt(1, vlasnik.getId());
            stmt.setString(2, vlasnik.getIme());
            stmt.setString(3, vlasnik.getPrezime());
            stmt.setString(4, vlasnik.getImeRoditelja());
            stmt.setString(5, String.valueOf(vlasnik.getDatumRodjenja().atStartOfDay(
                    ZoneId.systemDefault()).toEpochSecond()));
            stmt.setInt(6, vlasnik.getMjestoRodjenja().getId());
            stmt.setString(7, vlasnik.getAdresaPrebivalista());
            stmt.setInt(8, vlasnik.getMjestoPrebivalista().getId());
            stmt.setString(9, vlasnik.getJmbg());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void promijeniVlasnika(Vlasnik vlasnik) {
        try {

            if (vlasnik.getMjestoRodjenja().getId() == 0) {
                vlasnik.getMjestoRodjenja().setId(dajNajveciIdMjesta() + 1);
                dodajMjesto(vlasnik.getMjestoRodjenja());
            }

            if (vlasnik.getMjestoPrebivalista().getId() == 0) {
                vlasnik.getMjestoPrebivalista().setId(dajNajveciIdMjesta() + 1);
                dodajMjesto(vlasnik.getMjestoPrebivalista());
            }

            PreparedStatement stmt = conn.prepareStatement("UPDATE vlasnik SET ime=?, prezime=?, " +
                    "ime_roditelja=?, datum_rodjenja=?, mjesto_rodjenja=?, adresa_prebivalista=?, mjesto_prebivalista=?," +
                    "jmbg=? WHERE id=?");
            stmt.setString(1, vlasnik.getIme());
            stmt.setString(2, vlasnik.getPrezime());
            stmt.setString(3, vlasnik.getImeRoditelja());
            stmt.setString(4, String.valueOf(vlasnik.getDatumRodjenja().atStartOfDay(
                    ZoneId.systemDefault()).toEpochSecond()));
            stmt.setInt(5, vlasnik.getMjestoRodjenja().getId());
            stmt.setString(6, vlasnik.getAdresaPrebivalista());
            stmt.setInt(7, vlasnik.getMjestoPrebivalista().getId());
            stmt.setString(8, vlasnik.getJmbg());
            stmt.setInt(9, vlasnik.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }

    private Boolean daLiPostojiVlasnik(int id) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT id from vlasnik WHERE id=?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void obrisiVlasnika(Vlasnik vlasnik) {
        if (daLiPosjedujeVozilo(vlasnik)) {
            throw new IllegalArgumentException("Vlasnik posjeduje vozilo!");
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM vlasnik WHERE id=?");
            stmt.setInt(1, vlasnik.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean daLiPosjedujeVozilo(Vlasnik vlasnik) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT id from vozilo WHERE vlasnik=?");
            stmt.setInt(1, vlasnik.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void dodajVozilo(Vozilo vozilo) {
        if (!daLiPostojiVlasnik(vozilo.getVlasnik().getId())) {
            throw new IllegalArgumentException("Ne postoji vlasnik!");
        }
        if (vozilo.getId() == 0) {
            vozilo.setId(dajNajveciIdVozila()+1);
        }
        if (vozilo.getProizvodjac().getId() == 0) {
            vozilo.getProizvodjac().setId(dajNajveciIdProizvodjac()+1);
            dodajProizvodjac(vozilo.getProizvodjac());
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO vozilo(id, proizvodjac, model, " +
                    "broj_sasije, broj_tablica, vlasnik) VALUES(?,?,?,?,?,?)");
            stmt.setInt(1, vozilo.getId());
            stmt.setInt(2, vozilo.getProizvodjac().getId());
            stmt.setString(3, vozilo.getModel());
            stmt.setString(4, vozilo.getBrojSasije());
            stmt.setString(5, vozilo.getBrojTablica());
            stmt.setInt(6, vozilo.getVlasnik().getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }

    private void dodajProizvodjac(Proizvodjac proizvodjac) {
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO proizvodjac(id, naziv) VALUES(?,?)");
            stmt.setInt(1, proizvodjac.getId());
            stmt.setString(2, proizvodjac.getNaziv());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void promijeniVozilo(Vozilo vozilo) {
        try {
            if (vozilo.getProizvodjac().getId() == 0) {
                vozilo.getProizvodjac().setId(dajNajveciIdProizvodjac()+1);
                dodajProizvodjac(vozilo.getProizvodjac());
            }

            PreparedStatement stmt = conn.prepareStatement("UPDATE vozilo SET id=?, proizvodjac=?, model=?, " +
                    "broj_sasije=?, broj_tablica=?, vlasnik=? WHERE id=?");

            stmt.setInt(1, vozilo.getId());
            stmt.setInt(2, vozilo.getProizvodjac().getId());
            stmt.setString(3, vozilo.getModel());
            stmt.setString(4, vozilo.getBrojSasije());
            stmt.setString(5, vozilo.getBrojTablica());
            stmt.setInt(6, vozilo.getVlasnik().getId());
            stmt.setInt(7, vozilo.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void obrisiVozilo(Vozilo vozilo) {
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM vozilo WHERE id=?");
            stmt.setInt(1, vozilo.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() {

    }
}
