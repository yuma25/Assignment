/**
 * 最終課題 8.A Task8_AdvancedController
 * @author 241205159 増田侑真
 */

package jp.ac.meijo_u.id241205159;

import javafx.application.Platform;      // タスクを実行するためのクラス
import javafx.event.ActionEvent;         // ボタンクリックなどを扱うクラス
import javafx.fxml.FXML;                 // FXMLファイルとJavaコードに結びつける
import javafx.scene.control.Label;       // テキストを表示するため
import javafx.scene.control.Button;      // クリック可能なボタン
import javafx.scene.image.Image;         // 画像データを扱うクラス
import javafx.scene.image.ImageView;     // Imageを表示するため
import javafx.scene.layout.HBox;         // 子ノードを水平に配置する
import javafx.animation.Timeline;        // キーフレームアニメーション
import javafx.animation.KeyFrame;        // アニメーションの特定の時点を定義
import javafx.util.Duration;             // 時間を表現するためのクラス
import javafx.animation.FadeTransition;  // フェードイン・アウト
import java.util.Random;                 // 乱数を生成するためのクラス


public class Task8_AdvancedController {
    @FXML private Label playerResultLabel;      // プレイヤーの勝利数
    @FXML private Label computerResultLabel;    // コンピュータの勝利数
    @FXML private Label messageLabel;           // ゲームの進行状況や結果
    @FXML private Button rockButton;            // 「グー」の選択ボタン
    @FXML private Button scissorsButton;        // 「チョキ」の選択ボタン
    @FXML private Button paperButton;           // 「パー」の選択ボタン
    @FXML private HBox buttonBox;
    @FXML private ImageView playerHandImage;    // プレイヤーの出した手（グー, チョキ, パー）の画像
    @FXML private ImageView computerHandImage;  // コンピュータの出した手（グー, チョキ, パー）の画像
    @FXML private ImageView winLoseImage;       // ゲーム全体の勝敗（WIN/LOSE）の画像
    @FXML private Button playAgainButton;       // ゲームを最初からやり直すためのボタン
    @FXML private Button exitButton;            // アプリケーションを終了するためのボタン

    private int playerScore = 0;                // プレイヤーの勝数
    private int computerScore = 0;              // コンピュータの勝数
    private int result = 3;                     // 勝敗結果を保持 (0:負け, 1:あいこ, 2:勝ち, 3:初期値)
    private int playerHand;                     // プレイヤーが出した手
    private int computerHand;                   // コンピュータが出した手
    private final Random random = new Random(); // コンピュータの手を決定するための乱数ジェネレーター
    private final int ROCK = 0;                 // グー
    private final int SCISSORS = 1;             // チョキ
    private final int PAPER = 2;                // パー

    private void playGame(int newPlayerHand) {
        setButtonsDisable(true);                // ボタンを無効化して連続入力を防止
        this.playerHand = newPlayerHand;
        this.computerHand = random.nextInt(3);    // 0, 1, 2のいずれかをランダムに選択

        if (result != 1) {
            messageLabel.setText("じゃんけん...");
        } else {
            messageLabel.setText("あいこで...");
        }

        // 1秒の遅延を設定
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000)));
        timeline.play();

        // アニメーション終了後の処理
        timeline.setOnFinished(e -> {
            updateHands(this.playerHand, this.computerHand);        // 手の画像を更新
            result = getResult(this.playerHand, this.computerHand); // 勝敗を判定
            
            // 1秒の遅延を設定して結果を表示
            Timeline resultDelay = new Timeline(new KeyFrame(Duration.millis(1000), e2 -> {
                if (result == 2) {
                    playerScore++;
                    messageLabel.setText("あなたの勝ち！");
                } else if (result == 0) {
                    computerScore++;
                    messageLabel.setText("コンピュータの勝ち！");
                } else {
                    messageLabel.setText("あいこ！");
                }
                playerResultLabel.setText("あなた: " + playerScore + " 勝");
                computerResultLabel.setText("コンピュータ: " + computerScore + " 勝");

                // 勝敗が決まったらゲーム終了処理
                if (playerScore >= 3 || computerScore >= 3) {
                    String imagePath;
                    if (playerScore >= 3) {
                        messageLabel.setText("ゲーム終了！あなたの勝利です！");
                        imagePath = "/jp/ac/meijo_u/id241205159/images/win.png"; // 勝利画像
                    } else {
                        messageLabel.setText("ゲーム終了！あなたの敗北です！");
                        imagePath = "/jp/ac/meijo_u/id241205159/images/lose.png"; // 敗北画像
                    }
                    setButtonsDisable(true);              // ゲーム終了のため, じゃんけんボタンを無効化
                    playAgainButton.setVisible(true);       // 「もう一度遊ぶ」ボタンを表示
                    playAgainButton.setDisable(false);      // 「もう一度遊ぶ」ボタンを有効化
                    exitButton.setVisible(true);            // 「終了」ボタンを表示
                    exitButton.setDisable(false);           // 「終了」ボタンを有効化
                    playerHandImage.setVisible(false);      // じゃけんの手の画像を非表示にする
                    computerHandImage.setVisible(false);    // じゃけんの手の画像を非表示にする

                    // 勝敗画像をセット
                    winLoseImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                    winLoseImage.setVisible(true);          // 勝敗画像を表示
                    
                    FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), winLoseImage);
                    fadeTransition.setFromValue(0); // 透明度を0
                    fadeTransition.setToValue(1);   // 透明度を1
                    fadeTransition.play();
                } else {
                    // ゲームが続く場合はボタンを有効化
                    setButtonsDisable(false); 
                }
            }));
            messageLabel.setText("ポンッ！"); // じゃんけんの結果をアニメーション的に表示
            resultDelay.play();
        });
        timeline.play();
    }

    // プレイヤーとコンピュータの手の画像を更新
    private void updateHands(int playerHand, int computerHand) {
        playerHandImage.setImage(new Image(getClass().getResourceAsStream(getImagePath(playerHand))));
        computerHandImage.setImage(new Image(getClass().getResourceAsStream(getImagePath(computerHand))));
        playerHandImage.setVisible(true);
        computerHandImage.setVisible(true);
    }

    // 手に対応する画像ファイルのパスを取得
    private String getImagePath(int hand) {
        if (hand == ROCK) {
            return "/jp/ac/meijo_u/id241205159/images/rock.png";
        } else if (hand == SCISSORS) {
            return "/jp/ac/meijo_u/id241205159/images/scissors.png";
        } else if (hand == PAPER) {
            return "/jp/ac/meijo_u/id241205159/images/paper.png";
        }
        return null;
    }

    /**
     * じゃんけんの勝敗を判定
     * 勝敗結果 (0:負け, 1:あいこ, 2:勝ち)
     */
    private int getResult(int playerHand, int computerHand) {
        int[][] winLossMatrix = {{1, 2, 0},{0, 1, 2},{2, 0, 1}};    // 勝敗判定用の行列
        return winLossMatrix[playerHand][computerHand];
    }

    /**
     * ゲーム中のボタンの有効/無効を切り替え
     * 無効にする場合はtrue, 有効にする場合はfalse
     */
    private void setButtonsDisable(boolean disable) {
        rockButton.setDisable(disable);     // グーボタンの有効/無効
        scissorsButton.setDisable(disable); // チョキボタンの有効/無効
        paperButton.setDisable(disable);    // パーボタンの有効/無効
    }

    // グーボタンがクリックされた時
    @FXML
    public void handleRockAction(ActionEvent event) {
        if (result != 1) { // あいこではない場合
            updateHands(ROCK, ROCK);
            messageLabel.setText("最初はぐ～...");
        }
        // 1秒の遅延後にゲームを開始
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(1000), e -> playGame(ROCK)));
        delay.play();
    }

    // チョキボタンがクリックされた時
    @FXML
    public void handleScissorsAction(ActionEvent event) {
        if (result != 1) { // あいこではない場合
            updateHands(ROCK, ROCK);
            messageLabel.setText("最初はぐ～...");
        }
        // 1秒の遅延後にゲームを開始
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(1000), e -> playGame(SCISSORS)));
        delay.play();
    }

    // パーボタンがクリックされた時
    @FXML
    public void handlePaperAction(ActionEvent event) {
        if (result != 1) { // あいこではない場合
            updateHands(ROCK, ROCK);
            messageLabel.setText("最初はぐ～...");
        }
        // 1秒の遅延後にゲームを開始
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(1000), e -> playGame(PAPER)));
        delay.play();
    }

    // もう一度遊ぶボタンがクリックされた時
    @FXML
    private void handlePlayAgainAction(ActionEvent event) {
        playerScore = 0;
        computerScore = 0;
        result = 3;
        playerResultLabel.setText("あなた: 0 勝");
        computerResultLabel.setText("コンピュータ: 0 勝");
        messageLabel.setText("【 じゃんけんゲーム　3勝先取 】");
        playerHandImage.setImage(new Image(getClass().getResourceAsStream("/jp/ac/meijo_u/id241205159/images/rock.png")));
        computerHandImage.setImage(new Image(getClass().getResourceAsStream("/jp/ac/meijo_u/id241205159/images/rock.png")));
        setButtonsDisable(false);               // じゃんけんのボタンを再び有効化
        playAgainButton.setVisible(false);        // 「もう一度遊ぶ」ボタンを非表示
        playAgainButton.setDisable(true);         // 「もう一度遊ぶ」ボタンを無効化
        exitButton.setVisible(false);             // 「終了」ボタンを非表示
        exitButton.setDisable(true);              // 「終了」ボタンを無効化
        winLoseImage.setVisible(false);
        winLoseImage.setOpacity(0);
        playerHandImage.setVisible(true);
        computerHandImage.setVisible(true);
    }

    // 終了ボタンがクリックされた時
    @FXML
    private void handleExitAction(ActionEvent event) {
        Platform.exit(); // アプリケーションを終了
    }
}
