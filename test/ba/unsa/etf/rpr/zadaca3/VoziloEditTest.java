package ba.unsa.etf.rpr.zadaca3;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class VoziloEditTest {
    Stage theStage;
    VozilaDAO dao;
    VoziloController controller;

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

        // Dodajemo novo vozilo
        ObservableList<Vlasnik> vlasnici = dao.getVlasnici();
        Vlasnik testTestovic = dao.getVlasnici().get(0);
        Proizvodjac testProizvodjac = dao.getProizvodjaci().get(0);

        Vozilo testVozilo = new Vozilo(0, testProizvodjac, "Model", "123456", "M12-A-456", testTestovic);
        dao.dodajVozilo(testVozilo);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vozilo.fxml"));
        VoziloController voziloController = new VoziloController(dao, testVozilo);
        loader.setController(voziloController);
        Parent root = loader.load();
        stage.setTitle("Vozilo");
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
        robot.clickOn("#modelField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

        robot.clickOn("#okButton");
        // Forma nije validna i neće se zatvoriti!
        assertTrue(theStage.isShowing());

        TextField ime = robot.lookup("#modelField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testBrojTablicaValidacija (FxRobot robot) {
        robot.clickOn("#brojTablicaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        //robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("A1234456");

        robot.clickOn("#okButton");
        // Forma nije validna i neće se zatvoriti!
        assertTrue(theStage.isShowing());

        TextField ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#brojTablicaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        //robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("M12-A-456");

        ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

    }

    @Test
    public void testModelFieldValidacija (FxRobot robot) {
        // Ovim testom provjeravamo sva polja čiji je uslov validnosti da polje nije prazno
        robot.clickOn("#modelField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

        robot.clickOn("#brojSasijeField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("d");
        robot.clickOn("#brojTablicaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("M12-A-345");

        robot.clickOn("#okButton");
        // Forma nije validna i neće se zatvoriti!

        TextField ime = robot.lookup("#brojSasijeField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

        ime = robot.lookup("#brojSasijeField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

        ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

    }

    @Test
    public void testPromjena (FxRobot robot) {
        robot.clickOn("#modelField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("abc");

        robot.clickOn("#brojSasijeField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("e");

        robot.clickOn("#proizvodjacCombo");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("BMW");

        // Sve validno, prozor se zatvara
        robot.clickOn("#okButton");
        assertFalse(theStage.isShowing());

        // Da li je novo vozilo u bazi
        ObservableList<Vozilo> vozila = dao.getVozila();
        assertEquals(2, vozila.size());
        assertEquals(2, vozila.get(1).getId());
        assertEquals("abc", vozila.get(1).getModel());
        assertEquals("e", vozila.get(1).getBrojSasije());
        assertEquals(4, vozila.get(1).getProizvodjac().getId());
        assertEquals("BMW", vozila.get(1).getProizvodjac().getNaziv());

        //Provjeravamo da li je BMW zaista dodat u proizodjace
        ObservableList<Proizvodjac> proizvodjaci = dao.getProizvodjaci();
        assertEquals(4, proizvodjaci.size());
        assertEquals(4, proizvodjaci.get(0).getId());
        assertEquals("BMW", proizvodjaci.get(0).getNaziv());
    }
}
