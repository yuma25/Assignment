import json
import bcrypt
import os
from datetime import datetime
from flask import Flask, request, render_template, redirect, url_for, session

app = Flask(__name__)
app.secret_key = 'your_secret_key'  # セッションの秘密鍵


if not os.path.exists('users.json'):
    with open('users.json', 'w') as file:
        json.dump([], file)

if not os.path.exists('diary.json'):
    with open('diary.json', 'w') as file:
        json.dump([], file)


@app.route('/')
def home():
    return redirect(url_for('login'))

# ユーザー登録機能
@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')

        try:
            with open('users.json', 'r') as file:
                users = json.load(file)
        except:
            users = []

        # ユーザー名の重複を確認
        for user in users:
            if user['username'] == username:
                return render_template('register.html', error_message="このユーザー名はすでに使用されています.")

        # パスワードをハッシュ化して保存
        hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        users.append({"username": username, "password": hashed_password})

        with open('users.json', 'w') as file:
            json.dump(users, file, indent=4)

        return redirect(url_for('login'))

    return render_template('register.html')


# ログイン機能
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')

        try:
            with open('users.json', 'r') as file:
                users = json.load(file)
        except:
            users = []

        for user in users:
            if user['username'] == username and bcrypt.checkpw(password.encode('utf-8'), user['password'].encode('utf-8')):
                session['username'] = username
                return redirect(url_for('diary'))

        return render_template('login.html', error_message="ユーザー名またはパスワードが間違っています.")

    return render_template('login.html')


# 日記投稿機能
@app.route('/diary', methods=['GET', 'POST'])
def diary():
    try:
        with open('diary.json', 'r') as file:
            diaries = json.load(file)
    except:
        diaries = []

    # 現在のユーザーの日記のみ取得し、最新順に並べる
    current_diaries = []
    for user in diaries:
        if user['username'] == session['username']:
            current_diaries.append(user)
    current_diaries.sort(key=lambda x: x['date'], reverse=True)

    if request.method == 'POST':
        diary_entry = request.form.get('entry')

        # 空白や改行だけの投稿を禁止
        if not diary_entry.strip():
            return render_template('diary.html', username=session['username'], error_message="日記の内容を入力してください。", diary_entries=current_diaries)

        # 日記の内容をHTML表示用に変換
        diary_entry = diary_entry.replace('\n', '<br>').replace(' ', '&nbsp;')
        date = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

        # 新しい日記を追加
        diaries.append({"username": session['username'], "entry": diary_entry, "date": date})

        with open('diary.json', 'w') as file:
            json.dump(diaries, file, indent=4)

        return redirect(url_for('diary'))

    return render_template('diary.html', username=session['username'], diary_entries=current_diaries)

# ログアウト機能
@app.route('/logout')
def logout():
    session.pop('username', None)
    return redirect(url_for('login'))

# アプリの実行
if __name__ == '__main__':
    app.run(debug=True)
