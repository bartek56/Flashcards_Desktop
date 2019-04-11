package Model.DataBase.XLSFile;

import Model.DataBase.SQLite.FlashcardHelper;
import Model.DataBase.SQLite.SQLiteJDBCDriverConnection;
import Model.DataBase.SQLite.Tables.Flashcard;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class XLSSave {

    public void SaveToXLSFile(File file, String category) throws Exception {

        String category2 = category.replace(" ","_");
        Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT idFlashcard FROM " + category2 + " ");

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet("sheet");

        int rowNum = 0;

            while (rs.next()) {

                int idFlashcard = rs.getInt(1);
                Flashcard flashcard = FlashcardHelper.GetFlashcard(idFlashcard);

                Row row = sheet.createRow(rowNum++);

                int colNum = 0;

                Cell cell = row.createCell(colNum++);
                cell.setCellValue((String) flashcard.getEngWord());

                Cell cell2 = row.createCell(colNum++);
                cell2.setCellValue((String) flashcard.getPlWord());

                Cell cell3 = row.createCell(colNum++);
                cell3.setCellValue((String) flashcard.getEngSentence());

                Cell cell4 = row.createCell(colNum++);
                cell4.setCellValue((String) flashcard.getPlSentence());

            }

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            xssfWorkbook.write(outputStream);
            xssfWorkbook.close();
            System.out.println("ok");
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public void SaveAllToXLSFile(File file) throws Exception {
        List<String> categoriesList = FlashcardHelper.GetCategories();

        for(String category : categoriesList) {

            String category2 = category.replace(" ", "_");
            Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idFlashcard FROM " + category2 + " ");

            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet sheet = xssfWorkbook.createSheet("sheet");

            int rowNum = 0;

            while (rs.next()) {

                int idFlashcard = rs.getInt(1);
                Flashcard flashcard = FlashcardHelper.GetFlashcard(idFlashcard);

                Row row = sheet.createRow(rowNum++);

                int colNum = 0;

                Cell cell = row.createCell(colNum++);
                cell.setCellValue((String) flashcard.getEngWord());

                Cell cell2 = row.createCell(colNum++);
                cell2.setCellValue((String) flashcard.getPlWord());

                Cell cell3 = row.createCell(colNum++);
                cell3.setCellValue((String) flashcard.getEngSentence());

                Cell cell4 = row.createCell(colNum++);
                cell4.setCellValue((String) flashcard.getPlSentence());

            }

            try {
                FileOutputStream outputStream = new FileOutputStream(file+"/"+category2+".xlsx");
                xssfWorkbook.write(outputStream);
                xssfWorkbook.close();
                System.out.println("ok");
            } catch (FileNotFoundException e) {
                System.err.println(e);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

}

