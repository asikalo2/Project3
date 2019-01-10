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
                LocalDate datumRodjenja = Instant.ofEpochMilli(Integer.valueOf(
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

    @Override
    public void dodajVlasnika(Vlasnik vlasnik) {
        int noviId = dajNajveciIdVozila() + 1;
        System.out.println(noviId);

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
