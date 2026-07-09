package ai.test;

import ai.service.AIService;
import utilities.api.config_utils;

public class AITest {


        public static void main(String[] args) throws Exception {

            config_utils.loadProperties();
            AIService ai = new AIService();

            String answer = ai.ask("Explain Selenium in one sentence.");

            System.out.println(answer);

        }

    }

