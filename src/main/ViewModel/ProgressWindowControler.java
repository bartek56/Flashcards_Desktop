package ViewModel;

import Model.DataBase.CSVFile.CSVBackupRead;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;

public class ProgressWindowControler {

    @FXML
    private ProgressBar progressBar;

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

        progressBar.setProgress(0);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(csvBackupRead.progressProperty());
        lInfo.textProperty().bind(csvBackupRead.messageProperty());

        csvBackupRead.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                new EventHandler<WorkerStateEvent>() {

                    @Override
                    public void handle(WorkerStateEvent t) {
                        bOk.setDisable(false);
                        lInfo.textProperty().unbind();
                        lInfo.setText("Wczytano " + csvBackupRead.getCount() + " pliki");
                    }
                });

        new Thread(csvBackupRead).start();
    }


    public void OkButton_onClick(ActionEvent actionEvent) {
        mainWindowsControler.UpdateRow(null);
        Stage stage = (Stage) lInfo.getScene().getWindow();
        stage.close();
    }


    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowsControler = mainWindowControler;
    }

}
