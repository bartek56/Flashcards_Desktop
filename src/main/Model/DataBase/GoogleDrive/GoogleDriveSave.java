package Model.DataBase.GoogleDrive;

import Model.DataBase.SQLite.FileContent;
import Model.DataBase.SQLite.FlashcardHelper;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import javafx.concurrent.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GoogleDriveSave extends Task<Boolean> {

    public GoogleDriveSave(Drive service, String fileId) {
        this.service = service;
        this.fileId = fileId;
    }

    private Drive service;
    private String fileId=null;

    private boolean isSaved = false;

    @Override
    protected Boolean call() throws Exception {

        try {

            List<String> list = FlashcardHelper.GetAllFlashcardsAsStringList();

            File file = new File();

            file.setMimeType("text/plain");


            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            for (String line : list) {
                baos.write(line.getBytes(StandardCharsets.UTF_8));
                //baos.write('\n');
            }

            byte[] bytes = baos.toByteArray();

            InputStream is = new ByteArrayInputStream(bytes);

            FileContent mediaContent = new FileContent("text/plain", is);

            System.out.println("fileID writeToFile: "+fileId);
            service.files().update(fileId,file,mediaContent).execute();
            isSaved=true;

        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
            isSaved=false;
            //error=true;
            //errorMessage+="ERROR: WriteToFile "+e;
        }

        return null;
    }

    public void setService(Drive service)
    {
        this.service = service;
    }

    public void setFileId(String id)
    {
        this.fileId = id;
    }

    public boolean isSaved() {
        return isSaved;
    }
}