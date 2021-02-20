# ShareBookmarks

## 開発環境

* Java 11
* Android Studio
* server 環境
  * [ShareBookmarksApi](https://github.com/bvlion/ShareBookmarksApi) を実行する

## 配信環境

* master にマージすると GitHub Actions で staging 用の DeployGate が配信される
* v タグを切ることで本番デプロイが行われる
  * タグ作成自体は[お問い合わせ](https://docs.google.com/spreadsheets/d/1TkHeLeKNvcX-pRAp6HBVXs9yc4d63TQPF-0ZTlYFvXg/edit)ファイルの「App リリース」シートより行う

## テスト

master にマージされるとテストが走り自動で[ GitHub Pages](https://bvlion.github.io/ShareBookmarks/index.html) に結果がアップされる

## キー管理
各 build variant、およびリリースファイルは openssl によって暗号化している。
debug 以外は不要にし、必要に応じてオーナーが権限を付与することとする。

* debug
  * https://docs.google.com/document/d/1nkDVcpHlMS_jmYTXQJb0KPGyDKSaYn315QDpeYtgqTE/edit
* staging
  * https://docs.google.com/document/d/1geBHimDdUKHSepyg5cnhUU2KDIf-00SCEgNhBtBnIcE/edit
* release
  * https://docs.google.com/document/d/1lPx39sDJvsr7Cuf4QSGbjJTuLXzH2G2YRcvpofmU3qk/edit
* リリースファイル
  * https://docs.google.com/document/d/1kInrBcZXFYRcBXD79AZ5GZJlFq7IIKelPB6963l862g/edit

  内容はドキュメントを確認

## お問い合わせファイル

Google Spread Sheet にてある程度は返信を自動化している。
https://docs.google.com/spreadsheets/d/1TkHeLeKNvcX-pRAp6HBVXs9yc4d63TQPF-0ZTlYFvXg/edit