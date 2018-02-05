package Model.DataBase.CSVFile;

import Model.DataBase.SQLite.FlashcardHelper;
import Model.DataBase.SQLite.SQLiteJDBCDriverConnection;
import Model.DataBase.SQLite.Tables.Flashcard;
import com.opencsv.CSVWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class CSVBackupSave {

    public void SaveToCSVFile(File file) throws Exception {
        List<String> categoriesList = FlashcardHelper.GetCategories();

        CSVWriter csvWriter = null;


        csvWriter = new CSVWriter(new FileWriter(file.getAbsolutePath()));



        for(String category : categoriesList) {

                String category2 = category.replace(" ","_");
                Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT idFlashcard FROM " + category2 + " ");

                while (rs.next()) {

                    int idFlashcard = rs.getInt(1);
                    Flashcard flashcard = FlashcardHelper.GetFlashcard(idFlashcard);

                    String [] str = {flashcard.getEngWord(),flashcard.getPlWord(),flashcard.getEngSentence(),flashcard.getPlSentence(),category2};
                    csvWriter.writeNext(str);
                }
        }

        csvWriter.close();

    }


}
