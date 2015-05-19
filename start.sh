#!/bin/bash
java -cp ExportHbase.jar net.mccarroll.hbaseload.HBaseExporter product_info_db /home/clickwise/ghh/exportdata/mydata1.csv  > log.txt 2>&1 &
