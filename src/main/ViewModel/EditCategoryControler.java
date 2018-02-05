package ViewModel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import static Model.DataBase.SQLite.FlashcardHelper.RenameCategory;


public class EditCategoryControler {

    @FXML
    public TextField tCategory;

    private MainWindowControler mainWindowControler;
    private String oldCategory;
    private String actualLanguage;


    private EventHandler enterClick = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.ENTER)) {
                EditCategory_Click(null);
            }
        }
    };


    public void initialize() {
        tCategory.setOnKeyPressed(enterClick);

    }


    public void EditCategory_Click(ActionEvent actionEvent) {
        RenameCategory(oldCategory,tCategory.getText());
        mainWindowControler.UpdateRow(tCategory.getText());
        Stage stage = (Stage) tCategory.getScene().getWindow();
        stage.close();
    }


    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowControler = mainWindowControler;
    }

    public void SetActualCategory(String actualCategory)
    {
        oldCategory = actualCategory;
        tCategory.setText(actualCategory);
    }





}
