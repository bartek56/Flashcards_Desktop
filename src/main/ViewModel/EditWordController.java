package ViewModel;


import Model.DataBase.SQLite.FlashcardHelper;
import Model.DataBase.SQLite.Tables.Flashcard;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static Model.DataBase.SQLite.FlashcardHelper.GetCategories;
import static Model.DataBase.SQLite.FlashcardHelper.UpdateFlashcard;
import static Model.DataBase.SQLite.FlashcardHelper.UpdateFlashcardAndCategory;

/**
 * Created by Bartek on 2017-09-17.
 */
public class EditWordController {


    @FXML
    public ComboBox cbFlashcardCategory;
    public TextArea engSentenceText;
    public TextArea plSentenceText;
    public TextField engWordText;
    public TextField plWordText;
    public Label lEngWord;
    public Label lEngSentence;



    private Model.FlashcardTableHelper flashcardTable;
    private MainWindowControler mainWindowsControler;

    private EventHandler enterClick = new EventHandler<KeyEvent>(){
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode().equals(KeyCode.ENTER))
            {
                try {
                    EditFlashcard_Click(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void initialize() {
        engSentenceText.setOnKeyPressed(enterClick);
        plSentenceText.setOnKeyPressed(enterClick);
        engWordText.setOnKeyPressed(enterClick);
        plWordText.setOnKeyPressed(enterClick);


    }

    public void setFlashcardTable(Model.FlashcardTableHelper flashcardTable) {
        this.flashcardTable = flashcardTable;
        plWordText.setText(flashcardTable.getPlWord());
        plSentenceText.setText(flashcardTable.getPlSentence());

        engWordText.setText(flashcardTable.getEngWord());
        engSentenceText.setText(flashcardTable.getEngSentence());

        List<String> categories = GetCategories();

        for(String category : categories)
            cbFlashcardCategory.getItems().add(category);

        cbFlashcardCategory.getSelectionModel().select(flashcardTable.getCategory());

    }

    private void SaveFlashcard()
    {
        String newCategory = cbFlashcardCategory.getSelectionModel().getSelectedItem().toString();
        String oldCategory = flashcardTable.getCategory();


        Flashcard flashcard = new Flashcard(engWordText.getText(), plWordText.getText(), engSentenceText.getText(), plSentenceText.getText());
        flashcard.setId(flashcardTable.getId());

        if (!newCategory.equals(oldCategory))
            UpdateFlashcardAndCategory(flashcard, oldCategory, newCategory);
        else
            UpdateFlashcard(flashcard);

        mainWindowsControler.ReadFromDataBase_Click();

    }

    public void EditFlashcard_Click(ActionEvent actionEvent) throws IOException {
            SaveFlashcard();
            Stage stage = (Stage) cbFlashcardCategory.getScene().getWindow();
            stage.close();
    }

    public void setMainWindowController(MainWindowControler mainWindowControler) {
        this.mainWindowsControler = mainWindowControler;
    }

    public void setActualLanguage(String actualLanguage)
    {
        switch (actualLanguage)
        {
            case "eng": lEngWord.setText("Słowo Eng:"); lEngSentence.setText("Zdanie Eng:"); break;
            case "fr": lEngWord.setText("Słowo Fr:"); lEngSentence.setText("Zdanie Fr:"); break;
            case "de": lEngWord.setText("Słowo De:"); lEngSentence.setText("Zdanie De:"); break;
        }

    }
}
