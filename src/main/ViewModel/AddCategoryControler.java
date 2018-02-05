package ViewModel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import static Model.DataBase.SQLite.FlashcardHelper.AddCategory;

/**
 * Created by Bartek on 2017-09-17.
 */
public class AddCategoryControler {

    @FXML
    public TextField tCategory;

    private MainWindowControler mainWindowControler;

    private EventHandler enterClick = new EventHandler<KeyEvent>(){
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode().equals(KeyCode.ENTER))
            {
                AddCategory_Click(null);
            }
        }
    };


    public void initialize()
    {
        tCategory.setOnKeyPressed(enterClick);
    }

    public void AddCategory_Click(ActionEvent actionEvent) {
        AddCategory(tCategory.getText());
        mainWindowControler.UpdateRow(tCategory.getText());
        Stage stage = (Stage) tCategory.getScene().getWindow();
        stage.close();
    }

    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowControler = mainWindowControler;
    }
}
