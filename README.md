# Assignment Project

本リポジトリは，大学の講義（実習ICTおよびプログラミング演習III）における課題成果物を集約したものである．Pythonを用いたWebアプリケーション開発から，Java/JavaFXを用いたGUI・CLIアプリケーション開発まで，多岐にわたる実装が含まれる．

## プロジェクト構成

```text
.
├── PracticalICT/                # 実習ICT 関連課題
│   └── pict/                    # Flaskベースの日記投稿システム
│       ├── app.py               # メインアプリケーション
│       ├── static/              # スタイルシート，画像等の静的資産
│       └── templates/           # Jinja2形式のHTMLテンプレート
└── ProgrammingExercisesⅡ/       # プログラミング演習III 関連課題
    ├── Task8_Advanced/          # 最終課題 8.A：JavaFX じゃんけんゲーム
    │   ├── src/                 # ソースコード（MVC制御ロジック，FXML）
    │   └── bin/                 # コンパイル済みクラスファイル
    ├── Task8_GUI/               # 最終課題 8.GUI：JavaFX版 ASCII Art生成
    │   ├── src/                 # ソースコード（画像処理ロジック，FXML）
    │   └── bin/                 # コンパイル済みクラスファイル
    └── Task8_CLI/               # 最終課題 8.CLI：コマンドライン版 ASCII Art生成
        ├── Task8_CLI_ver1.java  # 基本実装
        ├── Task8_CLI_ver2.java  # 改良版実装
        └── sample.png           # 動作確認用サンプル画像
```

---

## 各アプリケーションの技術詳細

### 1．実習ICT：日記投稿システム (PracticalICT/pict)

Flaskフレームワークを用いた，シンプルかつセキュアな日記投稿Webアプリケーションである．

#### 技術的特徴
- **ユーザー認証**: `bcrypt` を利用し，パスワードをソルト付きハッシュ形式で保存している．ログイン時にはハッシュ比較による認証を行う．
- **データ永続化**: 実行時に JSON ファイル（`users.json`，`diary.json`）が自動生成され，データベースとして機能する．
- **セッション管理**: Flaskの `session` オブジェクトを用いてログイン状態を維持し，未ログインユーザーのアクセス制限を実現している．
- **UI/UX**: HTML5/CSS3 を用い，日記の改行や空白を保持するための変換処理（`<br>` や `&nbsp;` への置換）を実装している．

#### 動作環境と起動方法
1．Python 3.x および `pip` がインストールされている必要がある．
2．以下のコマンドで依存ライブラリを導入する．
   ```bash
   pip install flask bcrypt
   ```
3．`app.py` を execute し，ローカルサーバーを起動する．
   ```bash
   cd PracticalICT/pict
   python app.py
   ```

---

### 2．プログラミング演習III：Java アプリケーション (ProgrammingExercisesⅡ)

Java 開発環境および JavaFX を利用した，複数のデスクトップアプリケーションの実装である．

#### 動作デモ動画
アプリケーションの実際の動作については，以下のリンクより確認可能である．
- [プログラミング演習III デモ動画 (YouTube)](https://youtu.be/JZ-AK-jQJVc?si=D6NKa60YIjc-ccB1)

#### Task8_Advanced：JavaFX じゃんけんゲーム
JavaFX の FXML を利用して UI を定義したインタラクティブなゲームである．
- **MVCモデル**: FXML によるレイアウト，CSS によるスタイリング，および Java によるイベント制御（Controller）を分離して実装している．
- **視覚効果**: じゃんけんの各手（グー・チョキ・パー）を画像で表現し，動的に表示を切り替えている．

#### Task8_CLI / Task8_GUI：ASCII Art 生成
画像処理アルゴリズムに基づき，任意の PNG 画像をアスキーアートへと変換する．
- **アルゴリズム**: 
  1．画像の各ピクセルの RGB 値を取得する．
  2．以下の式を用いて輝度（Y）を算出する．
     `Y = (R * 30 + G * 59 + B * 11) / 100`
  3．算出された輝度値に基づき，定義されたアスキー文字配列（`@`，`%`，`#`，`*`，`+` 等）から対応する文字を選択する．
- **背景対応**: 黒背景用と白背景用の異なる文字マップを保持し，ユーザーの選択に応じて変換ルールを切り替える機能を備える．
- **ファイル整合性**: PNG ファイルのシグネチャ（マジックナンバー）をバイト単位で検証し，不正なファイルの読み込みを防止している．

#### CLI版の実行例
```bash
cd ProgrammingExercisesⅡ/Task8_CLI
javac Task8_CLI_ver1.java
java Task8_CLI_ver1 <対象画像ファイル名>
```
