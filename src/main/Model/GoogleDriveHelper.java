package Model;

import Model.DataBase.SQLite.SQLiteJDBCDriverConnection;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;

import static Model.DataBase.SQLite.SQLiteJDBCDriverConnection.DATABASE_FILE_NAME;
import static Model.DataBase.SQLite.SQLiteJDBCDriverConnection.connect;


/**
 * Created by Bartek on 2017-09-05.
 */
public class GoogleDriveHelper {
    /** Application name. */
    private static final String APPLICATION_NAME = "Drive API Flashcards";

    //private static final String DATABASE_FILE_NAME = "flashcard";

    public static boolean error=false;

    private static String errorMessage="";
    public static boolean isOpen=false;

    private static String fileId=null;

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR_GOOGLEDRIVE = new java.io.File(
            //System.getProperty("user.home"), ".credentials/drive-java-quickstart");
            System.getProperty("user.home"), "Documents/Flashcards/googleClient");


    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    //private static FileD

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
    private static File fileGoogle;
    private static Drive service;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    /*
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE,
            DriveScopes.DRIVE,DriveScopes.DRIVE_METADATA,DriveScopes.DRIVE_APPDATA,
            DriveScopes.DRIVE_SCRIPTS
    );
    */
    private static final java.util.Collection<String> SCOPES = DriveScopes.all();
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR_GOOGLEDRIVE);
        } catch (Throwable t) {
            error=true;
            errorMessage+="ERROR: SCOPES "+t;
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                GoogleDriveHelper.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR_GOOGLEDRIVE.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    private static String GetCsvFileName(String DATABASE_FILE_NAME)
    {
        String csvFileName="";
        switch (DATABASE_FILE_NAME)
        {
            case "flashcardsEng.db": csvFileName="flashcardsEng.csv";break;
            case "flashcardsDe.db": csvFileName="flashcardsDe.csv"; break;
            case "flashcardsFr.db": csvFileName="flashcardsFr.csv"; break;
        }
        return csvFileName;
    }

    public static void ConnectWithGoogle ()
    {
        try
        {
            service = getDriveService();

            boolean isFileInDatabase = false;
            String pageToken = null;
            fileId = null;
            do {
                FileList result = service.files().list()
                        .setQ("mimeType='text/plain'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                for (File file : result.getFiles()) {
                    if (file.getName().equals(GetCsvFileName(DATABASE_FILE_NAME))) {
                        fileId = file.getId();
                        System.out.println("DataBase file Exist id: "+fileId);

                        fileGoogle = file;
                        break;
                    }
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);


            System.out.println("fileID: "+fileId);

            if (fileId == null) {
                System.err.println("nie ma pliku");
                error=true;
                errorMessage+="ERROR: Nie ma słów w tym języku ";
            }
            else
            {
                connect();
                ReadFromFile(service);
            }
        }
        catch (Exception ex)
        {
            System.err.println("ConnectWithGoogle: "+ex);
            error=true;
            errorMessage+="ERROR: ConnectWithGoogle "+ex;
        }

    }


    public static void WriteToFile()
    {
        try {
        // First retrieve the file from the API.
        //File file = service.files().get(fileId).execute();
            File file = new File();

            file.setMimeType("text/plain");

        // File's new metadata.
            /*
            file.setName(DATABASE_FILE_NAME);

        file.setDescription("description");
        file.setMimeType("text/plain");
*/
        // File's new content.

        //java.io.File fileContent = new java.io.File("C:/sqlite/" + DATABASE_FILE_NAME);
        java.io.File fileContent = new java.io.File(SQLiteJDBCDriverConnection.DATABASE_FILE_LOCATION + DATABASE_FILE_NAME);
       //file.setTrashed(true);

        FileContent mediaContent = new FileContent("text/plain", fileContent);

        // Send the request to the API.
        //File updatedFile = service.files().update(fileId, file, mediaContent).execute();
            System.out.println("fileID writeToFile: "+fileId);
        File updatedFile = service.files().update(fileId,file,mediaContent).execute();


        //return updatedFile;
    } catch (IOException e) {
        System.out.println("An error occurred: " + e);
            error=true;
            errorMessage+="ERROR: WriteToFile "+e;
    }
    }

    private static String CreateFile( Drive service) {

        String fileId=null;
        try
        {
            File fileMetadata = new File();
            fileMetadata.setName(DATABASE_FILE_NAME);
            fileMetadata.setMimeType("text/plain");

            File file = service.files().create(fileMetadata)
                    .setFields("id")
                    .setKey("flashcards")
                    .execute();
            fileId = file.getId();
            System.out.println("NewFile, ID: " + fileId);
            //WriteToFile();
        }catch (IOException ex) {
            System.err.println("CreateFile: "+ex);
            error=true;
            errorMessage+="ERROR: CreateFile "+ex;
        }

        return fileId;

    }

    private static void DeleteFile(Drive service)
    {
        try
        {
            //File file = service.files().get(fileId).execute();
            service.files().delete(fileId).execute();

        }
        catch (IOException ex)
        {
            System.out.println(ex);
            error=true;
            errorMessage+="ERROR: DeleteFile "+ex;
        }

    }

    private static void ReadFromFile(Drive service)
    {

        java.io.File file = new java.io.File(SQLiteJDBCDriverConnection.DATABASE_FILE_LOCATION + DATABASE_FILE_NAME);

        try {
            OutputStream oos = new FileOutputStream(file);
            //Writer writer = new OutputStreamWriter(oos);

            //service.files().get(fileId).executeMediaAsInputStream()
            service.files().get(fileId).executeMediaAndDownloadTo(oos);
            //service.files().get

            /*
            InputStream is = result.getDriveContents().getInputStream();

            byte[] buf = new byte[4096];
            int c;
            while ((c = is.read(buf, 0, buf.length)) > 0) {
                oos.write(buf, 0, c);
                oos.flush();
            }
            writer.close();


            SQLiteDatabase.openOrCreateDatabase(file, null);
            */

            System.out.println("Wczytano dane z Google Drive");
            //Log.d(TAG,"Wczytano dane z Google Drive");
        } catch (IOException e) {
            System.err.println("ReadFromFile: "+e);
            error=true;
            errorMessage+="ERROR: ReadFromFile "+e;
        }
    }

    public static String getErrorMessage() {
        return errorMessage;

    }

    public static void removeError() {
        errorMessage="";
        error=false;
    }

}
