package ba.unsa.etf.rpr.zadaca3;

import static org.junit.jupiter.api.Assertions.*;
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
import java.nio.file.Path;
import java.time.LocalDate;

@ExtendWith(ApplicationExtension.class)
class VoziloControllerTest {
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

        // Ovo bi trebalo da iskopira fajl iz resources u test-resources, a ipak radi i sa mavenom
        File fxml = new File("resources/fxml/vozilo.fxml");
        if (fxml.exists()) {
            File rsrc = new File("test-resources/fxml/vozilo.fxml");
            if (rsrc.exists()) rsrc.delete();
            Files.copy(fxml.toPath(), rsrc.toPath());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vozilo.fxml"));
        VoziloController voziloController = new VoziloController(dao, null);
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
    }

    @Test
    public void testOk (FxRobot robot) {
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
    public void testModelValidacija (FxRobot robot) {
        // Ovim testom provjeravamo sva polja čiji je uslov validnosti da polje nije prazno
        robot.clickOn("#modelField");
        robot.write("abc");
        robot.clickOn("#brojSasijeField");
        robot.write("d");

        robot.clickOn("#okButton");
        // Forma nije validna i neće se zatvoriti!

        TextField ime = robot.lookup("#modelField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills()) {
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        }
        assertTrue(colorFound);

        ime = robot.lookup("#brojSasijeField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);
    }



    @Test
    public void testTablicaValidacija (FxRobot robot) {
        robot.clickOn("#brojTablicaField");
        robot.write("1234");

        robot.clickOn("#okButton");

        TextField ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        Background bg = ime.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#brojTablicaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("A12345678");

        robot.clickOn("#okButton");

        ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#brojTablicaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("A12-B-678"); // slovo B ne može

        robot.clickOn("#okButton");

        ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#brojTablicaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("123-A-456");

        robot.clickOn("#okButton");

        ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#brojTablicaField");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.write("M23-K-456");

        robot.clickOn("#okButton");

        ime = robot.lookup("#brojTablicaField").queryAs(TextField.class);
        bg = ime.getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testProizvodjac (FxRobot robot) {
        ComboBox proizvodjacCombo = robot.lookup("#proizvodjacCombo").queryAs(ComboBox.class);
        Platform.runLater(() -> proizvodjacCombo.show());

        // Čekamo da se pojavi meni
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Renault");

        robot.clickOn("#okButton");

        ComboBox ime = robot.lookup("#proizvodjacCombo").queryAs(ComboBox.class);
        Background bg = ime.getEditor().getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        assertTrue(colorFound);

        robot.clickOn("#proizvodjacCombo");
        robot.press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

        robot.clickOn("#okButton");

        ime = robot.lookup("#proizvodjacCombo").queryAs(ComboBox.class);
        bg = ime.getEditor().getBackground();
        colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        assertTrue(colorFound);
    }

    @Test
    public void testDodavanje (FxRobot robot) {
        robot.clickOn("#proizvodjacCombo");
        robot.write("Skoda");
        robot.clickOn("#modelField");
        robot.write("Fabia");
        robot.clickOn("#brojSasijeField");
        robot.write("1234193459845");
        robot.clickOn("#brojTablicaField");
        robot.write("M23-K-456");

        ComboBox vlasnikCombo = robot.lookup("#vlasnikCombo").queryAs(ComboBox.class);
        Platform.runLater(() -> vlasnikCombo.show());

        // Čekamo da se pojavi meni
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Mehic Meho");

        robot.clickOn("#okButton");
        assertFalse(theStage.isShowing()); // Prozor se zatvorio

        // Da li je novo vozilo u bazi
        ObservableList<Vozilo> vozila = dao.getVozila();
        assertEquals(2, vozila.size());
        assertEquals(2, vozila.get(1).getId());
        assertEquals("Skoda", vozila.get(1).getProizvodjac().getNaziv());
        assertEquals("Fabia", vozila.get(1).getModel());
        assertEquals("1234193459845", vozila.get(1).getBrojSasije());
        assertEquals("M23-K-456", vozila.get(1).getBrojTablica());

        // Provjeravamo da li je Skoda zaista dodata u proizvodjace
        ObservableList<Proizvodjac> proizvodjacs = dao.getProizvodjaci();
        assertEquals(4, proizvodjacs.size());
        // Ovo će vratiti abecedno, tako da će škoda biti na indeksu 2 (poslije Renault a prije Volskwagen)
        assertEquals(4, proizvodjacs.get(2).getId());
        assertEquals("Skoda", proizvodjacs.get(2).getNaziv());
    }
}