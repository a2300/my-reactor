package a2300.spring;

import static a2300.spring.controller.AccountController.DIRTY_TRICK_HEADER;
import static a2300.spring.controller.AccountController.FAIL_TRANSFER;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import a2300.spring.dto.MoneyTransferDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTransferSpringTest extends AbstractSpringTest {


    @ParameterizedTest
    @CsvSource({
        "USD -> USD, 1, 2, 40, 300, 613",
        "GBP -> USD, 6, 5, 25, 55,  150"
    })
    void transfer(String description, long senderId, long recipientId, int transferAmount,
                  int expectedSenderMoney, int expectedRecipientMoney) {

        var requestBody = MoneyTransferDto.builder()
            .senderId(senderId)
            .recipientId(recipientId)
            .moneyAmount(transferAmount)
            .build();

        //@formatter:off
        given()
            .body(requestBody)
            .contentType(ContentType.JSON)
        .when()
            .post("/accounts/transfers")
        .then()
            .statusCode(204);


        get("/accounts/" + senderId).then()
            .assertThat().body("moneyAmount", equalTo(expectedSenderMoney));
        get("/accounts/" + recipientId).then().assertThat()
            .body("moneyAmount", equalTo(expectedRecipientMoney));
        //@formatter:on
    }

    @Test
    void transferWithServerProblemDuringTransfer() {
        var transferAmount = 40;
        // Sender
        var senderId = 3L;
        var senderMoneyAmountOriginal = 79;
        // Recipient
        var recipientId = 4L;
        var recipientMoneyAmountOriginal = 33;

        var requestBody = MoneyTransferDto.builder()
            .senderId(senderId)
            .recipientId(recipientId)
            .moneyAmount(transferAmount)
            .build();

        //@formatter:off
        given()
            .body(requestBody)
            .header(DIRTY_TRICK_HEADER, FAIL_TRANSFER)
            .contentType(ContentType.JSON)
        .when()
            .post("/accounts/transfers")
        .then()
            .statusCode(500)
            .body("errors", hasItem("Dirty Trick: Error during money transfer"));
        //@formatter:on

        get("/accounts/" + senderId).then()
            .body("moneyAmount", is(senderMoneyAmountOriginal));
        get("/accounts/" + recipientId).then()
            .body("moneyAmount", is(recipientMoneyAmountOriginal));
    }

    @Test
    void transferWithoutRequiredField() {

        var requestBody = MoneyTransferDto.builder()
            .senderId(1L)
            .recipientId(2L)
            .moneyAmount(null) // Required field is null
            .build();

        //@formatter:off
        given()
            .body(requestBody)
            .contentType(ContentType.JSON)
        .when()
            .post("/accounts/transfers")
        .then()
            .statusCode(422)
            .body("errors", hasItem("'moneyAmount' must not be null"));
        //@formatter:on
    }
}
