package ibb.api.parser;

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

                assoc.setDb(cols[0]);
                assoc.setDbObjectId(cols[1]);
                assoc.setDbObjectSymbol(cols[2]);
                assoc.setQualifier(cols[3]);
                assoc.setGoId(cols[4]);
                assoc.setDbReference(cols[5]);
                assoc.setEvidence(cols[6]);
                assoc.setWithOrFrom(cols[7]);
                assoc.setAspect(cols[8]);
                assoc.setDbObjectName(cols[9]);
                assoc.setDbObjectSynonym(cols[10]);
                assoc.setDbObjectType(cols[11]);
                assoc.setTaxon(cols[12]);
                assoc.setDate(cols[13]);
                assoc.setAssignedBy(cols[14]);

                if (cols.length > 15) {
                    assoc.setAnnotationExtension(cols[15]);
                }

                if (cols.length > 16) {
                    assoc.setGeneProductFormId(cols[16]);
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
