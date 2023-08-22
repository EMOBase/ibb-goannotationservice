package ibb.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;

public class PossiblyCompressedInputStream extends InputStream {

    private InputStream inputStream;

    public PossiblyCompressedInputStream(InputStream inputStream) throws IOException {
        PushbackInputStream pb = new PushbackInputStream(inputStream, 2);
        byte[] signature = new byte[2];
        int len = pb.read(signature);

        if (len > 0) {
            pb.unread(signature, 0, len);
        }

        if (isGzipped(signature)) {
            this.inputStream = new GZIPInputStream(pb);
        } else {
            this.inputStream = pb;
        }
    }

    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    private boolean isGzipped(byte[] data) {
        return data != null && data.length >= 2 && (data[0] & 0xff
                | (data[1] << 8) & 0xff00) == GZIPInputStream.GZIP_MAGIC;
    }
}
