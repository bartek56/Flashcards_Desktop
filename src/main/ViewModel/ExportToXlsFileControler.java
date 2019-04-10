package ViewModel;

import Model.DataBase.CSVFile.CSVBackupSave;
import Model.DataBase.XLSFile.XLSSave;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

import static Model.DataBase.SQLite.FlashcardHelper.DeleteAllFlashcardsFromCategory;
import static Model.DataBase.SQLite.FlashcardHelper.GetCategories;
import static Model.DataBase.SQLite.FlashcardHelper.RenameCategory;

public class ExportToXlsFileControler {

    @FXML
    public ComboBox cbCategory;


    private MainWindowControler mainWindowControler;
    private String oldCategory;
    private String actualLanguage;

    public void LoadCategory()
    {
        List<String> categories = GetCategories();

        for(String category : categories)
        {
            if(!category.equals("inne"))
                cbCategory.getItems().add(category);
        }

        cbCategory.getSelectionModel().select(1);
    }

    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowControler = mainWindowControler;
    }

    public void bExportToXls_Click(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Flashcards to file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        Stage stage = (Stage) cbCategory.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if(file != null)
        {
            XLSSave xlsSave = new XLSSave();
            try {
                xlsSave.SaveToXLSFile(file, cbCategory.getSelectionModel().getSelectedItem().toString());
            } catch (Exception ex) {
                //Message("SaveOnFile_MainWindow: "+ex.toString());
            }
        }

        stage.close();

    }

}
