package ViewModel;

import Model.GoogleDriveHelper;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * Created by Bartek on 2017-09-05.
 */
public class Start extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/MainWindow.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        primaryStage.setTitle("Fiszki");

        MainWindowControler controller = fxmlLoader.<MainWindowControler>getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Fiszki");
        primaryStage.setScene(new Scene(root1));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(controller.isConnected)
                {
                    Alert alert = new Alert(Alert.AlertType.NONE, "Save Changes ?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES)
                    {
                        controller.SaveOnDataBase();
                    }
                }
            }
        });
    }


    @Override
    public void stop(){

    }


    public static void main(String[] args) {
        launch(args);
    }
}