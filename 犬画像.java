import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class 犬画像 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("犬画像ビューア");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            try {
                // Dog CEO APIからランダム犬画像を取得
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://dog.ceo/api/breeds/image/random"))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                // JSONからurlを抽出
                Pattern pattern = Pattern.compile("\\\"message\\\":\\\"(.*?)\\\"");
                Matcher matcher = pattern.matcher(body);
                String imageUrl = null;
                if (matcher.find()) {
                    imageUrl = matcher.group(1).replace("\\", ""); // バックスラッシュを除去
                } else {
                    throw new Exception("画像URLが取得できませんでした");
                }
                URL url = URI.create(imageUrl).toURL();
                BufferedImage img = ImageIO.read(url);
                if (img == null) throw new Exception("画像の読み込みに失敗しました");
                ImageIcon icon = new ImageIcon(img);
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
