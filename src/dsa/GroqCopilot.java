package dsa;


import data.ApiConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.ApiConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;

public class GroqCopilot {

    // Get suggestion for a line
    public String getSuggestion(String codeLine) {
        String prompt = "You are a Java code assistant. Analyze this line and give ONE short helpful suggestion (max 15 words):\n\nCode: " + codeLine + "\n\nReply with only the suggestion, no explanation.";
        return callGroq(prompt);
    }

    // Full code review
    public String reviewCode(String fullCode) {
        String prompt = """
                You are a Java code reviewer. Review this code and list issues.
                Format each issue starting with ❌ for errors, ⚠️ for warnings, ✅ for good practices.
                Keep each point under 10 words. Maximum 5 points.
                
                Code:
                """ + fullCode;
        return callGroq(prompt);
    }

    // Call Groq API
    private String callGroq(String prompt) {
        try {
            URI uri = new URI(ApiConfig.GROQ_API_URL);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + ApiConfig.GROQ_API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            // Build request
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);

            JsonArray messages = new JsonArray();
            messages.add(message);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "llama-3.1-8b-instant");
            requestBody.add("messages", messages);
            requestBody.addProperty("max_tokens", 150);
            requestBody.addProperty("temperature", 0.3);

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

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

                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                return json.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString().trim();
            } else {
                return "❌ API Error: " + responseCode;
            }

        } catch (Exception e) {
            return "❌ Connection error: " + e.getMessage();
        }
    }
}