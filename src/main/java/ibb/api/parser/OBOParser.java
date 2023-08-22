package ibb.api.parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Obo format definition
 * https://owlcollab.github.io/oboformat/doc/GO.format.obo-1_4.html
 */
public final class OBOParser extends Parser<OBOStanza> {

    private OBOStanza current;

    public OBOParser(InputStream inputStream) {
        super(inputStream);
        current = null;
    }

    @Override
    protected OBOStanza getNextItem() throws IOException {
        OBOStanza stanza = current;
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                // end of file
                current = null;
                return stanza;
            }

            line = line.trim();

            if ("".equals(line) || line.startsWith("!")) {
                // empty or comment lines
                continue;
            }

            if (line.startsWith("[")) {
                if (stanza == null) {
                    // first stanza
                    stanza = new OBOStanza(getTypeForLine(line));
                } else {
                    current = new OBOStanza(getTypeForLine(line));
                    return stanza;
                }
            } else if (stanza != null) {
                String[] cols = line.split(":", 2);
                if (cols.length != 2) {
                    // invalid line, ignore or raise an error?
                    continue;
                }
                String tag = cols[0].trim();
                String valueWithoutComment = cols[1].split("!", 2)[0].trim();
                stanza.addAttribute(tag, valueWithoutComment);
            } else {
                // header lines
                continue;
            }
        }
    }

    private static OBOStanza.Type getTypeForLine(String line) {
        switch (line.trim().toLowerCase()) {
            case "[term]" :
                return OBOStanza.Type.TERM;
            case "[typedef]" :
                return OBOStanza.Type.TYPEDEF;
            case "[instance]" :
                return OBOStanza.Type.INSTANCE;
            default :
                return OBOStanza.Type.UNKNOWN;
        }
    }
}
