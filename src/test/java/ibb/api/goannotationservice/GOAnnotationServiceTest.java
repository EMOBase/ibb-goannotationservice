package ibb.api.goannotationservice;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
@TestHTTPEndpoint(GOAnnotationService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GOAnnotationServiceTest {

    private static class Timeline {
        public static final int INITIAL = 1;
        public static final int BEFORE_ADD_TRIBOLIUM_ANNOTATION = 2;
        public static final int AFTER_ADD_TRIBOLIUM_ANNOTATION = 3;
        public static final int BEFORE_INTERNAL_REVIEW = 4;
        public static final int AFTER_INTERNAL_REVIEW = 5;
    }

    private static List<String> ids;

    @Inject
    GOAnnotationIndexFactory indexFactory;

    @BeforeEach
    void refreshAll() {
        indexFactory.get("*").refreshIndex();
    }

    @Test
    @Order(Timeline.INITIAL)
    public void shouldFindNoAnnotationByTriboliumGene() {
        RestAssured
            .given()
            .when()
                .get("/by-genes/TC003413")
            .then()
                .statusCode(200)
                .body(
                    "size()", equalTo(0)
                );
    }

    @Test
    @Order(Timeline.INITIAL)
    public void shouldFindNoAnnotationForInternalReview() {
        RestAssured
            .given()
            .when()
                .get("/for-internal-review")
            .then()
                .statusCode(200)
                .body(
                    "size()", equalTo(0)
                );
    }

    @Test
    @Order(Timeline.INITIAL)
    public void shouldFindAnnotationsByDrosophilaGene() {
        RestAssured
            .given()
            .when()
                .get("/by-genes/FBgn0001323")
            .then()
                .statusCode(200)
                .body("size()", equalTo(11))
                .body("gene", everyItem(equalTo("FBgn0001323")))
                .body("taxon", everyItem(equalTo("taxon:7227")))
                .body("status", everyItem(equalTo("OFFICIAL")))
                .body("date", everyItem(hasLength(8)))
                .body("term.id", everyItem(notNullValue()))
                .body("term.aspect", everyItem(notNullValue()))
                .body("evidence", everyItem(notNullValue()));
    }

    @Test
    @Order(Timeline.INITIAL)
    public void shouldFindAnnotationsByMultipleDrosophilaGenes() {
        RestAssured
            .given()
            .when()
                .get("/by-genes/FBgn0001323,FBgn0001180")
            .then()
                .statusCode(200)
                .body("size()", equalTo(45))
                .body("gene", everyItem(anyOf(equalTo("FBgn0001323"), equalTo("FBgn0001180"))));
    }

    @Test
    @Order(Timeline.BEFORE_ADD_TRIBOLIUM_ANNOTATION)
    public void shouldCreateTriboliumAnnotation() {
        GOAnnotation ann = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(createValidGoAnnotation())
            .when()
                .post("/unreviewed")
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("gene", equalTo("TC003413"))
                .body("status", equalTo("UNREVIEWED"))
                .body("term.id", equalTo("GO:0000001"))
                .body("term.aspect", equalTo("P"))
                .body("geneProduct", equalTo("protein"))
                .body("evidence", equalTo("EXP"))
                .body("reference", equalTo("PMID:1234567"))
                .body("date", notNullValue())
                .body("taxon", equalTo("taxon:7070"))
                .body("lab", equalTo("Gregor Bucher"))
            .extract().as(GOAnnotation.class);

        GOAnnotation ann2 = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(createValidGoAnnotation())
            .when()
                .post("/unreviewed")
            .then()
                .statusCode(200)
            .extract().as(GOAnnotation.class);

        ids = List.of(ann.id, ann2.id);
    }

    @Test
    @Order(Timeline.AFTER_ADD_TRIBOLIUM_ANNOTATION)
    public void shouldFindAnnotationsByTriboliumGene() {
        RestAssured
            .given()
            .when()
                .get("/by-genes/TC003413")
            .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("gene", everyItem(equalTo("TC003413")));
    }

    @Test
    @Order(Timeline.AFTER_ADD_TRIBOLIUM_ANNOTATION)
    public void shouldFindAnnotationsForInternalReview() {
        RestAssured
            .given()
            .when()
                .get("/for-internal-review")
            .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("gene", everyItem(equalTo("TC003413")))
                .body("status", everyItem(equalTo("UNREVIEWED")))
                .body("id",  equalTo(ids));
    }

    @Test
    @Order(Timeline.AFTER_ADD_TRIBOLIUM_ANNOTATION)
    public void shouldFindNoAnnotationsForOfficialSubmission() {
        RestAssured
            .given()
            .when()
                .get("/for-official-submission")
            .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @Order(Timeline.BEFORE_INTERNAL_REVIEW)
    public void shouldSubmitInternallyReviewedAnnotationsSuccessfully() {
        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(List.of(
                    new GOAnnotation() {{
                        id = ids.get(0);
                        status = GOAnnotation.Status.INTERNAL;
                    }},
                    new GOAnnotation() {{
                        id = ids.get(1);
                        status = GOAnnotation.Status.TO_BE_DELETED;
                    }}
                ))
            .when()
                .put("/")
            .then()
                .statusCode(204);
    }

    @Test
    @Order(Timeline.AFTER_INTERNAL_REVIEW)
    public void shouldFindNoAnnotationsForInternalReview() {
        RestAssured
            .given()
            .when()
                .get("/for-internal-review")
            .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @Order(Timeline.AFTER_INTERNAL_REVIEW)
    public void shouldFindInternallyReviewedAnnotationsByTriboliumGene() {
        RestAssured
            .given()
            .when()
                .get("/by-genes/TC003413")
            .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("gene", everyItem(equalTo("TC003413")))
                .body("status", everyItem(equalTo("INTERNAL")));
    }

    @Test
    @Order(Timeline.AFTER_INTERNAL_REVIEW)
    public void shouldFindAnnotationsForOfficialSubmission() {
        RestAssured
            .given()
            .when()
                .get("/for-official-submission")
            .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("gene", everyItem(equalTo("TC003413")))
                .body("status", everyItem(equalTo("INTERNAL")));
    }

    private GOAnnotation createValidGoAnnotation() {
        return new GOAnnotation() {{
            gene = "TC003413";
            term = new Term() {{
                id = "GO:0000001";
                aspect = "P";
            }};
            geneProduct = "protein";
            evidence = "EXP";
            reference = "PMID:1234567";
            date = "20210101";
            lab = "Gregor Bucher";
        }};
    }
}
