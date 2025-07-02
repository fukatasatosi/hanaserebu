import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class 猫画像 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("猫画像ビューア２");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            try {
                // The Cat APIからランダム画像を取得
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.thecatapi.com/v1/images/search"))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                // JSONからurlを抽出
                Pattern pattern = Pattern.compile("\\\"url\\\":\\\"(.*?)\\\"");
                Matcher matcher = pattern.matcher(body);
                String imageUrl = null;
                if (matcher.find()) {
                    imageUrl = matcher.group(1);
                } else {
                    throw new Exception("画像URLが取得できませんでした");
                }
                URL url = URI.create(imageUrl).toURL();
                ImageIcon icon = new ImageIcon(url);
                JLabel label = new JLabel(icon);
                frame.getContentPane().add(label, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "画像の読み込みに失敗しました: " + e.getMessage());
            }
        });
    }
}
