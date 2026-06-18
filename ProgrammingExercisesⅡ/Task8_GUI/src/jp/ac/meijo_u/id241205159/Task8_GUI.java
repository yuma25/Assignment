/**
 * 最終課題 8.GUI Task8_GUI
 * @author 241205159 増田侑真
 */

package jp.ac.meijo_u.id241205159;

import javafx.application.Application;  // JavaFXのアプリケーションクラスを継承するために必要なライブラリ
import javafx.fxml.FXMLLoader;          // GUIのレイアウトを定義するFXMLファイルを読み込むためのライブラリ
import javafx.scene.Scene;              // シーンを扱うためのライブラリ
import javafx.scene.layout.VBox;        // レイアウト
import javafx.stage.Stage;              // アプリケーションのウィンドウを扱うためのライブラリ
import java.io.IOException;             // 入出力処理におけるエラーを扱うためのライブラリ

public class Task8_GUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // FXMLファイルを読み込み
            VBox root = FXMLLoader.load(getClass().getResource("Task8_GUI.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Task8_GUI : ASCII Art");   // ウィンドウのタイトルを設定
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            // FXMLファイルの読み込みに失敗した場合, エラーを出力
            e.printStackTrace();
        }
    }

    // Javaプログラムのメイン
    public static void main(String[] args) {
        launch(args);// JavaFXアプリケーションを起動
    }
}
