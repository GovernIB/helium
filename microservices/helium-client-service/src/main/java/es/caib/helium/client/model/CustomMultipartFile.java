package es.caib.helium.client.model;

import org.apache.tika.Tika;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomMultipartFile implements MultipartFile {

    private final byte[] fileContent;
    private String fileName;
    private String contentType;
//    private File file;
//    private String destPath = System.getProperty("java.io.tmpdir");
    private FileOutputStream fileOutputStream;

    public CustomMultipartFile(byte[] fileData, String name) {
        this.fileContent = fileData;
        this.fileName = name;
//        file = new File(destPath + fileName);
    }

//    public void clearOutStreams() throws IOException {
//        if (null != fileOutputStream) {
//            fileOutputStream.flush();
//            fileOutputStream.close();
//            file.deleteOnExit();
//        }
//    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public String getOriginalFilename() {
        return this.fileName;
    }

    @Override
    public String getContentType() {
        return new Tika().detect(this.fileContent);
    }

    @Override
    public boolean isEmpty() {
        return this.fileContent == null || this.fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return this.fileContent == null ? 0 : this.fileContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.fileContent);
    }

    @Override
    public Resource getResource() {
        return new ByteArrayResource(this.fileContent) {
            @Override
            public String getFilename() {
                return this.getFilename();
            }
        };
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        fileOutputStream = new FileOutputStream(dest);
        fileOutputStream.write(fileContent);
    }
}
