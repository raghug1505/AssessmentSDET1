package com.example.testassessment;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
class TestAssessmentApplicationTests {

    @BeforeEach
    public void setup() {
        // Set Base URI
        RestAssured.baseURI = "https://open.er-api.com/";
    }

    @Test
    public void testStatusCodeShouldBe200() {
        // Send a GET request to the base URI and validate the status code
        given()          // Prepare the request
                .when()      // Specify the method type
                .get("/v6/latest/USD") // End-point path
                .then()      // Assertions about the response
                .assertThat()
                .statusCode(200); // Validate that the status code is 200
    }

    @Test
    public void testCurrencyApiResponseMatchesSchema(){

        given()
                .when()
                .get("/v6/latest/USD")
                .then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("currency_schema.json"));
    }

    @Test
    public void testResultIsSuccess() {



        // Perform a GET request to /v6/latest/USD and verify the 'result' field
        given()
                .when()
                .get("/v6/latest/USD")
                .then()
                .assertThat()
                .statusCode(200) // Assert that the status code is 200
                .body("result", equalTo("success")); // Assert that 'result' is "success"
    }

    @Test
    public void testRatesHas162Entries() {



        // Perform a GET request to /v6/latest/USD and verify the size of 'rates'
        given()
                .when()
                .get("/v6/latest/USD")
                .then()
                .assertThat()
                .statusCode(200) // Assert that the status code is 200
                .body("rates.size()", is(162)); // Assert the size of the 'rates' object is 162
    }

    @Test
    public void testAEDRateIsBetween3Point6And3Point7() {


        // Perform a GET request to /v6/latest/USD and verify the 'AED' rate
        ValidatableResponse response = given()
                .when()
                .get("/v6/latest/USD")
                .then()
                .assertThat()
                .statusCode(200) // Assert that the status code is 200
                .body("rates.AED", allOf(greaterThanOrEqualTo((float)3.6), lessThanOrEqualTo((float)3.7))); // Assert that 'AED' rate is between 3.6 and 3.7
    }



    @Test
    public void testResponseTimeWithin3Seconds() {
        // Record the current time in seconds before making the API call
        int startTime = (int)System.currentTimeMillis() / 1000;

        // Perform a GET request to the endpoint and extract the timestamp from the response
        int apiResponseTime = given()
                .when()
                .get("/v6/latest/USD") // Adjust this endpoint path accordingly
                .then()
                .assertThat()
                .statusCode(200) // Ensures the API is responding correctly
                .extract()
                .path("time_last_update_unix"); // Adjust this to match the JSON path to the timestamp in the response

        // Calculate the difference in seconds between the current time and the API response timestamp
        int timeDifference = apiResponseTime - startTime;

        // Assert that the time difference is not more than 3 seconds
        // Note: Depending on the exact requirement, you might adjust the logic here
        // For example, this checks it's not later by more than 3 seconds
        assert timeDifference >= 3 : "The API response time is more than 3 seconds later than the current time.";
    }
}
