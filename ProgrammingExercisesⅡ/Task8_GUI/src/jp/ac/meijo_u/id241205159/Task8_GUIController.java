/**
 * 最終課題 8.GUI Task8_GUIController
 * @author 241205159 増田侑真
 */

package jp.ac.meijo_u.id241205159;

import javafx.fxml.FXML;                    // FXMLファイルとJavaコードを連携するためのクラス
import javafx.scene.canvas.Canvas;          // 自由な描画が可能な領域
import javafx.scene.control.Alert;          // アラートダイアログ
import javafx.scene.control.RadioButton;    // ラジオボタン（複数から一つを選択）
import javafx.scene.control.ToggleGroup;    // ラジオボタンをグループ化するクラス
import javafx.scene.input.MouseEvent;       // マウスイベント（クリック、ドラッグなど）
import javafx.scene.input.ScrollEvent;      // マウススクロールイベント
import javafx.stage.FileChooser;            // ファイル選択ダイアログ
import javafx.stage.Window;                 // ダイアログを表示するウィンドウ
import javafx.event.ActionEvent;            // アクションイベント（ボタンクリックなど）
import javax.imageio.ImageIO;               // 画像の読み書き
import java.awt.image.BufferedImage;        // メモリ上で画像を扱うクラス
import java.io.File;                        // ファイルやディレクトリの抽象パス名
import java.io.FileInputStream;             // ファイルからのバイト入力ストリーム
import java.io.BufferedInputStream;         // バッファリングされた入力ストリーム
import java.io.IOException;                 // 入出力操作におけるエラー
import java.util.Arrays;                    // 配列操作

public class Task8_GUIController {
    @FXML private Canvas asciiCanvas;                       // アスキーアートを描画するキャンバス
    @FXML private RadioButton whiteBackgroundRadioButton;   // 白背景を選択するラジオボタン
    @FXML private RadioButton blackBackgroundRadioButton;   // 黒背景を選択するラジオボタン
    @FXML private ToggleGroup backgroundToggleGroup;        // ラジオボタンのグループ

    private double zoom = 1.0;                                            // 拡大・縮小率を保持 (初期値1.0)
    private double lastX, lastY;                                          // ドラッグ開始時のマウス座標を保持
    private BufferedImage currentImage;                                   // 現在の画像を保持する変数
    private Task8_ASCII ascii = new Task8_ASCII();   // アスキーアート変換クラスのインスタンス

    // PNGファイルのシグネチャ
    private byte[] PNG_SIGNATURE = {
        (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
        (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
    };
    
    // FXMLがロードされた後に自動的に呼び出される
    @FXML
    public void initialize() { 
        asciiCanvas.setOnScroll(event -> handleScroll(event));              // マウスのスクロールされたとき
        asciiCanvas.setOnMousePressed(event -> handleMousePressed(event));  // マウスボタンが押されたとき
        asciiCanvas.setOnMouseDragged(event -> handleMouseDragged(event));  // マウスがドラッグされたとき
        
        // 白背景ラジオボタン
        whiteBackgroundRadioButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (currentImage != null) {
                drawAsciiArt();
            }
        });
        
        // 黒背景ラジオボタン
        blackBackgroundRadioButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (currentImage != null) {
                drawAsciiArt();
            }
        });
    }
    
    // マウススクロールの処理
    private void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {
            double delta = event.getDeltaY();
            if (delta > 0) {
                zoom *= 1.5; // ズームイン
            } else {
                zoom /= 1.5; // ズームアウト
            }
            // ズームを0.1倍から10.0倍の間に制限
            if (zoom < 0.1) zoom = 0.1;
            if (zoom > 10.0) zoom = 10.0;

            // Canvasのスケール（拡大・縮小）を更新
            asciiCanvas.setScaleX(zoom);
            asciiCanvas.setScaleY(zoom);
            event.consume();
        }
    }
    
    // マウスボタンの処理
    private void handleMousePressed(MouseEvent event) {
        // ドラッグ開始時のマウス座標を記録
        lastX = event.getX();
        lastY = event.getY();
    }

    // マウスドラッグの処理
    private void handleMouseDragged(MouseEvent event) {
        // マウスの移動量（現在の座標 - 記録した座標）を計算
        double deltaX = event.getX() - lastX;
        double deltaY = event.getY() - lastY;
        
        // Canvasを移動量に移動
        asciiCanvas.setTranslateX(asciiCanvas.getTranslateX() + deltaX);
        asciiCanvas.setTranslateY(asciiCanvas.getTranslateY() + deltaY);
    }

    // メニューの「開く」ボタンが押された時の処理
    @FXML
    private void handleOpenFile(ActionEvent event) {
        // ファイルチューザー（ファイル選択ダイアログ）を作成
        Window window = asciiCanvas.getScene().getWindow(); // ダイアログを表示するウィンドウを取得
        FileChooser fileChooser = new FileChooser();        // ファイル選択ダイアログのインスタンス
        fileChooser.setTitle("PNGファイルを選択");     // ダイアログのタイトル
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png")); // PNGファイルのみにフィルタ
        File file = fileChooser.showOpenDialog(window);     // ダイアログを表示し, 選択されたファイルを取得

        if (file != null) {
            try {
                // ファイルがPNGかどうかをシグネチャで検証
                if (!isPngFile(file)) {
                    showAlert(Alert.AlertType.ERROR, "読み込み失敗", "指定されたファイルは有効なPNG画像ではありません.");
                    return;
                }
                // ファイルから画像を読み込み, BufferedImageとして保持
                currentImage = ImageIO.read(file);
                if (currentImage == null) {
                    showAlert(Alert.AlertType.ERROR, "読み込み失敗", "画像の読み込みに失敗しました.");
                    return;
                }
                // Canvasにアスキーアートを描画
                drawAsciiArt();
                // 読み込み成功のアラートを表示
                showAlert(Alert.AlertType.INFORMATION, "読み込み成功", file.getName() + " を読み込み，アスキーアートに変換しました．");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "読み込み失敗", "ファイルの読み込み中にエラーが発生しました．");
            }
        }
    }

    // アスキーアートの描画処理
    private void drawAsciiArt() {
        boolean isWhiteBackground = whiteBackgroundRadioButton.isSelected(); // 現在選択されている背景色を取得
        // アスキーアートを描画
        ascii.drawAsciiArt(currentImage, asciiCanvas, isWhiteBackground);

        // ズームと移動を初期状態にリセット
        zoom = getZoom();
        asciiCanvas.setScaleX(zoom);
        asciiCanvas.setScaleY(zoom);
        asciiCanvas.setTranslateX(0);
        asciiCanvas.setTranslateY(0);
    }

    // ファイルがPNG形式であるかを判定
    private boolean isPngFile(File file) throws IOException {
        byte[] fileSignature = new byte[8]; // ファイルの先頭8バイトを格納する配列
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            int bytesRead = bis.read(fileSignature); // ファイルから8バイト読み込む
            // 読み込んだバイト数が8であり、かつPNGシグネチャと一致するかを判定
            return bytesRead == 8 && Arrays.equals(fileSignature, PNG_SIGNATURE);
        }
    }
    
    // アラートダイアログを表示
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);     // アラートダイアログのインスタンス
        alert.setTitle(title);                  // ダイアログのタイトル
        alert.setHeaderText(null);   // ヘッダーテキスト
        alert.setContentText(message);          // ダイアログのメッセージ
        alert.showAndWait();
    }

    private double getZoom(){
        return this.zoom;
    }
}
