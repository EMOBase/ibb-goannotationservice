package ibb.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import ibb.api.parser.OBOParser;
import ibb.api.parser.OBOStanza;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/go-terms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GOTermResource {

    @Inject
    GOTermRepository goTermRepository;

    @POST
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response mergeOfficial(@MultipartForm FileUploadForm form) throws IOException {
        InputStream inputStream = new PossiblyCompressedInputStream(
            new ByteArrayInputStream(form.getFileData())
        );

        try (OBOParser parser = new OBOParser(inputStream)) {
            List<GOTerm> goTerms = parser.stream()
                .filter(oboStanza -> oboStanza.getType() == OBOStanza.Type.TERM)
                .map(item -> GOTerm.fromOBOStanza(item))
                .collect(Collectors.toList());
            goTermRepository.create(goTerms);
            return Response.status(Response.Status.OK)
                .entity(Map.of())
                .build();
        }
    }
}
