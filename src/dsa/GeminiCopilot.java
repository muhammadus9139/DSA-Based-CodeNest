package dsa;

import data.ApiConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeminiCopilot {

    // Get real-time suggestions for a line of code
    public String getSuggestion(String codeLine) {
        String prompt = """
                You are a Java code assistant. Analyze this line of code and give ONE short helpful suggestion (max 15 words):
                
                Code: %s
                
                Reply with only the suggestion, no explanation, no markdown.
                """.formatted(codeLine);

        return callGemini(prompt);
    }

    // Full code review
    public String reviewCode(String fullCode) {
        String prompt = """
                You are a Java code reviewer. Review this code and list issues found.
                Format each issue on a new line starting with ❌ for errors, ⚠️ for warnings, ✅ for good practices.
                Keep each point under 10 words. Maximum 5 points.
                
                Code:
                %s
                """.formatted(fullCode);

        return callGemini(prompt);
    }

    // Call Gemini API
    private String callGemini(String prompt) {
        try {
            URL url = new URL(ApiConfig.GEMINI_API_URL + "?key=" + ApiConfig.GEMINI_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            // Build request body
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);

            JsonArray parts = new JsonArray();
            parts.add(textPart);

            JsonObject content = new JsonObject();
            content.add("parts", parts);

            JsonArray contents = new JsonArray();
            contents.add(content);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contents);

            // Send request
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse response
                JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                return jsonResponse
                        .getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
            } else {
                return "❌ API Error: " + responseCode;
            }

        } catch (Exception e) {
            return "❌ Connection error: " + e.getMessage();
        }
    }
}