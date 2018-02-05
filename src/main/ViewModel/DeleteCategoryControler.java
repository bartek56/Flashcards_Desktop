package ViewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

import static Model.DataBase.SQLite.FlashcardHelper.DeleteAllFlashcardsFromCategory;
import static Model.DataBase.SQLite.FlashcardHelper.GetCategories;

/**
 * Created by Bartek on 2017-09-17.
 */
public class DeleteCategoryControler {

    @FXML
    public ComboBox cbFlashcardCategory;


    private MainWindowControler mainWindowsControler;

    public void LoadCategory()
    {
        List<String> categories = GetCategories();

        for(String category : categories)
        {
            if(!category.equals("inne"))
                cbFlashcardCategory.getItems().add(category);
        }

        cbFlashcardCategory.getSelectionModel().select(1);
    }

    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowsControler = mainWindowControler;
    }

    public void DeleteCategory_Click(ActionEvent actionEvent) {

        String category = cbFlashcardCategory.getSelectionModel().getSelectedItem().toString();
        DeleteAllFlashcardsFromCategory(category);
        mainWindowsControler.UpdateRow(null);
        Stage stage = (Stage) cbFlashcardCategory.getScene().getWindow();
        stage.close();

    }
}
