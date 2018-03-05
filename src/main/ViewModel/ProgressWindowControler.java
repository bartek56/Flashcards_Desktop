package ViewModel;

import Model.DataBase.CSVFile.CSVBackupRead;
import Model.DataBase.SQLite.FlashcardHelper;
import Model.GoogleDriveHelper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;

public class ProgressWindowControler {


    @FXML
    private Label lInfo;

    @FXML
    private Button bOk;

    private CSVBackupRead csvBackupRead;
    private MainWindowControler mainWindowsControler;

    public void setFi(File file)
    {
        csvBackupRead.setFile(file);
    }

    public void initialize()
    {
        csvBackupRead = new CSVBackupRead();
        bOk.setDisable(true);

        lInfo.textProperty().bind(csvBackupRead.messageProperty());




        FlashcardHelper.SetAutoCommit(false);
        csvBackupRead.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,(WorkerStateEvent t)->{
            bOk.setDisable(false);
            lInfo.textProperty().unbind();
            FlashcardHelper.Commit();
            FlashcardHelper.SetAutoCommit(true);
            mainWindowsControler.getPrimarystage().getScene().setCursor(Cursor.DEFAULT);
            lInfo.setText("Wczytano " + csvBackupRead.getCount() + " słówka");
        });

        Thread thread = new Thread(csvBackupRead);
        //thread.setDaemon(true);
        thread.start();


    }


    public void OkButton_onClick(ActionEvent actionEvent) {
        mainWindowsControler.UpdateRow(null);
        Stage stage = (Stage) lInfo.getScene().getWindow();
        stage.close();
    }


    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowsControler = mainWindowControler;
        mainWindowsControler.getPrimarystage().getScene().setCursor(Cursor.WAIT);
    }

}
