import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

public class ジョーク {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ランダムジョーク");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JButton jokeButton = new JButton("ジョーク取得");
            JTextArea jokeArea = new JTextArea(4, 30);
            jokeArea.setLineWrap(true);
            jokeArea.setWrapStyleWord(true);
            jokeArea.setEditable(false);
            frame.add(jokeButton, BorderLayout.NORTH);
            frame.add(new JScrollPane(jokeArea), BorderLayout.CENTER);

            jokeButton.addActionListener(e -> {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://official-joke-api.appspot.com/jokes/random"))
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String body = response.body();
                    // JSONからsetupとpunchlineを抽出
                    Pattern setupPattern = Pattern.compile("\\\"setup\\\":\\\"(.*?)\\\"");
                    Pattern punchlinePattern = Pattern.compile("\\\"punchline\\\":\\\"(.*?)\\\"");
                    Matcher setupMatcher = setupPattern.matcher(body);
                    Matcher punchlineMatcher = punchlinePattern.matcher(body);
                    String setup = setupMatcher.find() ? setupMatcher.group(1) : "";
                    String punchline = punchlineMatcher.find() ? punchlineMatcher.group(1) : "";
                    if (setup.isEmpty() && punchline.isEmpty()) {
                        jokeArea.setText("ジョークの取得に失敗しました");
                    } else {
                        // LibreTranslate APIで日本語に翻訳
                        String jpSetup = translateToJapanese(setup);
                        String jpPunchline = translateToJapanese(punchline);
                        jokeArea.setText(jpSetup + "\n" + jpPunchline);
                    }
                } catch (Exception ex) {
                    jokeArea.setText("エラー: " + ex.getMessage());
                }
            });

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // LibreTranslate APIで英語→日本語翻訳
    private static String translateToJapanese(String text) {
        try {
            String json = String.format("{\"q\":\"%s\",\"source\":\"en\",\"target\":\"ja\"}", text.replace("\"", "\\\""));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://libretranslate.de/translate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = response.body();
            Pattern pattern = Pattern.compile("\\\"translatedText\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                String result = matcher.group(1);
                // Unicodeエスケープをデコード
                return decodeUnicode(result);
            } else {
                return text;
            }
        } catch (Exception e) {
            return text + "\n(翻訳失敗: " + e.getMessage() + ")";
        }
    }

    // Unicodeエスケープ(uXXXX)をデコード
    private static String decodeUnicode(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            char c = str.charAt(i++);
            if (c == '\\' && i < str.length() && str.charAt(i) == 'u') {
                i++;
                int code = Integer.parseInt(str.substring(i, i + 4), 16);
                sb.append((char) code);
                i += 4;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
