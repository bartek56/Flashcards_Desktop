package Model.DataBase.CSVFile;

import Model.DataBase.SQLite.FlashcardHelper;
import Model.DataBase.SQLite.Tables.Flashcard;
import com.opencsv.CSVReader;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class CSVBackupRead extends Task<List<String[]>> {

    private File file;
    private int count;

    public void setFile(File file)
    {
        this.file = file;
    }


    @Override
    protected List<String[]> call() throws Exception {
        CSVReader csvReader;

        csvReader = new CSVReader(new FileReader(file.getAbsolutePath()));

        List<String[]> flashcardsFromFileList = csvReader.readAll();

        for(String[] nextLine:flashcardsFromFileList){

            Flashcard flashcard = new Flashcard(nextLine[0],nextLine[1],nextLine[2],nextLine[3]);
            String category = nextLine[4];
            List<Flashcard> flashcardList = FlashcardHelper.FlashcardExist(flashcard.getEngWord(), flashcard.getPlWord());

            if(flashcardList.isEmpty())
            {
                count ++;
                FlashcardHelper.AddCategory(category);
                int id = FlashcardHelper.AddFlashcard(flashcard);

                FlashcardHelper.AddFlashcardToCategory(category,id);

                this.copy(flashcard.getPlWord() + " " +flashcard.getEngWord());
            }
        }
        return null;
    }

    private void copy(String text) throws Exception {
        this.updateMessage("Słówko: " + text);
    }

    public int getCount()
    {
        return count;
    }

}
