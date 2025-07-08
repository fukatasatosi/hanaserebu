import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ジョーク {
    public static class Joke {
        public final String setup;
        public final String punchline;
        public Joke(String setup, String punchline) {
            this.setup = setup;
            this.punchline = punchline;
        }
    }

    // ジョークAPIから英語のジョークを取得
    public static Joke getJoke() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://official-joke-api.appspot.com/jokes/random"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Pattern setupPattern = Pattern.compile("\"setup\":\"(.*?)\"");
            Pattern punchlinePattern = Pattern.compile("\"punchline\":\"(.*?)\"");
            Matcher setupMatcher = setupPattern.matcher(body);
            Matcher punchlineMatcher = punchlinePattern.matcher(body);
            String setup = setupMatcher.find() ? setupMatcher.group(1) : "";
            String punchline = punchlineMatcher.find() ? punchlineMatcher.group(1) : "";
            return new Joke(setup, punchline);
        } catch (Exception e) {
            return new Joke("ジョークの取得に失敗しました", "");
        }
    }
}
