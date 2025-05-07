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

/**
 * The OpenAI class interacts with the OpenAI API to generate text responses for recipes
 * and retrieve lists of default ingredients. It is used to send user prompts and receive
 * detailed responses in the form of recipes or ingredient lists.
 */
public class OpenAI {

    // API key stored as an environment variable
    private static final String API_KEY = System.getenv("key");

    /**
     * Sends a prompt to the OpenAI API to generate a detailed recipe based on the ingredients and user preferences.
     * The response includes sections like Ingredients, Preparation, Cooking Steps, Tips, and Serving Suggestions.
     *
     * @param prompt The list of ingredients provided by the user.
     * @param preferences The preferences or dietary restrictions associated with the user.
     * @return A detailed recipe text, including ingredients, steps, tips, and serving suggestions.
     * @throws Exception If there is an issue with the API request or response.
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
    /**
     * Sends a prompt to the OpenAI API to generate a list of 20 commonly available cooking ingredients.
     * The ingredients are returned as a list of strings, one per line.
     *
     * @return A list of 20 commonly available cooking ingredients.
     * @throws Exception If there is an issue with the API request or response.
     */
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
