package Model.DataBase.SQLite;

import java.io.File;
import java.sql.*;

/**
 * Created by Bartek on 2017-09-06.
 */
public class SQLiteJDBCDriverConnection {

    public static Connection connection = null;
    private static Statement statement = null;
    public static String DATABASE_FILE_NAME;
    //public static String DATABASE_FILE_LOCATION="C:/sqlite/";
    public static String DATABASE_FILE_LOCATION = System.getProperty("user.home") + "/Documents/Flashcards/";
    //private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/drive-java-quickstart");
    public static boolean error=false;
    private static String errorMessage="";


    public static void connect() {

        try {
            File theDir = new File(DATABASE_FILE_LOCATION);
            if (!theDir.exists()) {
                System.out.println("creating directory: " + theDir.getName());
                boolean result = false;

                try{
                    theDir.mkdir();
                    result = true;
                }
                catch(SecurityException e){
                    System.out.println("SQLite Security Exception: " + e);
                }
                if(result) {
                    System.out.println("DIR created");
                }
            }
            Class.forName("org.sqlite.JDBC");
            File file = new File(DATABASE_FILE_LOCATION+""+DATABASE_FILE_NAME);
            if(file.exists())
                file.delete();
            //connection = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/"+DATABASE_FILE_NAME);
            connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_FILE_LOCATION+""+DATABASE_FILE_NAME);
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            error=true;
            errorMessage+="SQL connect: "+e;
        }
    }


}
