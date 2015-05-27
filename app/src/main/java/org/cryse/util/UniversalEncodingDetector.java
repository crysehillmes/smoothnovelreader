package org.cryse.util;

import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class UniversalEncodingDetector implements EncodingDetector {

    private static final int BUFSIZE = 1024;

    private static final int LOOKAHEAD = 16 * BUFSIZE;

    public Charset detect(InputStream input, Metadata metadata)
            throws IOException {
        if (input == null) {
            return null;
        }

        input.mark(LOOKAHEAD);
        try {
            UniversalEncodingListener listener =
                    new UniversalEncodingListener(metadata);

            byte[] b = new byte[BUFSIZE];
            int n = 0;
            int m = input.read(b);
            while (m != -1 && n < LOOKAHEAD && !listener.isDone()) {
                n += m;
                listener.handleData(b, 0, m);
                m = input.read(b, 0, Math.min(b.length, LOOKAHEAD - n));
            }

            return listener.dataEnd();
        } catch (LinkageError e) {
            return null; // juniversalchardet is not available
        } finally {
            input.reset();
        }
    }

}
