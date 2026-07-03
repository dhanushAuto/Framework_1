package factories;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class APIClientFactory {

    public static RequestSpecification APIRequest(String baseUri) {

        return RestAssured
                .given()
                .baseUri(baseUri)
                .contentType("application/json");
    }
}