package a2300.spring;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;

public class FindAccountsSpringTest extends AbstractSpringTest {
    @Test
    void getAll() {
        when()
                .get("/accounts")
                .then()
                .statusCode(200);
    }
}
