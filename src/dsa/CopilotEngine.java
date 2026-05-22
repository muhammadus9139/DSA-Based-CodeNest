package dsa;

import java.util.HashMap;

public class CopilotEngine {

    private HashMap<String, String> rules = new HashMap<>();

    public CopilotEngine() {
        loadRules();
    }

    // Load all rules
    private void loadRules() {
        // Loop suggestions
        rules.put("for(", "💡 Consider using enhanced for loop if iterating over array/list");
        rules.put("for (", "💡 Consider using enhanced for loop if iterating over array/list");
        rules.put("while(", "💡 Make sure loop has proper exit condition to avoid infinite loop");
        rules.put("while (", "💡 Make sure loop has proper exit condition to avoid infinite loop");

        // Data structure suggestions
        rules.put("ArrayList", "💡 Consider LinkedList if you need frequent insertions/deletions");
        rules.put("LinkedList", "💡 Consider ArrayList if you need frequent random access");
        rules.put("HashMap", "💡 Consider TreeMap if you need sorted keys");
        rules.put("TreeMap", "💡 Consider HashMap for O(1) lookup if order not needed");
        rules.put("Stack", "💡 Consider Deque as a more complete Stack implementation");
        rules.put("Array", "💡 Consider ArrayList for dynamic sizing");

        // Exception handling
        rules.put("FileReader", "💡 Wrap FileReader in try-catch for IOException");
        rules.put("FileWriter", "💡 Wrap FileWriter in try-catch for IOException");
        rules.put("Integer.parseInt", "💡 Wrap parseInt in try-catch for NumberFormatException");
        rules.put("Double.parseDouble", "💡 Wrap parseDouble in try-catch for NumberFormatException");

        // OOP suggestions
        rules.put("public class", "💡 Make sure class follows Single Responsibility Principle");
        rules.put("extends", "💡 Favor composition over inheritance when possible");
        rules.put("static", "💡 Avoid overusing static — it makes testing harder");

        // Sorting
        rules.put("Collections.sort", "💡 Consider using Stream API for more functional sorting");
        rules.put("Arrays.sort", "💡 Arrays.sort uses dual-pivot quicksort — O(n log n)");

        // Null safety
        rules.put("= null", "💡 Consider using Optional<> to avoid NullPointerException");
        rules.put("== null", "💡 Use Objects.isNull() for cleaner null checks");

        // Recursion
        rules.put("return ", "💡 If recursive, ensure base case is defined to avoid StackOverflow");
    }

    // Get suggestions for given code line
    public SuggestionQueue getSuggestions(String codeLine) {
        SuggestionQueue suggestions = new SuggestionQueue();
        for (String keyword : rules.keySet()) {
            if (codeLine.contains(keyword)) {
                suggestions.enqueue(rules.get(keyword));
            }
        }
        return suggestions;
    }

    // Full code review
    public SuggestionQueue reviewCode(String fullCode) {
        SuggestionQueue issues = new SuggestionQueue();
        String[] lines = fullCode.split("\n");

        // Check nested loops
        int loopCount = 0;
        for (String line : lines) {
            if (line.contains("for(") || line.contains("for (") ||
                    line.contains("while(") || line.contains("while (")) {
                loopCount++;
            }
        }
        if (loopCount >= 2) {
            issues.enqueue("❌ Nested/multiple loops detected → Possible O(n²) complexity");
        }

        // Check try-catch
        boolean hasTryCatch = fullCode.contains("try") && fullCode.contains("catch");
        boolean hasRiskyOps = fullCode.contains("FileReader") || fullCode.contains("parseInt") ||
                fullCode.contains("FileWriter") || fullCode.contains("parseDouble");
        if (hasRiskyOps && !hasTryCatch) {
            issues.enqueue("❌ Risky operations found without try-catch block");
        }

        // Check variable naming
        int badNames = 0;
        for (String line : lines) {
            if (line.matches(".*\\bint [a-z]\\b.*") ||
                    line.matches(".*\\bString [a-z]\\b.*") ||
                    line.matches(".*\\bdouble [a-z]\\b.*")) {
                badNames++;
            }
        }
        if (badNames > 0) {
            issues.enqueue("❌ Single-letter variable names found → Use meaningful names");
        }

        // Check null usage
        if (fullCode.contains("= null") || fullCode.contains("== null")) {
            issues.enqueue("⚠️ Null references found → Consider using Optional<>");
        }

        // Check static overuse
        long staticCount = java.util.Arrays.stream(lines)
                .filter(l -> l.contains("static")).count();
        if (staticCount > 3) {
            issues.enqueue("⚠️ Too many static members → Reconsider design");
        }

        // Check empty catch blocks
        if (fullCode.contains("catch") && fullCode.contains("{ }") ||
                fullCode.contains("catch") && fullCode.contains("{}")) {
            issues.enqueue("❌ Empty catch block found → Always handle exceptions properly");
        }

        // Positive feedback
        if (issues.isEmpty()) {
            issues.enqueue("✅ No major issues found! Code looks clean.");
        }

        return issues;
    }
}