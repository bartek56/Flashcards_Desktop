package ViewModel;

import Model.DataBase.CSVFile.CSVBackupSave;
import Model.DataBase.GoogleDrive.GoogleDriveConnect;
import Model.DataBase.GoogleDrive.GoogleDriveSave;
import Model.DataBase.SQLite.FlashcardHelper;
import Model.DataBase.SQLite.Tables.Flashcard;
import Model.FlashcardTableHelper;
import Model.GoogleDriveHelper;
import com.google.api.services.drive.Drive;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Model.DataBase.SQLite.FlashcardHelper.*;
import static Model.DataBase.SQLite.SQLiteJDBCDriverConnection.DATABASE_FILE_LOCATION;
import static Model.DataBase.SQLite.SQLiteJDBCDriverConnection.DATABASE_FILE_NAME;
import static Model.DataBase.SQLite.SQLiteJDBCDriverConnection.connect;


/**
 * Created by Bartek on 2017-09-05.
 */
public class MainWindowControler {

    private Drive service;
    private String fileId=null;
    public boolean isConnected=false;

    @FXML
    public TableView<FlashcardTableHelper> flashcardsTable;
    public TableColumn<FlashcardTableHelper,String> engWordColumn;
    public TableColumn<FlashcardTableHelper,String> plWordColumn;
    public TableColumn<FlashcardTableHelper,String> engSentenceColumn;
    public TableColumn<FlashcardTableHelper,String> plSentenceColumn;
    public TableColumn<FlashcardTableHelper,String> idColumn;
    public ComboBox cbFlashcardCategory;
    public TextArea engSentenceText;
    public TextArea plSentenceText;
    public TextField engWordText;
    public TextField plWordText;
    public RadioMenuItem eng;
    public RadioMenuItem fr;
    public RadioMenuItem de;
    public MenuItem bSaveFlashcardOnGoogle;
    public MenuItem bAddCategory;
    public MenuItem bDeleteCategory;
    public Button bAddFlashcard;
    public Menu menuCategory;
    public Label lEnglishWord;
    public Label lEnglishSentence;
    public TextField tfSearch;
    public GridPane SearchBarGrid;
    public GridPane mainGrid;
    public RowConstraints row;


    private String actualLanguage;


    private Stage primaryStage;

    private final ObservableList<FlashcardTableHelper> data =
            FXCollections.observableArrayList(
            );

    private ObservableList<FlashcardTableHelper> filteredData = FXCollections.observableArrayList();


    private EventHandler enterClick = new EventHandler<KeyEvent>(){
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode().equals(KeyCode.ENTER))
            {
               AddFlashcard_Click(null);
            }
        }
    };

    public void initialize()
    {
        DisableButtons(true);
        engSentenceText.setOnKeyPressed(enterClick);
        plSentenceText.setOnKeyPressed(enterClick);
        engWordText.setOnKeyPressed(enterClick);
        plWordText.setOnKeyPressed(enterClick);
        mainGrid.getChildren().remove(SearchBarGrid);
        tfSearch.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateFilteredData();
            }
        });
    }

    private void updateFilteredData()
    {
        filteredData.clear();

        for (FlashcardTableHelper p : data) {
            if (matchesFilter(p)) {
                filteredData.add(p);
            }
        }
        // Must re-sort table after items changed
        reapplyTableSortOrder();
    }

    private void reapplyTableSortOrder()
    {
        ArrayList<TableColumn<FlashcardTableHelper, ?>> sortOrder = new ArrayList<>(flashcardsTable.getSortOrder());
        flashcardsTable.getSortOrder().clear();
        flashcardsTable.getSortOrder().addAll(sortOrder);
    }

    private boolean matchesFilter(FlashcardTableHelper person) {
        String filterString = tfSearch.getText();

        if (filterString == null || filterString.isEmpty()) {
            return true;
        }

        String lowerCaseFilterString = filterString.toLowerCase();

        if (person.getPlWord().toLowerCase().contains(lowerCaseFilterString)) {
            return true;
        } else if (person.getEngWord().toLowerCase().contains(lowerCaseFilterString)) {
            return true;
        }

        return false; // Does not match
    }

    public void RefreshTable()
    {
        flashcardsTable.refresh();
    }

    public void UpdateRow(String actualCategory){
        filteredData.clear();
        data.clear();
        //data.removeAll(data);
        File file = new File(DATABASE_FILE_LOCATION+""+DATABASE_FILE_NAME);
        if(file.exists())
        {
            List<FlashcardTableHelper> list =  GetAllFlashcardsToTableHelper();
            for (FlashcardTableHelper flashcardTableHelper : list) {
                filteredData.add(flashcardTableHelper);
                data.add(flashcardTableHelper);
            }

            flashcardsTable.setItems(filteredData);
            List<String> categories = GetCategories();


            cbFlashcardCategory.getItems().clear();
            for (String category : categories)
                cbFlashcardCategory.getItems().add(category);

            if(actualCategory==null)
                cbFlashcardCategory.getSelectionModel().select("inne");
            else
                cbFlashcardCategory.getSelectionModel().select(actualCategory);

            idColumn.setSortType(TableColumn.SortType.ASCENDING);
            idColumn.setSortable(true);
            flashcardsTable.getSortOrder().add(idColumn);
            flashcardsTable.sort();

        }
    }

    public void ReadFromDataBase_Click()
    {
        UpdateRow(null);
    }

    private void AddFlashcardMain()
    {
        Flashcard flashcard = new Flashcard(engWordText.getText(),plWordText.getText(),engSentenceText.getText(),plSentenceText.getText());
        int id = AddFlashcard(flashcard);

        AddFlashcardToCategory(cbFlashcardCategory.getSelectionModel().getSelectedItem().toString(),id);

        FlashcardTableHelper flashcardTableHelper = new FlashcardTableHelper();
        flashcardTableHelper.setCategory(cbFlashcardCategory.getSelectionModel().getSelectedItem().toString());
        flashcardTableHelper.setEngSentence(flashcard.getEngSentence());
        flashcardTableHelper.setPlSentence(flashcard.getPlSentence());
        flashcardTableHelper.setId(id);
        flashcardTableHelper.setEngWord(flashcard.getEngWord());
        flashcardTableHelper.setPlWord(flashcard.getPlWord());

        data.add(flashcardTableHelper);
        filteredData.add(flashcardTableHelper);
        ClearFields();
    }

    public void AddFlashcard_Click(ActionEvent actionEvent){

        if(engWordText.getText().isEmpty() || plWordText.getText().isEmpty())
        {
            Message("Nie zostały wypełnione pola");
        }
        else {
            List<Flashcard> flashcards = FlashcardHelper.FlashcardExist(engWordText.getText(), plWordText.getText());
            if (flashcards.isEmpty()) {
                AddFlashcardMain();
            } else {

                String text = "";
                for (Flashcard f : flashcards) {
                    String id = Integer.toString(f.getId());
                    String category = GetFlashCardCategory(id);
                    text += id + " " + f.getPlWord() + " " + f.getEngWord() + ", kategoria: " + category + "\n" ;
                }

                Alert alert = new Alert(Alert.AlertType.NONE, "Słówko " + engWordText.getText() + ", " + plWordText.getText() + " istnieje jako:\n" + text +
                        ", czy chcesz dodać nowe słowo?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    AddFlashcardMain();
                }

            }
        }
    }

    private void ClearFields(){
        plWordText.clear();
        engWordText.clear();
        plSentenceText.clear();
        engSentenceText.clear();
    }

    public void SaveOnDataBase() {

        GoogleDriveSave googleDriveSave = new GoogleDriveSave(service,fileId);
        Thread thread = new Thread(googleDriveSave);
        thread.start();

    }

    public void SaveOnDataBase_Click(ActionEvent actionEvent) {

        GoogleDriveSave googleDriveSave = new GoogleDriveSave(service,fileId);

        googleDriveSave.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,(WorkerStateEvent t)->{

            primaryStage.getScene().setCursor(Cursor.DEFAULT);
            if(googleDriveSave.isSaved())
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Zapisano na Google Drive", ButtonType.OK);
                alert.showAndWait();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Nie można zapisać na Google Drive", ButtonType.OK);
                alert.showAndWait();
            }

        });
        primaryStage.getScene().setCursor(Cursor.WAIT);
        Thread thread = new Thread(googleDriveSave);
        thread.start();

    }

    public void AddCategory_Click(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/AddCategory.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();

        AddCategoryControler controller = fxmlLoader.<AddCategoryControler>getController();
        controller.setMainWindowController(this);


        Stage stage = new Stage();

        stage.setResizable(false);
        stage.setTitle("Add Category");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void DeleteFlaschcard_Click(ActionEvent actionEvent) {


        FlashcardTableHelper flashcardTableHelper = flashcardsTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE, "Delete " + flashcardTableHelper.getPlWord() + ", "+flashcardTableHelper.getEngWord() +" ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            DeleteFlashcardFromCategory(flashcardTableHelper.getCategory(),flashcardTableHelper.getId());
            DeleteFlashcard(flashcardTableHelper.getId());
            UpdateRow(null);
        }



    }

    public void EditFlaschcard_Click(ActionEvent actionEvent) throws IOException {

        FlashcardTableHelper flashcardTableHelper = flashcardsTable.getSelectionModel().getSelectedItem();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/EditWord.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();

        EditWordController controller = fxmlLoader.<EditWordController>getController();
        controller.setMainWindowController(this);
        controller.setFlashcardTable(flashcardTableHelper);
        controller.setActualLanguage(actualLanguage);

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Edit Word");
        stage.setScene(new Scene(root1));
        stage.show();

    }

    public void DeleteCategory_Click(ActionEvent actionEvent) throws IOException {
        FlashcardTableHelper flashcardTableHelper = flashcardsTable.getSelectionModel().getSelectedItem();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/DeleteCategory.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();

        DeleteCategoryControler controller = fxmlLoader.<DeleteCategoryControler>getController();
        controller.setMainWindowController(this);
        controller.LoadCategory();


        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Delete Category");
        stage.setScene(new Scene(root1));
        stage.setResizable(false);
        stage.show();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void Exit_Click(ActionEvent actionEvent) {

        //if(GoogleDriveHelper.isOpen)
        //{
            Alert alert = new Alert(Alert.AlertType.NONE, "Save Changes ?", ButtonType.YES, ButtonType.NO,ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES)
            {
                GoogleDriveHelper.WriteToFile();
                Stage stage = (Stage) cbFlashcardCategory.getScene().getWindow();
                // do what you have to do
                stage.close();
                System.exit(0);
            }
            else if (alert.getResult() == ButtonType.NO) {
                Stage stage = (Stage) cbFlashcardCategory.getScene().getWindow();
                // do what you have to do
                stage.close();
                System.exit(0);
            }

            /*
        }
        else
        {
            Stage stage = (Stage) cbFlashcardCategory.getScene().getWindow();
            // do what you have to do
            stage.close();
            System.exit(0);
        }
*/

    }

    public void Language_Click(ActionEvent actionEvent) {


        if(eng.isSelected())
        {
            System.out.println("eng");
            actualLanguage="eng";
            DATABASE_FILE_NAME="flashcardsEng.db";
            primaryStage.setTitle("FlashCard English");

            lEnglishWord.setText("Słowo Eng:");
            lEnglishSentence.setText("Zdanie Eng:");
            engWordColumn.setText("Słowo Eng");
            engSentenceColumn.setText("Zdanie Eng");

        }
        else if(de.isSelected())
        {
            System.out.println("de");
            actualLanguage="de";
            DATABASE_FILE_NAME="flashcardsDe.db";
            primaryStage.setTitle("FlashCard German");

            lEnglishWord.setText("Słowo De:");
            lEnglishSentence.setText("Zdanie De:");
            engWordColumn.setText("Słowo De");
            engSentenceColumn.setText("Zdanie De");
        }
        else if(fr.isSelected())
        {
            System.out.println("fr");
            actualLanguage="fr";
            DATABASE_FILE_NAME="flashcardsFr.db";
            primaryStage.setTitle("FlashCard French");

            lEnglishWord.setText("Słowo Fr:");
            lEnglishSentence.setText("Zdanie Fr:");
            engWordColumn.setText("Słowo Fr");
            engSentenceColumn.setText("Zdanie Fr");
        }


        primaryStage.getScene().setCursor(Cursor.WAIT);
        connect(); // connect with Sqlite
        FlashcardHelper.CreateDefaultTables();
        FlashcardHelper.SetAutoCommit(false);


        GoogleDriveConnect googleDriveConnect = new GoogleDriveConnect();

        googleDriveConnect.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,(WorkerStateEvent t)->{
            primaryStage.getScene().setCursor(Cursor.DEFAULT);

            if(googleDriveConnect.isConnect())
            {
                fileId = googleDriveConnect.getFileId();
                service = googleDriveConnect.getService();
                isConnected=true;
                DisableButtons(false);
                FlashcardHelper.Commit();
                ReadFromDataBase_Click();
            }
            else
            {
                Message("Błąd odczytu danych z Google Drive");
            }
            FlashcardHelper.SetAutoCommit(true);
        });


        Thread thread = new Thread(googleDriveConnect);
        thread.start();

    }

    public void Refresh_Click()
    {
        /*
        if(GoogleDriveHelper.error)
        {
            Message(GoogleDriveHelper.getErrorMessage());
            DisableButtons(true);
            GoogleDriveHelper.removeError();
        }
        else
        {
            DisableButtons(false);
            ReadFromDataBase_Click();
        }
        */
        //ReadFromDataBase_Click();
    }

    public void DisableButtons(boolean isDisable){
        if(isDisable==false)

        bSaveFlashcardOnGoogle.setDisable(isDisable);
        bAddFlashcard.setDisable(isDisable);
        menuCategory.setDisable(isDisable);
    }

    public void EditCategory_Click(ActionEvent actionEvent) throws IOException {

        if(cbFlashcardCategory.getSelectionModel().getSelectedItem().toString().equals("inne"))
        {
            Message("Nie można edytować kategori inne");
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/EditCategory.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            EditCategoryControler controller = fxmlLoader.<EditCategoryControler>getController();
            controller.setMainWindowController(this);
            controller.SetActualCategory(cbFlashcardCategory.getSelectionModel().getSelectedItem().toString());

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Category");
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.show();
        }
    }

    public void SaveOnFile_Click(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Flashcards to file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = fileChooser.showSaveDialog(primaryStage);

        if(file != null)
        {
            CSVBackupSave csvBackup = new CSVBackupSave();
            try {
                csvBackup.SaveToCSVFile(file);
            } catch (Exception ex) {
                Message("SaveOnFile_MainWindow: "+ex.toString());
            }
        }
    }

    public void ReadFromFile_Click(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Read Flashcards from file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

        File file = fileChooser.showOpenDialog(primaryStage);

        if(file != null)
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/ProgressWindow.fxml"));
            Parent root1 = null;
            try {
                root1 = (Parent) fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();

            }

            ProgressWindowControler controller = fxmlLoader.<ProgressWindowControler>getController();
            controller.setFi(file);
            controller.setMainWindowController(this);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Wczytywanie");
            stage.setScene(new Scene(root1));
            stage.show();

        }


    }

    private void Message(String ex)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, ex, ButtonType.OK);
        alert.showAndWait();
    }

    public void SearchField_EditText(ActionEvent actionEvent) {

        //System.out.println("fdf");

    }

    public void SearchWord_Click(ActionEvent actionEvent) {
        if(mainGrid.getChildren().contains(SearchBarGrid)) {
            mainGrid.getChildren().remove(SearchBarGrid);
            tfSearch.setText("");
            updateFilteredData();
        }
        else
        {
            mainGrid.getChildren().add(SearchBarGrid);
        }
    }

    /*
     if(mainGrid.getChildren().contains(SearchBarGrid)) {
        mainGrid.getChildren().remove(SearchBarGrid);
        tfSearch.setText("");
        updateFilteredData();
    }
        else
    {
        mainGrid.getChildren().add(SearchBarGrid);
    }
    */

    public String getActualLanguage()
    {
        return actualLanguage;
    }

    public Stage getPrimarystage()
    {
        return primaryStage;
    }

}
