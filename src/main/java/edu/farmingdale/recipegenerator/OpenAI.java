package edu.farmingdale.recipegenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OpenAI {


    private static final String API_KEY = System.getenv("key");


    /**
     * Sends a prompt to the OpenAI API and retrieves a text response.
     *
     * @param prompt The text prompt to send to the model.
     * @param preferences The preferences associated with the user.
     * @return The response text from the model.
     * @throws Exception If there is an error with the API request.
     */
    public static String getTextResponse(String prompt, String preferences) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject body = new JSONObject();

        // Customizing prompt based on preferences
        String fullPrompt = "You are a professional chef with years of experience in cooking. Based on the preferences: " + preferences +
                ", and the following ingredients: " + prompt + ", generate a detailed recipe with clear instructions. " +
                "Your recipe should include the following sections: Ingredients, Preparation, Cooking Steps, Tips, and Serving Suggestions.";

        body.put("model", "gpt-3.5-turbo");
        body.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a professional chef with expertise in creating detailed and structured recipes."))
                .put(new JSONObject().put("role", "user").put("content", fullPrompt))
        );
        body.put("max_tokens", 2000);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject responseBody = new JSONObject(response.body());

        //debugging AI message
        //        System.out.println("API Response: " + response.body());
        //        System.out.println("API Key from Environment: " + System.getenv("APIKEY"));

        if (responseBody.has("choices")) {
            String text = responseBody.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            return text.trim();
        } else {
            throw new Exception("Response does not contain 'choices' field");
        }
    }
    public static List<String> getDefaultIngredients() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject body = new JSONObject();

        String prompt = "Generate a list of 20 different, commonly available cooking ingredients. Only list the ingredient names, one per line, without numbering or extra text.";

        body.put("model", "gpt-3.5-turbo");
        body.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "user").put("content", prompt))
        );
        body.put("max_tokens", 500);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject responseBody = new JSONObject(response.body());

        if (responseBody.has("choices")) {
            String text = responseBody.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            // Split the result into a list
            return List.of(text.split("\\r?\\n"));
        } else {
            throw new Exception("OpenAI response missing 'choices'");
        }
    }
}
