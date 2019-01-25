package ba.unsa.etf.rpr.zadaca3;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

@ExtendWith(ApplicationExtension.class)
class VlasnikControllerTest {
    Stage theStage;
    VozilaDAO dao;
    VlasnikController controller;

    @Start
    public void start (Stage stage) throws Exception {
        File dbfile = new File("vozila.db");
        ClassLoader classLoader = getClass().getClassLoader();
        File srcfile = new File(classLoader.getResource("db/vozila.db").getFile());
        try {
            dbfile.delete();
            Files.copy(srcfile.toPath(), dbfile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Ne mogu kreirati bazu");
        }

        dao = new VozilaDAOBaza();

        // Ovo bi trebalo da iskopira fajl iz resources u test-resources, a ipak radi i sa mavenom
        File fxml = new File("resources/fxml/vlasnik.fxml");
        if (fxml.exists()) {
            File rsrc = new File("test-resources/fxml/vlasnik.fxml");
            if (rsrc.exists()) rsrc.delete();
            Files.copy(fxml.toPath(), rsrc.toPath());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vlasnik.fxml"));
        VlasnikController vlasnikController = new VlasnikController(dao, null);
        loader.setController(vlasnikController);
        Parent root = loader.load();
        stage.setTitle("Vlasnik");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();
        stage.toFront();

        theStage = stage;
    }

    @Test
    public void testCancel (FxRobot robot) {
        robot.clickOn("#cancelButton");
        assertFalse(theStage.isShowing());
    }

    @Test
    public void testOk (FxRobot robot) {
        robot.clickOn("#okButton");
        // Forma nije validna i neće se zatvoriti!
        assertTrue(theStage.isShowing());

        TextField ime = robot.lookup("#imeField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testImeValidacija (FxRobot robot) {
        // Ovim testom provjeravamo sva polja čiji je uslov validnosti da polje nije prazno
        robot.clickOn("#imeField");
        robot.write("abc");
        robot.clickOn("#prezimeField");
        robot.write("d");
        robot.clickOn("#imeRoditeljaField");
        robot.write("e");
        robot.clickOn("#adresaField");
        robot.write("f");

        robot.clickOn("#okButton");
        // Forma nije validna i neće se zatvoriti!

        TextField ime = robot.lookup("#imeField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills()) {
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        }
        assertTrue(colorFound);

        ime = robot.lookup("#prezimeField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

        ime = robot.lookup("#imeRoditeljaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

        ime = robot.lookup("#adresaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

    }

    @Test
    public void testDatumValidacija (FxRobot robot) {
        robot.clickOn("#datumField");
        robot.write("1/1/2020");

        robot.clickOn("#okButton");

        DatePicker ime = robot.lookup("#datumField").queryAs(DatePicker.class);
        Background bg = ime.getEditor().getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#datumField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("1/1/2018");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);

        robot.clickOn("#okButton");

        ime = robot.lookup("#datumField").queryAs(DatePicker.class);
        bg = ime.getEditor().getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testNovoMjesto (FxRobot robot) {
        // Ako se unese novo mjesto prebivališta, polje poštanski broj ne smije biti prazno
        robot.clickOn("#adresaMjesto");
        robot.write("Zenica");

        robot.clickOn("#okButton");

        TextField ime = robot.lookup("#postanskiBrojField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#postanskiBrojField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("75000");

        robot.clickOn("#okButton");

        // Dajemo vremena da se validira poštanski broj
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ime = robot.lookup("#postanskiBrojField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testMjesta (FxRobot robot) {
        ComboBox adresaMjesto = robot.lookup("#adresaMjesto").queryAs(ComboBox.class);
        Platform.runLater(() -> adresaMjesto.show());

        // Čekamo da se pojavi meni
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Sarajevo");
        //robot.press(KeyCode.DOWN).press(KeyCode.ENTER);

        String mjesto = robot.lookup("#adresaMjesto").queryAs(ComboBox.class).getValue().toString();
        assertEquals("Sarajevo", mjesto);

        String postanskiBroj = robot.lookup("#postanskiBrojField").queryAs(TextField.class).getText();
        assertEquals("71000", postanskiBroj);

    }

    @Test
    public void testJmbgValidacija (FxRobot robot) {
        robot.clickOn("#datumField");
        robot.write("1/8/2003");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);

        robot.clickOn("#jmbgField");
        robot.write("1234");

        robot.clickOn("#okButton");

        TextField ime = robot.lookup("#jmbgField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#jmbgField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("080100350");

        robot.clickOn("#okButton");

        ime = robot.lookup("#jmbgField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#jmbgField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("0801003500006");

        robot.clickOn("#okButton");

        ime = robot.lookup("#jmbgField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#jmbgField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("0801003500007");

        robot.clickOn("#okButton");

        ime = robot.lookup("#jmbgField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testDodavanje (FxRobot robot) {
        robot.clickOn("#imeField");
        robot.write("abc");
        robot.clickOn("#prezimeField");
        robot.write("d");
        robot.clickOn("#imeRoditeljaField");
        robot.write("e");
        robot.clickOn("#adresaField");
        robot.write("f");
        robot.clickOn("#datumField");
        robot.write("1/8/2003");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.clickOn("#jmbgField");
        robot.write("0801003500007");

        ComboBox mjestoRodjenja = robot.lookup("#mjestoRodjenja").queryAs(ComboBox.class);
        Platform.runLater(() -> mjestoRodjenja.show());

        // Čekamo da se pojavi meni
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Sarajevo");

        robot.clickOn("#adresaMjesto");
        robot.write("Zenica");

        robot.clickOn("#postanskiBrojField");
        robot.write("75000");

        // Sve validno, prozor se zatvara
        robot.clickOn("#okButton");

        // Dajemo vremena da se validira poštanski broj
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(theStage.isShowing());

        // Da li je novi vlasnik u bazi
        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        assertEquals(2, vlasnici.size());
        assertEquals(2, vlasnici.get(1).getId());
        assertEquals("abc", vlasnici.get(1).getIme());
        assertEquals("d", vlasnici.get(1).getPrezime());
        assertEquals("e", vlasnici.get(1).getImeRoditelja());
        assertEquals("f", vlasnici.get(1).getAdresaPrebivalista());
        assertEquals(LocalDate.of(2003,1,8), vlasnici.get(1).getDatumRodjenja());
        assertEquals("0801003500007", vlasnici.get(1).getJmbg());
        assertEquals("Sarajevo", vlasnici.get(1).getMjestoRodjenja().getNaziv());
        assertEquals(1, vlasnici.get(1).getMjestoRodjenja().getId());
        assertEquals("Zenica", vlasnici.get(1).getMjestoPrebivalista().getNaziv());
        assertEquals(3, vlasnici.get(1).getMjestoPrebivalista().getId());

        // Provjeravamo da li je Zenica zaista dodata u mjesta
        ObservableList<Mjesto> mjesta = dao.getMjesta();
        assertEquals(3, mjesta.size());
        assertEquals(3, mjesta.get(2).getId());
        assertEquals("Zenica", mjesta.get(2).getNaziv());
        assertEquals("75000", mjesta.get(2).getPostanskiBroj());
    }
}