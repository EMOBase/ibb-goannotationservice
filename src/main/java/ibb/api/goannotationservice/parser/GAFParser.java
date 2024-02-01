package ibb.api.goannotationservice.parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for GO Annotation File (GAF) format 2.2
 * http://geneontology.org/docs/go-annotation-file-gaf-format-2.2/
 */
public final class GAFParser extends Parser<OfficialGOAnnotation> {

    private long cnt = 0;

    public GAFParser(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    protected OfficialGOAnnotation getNextItem() throws IOException {
        while (true) {
            String line = bufferedReader.readLine();
            cnt++;

            if (line == null) {
                // end of file
                return null;
            }

            if (line.startsWith("!") || "".equals(line.trim())) {
                // comment line or empty line
                continue;
            } else {
                OfficialGOAnnotation assoc = new OfficialGOAnnotation();
                String[] cols = line.split("\t");
                if (cols.length < 15) {
                    throw new AssertionError(
                        "GAF line must have at least 15 columns\nLine (" + cnt + "): " + line
                    );
                }

                assoc.db = cols[0];
                assoc.dbObjectId = cols[1];
                assoc.dbObjectSymbol = cols[2];
                assoc.qualifier = cols[3];
                assoc.goId = cols[4];
                assoc.dbReference = cols[5];
                assoc.evidence = cols[6];
                assoc.withOrFrom = cols[7];
                assoc.aspect = cols[8];
                assoc.dbObjectName = cols[9];
                assoc.dbObjectSynonym = cols[10];
                assoc.dbObjectType = cols[11];
                assoc.taxon = cols[12];
                assoc.date = cols[13];
                assoc.assignedBy = cols[14];

                if (cols.length > 15) {
                    assoc.annotationExtension = cols[15];
                }

                if (cols.length > 16) {
                    assoc.geneProductFormId = cols[16];
                }

                if (cols.length > 17) {
                    throw new AssertionError(
                        "GAF line has too many values\nLine (" + cnt + "): " + line
                    );
                }
                return assoc;
            }
        }
    }
}
