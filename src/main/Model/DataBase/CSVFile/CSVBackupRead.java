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


    public void start()
    {

        try {
            CSVReader csvReader;
            csvReader = new CSVReader(new FileReader(file.getAbsolutePath()));
            //csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));

            int wordNumber = 0;
            List<String[]> flashcardsFromFileList = csvReader.readAll();
            count = flashcardsFromFileList.size();


            //while ((nextLine = csvReader.readNext()) != null) {
            for (String[] nextLine : flashcardsFromFileList) {

                //System.out.println(nextLine[0]+" "+nextLine[1]+" "+nextLine[2]+" "+nextLine[3]);
                Flashcard flashcard = new Flashcard(nextLine[0], nextLine[1], nextLine[2], nextLine[3]);
                String category = nextLine[4];
                List<Flashcard> flashcardList = FlashcardHelper.FlashcardExist(flashcard.getEngWord(), flashcard.getPlWord());

                if (flashcardList.isEmpty()) {
                    FlashcardHelper.AddCategory(category);
                    int id = FlashcardHelper.AddFlashcard(flashcard);

                    FlashcardHelper.AddFlashcardToCategory(category, id);

                    this.copy(flashcard.getPlWord() + " " + flashcard.getEngWord());
                    this.updateProgress(wordNumber, flashcardsFromFileList.size());

                }
                wordNumber++;

            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }


    @Override
    protected List<String[]> call() throws Exception {
        CSVReader csvReader;

        csvReader = new CSVReader(new FileReader(file.getAbsolutePath()));

        int wordNumber=0;
        List<String[]> flashcardsFromFileList = csvReader.readAll();
        count = flashcardsFromFileList.size();


        //while ((nextLine = csvReader.readNext()) != null) {
        for(String[] nextLine:flashcardsFromFileList){

            //System.out.println(nextLine[0]+" "+nextLine[1]+" "+nextLine[2]+" "+nextLine[3]);
            Flashcard flashcard = new Flashcard(nextLine[0],nextLine[1],nextLine[2],nextLine[3]);
            String category = nextLine[4];
            List<Flashcard> flashcardList = FlashcardHelper.FlashcardExist(flashcard.getEngWord(), flashcard.getPlWord());

            if(flashcardList.isEmpty())
            {
                FlashcardHelper.AddCategory(category);
                int id = FlashcardHelper.AddFlashcard(flashcard);

                FlashcardHelper.AddFlashcardToCategory(category,id);

                this.copy(flashcard.getPlWord() + " " +flashcard.getEngWord());
                this.updateProgress(wordNumber,flashcardsFromFileList.size());

            }
            wordNumber++;

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
