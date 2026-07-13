package factories;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class APIClientFactory {
    public static RequestSpecification getRequestSpec(String baseUrl) {
        return RestAssured.given().baseUri(baseUrl);
    }
}
