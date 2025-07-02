import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class hanaserebu {
    private static final String API_KEY = "AIzaSyClEis8qc3GR4vzGh_QUL0WeiTMlUnYjQQ";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private static final String POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/pikachu";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("終了するには 'exit' と入力");
        while (true) {
            System.out.print("あなた: ");
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("チャットを終了します。");
                break;
            }
            String response = getGeminiResponse(userInput);
            System.out.println("ボット: " + response);
        }
        scanner.close();
    }

    private static String getGeminiResponse(String userInput) {
        try {
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            String jsonInput = "{" +
                "\"contents\": [{\"parts\": [{\"text\": \""+"Please Answer the following sentence as if you are "+POKEMON_URL +" "+ escapeJson(userInput) + "\"}]}]}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if( code != 200) {
                return "エラー: " + code + " - " + conn.getResponseMessage();
            }
            InputStream is = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();
            conn.disconnect();
            return parseGeminiResponse(response.toString());
        } catch (Exception e) {
            return "エラー: " + e.getMessage();
        }
    }

    private static String parseGeminiResponse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray candidates = obj.getJSONArray("candidates");
            if (candidates.length() == 0) return "返答が取得できませんでした。(candidatesなし)";
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            if (parts.length() == 0) return "返答が取得できませんでした。(partsなし)";
            JSONObject part = parts.getJSONObject(0);
            if (!part.has("text")) return "返答が取得できませんでした。(textなし)";
            return part.getString("text");
        } catch (Exception e) {
            return "返答が取得できませんでした。(JSONパースエラー): " + e.getMessage();
        }
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
