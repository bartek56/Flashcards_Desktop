package Model.DataBase.GoogleDrive;

import Model.DataBase.SQLite.FlashcardHelper;
import Model.DataBase.SQLite.Tables.Flashcard;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static Model.DataBase.SQLite.SQLiteJDBCDriverConnection.DATABASE_FILE_NAME;
import static Model.GoogleDriveHelper.getDriveService;

public class GoogleDriveConnect extends Task<Boolean> {

    private Drive service;
    private String fileId=null;
    private File fileGoogle;
    private static final java.io.File DATA_STORE_DIR_GOOGLEDRIVE = new java.io.File(
            System.getProperty("user.home"), "Documents/Flashcards/googleClient");


    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final java.util.Collection<String> SCOPES = DriveScopes.all();
    private final String APPLICATION_NAME = "Drive API Flashcards";
    private boolean isConnect=false;

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

    public Credential authorize() throws IOException {
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
    public Drive getDriveService() throws IOException {
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
                ReadFromFile();
                return true;
                //
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

    private String GetCsvFileName(String DATABASE_FILE_NAME)
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

    private void ReadFromFile()
    {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(service.files().get(fileId).executeMediaAsInputStream(), StandardCharsets.UTF_8));

            String line;
            String allText="";
            String category="";

            String plWords="";
            String engWords="";
            String plSentences = "";
            String engsentences ="";
            String flashcards="";
            List<String> categoryList = new ArrayList<>();
            //List <String>


            int i=0;
            Flashcard flashcard = new Flashcard(null,null,null,null);

            while ((line = reader.readLine()) != null)
            {
                for (char ch:line.toCharArray()) {
                    if(ch=='~')
                    {
                        i++;

                        switch (i)
                        {
                            case 1: category = allText; FlashcardHelper.AddCategory(category);  break;
                            case 2: flashcard.setEngWord(allText);  break;
                            case 3: flashcard.setPlWord(allText); break;
                            case 4: flashcard.setEngSentence(allText); break;
                            case 5: flashcard.setPlSentence(allText); break;
                            case 6:
                            {
                                i=0;
                                int id = FlashcardHelper.AddFlashcard(flashcard);

                                if(id!=0)
                                {
                                    FlashcardHelper.AddFlashcardToCategory(category,id);
                                }

                                break;
                            }
                        }
                        allText="";
                    }
                    else
                    {
                        allText+=ch;
                    }
                }

            }

            reader.close();

            System.out.println("Wczytano dane z Google Drive");
            //Log.d(TAG,"Wczytano dane z Google Drive");
        } catch (IOException e) {
            System.err.println("ReadFromFile: "+e);
            //error=true;
            //errorMessage+="ERROR: ReadFromFile "+e;
        }
    }

    public boolean isConnect() {
        return isConnect;
    }

    public Drive getService()
    {
        return service;
    }

    public String getFileId() {
        return fileId;
    }
}
