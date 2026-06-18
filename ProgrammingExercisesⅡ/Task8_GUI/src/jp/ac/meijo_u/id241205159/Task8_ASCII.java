/**
 * 最終課題 8.GUI Task8_ASCII
 * @author 241205159 増田侑真
 */

package jp.ac.meijo_u.id241205159;

import javafx.scene.canvas.Canvas;          // JavaFXの描画領域
import javafx.scene.canvas.GraphicsContext; // Canvasへの描画
import javafx.scene.paint.Color;            // 色
import javafx.scene.text.Font;              // フォント
import javafx.scene.text.Text;              // フォントの幅と高さを測定
import java.awt.image.BufferedImage;        // 画像データを扱うためのクラス

public class Task8_ASCII {

    private char[] ASCII_CHARS_BLACK_BG = {'@', '%', '#', '*', '+', '=', '-', ':', '.', ' '}; // 黒背景用のアスキーアート文字配列
    private char[] ASCII_CHARS_WHITE_BG = {' ', '.', ':', '-', '=', '+', '*', '#', '%', '@'}; // 白背景用のアスキーアート文字配列

    public void drawAsciiArt(BufferedImage image, Canvas canvas, boolean isWhiteBackground) {
        // 背景色と文字色，使用する文字配列を設定
        Color bgColor;
        Color fgColor;
        char[] chars;
        if (isWhiteBackground) {
            bgColor = Color.WHITE;
            fgColor = Color.BLACK;
            chars = ASCII_CHARS_WHITE_BG;
        } else {
            bgColor = Color.BLACK;
            fgColor = Color.WHITE;
            chars = ASCII_CHARS_BLACK_BG;
        }

        // Canvasの描画コンテキストを取得
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 元画像の幅と高さを取得
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // アスキーアート文字列を効率的に構築するためのStringBuilder
        StringBuilder asciiArtBuilder = new StringBuilder();

        int sampleX = 2; // 横方向のサンプリング間隔
        int sampleY = 4; // 縦方向のサンプリング間隔

        for (int y = 0; y < originalHeight; y += sampleY) {
            for (int x = 0; x < originalWidth; x += sampleX) {
                // 指定座標のピクセルのRGB値を取得
                java.awt.Color color = new java.awt.Color(image.getRGB(x, y));
                // RGB各成分を変数に格納
                int r = color.getRed();     // 赤色
                int g = color.getGreen();   // 緑色
                int b = color.getBlue();    // 青色

                // RGB値から輝度を計算
                int gray = (r * 30 + g * 59 + b * 11) / 100;

                // 輝度をアスキー文字配列のインデックスに変換
                int index = (int) (gray / 256.0 * chars.length);

                // 配列の範囲を超えないようにインデックスを調整
                if (index >= chars.length) {
                    index = chars.length - 1;
                }

                // 輝度に対応するアスキー文字を選択し，文字列に追加
                char asciiChar = chars[index];
                asciiArtBuilder.append(asciiChar);
            }
            asciiArtBuilder.append("\n"); // 1行の終わりに改行を追加
        }

        // Canvas全体を最初にクリア
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Font font = Font.font("Monospaced", 15);
        gc.setFont(font);

        // 文字の幅と高さを取得して，Canvasのサイズを決定
        Text textMeasure = new Text(" ");
        textMeasure.setFont(font);
        double charWidth = textMeasure.getLayoutBounds().getWidth();
        double charHeight = textMeasure.getLayoutBounds().getHeight();

        // 画像のサンプリング間隔に合わせてCanvasのサイズを計算し，設定
        double canvasWidth = (double) (originalWidth / sampleX) * charWidth;
        double canvasHeight = (double) (originalHeight / sampleY) * charHeight;
        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        // 背景色でCanvasを塗りつぶし
        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // 文字色を設定
        gc.setFill(fgColor);
        gc.setFont(font);

        // 生成した文字列を1行ずつ描画
        String[] asciiLines = asciiArtBuilder.toString().split("\n");
        for (int i = 0; i < asciiLines.length; i++) {
            // 各行をCanvasに描画
            gc.fillText(asciiLines[i], 0, (double) (i + 1) * charHeight);
        }
    }
}
