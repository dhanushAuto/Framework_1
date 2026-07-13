package ai.service;

import ai.generator.SonarFixGenerator;
import ai.model.SonarFix;
import ai.model.SonarIssue;

import java.util.List;
import java.util.concurrent.Callable;

public class SonarTask implements Callable<List<SonarFix>> {

    private final List<SonarIssue> issues;
    private final SonarFixGenerator generator;

    public SonarTask(List<SonarIssue> issues) {
        this.issues = issues;
        this.generator = new SonarFixGenerator();
    }

    @Override
    public List<SonarFix> call() {
        return generator.processFileIssues(issues);
    }
}
