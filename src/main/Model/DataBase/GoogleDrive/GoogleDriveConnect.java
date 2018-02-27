package Model.DataBase.GoogleDrive;

import Model.GoogleDriveHelper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static Model.DataBase.SQLite.SQLiteJDBCDriverConnection.DATABASE_FILE_NAME;
import static Model.GoogleDriveHelper.getDriveService;

public class GoogleDriveConnect extends Task<Boolean> {

    private static Drive service;
    private static String fileId=null;
    private static File fileGoogle;
    private static final java.io.File DATA_STORE_DIR_GOOGLEDRIVE = new java.io.File(
            System.getProperty("user.home"), "Documents/Flashcards/googleClient");


    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final java.util.Collection<String> SCOPES = DriveScopes.all();
    private static final String APPLICATION_NAME = "Drive API Flashcards";
    private static boolean isConnect=false;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR_GOOGLEDRIVE);
        } catch (Throwable t) {
            //error=true;
            //errorMessage+="ERROR: SCOPES "+t;
            t.printStackTrace();
            System.exit(1);
        }
    }

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


    @Override
    protected Boolean call() throws Exception {

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
                //error=true;
                //errorMessage+="ERROR: Nie ma słów w tym języku ";
                isConnect=false;
                return false;
            }
            else
            {
                isConnect=true;
                return true;
                //ReadFromFile(service);
            }
        }
        catch (Exception ex)
        {
            isConnect=false;
            System.err.println("ConnectWithGoogle: "+ex);
            //error=true;
            //errorMessage+="ERROR: ConnectWithGoogle "+ex;
        }

        return null;
    }

    private static String GetCsvFileName(String DATABASE_FILE_NAME)
    {
        String csvFileName="";
        switch (DATABASE_FILE_NAME)
        {
            case "flashcardsEng.db": csvFileName="flashcardsEng.txt";break;
            case "flashcardsDe.db": csvFileName="flashcardsDe.txt"; break;
            case "flashcardsFr.db": csvFileName="flashcardsFr.txt"; break;
        }
        return csvFileName;
    }

    public static boolean isConnect() {
        return isConnect;
    }
}
