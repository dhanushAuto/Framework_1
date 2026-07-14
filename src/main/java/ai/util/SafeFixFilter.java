package ai.util;

import ai.model.SonarFix;
import ai.model.SonarIssue;
import utilities.api.ConfigUtils;
import utilities.common_utils.LogUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Central "safety gate" for the AI Sonar Auto-Fix pipeline.
 * <p>
 * Only issues whose Sonar rule key is on the safe whitelist below are ever
 * sent to the AI for a fix, and only fixes that come back with acceptable
 * confidence/risk are ever applied to disk. This keeps the auto-fixer from
 * touching anything that could change runtime behaviour (security
 * vulnerabilities, concurrency issues, business logic, etc.).
 * <p>
 * The whitelist can be extended without code changes via the
 * {@code sonar.autofix.extra.safe.rules} property (comma separated rule
 * keys) in config.properties.txt.
 */
public final class SafeFixFilter {

    private static final String UNKNOWN = "unknown";

    // Conservative, behaviour-preserving Sonar/Java rule keys only.
    private static final Set<String> SAFE_RULE_KEYS = new HashSet<>(Arrays.asList(
            "java:S1128",  // Unused imports should be removed
            "java:S1481",  // Unused local variables should be removed
            "java:S1854",  // Unused assignments should be removed
            "java:S1068",  // Unused private fields should be removed
            "java:S1144",  // Unused private methods should be removed
            "java:S1118",  // Utility classes should not have public constructors
            "java:S1116",  // Empty statements should be removed
            "java:S1121",  // Assignments should not be made from within sub-expressions
            "java:S1488",  // Local variables should not be declared and then immediately returned
            "java:S1155",  // Collection.isEmpty() should be used instead of size checks
            "java:S1066",  // Collapsible "if" statements should be merged
            "java:S1602",  // Lambdas containing only one statement should not nest a block
            "java:S1132",  // String literals should be on the left side when checking equality
            "java:S1125",  // Boolean literals should not be redundant
            "java:S1149",  // StringBuffer/StringBuilder used only locally can be a String
            "java:S1135",  // Track uses of task tags (documentation only, safe no-op)
            "java:S125"    // Sections of code should not be commented out
    ));

    // Rule keys that are explicitly excluded even if they somehow appear in
    // the whitelist above - anything that could alter behaviour or security.
    private static final Set<String> ALWAYS_UNSAFE = new HashSet<>(Arrays.asList(
            "java:S2077", // SQL injection
            "java:S4830", // Certificate validation
            "java:S2245", // Weak PRNG
            "java:S5443",
            "java:S3649"
    ));

    private static final int MIN_CONFIDENCE = Integer.parseInt(
            ConfigUtils.getProperty("sonar.autofix.min.confidence", "70"));

    private SafeFixFilter() {
    }

    /** Whether this issue is even eligible to be sent to the AI for a fix. */
    public static boolean isSafeIssue(SonarIssue issue) {
        if (issue == null || issue.getRule() == null) {
            return false;
        }

        String rule = issue.getRule();

        if (ALWAYS_UNSAFE.contains(rule)) {
            return false;
        }

        if ("VULNERABILITY".equalsIgnoreCase(issue.getType())) {
            return false;
        }

        if (SAFE_RULE_KEYS.contains(rule)) {
            return true;
        }

        String extra = ConfigUtils.getProperty("sonar.autofix.extra.safe.rules", "");
        if (!extra.isBlank()) {
            for (String extraRule : extra.split(",")) {
                if (rule.equalsIgnoreCase(extraRule.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Whether an AI-generated fix is confident/low-risk enough to actually apply. */
    public static boolean isSafeFix(SonarFix fix) {
        if (fix == null || !fix.isFixed() || fix.getFixedCode() == null || fix.getFixedCode().isBlank()) {
            return false;
        }

        if ("High".equalsIgnoreCase(fix.getRiskRating())) {
            LogUtils.warn("Rejecting fix (High risk): " + (fix.getIssue() != null ? fix.getIssue().getRule() : UNKNOWN));
            return false;
        }

        if (fix.getConfidence() < MIN_CONFIDENCE) {
            LogUtils.warn("Rejecting fix (confidence " + fix.getConfidence() + "% < " + MIN_CONFIDENCE + "%): "
                    + (fix.getIssue() != null ? fix.getIssue().getRule() : UNKNOWN));
            return false;
        }

        if (fix.getStartLine() < 0 || fix.getEndLine() < 0) {
            LogUtils.warn("Rejecting fix (no valid line range resolved): "
                    + (fix.getIssue() != null ? fix.getIssue().getRule() : UNKNOWN));
            return false;
        }

        return true;
    }
}
