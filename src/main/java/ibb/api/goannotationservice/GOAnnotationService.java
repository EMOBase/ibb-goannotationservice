package ibb.api.goannotationservice;

import static java.util.stream.Collectors.toList;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestPath;

import ibb.api.goannotationservice.parser.OfficialGOAnnotation;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/go-annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GOAnnotationService {
    
    @Inject
    GOAnnotationIndexFactory indexFactory;

    @Inject
    Config config;

    private GOAnnotationIndex getTargetIndex() {
        return indexFactory.get(config.target().species());
    }

    @POST
    @Path("/unreviewed")
    @Operation(hidden = true)
    public GOAnnotation createUnreviewed(
        @Valid
        @ConvertGroup(to=GOAnnotation.ValidationGroups.Create.class)
        GOAnnotation goAnnotation) {

        goAnnotation.status = GOAnnotation.Status.UNREVIEWED;
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        goAnnotation.date = now.format(formatter);
        goAnnotation.taxon = "taxon:" + config.target().taxon();
        return getTargetIndex().create(goAnnotation);
    }

    @GET
    @Path("/by-genes/{genes}")
    public List<GOAnnotation> getByGeneIds(@RestPath("genes") String commaSeparatedGenes) {
        List<String> genes = Stream.of(commaSeparatedGenes.split(","))
            .map(String::trim)
            .collect(toList());

        return indexFactory.get("*").findByGenes(genes);
    }

    @GET
    @Path("/for-internal-review")
    public List<GOAnnotation> getForInternalReview() {
        return getTargetIndex().findByStatus(GOAnnotation.Status.UNREVIEWED);
    }

    @PUT
    @Path("/")
    @Authenticated
    @Operation(hidden = true)
    public void update(
        @NotEmpty
        List<@Valid @ConvertGroup(to=GOAnnotation.ValidationGroups.Update.class) GOAnnotation> anns) {

        getTargetIndex().update(anns);
    }

    @GET
    @Path("/for-official-submission")
    @Produces(MediaType.TEXT_PLAIN)
    public String getForOfficialSubmission() {
        List<OfficialGOAnnotation> anns = getTargetIndex().findByStatus(GOAnnotation.Status.INTERNAL)
            .stream()
            .map(ann -> ann.toOfficialGOAnnotation())
            .collect(toList());
        StringWriter writer = new StringWriter();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        writer.write("!gaf-version: 2.1\n");
        writer.write("!generated-by: iBeetle-Base\n");
        writer.write("!date-generated: " + LocalDate.now().format(formatter) + "\n");
        writer.write("!DB\tDB_Object_ID\tDB_Object_Symbol\tQualifier\tGO_ID\tDB:Reference\tEvidence\tWith_or_From\tAspect\tDB_Object_Name\tDB_Object_Synonym\tDB_Object_Type\tTaxon\tDate\tAssigned_By\tAnnotation_Extension\tGene_Product_Form_ID\n");
        anns.forEach(ann -> writer.write(ann.toGAFLine() + "\n"));
        return writer.toString();
    }
}
