/**
 * 最終課題 8.CLI Task8_CLI_ver2
 * @author 241205159 増田侑真
 */

import javax.imageio.ImageIO;           // 画像の読み込みと書き出しのためのクラス
import java.awt.Color;                  // RGB形式で色を表現するクラス
import java.awt.Font;                   // テキストを描画する際に使用するフォント
import java.awt.Graphics2D;             // 図形やテキストを描画するためのクラス
import java.awt.image.BufferedImage;    // 画像データをバッファとして保持するクラス
import java.io.File;                    // ファイルやディレクトリのパスを指定するクラス
import java.io.FileInputStream;         // ファイルからバイト単位でデータを読み込むためのクラス
import java.io.BufferedInputStream;     // 入力ストリームをバッファリングし，効率的な読み込みを可能にするクラス
import java.io.IOException;             // I/O関連のエラーを処理するためのクラス
import java.util.Scanner;               // ユーザーからの入力を読み取るためのクラス
import java.util.Arrays;                // 配列操作

public class Task8_CLI_ver2 {

    public static void main(String[] args) {
        // コマンドライン引数でファイル名を指定する
        if (args.length < 2) {
            System.out.println("【 システムエラー 】                ");
            System.out.println("使用法: java Task8_CLI_ver2 sample.png output.png");
            return;
        }
        String inputFileName = args[0];
        String outputFileName = args[1];
        /* 
        String inputFileName = "sample.png";
        String outputFileName = "output.png";
        */

        // アスキーアートに使用する文字セット(背景色に応じて定義)
        char[] ASCII_CHARS_BLACK_BG = {'@', '%', '#', '*', '+', '=', '-', ':', '.', ' '};    // 黒背景用（暗い色ほど濃い文字）
        char[] ASCII_CHARS_WHITE_BG = {' ', '.', ':', '-', '=', '+', '*', '#', '%', '@'};    // 白背景用（暗い色ほど薄い文字）

        // PNGファイルの先頭にあるシグネチャ
        byte[] PNG_SIGNATURE = {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
        };
        
        File inputFile = new File(inputFileName);   // 画像ファイルのpath指定
        char[] chars = new char[0];                 // 背景色の格納用変数
        int choice;                                 // 背景色の選択用変数

        try (Scanner scanner = new Scanner(System.in)) {
            // ユーザーに背景色を選択させるループ
            while (true) {
                System.out.print("アスキーアートの背景色を選択してください (1:黒, 2:白, 3:終了) > ");
                choice = scanner.nextInt();
                if (choice == 1) {
                    chars = ASCII_CHARS_BLACK_BG;
                    break;
                } else if (choice == 2) {
                    chars = ASCII_CHARS_WHITE_BG;
                    break;
                } else if (choice == 3) {
                    System.out.println("プログラムを終了します。");
                    return;
                } else {
                    System.out.println("不正な入力です. 1, 2, または 3 を入力してください.");
                }
            }

            // 入力ファイルが存在するか確認
            if (!inputFile.exists()) {
                System.out.println("エラー: ファイルが見つかりません: " + inputFileName);
                return;
            }
            
            // ファイルのシグネチャを読み込み、PNGファイルか確認
            byte[] fileSignature = new byte[8];
            try (FileInputStream fis = new FileInputStream(inputFile);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                int bytesRead = bis.read(fileSignature);
				if (bytesRead != 8 || !Arrays.equals(fileSignature, PNG_SIGNATURE)) {
				    System.out.println("エラー: 指定されたファイルはPNG画像ではありません．");
				    return;
				}
            }

            
            // 画像ファイルを読み込み, BufferedImageオブジェクトとして取得
            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                System.out.println("エラー: 画像の読み込みに失敗しました. ");
                return;
            }

            int originalWidth = image.getWidth();       // 幅取得
            int originalHeight = image.getHeight();     // 高さ取得
            System.out.println("変換前の画像サイズ: " + originalWidth + "x" + originalHeight);
            
            // アスキーアートを格納するStringBuilderを初期化
            StringBuilder asciiArt = new StringBuilder();
            // 元の画像を2ピクセルずつ縦にスキャンし, アスキーアートに変換
            for (int y = 0; y < originalHeight; y += 2) {
                for (int x = 0; x < originalWidth; x++) {
                    // 各ピクセルの色を取得
                    Color color = new Color(image.getRGB(x, y));
                    int r = color.getRed();     // 赤色
                    int g = color.getGreen();   // 緑色
                    int b = color.getBlue();    // 青色
                    
                    // RGB値から輝度（グレースケール値）を計算
                    int gray = (r * 30 + g * 59 + b * 11) / 100;
                    
                    // 輝度をアスキー文字配列のインデックスに変換
                    int index = (int)(gray / 256.0 * chars.length);
                    // インデックスが配列の範囲を超えないように調整
                    if (index >= chars.length) {
                        index = chars.length - 1;
                    }
                    // 計算したアスキー文字を追加
                    asciiArt.append(chars[index]);
                }
                // 1行の処理が終わったら改行を追加
                asciiArt.append("\n");
            }

            // アスキーアートのテキストを最終的な文字列に変換し，行ごとに分割
            String finalAsciiArt = asciiArt.toString();
            String[] lines = finalAsciiArt.split("\n");

            // 描画に使用するフォントを作成
            Font font = new Font("Monospaced", Font.PLAIN, 15);

            // フォントのメトリクス（サイズ情報）を取得するための一時的な画像を作成
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D tempGraphics = tempImage.createGraphics();
            tempGraphics.setFont(font);
            int charWidth = tempGraphics.getFontMetrics().charWidth(' '); // 幅, フォントサイズ15の場合 8ピクセル
            int charHeight = tempGraphics.getFontMetrics().getHeight();      // 高さ, フォントサイズ15の場合 21ピクセル
            tempGraphics.dispose(); 

            /* 最終的な画像のサイズをアスキーアートの文字数とフォントサイズに基づいて計算
             * 尚, modify_1変数,　modify_2変数ははsample.png専用の補正値である. 画像の縦横を１：１にするため.     
             */
            double modify_1 = 1.0;
            double modify_2 = 1.0;
            
            // sample.pngのサイズに合わせた補正値を設定
            if (originalWidth == 1024 && originalHeight == 1024) {
                modify_1 = 16.0 / 21.0;
                modify_2 = 8.0 / 21.0; 
            }

            int imageWidth = originalWidth * charWidth;
            int imageHeight = (int) (originalHeight / 2.0  * charHeight  * modify_1);
            System.out.println("変換後の画像サイズ: " + imageWidth + "x" + imageHeight);

            // 新しい画像を生成
            BufferedImage asciiImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = asciiImage.createGraphics();

            // 背景色と文字色を設定
            Color bgColor;
            Color fgColor;
            if (choice == 1) {
                bgColor = Color.BLACK;
                fgColor = Color.WHITE;
            } else {
                bgColor = Color.WHITE;
                fgColor = Color.BLACK;
            }
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, imageWidth, imageHeight);
            g2d.setColor(fgColor);
            g2d.setFont(font);


            // アスキーアートの各行を画像に描画
            for (int i = 0; i < lines.length; i++) {
                g2d.drawString(lines[i], 0, (int)((i + 1) * charHeight * modify_2) * 2); 
                // 縦は横に比べて2ピクセルずつのため2倍して比率を保つ
            }

            // グラフィックスリソースを解放
            g2d.dispose();

            // 画像をPNGファイルとして保存
            ImageIO.write(asciiImage, "png", new File(outputFileName));
            System.out.println(outputFileName + " として正常に保存されました．");
        
        } catch (IOException e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
