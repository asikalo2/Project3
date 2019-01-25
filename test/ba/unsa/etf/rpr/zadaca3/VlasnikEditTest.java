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
public class VlasnikEditTest {
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

        // Dodajemo novog vlasnika
        Mjesto sarajevo = new Mjesto(1, "Sarajevo", "71000");
        Vlasnik testTestovic = new Vlasnik(0, "Test", "Testović", "Te", LocalDate.now(), sarajevo,
                "Zmaja od Bosne bb", sarajevo, "1234567890");
        dao.dodajVlasnika(testTestovic);

        // Detaljna provjera dodavanja bi trebala biti urađena u testu dodajVlasnika()
        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        testTestovic = vlasnici.get(1);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vlasnik.fxml"));
        VlasnikController vlasnikController = new VlasnikController(dao, testTestovic);
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
        Platform.runLater(() -> theStage.show());
    }

    @Test
    public void testOk (FxRobot robot) {
        robot.clickOn("#imeField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

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
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

        robot.clickOn("#prezimeField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("d");
        robot.clickOn("#imeRoditeljaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("e");
        robot.clickOn("#adresaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("f");

        robot.clickOn("#okButton");
        // Forma nije validna i neće se zatvoriti!

        TextField ime = robot.lookup("#prezimeField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
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
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("1/1/2020");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);

        robot.clickOn("#okButton");
        assertTrue(theStage.isShowing());

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
        robot.clickOn("#imeField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

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
    public void testBrisanjeMjesta (FxRobot robot) {
        robot.clickOn("#adresaMjesto");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

        robot.clickOn("#okButton");
        assertTrue(theStage.isShowing());

        ComboBox ime = robot.lookup("#adresaMjesto").queryAs(ComboBox.class);
        Background bg = ime.getEditor().getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testJmbgValidacija (FxRobot robot) {
        robot.clickOn("#datumField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("1/8/2003");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);

        robot.clickOn("#jmbgField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("1234");

        robot.clickOn("#okButton");
        assertTrue(theStage.isShowing());

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
        robot.clickOn("#imeField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

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
    public void testPromjena (FxRobot robot) {
        robot.clickOn("#imeField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("abc");

        robot.clickOn("#adresaMjesto");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("Zenica");

        robot.clickOn("#postanskiBrojField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("75000");
        robot.clickOn("#datumField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("1/8/2003");
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.clickOn("#jmbgField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("0801003500007");

        // Sve validno, prozor se zatvara
        robot.clickOn("#okButton");
        assertFalse(theStage.isShowing());

        // Da li je novi vlasnik u bazi
        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        assertEquals(2, vlasnici.size());
        assertEquals(2, vlasnici.get(1).getId());
        assertEquals("abc", vlasnici.get(1).getIme());
        assertEquals("Testović", vlasnici.get(1).getPrezime());
        assertEquals(LocalDate.of(2003,1,8), vlasnici.get(1).getDatumRodjenja());
        assertEquals("0801003500007", vlasnici.get(1).getJmbg());
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