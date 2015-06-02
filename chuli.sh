#!/bin/bash
awk -F "\001" '{
if(index($1,"jd.")) { print $0"\001""JD""\001""NA" }
else if(index($1,"amazon.")){ print $0"\001""AMAZON""\001""NA"} 
else if(index($1,"taobao.")) { print $0"\001""TAOBAO""\001""NA" }
else if(index($1,"tmall.")) { print $0"\001""TMALL""\001""NA" }
else if(index($1,"yixun.")||index($1,"paipai.")) { print $0"\001""YIXUN""\001""NA" }
else if(index($1,"suning.")) { print $0"\001""SUNING""\001""NA" }
else { print $0"\001""NA""\001""NA" }
}' $1



cat mydata1.csv |head -2000|awk -F '\001' '{if($5!="NA") print $5"\001"$1"\001"$4"\001"$2"\001"$3 }'|awk '{if(index($1,"http://"))print $0}'>prosx.text
 cat mydata1.csv |awk -F '\001' '{if($5!="NA") print $5"\001"$1"\001"$4"\001"$2"\001"$3 }'|awk '{if(index($1,"http://"))print $0}'>prosx.text
 
 
 cat mydata1.csv |awk -F '\001' '{ print $1"\001"$2"\001"$5"\001"$3"\001"$4 }'|awk '{if(index($1,"http://"))print $0}'>prosxfinal.text
 
 url cate title ec tags

url   cate:,ec:,tags:,title:,url:
$5"\001"$1"\001"$4"\001"$2"\001"$3