package ibb.api;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class FileUploadForm {
    private byte[] fileData;

    public byte[] getFileData() {
        return fileData;
    }

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public void setFileData(final byte[] fileData) {
        this.fileData = fileData;
    }
}
