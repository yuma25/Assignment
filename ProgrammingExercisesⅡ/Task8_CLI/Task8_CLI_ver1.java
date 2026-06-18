/**
 * 最終課題 8.CLI Task8_CLI_ver1
 * @author 241205159 増田侑真
 */

import javax.imageio.ImageIO;             // 画像ファイルの読み込みと書き出しをするクラス
import java.awt.Color;                    // ピクセルの色情報を扱うクラス
import java.awt.image.BufferedImage;      // 画像データをメモリ上で表現するクラス
import java.io.File;                      // ファイルやディレクトリのパスを指定するクラス
import java.io.FileInputStream;           // ファイルからバイトデータを読み込むためのクラス
import java.io.BufferedInputStream;       // 入力ストリームの効率を向上させるためのバッファリングクラス
import java.io.IOException;               // 入出力操作中に発生する例外を処理するクラス
import java.util.Arrays;                  // 配列操作
import java.util.Scanner;                 // ユーザーからの入力を読み取るためのクラス

public class Task8_CLI_ver1 {
    public static void main(String[] args) {
        // コマンドライン引数が指定されているか確認
        if (args.length < 1) {
            System.out.println("【 システムエラー 】");
            System.out.println("使用法: java Task8_CLI_ver1 sample.png");
            return;
        }

        // コマンドライン引数からファイル名を取得
        String inputFileName = args[0];

        // 黒背景用と白背景用のアスキー文字配列を定義
        char[] ASCII_CHARS_BLACK_BG = {'@', '%', '#', '*', '+', '=', '-', ':', '.', ' '};
        char[] ASCII_CHARS_WHITE_BG = {' ', '.', ':', '-', '=', '+', '*', '#', '%', '@'};

        // PNGファイルのシグネチャを定義
        byte[] PNG_SIGNATURE = {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
        };
        
        File inputFile = new File(inputFileName);
        char[] chars = null;    // アスキー文字配列の選択用
        int choice;             // ユーザーの選択

        try (Scanner scanner = new Scanner(System.in)) {
            // 背景色を選択するループ
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
                    System.out.println("プログラムを終了します. ");
                    return;
                } else {
                    System.out.println("不正な入力です. 1, 2, または 3 を入力してください. ");
                }
            }

            // ファイルの存在確認
            if (!inputFile.exists()) {
                System.out.println("エラー: ファイルが見つかりません: " + inputFileName);
                return;
            }
            
            // ファイルがPNG画像であるかシグネチャで確認
            byte[] fileSignature = new byte[8];
            try (FileInputStream fis = new FileInputStream(inputFile);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                int bytesRead = bis.read(fileSignature);
                if (bytesRead != 8 || !Arrays.equals(fileSignature, PNG_SIGNATURE)) {
                    System.out.println("エラー: 指定されたファイルはPNG画像ではありません.");
                    return;
                }
            }

            // 画像の読み込み
            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                System.out.println("エラー: 画像の読み込みに失敗しました. ");
                return;
            }

            // 画像のサイズ情報を取得
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            System.out.println("画像サイズ: " + originalWidth + "x" + originalHeight);
            
            // アスキーアートを構築するためのStringBuilder
            StringBuilder asciiArt = new StringBuilder();

            // 画像のピクセルをサンプリングしてアスキーアートを生成
            // 縦方向は32ピクセル、横方向は16ピクセルごとにサンプリング
            for (int y = 0; y < originalHeight; y += 32) {
                for (int x = 0; x < originalWidth; x += 16) {
                    // 指定ピクセルのRGB値を取得
                    Color color = new Color(image.getRGB(x, y));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    
                    // RGB値から輝度（グレースケール値）を計算
                    int gray = (r * 30 + g * 59 + b * 11) / 100;
                    
                    // 輝度をアスキー文字配列のインデックスに変換
                    int index = (int)(gray / 256.0 * chars.length);
                    // インデックスが配列の範囲を超えないように調整
                    if (index >= chars.length) {
                        index = chars.length - 1;
                    }
                    asciiArt.append(chars[index]);
                }
                asciiArt.append("\n"); // 1行の終わりに改行を追加
            }

            // 生成したアスキーアートをコンソールに出力
            String finalAsciiArt = asciiArt.toString();
            System.out.print(finalAsciiArt);
        
        } catch (IOException e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
