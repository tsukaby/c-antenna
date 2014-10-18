# カテゴリアンテナ

### memo

my.cnf

    # BLOBデータ等を受け付ける為、許容サイズをデフォルト16MBから以下へ変更
    max_allowed_packet=100MB
    # 圧縮を利用する為、Barracudaを設定
    innodb_file_per_table
    innodb_file_format = Barracuda
    innodb_log_file_size=256M


圧縮するテーブルには`ROW_FORMAT=COMPRESSED`を付ける

