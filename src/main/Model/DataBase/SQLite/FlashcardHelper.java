package Model.DataBase.SQLite;

import Model.DataBase.SQLite.Tables.Flashcard;
import Model.FlashcardTableHelper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartek on 2017-09-14.
 */
public class FlashcardHelper {

    public static boolean error=false;
    private static String errorMessage="";

    public static void RenameCategory(String oldName, String newName)
    {

        try {
            String oldNameTable = oldName.replace(" ","_");
            String newNameTable = newName.replace(" ","_");
            String sql = "ALTER TABLE "+oldNameTable+" RENAME TO "+newNameTable+"";
            PreparedStatement pstmt = SQLiteJDBCDriverConnection.connection.prepareStatement(sql);
            pstmt.executeUpdate();

        }
        catch (Exception ex)
        {
            System.err.println("RenameCategory: "+ex);

            Message("RenameCategory: "+ex+ " "+ex.getMessage());
        }



    }

    public static String GetFlashCardCategorie(Flashcard flashcard)
    {

        List <String> categoriesList = GetCategories();


        for (String category : categoriesList) {

            try {

                Statement stmt = Model.DataBase.SQLite.SQLiteJDBCDriverConnection.connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT idFlashcard FROM "+ category +" ");

                while (rs.next()) {

                    return category;

                }

            } catch (Exception ex) {
                System.err.println("GetFlashcardFromCategory: " + ex);

                Message("GetFlashcardFromCategory: " + ex + " " + ex.getMessage());
            }
        }
        return null;
    }


    public static List <String> GetCategories()
    {
        List <String> category = new ArrayList<>();
        try {

            Statement stmt = Model.DataBase.SQLite.SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");

            while ( rs.next() ) {

                String column = rs.getString(1);
                if(!column.equals("android_metadata") && !column.equals("sqlite_sequence") && !column.equals("flashcard"))
                {
                    String column2 = column.replace("_"," ");
                    category.add(column2);
                }
            }

        }
        catch (Exception ex)
        {
            System.err.println("GetFlashcardFromCategory: "+ex);

            Message("GetFlashcardFromCategory: "+ex+ " "+ex.getMessage());
        }
        return category;
    }

    public static List<String> GetAllFlashcardsAsStringList()
    {
        List<String> categoriesList = GetCategories();
        List<String> flashcardList = new ArrayList<>();

        for(String category : categoriesList) {
            try {
                String category2 = category.replace(" ","_");
                Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT idFlashcard, known FROM " + category2 + " ");

                while (rs.next()) {

                    int idFlashcard = rs.getInt(1);
                    Flashcard flashcard = GetFlashcard(idFlashcard);

                    String flashcardString = category2+";"+
                            flashcard.getEngWord()+";"+
                            flashcard.getPlWord()+";"+
                            flashcard.getEngSentence()+";"+
                            flashcard.getPlSentence()+";"+
                            rs.getInt(2)+";\n";


                    flashcardList.add(flashcardString);

                }

                rs.close();

            } catch (Exception ex) {
                System.err.println("GetAllFlashcard: " + ex + " "+ex.getMessage());
                Message("GetAllFlashcard: "+ex);
            }

        }
        return flashcardList;
    }


    public static List<FlashcardTableHelper> GetAllFlashcardsToTableHelper()
    {
        List<String> categoriesList = GetCategories();
        List<FlashcardTableHelper> flashcardTableHelpersList = new ArrayList<>();

        for(String category : categoriesList) {
            try {
                String category2 = category.replace(" ","_");
                Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT idFlashcard FROM " + category2 + " ");

                while (rs.next()) {

                    int idFlashcard = rs.getInt(1);
                    Flashcard flashcard = GetFlashcard(idFlashcard);

                    FlashcardTableHelper flashcardTableHelper = new FlashcardTableHelper();
                    flashcardTableHelper.setId(flashcard.getId());
                    flashcardTableHelper.setCategory(category);
                    flashcardTableHelper.setPlWord(flashcard.getPlWord());
                    flashcardTableHelper.setEngWord(flashcard.getEngWord());
                    flashcardTableHelper.setPlSentence(flashcard.getPlSentence());
                    flashcardTableHelper.setEngSentence(flashcard.getEngSentence());
                    flashcardTableHelpersList.add(flashcardTableHelper);

                }

                rs.close();

            } catch (Exception ex) {
                System.err.println("GetAllFlashcard: " + ex + " "+ex.getMessage());
                Message("GetAllFlashcard: "+ex);
            }

        }
        return flashcardTableHelpersList;
    }

    public static Flashcard GetFlashcard(int id)
    {
        Flashcard flashcard=null;
        try {

            Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM flashcard WHERE id= " + id + " ");

            while (rs.next()) {

                int flashcardId = rs.getInt("id");
                String engWord = rs.getString("engWord");
                String plWord = rs.getString("plWord");
                String engSentence = rs.getString("engSentence");
                String plSentence = rs.getString("plSentence");


                flashcard = new Flashcard(engWord,plWord,engSentence,plSentence);
                flashcard.setId(flashcardId);


            }
        } catch (Exception ex) {
            Message("GetFlashcard: "+ex);

        }

        return flashcard;
    }

    public static int AddFlashcardIfNotExist(Flashcard flashcard)
    {
        int id=0;
        if(GetFlashcardId(flashcard.getEngWord())==0)
        {
            id = AddFlashcard(flashcard);
        }
        return id;
    }

    public static int GetFlashcardId(String engWord)
    {
        int id = 0;
        try {
            Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM flashcard WHERE engWord=\""+engWord+"\" ");

            while ( rs.next() ) {
                id = rs.getInt(1);
            }
        }
        catch (Exception ex)
        {
            System.err.println("GetFlashcardId: "+ex);
            Message("GetAllFlashcardId: " + ex);

        }
        return id;
    }

    public static int AddFlashcard(Flashcard flashcard)
    {
        int id=0;
        try
        {
            String sql = "INSERT INTO flashcard(engWord, plWord, engSentence, plSentence) VALUES(?,?,?,?)";
            PreparedStatement pstmt = SQLiteJDBCDriverConnection.connection.prepareStatement(sql);
            pstmt.setString(1, flashcard.getEngWord());
            pstmt.setString(2, flashcard.getPlWord());
            pstmt.setString(3, flashcard.getEngSentence());
            pstmt.setString(4, flashcard.getPlSentence());
            pstmt.executeUpdate();
            pstmt.close();


            Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid() ");

            while ( rs.next() ) {
                id = rs.getInt(1);
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println("AddFlashcard: "+ ex +" "+ ex.getMessage());
            Message("AddFlashcard: "+ ex );
        }

        return id;

    }

    public static void AddFlashcardToCategory(String category, int id)
    {
        try
        {
            String category2 = category.replace(" ","_");
            String sql = "INSERT INTO "+category2+" (idFlashcard,known) VALUES(?,?)";
            PreparedStatement pstmt = SQLiteJDBCDriverConnection.connection.prepareStatement(sql);

            pstmt.setInt(1, id);
            pstmt.setBoolean(2, false);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("AddFlashcardToCategory: "+ ex +" "+ ex.getMessage());
            Message("AddFlashcardToCategory: "+ ex );
        }
    }

    public static void DeleteFlashcardFromCategory(String category, int idFlashcard)
    {
        try
        {
            String category2 = category.replace(" ","_");
            String sql = "DELETE FROM "+category2+" WHERE idFlashcard = ?";
            PreparedStatement pstmt = SQLiteJDBCDriverConnection.connection.prepareStatement(sql);
            //pstmt.setString(1, category);
            pstmt.setInt(1, idFlashcard);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("DeleteFlashcardFromCategory: "+ ex +" "+ ex.getMessage());
            Message("DeleteFlashcardFromCategory: "+ ex );
        }
    }

    public static void DeleteFlashcard(int id)
    {
        try
        {
            String sql = "DELETE FROM flashcard WHERE id = ?";
            PreparedStatement pstmt = SQLiteJDBCDriverConnection.connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("DeleteFlashcard: "+ ex +" "+ ex.getMessage());
            Message("DeleteFlashcard: "+ ex );
        }
    }

    public static void UpdateFlashcard(Flashcard flashcard)
    {
        try
        {
            String sql = "UPDATE flashcard SET engWord = ?, plWord = ?, engSentence = ?, plSentence = ? WHERE id=?";
            PreparedStatement pstmt = SQLiteJDBCDriverConnection.connection.prepareStatement(sql);
            pstmt.setString(1, flashcard.getEngWord());
            pstmt.setString(2, flashcard.getPlWord());
            pstmt.setString(3, flashcard.getEngSentence());
            pstmt.setString(4, flashcard.getPlSentence());
            pstmt.setInt(5, flashcard.getId());
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("UpdateFlashcard " + ex + " " + ex.getMessage());
            Message("UpdateFlashcard: "+ ex );
        }
    }

    public static void UpdateFlashcardAndCategory(Flashcard flashcard, String oldCategory, String newCategory)
    {
        DeleteFlashcardFromCategory(oldCategory,flashcard.getId());
        AddFlashcardToCategory(newCategory,flashcard.getId());
        UpdateFlashcard(flashcard);
    }

    public static void AddCategory(String category)
    {
        try
        {
            String category2 = category.replace(" ","_");
            Statement statement = SQLiteJDBCDriverConnection.connection.createStatement();
            String sql2 = "create table if not exists "+category2+"(" +
                    "id integer primary key autoincrement," +
                    "idFlashcard integer,"+
                    "known boolean);"+
                    "";
            statement.executeUpdate(sql2);
        }
        catch (Exception ex)
        {
            Message("AddCategory: "+ex);
            System.err.println("AddCategory: "+ex);
        }

    }

    public static void CreateDefoulltsTables()
    {
        try
        {
            Statement statement = SQLiteJDBCDriverConnection.connection.createStatement();
            String sql2 = "create table if not exists inne(" +
                    "id integer primary key autoincrement," +
                    "idFlashcard integer,"+
                    "known boolean);"+
                    "";
            statement.executeUpdate(sql2);


            String sql = "create table if not exists flashcard(" +
                    "id integer primary key autoincrement," +
                    "engWord text," +
                    "plWord text," +
                    "engSentence text," +
                    "plSentence text);" +
                    "";
            statement.executeUpdate(sql);



        }
        catch (Exception ex)
        {
            Message("AddCategory: "+ex);
            System.err.println("AddCategory: "+ex);
        }

    }

    private static List<Integer> GetAllFlashcardByCategory(String category)
    {
        List<Integer> flashcardByCategory = new ArrayList<>();

        try {
            Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idFlashcard FROM "+category+" ");

            while ( rs.next() ) {
                int id = rs.getInt(1);
                flashcardByCategory.add(id);
            }
        }
        catch (Exception ex){
            System.err.println("GetAllFlashcardByCategory: "+ex);
            Message("GetAllFlashcardByCategory: "+ ex );
        }

        return flashcardByCategory;

    }

    private static void DeleteCategory(String category)
    {
        try
        {
            String category2 = category.replace(" ","_");
            String sql = "DROP TABLE IF EXISTS "+category2+" ";
            PreparedStatement pstmt = SQLiteJDBCDriverConnection.connection.prepareStatement(sql);

            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("DeleteCategory: "+ ex +" "+ ex.getMessage());
            Message("DeleteCategory: "+ ex );
        }
    }

    public static void DeleteAllFlashcardsFromCategory(String category)
    {
        List<Integer> flashcardByCategory = GetAllFlashcardByCategory(category);

        for(int id:flashcardByCategory)
        {
            DeleteFlashcard(id);
        }

        DeleteCategory(category);
    }

    private static void Message(String ex)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, ex, ButtonType.OK);
        alert.showAndWait();
    }

    public static List<Flashcard> FlashcardExist(String NewEngWord, String newPlWord)
    {

        List<Flashcard> flashcardList = new ArrayList<>();
        try {
            Statement stmt = SQLiteJDBCDriverConnection.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, engWord, plWord, engSentence, plSentence FROM flashcard WHERE engWord like '%"+NewEngWord+"%' OR plWord like '%"+newPlWord+"%' ");

            while ( rs.next() ) {
                int id = rs.getInt(1);
                String engWord  = rs.getString(2);
                String plWord  = rs.getString(3);
                String engSentence  = rs.getString(4);
                String plSentence  = rs.getString(5);
                Flashcard flashcard = new Flashcard(engWord,plWord,engSentence,plSentence);
                flashcard.setId(id);
                flashcardList.add(flashcard);
            }
        }
        catch (Exception ex){
            System.err.println("FlashCardExist: "+ex);
            Message("FlashCardExist: "+ ex );
        }

        return flashcardList;
    }

}
