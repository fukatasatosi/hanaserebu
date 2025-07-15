package URL使用;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// データフェッチ専用クラス
public class QRCodeFetcher {
    // QRコードAPIのURLを返す
    public static String getQrCodeUrl(String data) {
        try {
            String encoded = URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
            return "https://api.qrserver.com/v1/create-qr-code/?data=" + encoded;
        } catch (Exception e) {
            return "QRコードURL生成エラー: " + e.getMessage();
        }
    }
}
