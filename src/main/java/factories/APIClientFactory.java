package factories;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

    public class APIClientFactory {

        public static RequestSpecification getRequest() {

            return RestAssured
                    .given()
                    .baseUri("https://reqres.in")
                    .header("Content-Type", "application/json");
        }
    }

