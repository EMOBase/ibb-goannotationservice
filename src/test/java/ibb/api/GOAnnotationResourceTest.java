package ibb.api;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;

@QuarkusTest
@TestHTTPEndpoint(GOAnnotationResource.class)
public class GOAnnotationResourceTest {
    
    @Test
    public void testGetShouldSucceedWhenGeneIdsAreProvided() {
        givenJson()
            .when()
                .get("/TC003413")
            .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(
                    "size()", equalTo(2)
                );
    }

    private RequestSpecification givenJson() {
        return RestAssured
            .given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON);
    }
}
