package ibb.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import ibb.api.parser.GAFParser;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/go-annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GOAnnotationResource {
    
    @Inject
    GOAnnotationRepository goAnnotationRepository;

    @Inject
    GOTermRepository goTermRepository;

    @POST
    @Path("/unreviewed")
    @Transactional
    public Response createUnreviewed(@Valid GOAnnotation goAnnotation) {
        goAnnotation.setStatus(GOAnnotation.Status.UNREVIEWED);
        GOAnnotation dbGoAnnotation = goAnnotationRepository.create(goAnnotation);
        Optional<GOTerm> goTermOptional = goTermRepository.findByIdOptional(dbGoAnnotation.getGoTermId());
        goTermOptional.ifPresent((goTerm) -> {
            dbGoAnnotation.setGoTerm(goTerm);
        });
        return Response.status(Response.Status.CREATED)
            .entity(dbGoAnnotation)
            .build();
    }

    @GET
    @Path("/by-genes/{geneIds}")
    public Response getByGeneIds(@PathParam("geneIds") String commaSeparatedGeneIds) {
        List<String> geneIds = Stream.of(commaSeparatedGeneIds.split(","))
            .map(String::trim)
            .collect(Collectors.toList());

        List<GOAnnotation> goAnnotations = goAnnotationRepository.findByGeneIds(geneIds);
        return Response.status(Response.Status.OK)
            .entity(goAnnotations)
            .build();
    }

    @GET
    @Path("/for-internal-review")
    public Response getForInternalReview() {
        List<GOAnnotation> goAnnotations = goAnnotationRepository.findByStatus(GOAnnotation.Status.UNREVIEWED);
        return Response.status(Response.Status.OK)
            .entity(goAnnotations)
            .build();
    }

    @POST
    @Path("/internal-review")
    @Transactional
    public Response updateStatusAfterInternalReview(@NotEmpty List<GOAnnotation> goAnnotations) {
        Map<GOAnnotation.Status, List<Long>> statusToIdsMap = goAnnotations.stream()
            .filter(ann -> ann != null)
            .filter(ann -> ann.getId() != null)
            .collect(
                Collectors.groupingBy(GOAnnotation::getStatus,
                    Collectors.mapping(GOAnnotation::getId, Collectors.toList()))
            );

        int deleted = Optional.ofNullable(statusToIdsMap.get(GOAnnotation.Status.TO_BE_DELETED))
            .map(goAnnotationRepository::delete)
            .orElse(0);

        int reviewed = Optional.ofNullable(statusToIdsMap.get(GOAnnotation.Status.REVIEWED))
            .map(ids -> goAnnotationRepository.updateStatus(ids, GOAnnotation.Status.REVIEWED))
            .orElse(0);

        return Response.status(Response.Status.OK)
            .entity(Map.of(
                "deleted", deleted,
                "reviewed", reviewed
            ))
            .build();
    }

    @GET
    @Path("/for-official-submission")
    public Response getForOfficialSubmission() {
        List<GOAnnotation> goAnnotations = goAnnotationRepository.findByStatus(GOAnnotation.Status.REVIEWED);
        return Response.status(Response.Status.OK)
            .entity(goAnnotations)
            .build();
    }

    @POST
    @Path("/official")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response mergeOfficial(@MultipartForm FileUploadForm form) throws IOException {
        InputStream inputStream = new PossiblyCompressedInputStream(
            new ByteArrayInputStream(form.getFileData())
        );

        try (GAFParser parser = new GAFParser(inputStream)) {
            List<GOAnnotation> goAnnotations = parser.stream()
                .map(item -> GOAnnotation.fromOfficialGoAnnotation(item))
                .map((goAnnotation) -> {
                    goAnnotation.setStatus(GOAnnotation.Status.OFFICIAL);
                    return goAnnotation;
                })
                .collect(Collectors.toList());

            goAnnotationRepository.create(goAnnotations);
            return Response.status(Response.Status.OK)
                .entity(Map.of())
                .build();
        }
    }
}
