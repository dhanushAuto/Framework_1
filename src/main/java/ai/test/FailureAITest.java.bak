package ai.test;

import ai.analyzer.FailureAnalyzer;

public class FailureAITest {

    public static void main(String[] args)
            throws Exception {

        FailureAnalyzer analyzer =
                new FailureAnalyzer();

        String result =
                analyzer.analyze(

                        "Login Test",

                        new RuntimeException(

                                "TimeoutException"),


                        """
org.openqa.selenium.TimeoutException

at LoginPage.clickLogin()

at LoginTest.testLogin()

""");

        System.out.println(result);

    }

}