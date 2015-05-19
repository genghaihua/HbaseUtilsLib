#hbase org.apache.hadoop.hbase.mapreduce.Export cookie_map_ghh /user/clickwise/ghh/cookie_map_ghh
#hadoop fs -get /user/clickwise/ghh/cookie_map_ghh cookie_map_ghh
hbase org.apache.hadoop.hbase.mapreduce.Export product_info_db /user/clickwise/ghh/product_info_db
hadoop fs -get /user/clickwise/ghh/product_info_db product_info_db
hadoop fs -rmr /user/clickwise/ghh/product_info_db
