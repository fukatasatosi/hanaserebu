import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ジョーク取得・翻訳アプリ本体クラス
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

    // icanhazdadjoke.com からジョークを取得
    public static Joke getDadJoke() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://icanhazdadjoke.com/"))
                .header("Accept", "application/json")
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Pattern pattern = Pattern.compile("\\\"joke\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            String joke = matcher.find() ? matcher.group(1) : "ジョークの取得に失敗しました";
            return new Joke(joke, "");
        } catch (Exception e) {
            return new Joke("ジョークの取得に失敗しました", "");
        }
    }

    // api.chucknorris.io からジョークを取得
    public static Joke getChuckNorrisJoke() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.chucknorris.io/jokes/random"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Pattern pattern = Pattern.compile("\\\"value\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            String joke = matcher.find() ? matcher.group(1) : "ジョークの取得に失敗しました";
            return new Joke(joke, "");
        } catch (Exception e) {
            return new Joke("ジョークの取得に失敗しました", "");
        }
    }

    // 3つのジョークAPIからランダムで1つ取得
    public static Joke getRandomJoke() {
        Random rand = new Random();
        int n = rand.nextInt(3);
        switch (n) {
            case 0:
                return getJoke();
            case 1:
                return getDadJoke();
            case 2:
                return getChuckNorrisJoke();
            default:
                return getJoke();
        }
    }
}
