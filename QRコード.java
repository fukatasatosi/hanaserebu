import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URL;
import javax.imageio.ImageIO;

// QRコード生成アプリ本体クラス
public class QRコード {
    // メイン処理: SwingでQRコード生成UIを表示
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("QRコード生成");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel();
            JTextField urlField = new JTextField(30);
            JButton generateButton = new JButton("QR生成");
            inputPanel.add(new JLabel("URL: "));
            inputPanel.add(urlField);
            inputPanel.add(generateButton);
            frame.add(inputPanel, BorderLayout.NORTH);

            JLabel qrLabel = new JLabel();
            frame.add(qrLabel, BorderLayout.CENTER);

            // QR生成ボタン押下時の処理
            generateButton.addActionListener(e -> {
                String inputUrl = urlField.getText();
                if (inputUrl.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "URLを入力してください");
                    return;
                }
                try {
                    String encoded = URLEncoder.encode(inputUrl, "UTF-8");
                    String apiUrl = "https://api.qrserver.com/v1/create-qr-code/?data=" + encoded;
                    BufferedImage img = ImageIO.read(new URL(apiUrl));
                    if (img == null)
                        throw new Exception("QRコード画像の取得に失敗しました");
                    qrLabel.setIcon(new ImageIcon(img));
                    frame.pack();
                } catch (UnsupportedEncodingException ex) {
                    JOptionPane.showMessageDialog(frame, "エンコーディングエラー: " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "QRコードの生成に失敗しました: " + ex.getMessage());
                }
            });

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
