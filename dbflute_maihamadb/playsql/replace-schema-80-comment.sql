
-- MEMBER
comment on table MEMBER is '会員: 会員のプロフィールやアカウントなどの基本情報を保持する。
基本的に物理削除はなく、退会したらステータスが退会会員になる。
ライフサイクルやカテゴリの違う会員情報は、one-to-oneなどの関連テーブルにて。';
comment on column MEMBER.MEMBER_ID is '会員ID: 連番として自動採番される。会員IDだけに限らず採番方法はDBMS次第。';
comment on column MEMBER.MEMBER_NAME is '会員名称: 会員のフルネームの名称。
苗字と名前を分けて管理することが多いが、ここでは単純にひとまとめ。';
comment on column MEMBER.MEMBER_ACCOUNT is '会員アカウント: ログインIDとして利用する。
昨今メールアドレスをログインIDとすることが多いので、あまり見かけないかも!?';
comment on column MEMBER.MEMBER_STATUS_CODE is '会員ステータスコード: 会員ステータスを参照するコード。
ステータスが変わるたびに、このカラムが更新される。';
comment on column MEMBER.FORMALIZED_DATETIME is '正式会員日時: 会員が正式に確定した(正式会員になった)日時。
一度確定したらもう二度と更新されないはずだ！';
comment on column MEMBER.BIRTHDATE is '生年月日: 必須項目ではないので、このデータがない会員もいる。';
comment on column MEMBER.REGISTER_DATETIME is '登録日時: レコードが登録された日時。
会員が登録された日時とほぼ等しいが、そういった業務的な役割を兼務させるのはあまり推奨されない。といいつつ、このテーブルには会員登録日時がない...
仕様はどのテーブルでも同じなので、共通カラムの説明はこのテーブルでしか書かない。';
comment on column MEMBER.REGISTER_USER is '登録ユーザ: レコードを登録したユーザ。
会員テーブルであれば当然、会員自身であるはずだが、他のテーブルの場合では管理画面から運用者による登録など考えられるので、しっかり保持しておく。';
comment on column MEMBER.UPDATE_DATETIME is '更新日時: レコードが（最後に）更新された日時。
業務的な利用はあまり推奨されないと別項目で説明したが、このカラムはソートの要素としてよく利用される。';
comment on column MEMBER.UPDATE_USER is '更新ユーザ: レコードを更新したユーザ。
システムは誰が何をしたのかちゃんと覚えているのさ。';
comment on column MEMBER.VERSION_NO is 'バージョンNO: データのバージョンを示すナンバー。
更新回数と等しく、主に排他制御のために利用される。';

-- MEMBER_ADDRESS
comment on table MEMBER_ADDRESS is '会員住所情報: 会員の住所に関する情報で、同時に有効期間ごとに履歴管理されている。
会員を基点に考えた場合、構造的には one-to-many だが、業務的な定型条件で one-to-one になる。このような構造を「業務的one-to-one」と呼ぶ！
有効期間は隙間なく埋められるが、ここでは住所情報のない会員も存在し、厳密には(業務的な) "1 : 0..1" である。';
comment on column MEMBER_ADDRESS.MEMBER_ADDRESS_ID is '会員住所ID: 会員住所を識別するID。
期間ごとに同じ会員のデータを保持することがあるため、これは単なるPKであってFKではない。';
comment on column MEMBER_ADDRESS.MEMBER_ID is '会員ID: 会員を参照するID。
期間ごとのデータがあるので、これだけではユニークにはならない。有効開始日と合わせて複合ユニーク制約となるが、厳密には完全な制約にはなっていない。有効期間の概念はRDBでは表現しきれないのである。';
comment on column MEMBER_ADDRESS.VALID_BEGIN_DATE is '有効開始日: 一つの有効期間の開始を示す日付。
前の有効終了日の次の日の値が格納される。';
comment on column MEMBER_ADDRESS.VALID_END_DATE is '有効終了日: 有効期間の終了日。
期間の最後の日が格納される。基本的に、次の有効開始日の一日前の値となるが、次の有効期間がない場合は 9999/12/31 となる。';
comment on column MEMBER_ADDRESS.ADDRESS is '住所: まるごと住所';
comment on column MEMBER_ADDRESS.REGION_ID is '地域ID: 地域を参照するID。かなり漠然とした地域。';

-- MEMBER_FOLLOWING
comment on table MEMBER_FOLLOWING is '会員フォローイング: とある会員が他の会員をフォローできる。すると、フォローした会員の購入履歴が閲覧できる。';
comment on column MEMBER_FOLLOWING.MEMBER_FOLLOWING_ID is '会員フォローイングID: 連番';
comment on column MEMBER_FOLLOWING.MY_MEMBER_ID is 'わたし: 気になった人がいて...勇気を振り絞った会員のID。';
comment on column MEMBER_FOLLOWING.YOUR_MEMBER_ID is 'あなた: いきなりのアクションに...ちょっと心揺らいだ会員のID。';
comment on column MEMBER_FOLLOWING.FOLLOW_DATETIME is 'その瞬間: ふりかえるとちょっと恥ずかしい気持ちになる日時';

-- MEMBER_SECURITY
comment on table MEMBER_SECURITY is '会員セキュリティ情報: 会員とは one-to-one で、会員一人につき必ず一つのセキュリティ情報がある';
comment on column MEMBER_SECURITY.MEMBER_ID is '会員ID: そのまま one-to-one を構成するためのFKとなる。';
comment on column MEMBER_SECURITY.LOGIN_PASSWORD is 'ログインパスワード: ログイン時に利用するパスワード。
本当は良くないが、Exampleなのでひとまず暗号化していない。';
comment on column MEMBER_SECURITY.REMINDER_QUESTION is 'リマインダ質問: パスワードを忘れた際のリマインダ機能における質問の内容。';
comment on column MEMBER_SECURITY.REMINDER_ANSWER is 'リマインダ回答: パスワードを忘れた際のリマインダ機能における質問の答え。';
comment on column MEMBER_SECURITY.REMINDER_USE_COUNT is 'リマインダ利用回数: リマインダを利用した回数。
これが多いと忘れっぽい会員と言えるが、そんなことを知ってもしょうがない。';

-- MEMBER_SERVICE
comment on table MEMBER_SERVICE is '会員サービス: 会員のサービス情報（ポイントサービスなど）。
テストケースのために、あえて統一性を崩してユニーク制約経由の one-to-one を表現している。';
comment on column MEMBER_SERVICE.MEMBER_SERVICE_ID is '会員サービスID: 独立した主キーとなるが、実質的に会員IDとは one-to-one である。';
comment on column MEMBER_SERVICE.MEMBER_ID is '会員ID: 会員を参照するID。ユニークなので、会員とは one-to-one の関係に。';
comment on column MEMBER_SERVICE.SERVICE_POINT_COUNT is 'サービスポイント数: 購入したら増えて使ったら減る。';
comment on column MEMBER_SERVICE.SERVICE_RANK_CODE is 'サービスランクコード: どんなランクがあるのかドキドキですね。';

-- MEMBER_WITHDRAWAL
comment on table MEMBER_WITHDRAWAL is '会員退会情報: 退会会員の退会に関する詳細な情報。
退会会員のみデータが存在し、"1 : 0..1" のパターンの one-to-one である。
共通カラムがあってバージョンNOがないパターン。
基本的に更新は入らないが、重要なデータなので万が一のために更新系の共通カラムも。';
comment on column MEMBER_WITHDRAWAL.WITHDRAWAL_REASON_CODE is '退会理由コード: 定型的な退会した理由を参照するコード。
何も言わずに退会する会員もいるので必須項目ではない。';
comment on column MEMBER_WITHDRAWAL.WITHDRAWAL_REASON_INPUT_TEXT is '退会理由入力テキスト: 会員がフリーテキストで入力できる退会理由。
もう言いたいこと言ってもらう感じ。サイト運営側としてはこういうのは真摯に受け止めて改善していきたいところ。';
comment on column MEMBER_WITHDRAWAL.WITHDRAWAL_DATETIME is '退会日時: 退会した瞬間の日時。
正式会員日時と違い、こっちは one-to-one の別テーブルで管理されている。';

-- MEMBER_LOGIN
comment on table MEMBER_LOGIN is '会員ログイン: ログインするたびに登録されるログイン履歴。
登録されたら更新されるも削除されることもない。さらには、登録する人もプログラムもはっきりしているので、(紙面の都合上もあって)ここでは共通カラムは省略している。';
comment on column MEMBER_LOGIN.MEMBER_LOGIN_ID is '会員ログインID: ';
comment on column MEMBER_LOGIN.MEMBER_ID is '会員ID: ';
comment on column MEMBER_LOGIN.LOGIN_DATETIME is 'ログイン日時: ログインした瞬間の日時。
同じ会員が同じ日時にログインはできない。(ユニーク制約で重複ログインできないようにしてある)';
comment on column MEMBER_LOGIN.MOBILE_LOGIN_FLG is 'モバイルログインフラグ: モバイル機器からのログインか否か。';
comment on column MEMBER_LOGIN.LOGIN_MEMBER_STATUS_CODE is 'ログイン時会員ステータスコード: ログイン時の会員ステータス';

-- PURCHASE
comment on table PURCHASE is '購入: 一つの商品に対する購入を表現する(購入履歴とも言える)。
実業務であれば購入詳細というテーブルを作り、「購入という行為」と「その中身（詳細）」を違う粒度のデータとしてそれぞれ管理するのが一般的と言えるでしょう。というか、注文とか請求とかそういうことを考え始めたらもっと複雑になるはずですが、ExampleDBということで割り切っています。';
comment on column PURCHASE.MEMBER_ID is '会員ID: 会員を参照するID。
購入を識別する自然キー(複合ユニーク制約)の筆頭要素。';
comment on column PURCHASE.PRODUCT_ID is '商品ID: あなたは何を買ったのか？';
comment on column PURCHASE.PURCHASE_DATETIME is '購入日時: 購入した瞬間の日時。';
comment on column PURCHASE.PURCHASE_COUNT is '購入数量: 購入した商品の一回の購入における数量。';
comment on column PURCHASE.PURCHASE_PRICE is '購入価格: 購入によって実際に会員が支払った（支払う予定の）価格。
基本は商品の定価に購入数量を掛けたものになるが、ポイント利用や割引があったりと必ずしもそうはならない。';
comment on column PURCHASE.PAYMENT_COMPLETE_FLG is '支払完了フラグ: この購入に関しての支払いが完了しているか否か。';

-- PURCHASE_PAYMENT
comment on table PURCHASE_PAYMENT is '購入支払: 購入に対する支払。
分割払いもできるのでmanyとなり、会員からの孫テーブルのテストができてうれしい。';
comment on column PURCHASE_PAYMENT.PURCHASE_PAYMENT_ID is '購入支払ID: 連番';
comment on column PURCHASE_PAYMENT.PURCHASE_ID is '購入ID: 支払い対象の購入へのID';
comment on column PURCHASE_PAYMENT.PAYMENT_AMOUNT is '支払金額: 支払った金額。さて、小数点なのはなぜでしょう？';
comment on column PURCHASE_PAYMENT.PAYMENT_DATETIME is '支払日時: 支払ったときの日時';
comment on column PURCHASE_PAYMENT.PAYMENT_METHOD_CODE is '支払方法コード: 手渡しや銀行振込など';

-- PRODUCT
comment on table PRODUCT is '商品:';
comment on column PRODUCT.PRODUCT_NAME is '商品名称: ExampleDBとして、コメントの少ないケースを表現するため、あえてコメントを控えている。
実業務ではしっかりとコメントを入れることが強く強く推奨される。「よりによってこのテーブルでやらないでよ！」あわわ、何も聞こえません〜';
comment on column PRODUCT.PRODUCT_HANDLE_CODE is '商品ハンドルコード: これだけは書いておこう、商品を識別する業務上のコード。よく品番とか言うかもしれませんねぇ...';
comment on column PRODUCT.REGULAR_PRICE is '定価:';

-- MEMBER_STATUS
comment on table MEMBER_STATUS is '会員ステータス: 会員のステータスを示す固定的なマスタテーブル。いわゆるテーブル区分値！
業務運用上で増えることはなく、増減するときはプログラム修正ともなうレベルの業務変更と考えられる。

こういった固定的なマスタテーブルには、更新日時などの共通カラムは定義していないが、業務上そういった情報を管理する必要性が低いという理由に加え、ExampleDBとして共通カラムでER図が埋め尽くされてしまい見づらくなるというところで割り切っている。実業務では統一的に定義することもある。';
comment on column MEMBER_STATUS.MEMBER_STATUS_CODE is '会員ステータスコード: 会員ステータスを識別するコード。
固定的なデータなので連番とか番号にはせず、データを直接見たときも人が直感的にわかるように、例えば "FML" とかの３桁のコード形式にしている。(3って何会員だっけ？とかの問答をやりたくないので)
';
comment on column MEMBER_STATUS.MEMBER_STATUS_NAME is '会員ステータス名称: 表示用の名称。
国際化対応するときはもっと色々考える必要があるかと...ということで英語名カラムがないので、そのまま区分値メソッド名の一部としても利用される。';
comment on column MEMBER_STATUS.DESCRIPTION is '説明: 会員ステータスそれぞれの説明。
区分値の一つ一つの要素に気の利いた説明があるとディベロッパーがとても助かるので絶対に欲しい。';
comment on column MEMBER_STATUS.DISPLAY_ORDER is '表示順: UI上のステータスの表示順を示すNO。
並べるときは、このカラムに対して昇順のソート条件にする。';

-- SERVICE_RANK
comment on table SERVICE_RANK is 'サービスランク: 会員のサービスレベルを表現するランク。(ゴールドとかプラチナとか)';
comment on column SERVICE_RANK.SERVICE_RANK_CODE is 'サービスランクコード: サービスランクを識別するコード。';
comment on column SERVICE_RANK.SERVICE_RANK_NAME is 'サービスランク名称: サービスランクの名称。
ゴールドとかプラチナとか基本的には威厳のある名前';
comment on column SERVICE_RANK.SERVICE_POINT_INCIDENCE is 'サービスポイント発生率: 購入ごとのサービスポイントの発生率。
購入価格にこの値をかけた数が発生ポイント。ExampleDBとして数少ない貴重な小数点ありのカラム。';
comment on column SERVICE_RANK.NEW_ACCEPTABLE_FLG is '新規受け入れ可能フラグ: このランクへの新規受け入れができるかどうか。';
comment on column SERVICE_RANK.DESCRIPTION is '説明: ランクに関する業務的な説明。';
comment on column SERVICE_RANK.DISPLAY_ORDER is '表示順: UI上の表示順を表現する番号。';

-- PRODUCT_CATEGORY
comment on table PRODUCT_CATEGORY is '商品カテゴリ: 商品のカテゴリを表現するマスタ。自己参照FKの階層になっている。';
comment on column PRODUCT_CATEGORY.PRODUCT_CATEGORY_CODE is '商品カテゴリコード: 自分のテーブルの別のレコードからも参照される。';
comment on column PRODUCT_CATEGORY.PRODUCT_CATEGORY_NAME is '商品カテゴリ名称: ';
comment on column PRODUCT_CATEGORY.PARENT_CATEGORY_CODE is '親カテゴリコード: 最上位の場合はデータなし。まさひく自己参照FKカラム！';

-- PRODUCT_STATUS
comment on table PRODUCT_STATUS is '商品ステータス: 商品のステータスを表現する固定的なマスタ。';
comment on column PRODUCT_STATUS.PRODUCT_STATUS_CODE is '商品ステータスコード: 商品ステータスを識別するコード。';
comment on column PRODUCT_STATUS.PRODUCT_STATUS_NAME is '商品ステータス名称: 表示用の名称、英語名カラムがないのでそのままメソッド名の一部としても利用される。';
comment on column PRODUCT_STATUS.DISPLAY_ORDER is '表示順: もう、ご想像の通りです。';

-- REGION
comment on table REGION is '地域: 主に会員の住所に対応する漠然とした地域。
かなりざっくりした感じではある。唯一の業務的one-to-oneの親テーブルのケース。';
comment on column REGION.REGION_ID is '地域ID: 地域をしっかりと識別するID。
珍しく(固定的な)マスタテーブルとしては数値だが、Exampleなのでやはり色々なパターンがないとね、ってところで。';
comment on column REGION.REGION_NAME is '地域名称: 地域を漠然と表す名称。';

-- WITHDRAWAL_REASON
comment on table WITHDRAWAL_REASON is '退会理由: 会員に選ばせる定型的な退会理由のマスタ。そういえば、これ表示順カラムがないですねぇ...';
comment on column WITHDRAWAL_REASON.WITHDRAWAL_REASON_CODE is '退会理由コード: ';
comment on column WITHDRAWAL_REASON.WITHDRAWAL_REASON_TEXT is '退会理由テキスト: 退会理由の内容。
テキスト形式なので目いっぱい書けるが、そうするとUI側できれいに見せるのが大変でしょうね。';
