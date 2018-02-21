package Model.DataBase.SQLite;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public class FileContent extends AbstractInputStreamContent {

    private InputStream inputStream = null;
    private long inputLenght=0;

    public FileContent(String type, InputStream inputStream) throws IOException {
        super(type);
        this.inputStream = Preconditions.checkNotNull(inputStream);
        this.inputLenght = this.inputStream.available();

    }


    @Override
    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    @Override
    public long getLength() throws IOException {
        return this.inputLenght;
    }

    @Override
    public boolean retrySupported() {
        return false;
    }
}
