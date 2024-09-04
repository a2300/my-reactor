package a2300.spring;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;

import a2300.spring.dto.AccountDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

class CreateAccountSpringTest extends AbstractSpringTest {

    @Test
    void create() {
        var account = AccountDto.builder()
                .email("some-email@example.com")
                .firstName("MyName")
                .lastName("FamilyName")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .currency("USD")
                .moneyAmount(893)
                .build();

        //@formatter:off
        given()
                .body(account)
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
        //@formatter:on
    }

    @Test
    void createWithMissedEmail() {
        var account = AccountDto.builder()
                .email(null) // Required field is null
                .firstName("MyName")
                .lastName("FamilyName")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .currency("USD")
                .moneyAmount(893)
                .build();

        //@formatter:off
        given()
                .body(account)
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts")
                .then()
                .statusCode(422)
                .body("errors", hasItem("'email' must not be empty"));
        //@formatter:on
    }
}

