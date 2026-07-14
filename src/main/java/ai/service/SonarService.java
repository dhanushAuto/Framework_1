package ai.service;

import ai.model.SonarIssue;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import utilities.api.ConfigUtils;
import utilities.common_utils.JsonUtils;
import utilities.common_utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class SonarService {

    private final String sonarUrl =
            ConfigUtils.getProperty("sonar.url");

    private final String token =
            ConfigUtils.getProperty("sonar.token");

    public List<SonarIssue> getOpenIssues(String projectKey) {

        List<SonarIssue> issues = new ArrayList<>();
        int pageSize = 100;
        int pageIndex = 1;
        boolean hasMore = true;

        while (hasMore) {
            String api = sonarUrl +
                    "/api/issues/search?componentKeys=" +
                    projectKey +
                    "&resolved=false&ps=" + pageSize +
                    "&p=" + pageIndex;

            try (CloseableHttpClient client = HttpClients.createDefault()) {

                HttpGet get = new HttpGet(api);

                get.addHeader(
                        "Authorization",
                        "Basic " + java.util.Base64.getEncoder()
                                .encodeToString((token + ":").getBytes())
                );

                ClassicHttpResponse response =
                        client.executeOpen(null, get, null);

                JsonNode root =
                        JsonUtils.readJsonString(
                                new String(
                                        response.getEntity().getContent().readAllBytes()
                                )
                        );

                JsonNode array = root.get("issues");

                if (array != null && array.isArray() && array.size() > 0) {

                    for (JsonNode node : array) {

                        SonarIssue issue = new SonarIssue();

                        issue.setKey(node.path("key").asText());

                        issue.setRule(node.path("rule").asText());

                        issue.setSeverity(node.path("severity").asText());

                        issue.setMessage(node.path("message").asText());

                        issue.setComponent(node.path("component").asText());

                        issue.setFilePath(node.path("component").asText());

                        issue.setProject(node.path("project").asText());

                        issue.setType(node.path("type").asText());

                        issue.setLine(node.path("line").asInt());

                        issues.add(issue);
                    }
                    
                    if (array.size() < pageSize) {
                        hasMore = false;
                    } else {
                        pageIndex++;
                    }
                } else {
                    hasMore = false;
                }

            } catch (Exception e) {
                LogUtils.error("Unable to fetch Sonar Issues: " + e.getMessage());
                hasMore = false;
            }
        }
        
        LogUtils.info("Fetched " + issues.size() + " Sonar Issues total.");
        return issues;
    }

    public List<SonarIssue> getIssues() {

        String projectKey =
                ConfigUtils.getProperty("sonar.projectKey");
        LogUtils.info("Fetching Sonar issues for project: " + projectKey);
        return getOpenIssues(projectKey);
    }

}