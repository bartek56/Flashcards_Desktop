package ViewModel;

import Model.DataBase.CSVFile.CSVBackupSave;
import Model.DataBase.XLSFile.XLSSave;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
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
    public CheckBox cbExportAll;


    private MainWindowControler mainWindowControler;
    private String oldCategory;
    private String actualLanguage;

    public void LoadCategory()
    {
        List<String> categories = GetCategories();

        for(String category : categories)
        {
            System.out.println(category);
            if(!category.equals("inne"))
                cbCategory.getItems().add(category);
        }

        cbCategory.getSelectionModel().select(1);
    }

    public void Load() {
        LoadCategory();
    }

    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowControler = mainWindowControler;
    }

    public void cbExportAll_onClick(ActionEvent actionEvent)
    {
        cbCategory.setDisable(cbExportAll.isSelected());
    }


    public void bExportToXls_Click(ActionEvent actionEvent) {
        Stage stage = (Stage) cbCategory.getScene().getWindow();
        XLSSave xlsSave = new XLSSave();
        if(cbExportAll.isSelected())
        {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);

            if(selectedDirectory != null){
                try {
                    xlsSave.SaveAllToXLSFile(selectedDirectory);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
        }
        else

            {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("export flashcards to XLSX file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    xlsSave.SaveToXLSFile(file, cbCategory.getSelectionModel().getSelectedItem().toString());
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
        }
        stage.close();
    }
}
