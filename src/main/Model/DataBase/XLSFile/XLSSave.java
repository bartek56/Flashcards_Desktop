package Model.DataBase.XLSFile;

import Model.DataBase.SQLite.FlashcardHelper;
import Model.DataBase.SQLite.SQLiteJDBCDriverConnection;
import Model.DataBase.SQLite.Tables.Flashcard;

import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class XLSSave {

    public void SaveToXLSFile(File file, String category) throws Exception {
        List<String> categoriesList = FlashcardHelper.GetCategories();

        CSVWriter csvWriter = null;

        csvWriter = new CSVWriter(new FileWriter(file.getAbsolutePath()));
        //csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file),StandardCharsets.UTF_8));


            String category2 = category.replace(" ","_");
            Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idFlashcard FROM " + category2 + " ");

            while (rs.next()) {

                int idFlashcard = rs.getInt(1);
                Flashcard flashcard = FlashcardHelper.GetFlashcard(idFlashcard);

                String [] str = {flashcard.getEngWord(),flashcard.getPlWord()," ",flashcard.getEngSentence(),flashcard.getPlSentence()};
                csvWriter.writeNext(str);
            }

        csvWriter.close();

    }
}
