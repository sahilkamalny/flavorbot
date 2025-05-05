package edu.farmingdale.recipegenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



/**
 * Utility class to interact with OpenAI's API.
 */
public class OpenAI {
    private static final String API_KEY = System.getenv("APIKEY");

    /**
     * Sends a prompt to the OpenAI API and retrieves a text response.
     *
     * @param prompt The text prompt to send to the model.
     * @return The response text from the model.
     * @throws Exception If there is an error with the API request.
     */
    public static String getTextResponse(String prompt) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject body = new JSONObject();
        body.put("model","gpt-3.5-turbo");
        body.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                .put(new JSONObject().put("role", "user").put("content", prompt))
        );
        body.put("max_tokens", 2000);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //debugging AI message
//        System.out.println("API Response: " + response.body());
//        System.out.println("API Key from Environment: " + System.getenv("APIKEY"));

        JSONObject responseBody = new JSONObject(response.body());

        if (responseBody.has("choices")) {
            String text = responseBody.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            return text.trim();
        } else {
            throw new JSONException("Response does not contain 'choices' field");
        }
    }

}
