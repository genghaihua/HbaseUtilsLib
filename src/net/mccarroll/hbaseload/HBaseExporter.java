// HBaseExporter.java - export data from an HBase table to a CSV formatted file 
// Copyright (c) 2011 Niall McCarroll  
// Distributed under the MIT/X11 License (http://www.mccarroll.net/snippets/license.txt)

package net.mccarroll.hbaseload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HBaseExporter extends HBaseImportExport {
	private static Logger logger = LoggerFactory.getLogger(HBaseImportExport.class);
	
	String tableName;
	
	/**
	 * Construct an exporter for an HBase table
	 * 
	 * @param tableName name of the table in HBase
	 */
	HBaseExporter(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * export CSV from an HBase table
	 * 
	 * @param csvFilePath CSV file to export
	 * 
	 * @throws IOException
	 */
	public void exportCSV(File csvFilePath) throws IOException {
		Configuration config = HBaseConfiguration.create(); 
		config.addResource("/etc/hbase/conf/hbase-site.xml");
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.zookeeper.quorum", "192.168.10.130");
		config.set("hbase.master", "192.168.10.128:60000");
		/*hn
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.zookeeper.quorum", "192.168.10.103");
		config.set("hbase.master", "192.168.10.103:60000");
		*/
		/*sx
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.zookeeper.quorum", "192.168.10.39");
		config.set("hbase.master", "192.168.10.39:60000");
		*/
		HBaseAdmin admin = new HBaseAdmin(config);
		HTableDescriptor desc = admin.getTableDescriptor(tableName.getBytes());
		
		OutputStream os = new FileOutputStream(csvFilePath);
		CsvOutputStream cos = new CsvOutputStream(os);
		cos.setEncoding("UTF-8");
		cos.setDelimiter('\001');
		
		//HTable table = new HTable(config, tableName);
		HTableInterface table =new HTable(config, tableName);
		Scan scan = new Scan();
        ResultScanner resultScanner = table.getScanner(scan);
        Result result;
        
        int counter = 0;
        
        List<HBaseCol> columns = new ArrayList<HBaseCol>(); 
    List<String> values = new ArrayList<String>();
     //   ArrayList<String> values = new ArrayList<String>();
        
       // Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
        
        while((result=resultScanner.next()) != null) {
        	
        	values.clear();
        	
        	if (columns.size() == 0) {
        		for(KeyValue key: result.list()) {
        			String family = new String(key.getFamily(),"UTF-8");
        			String qualifier = new String(key.getQualifier(),"UTF-8");
            		columns.add(new HBaseCol(family,qualifier));
            		values.add(family+":"+qualifier);
            	}
        		cos.writeLine(values.toArray(new String[0]));
        		values.clear();
        	}
        	values.add(new String(result.getRow()));
        	for(HBaseCol column: columns) {
        		byte[] val = result.getValue(column.family.getBytes("UTF-8"), column.qualifier.getBytes("UTF-8"));
        		if (val != null) {
        			values.add(new String(val,"UTF-8"));
        		} else {
        			//values.add("");
        			values.add("NA");
        		}
        	}
        	
        	cos.writeLine(values.toArray(new String[0]));
        	//cos.writeLine(getStrFromList(arr));
        	counter += 1;
			if (counter % 10000 == 0) {
				logger.info("Exported "+counter+" records");
			}
        }
        cos.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Usage: HBaseExporter <tablename> <csv file path>");
		}
		
		String tableName = args[0];
		String filePath = args[1];
		File csvOutputFile = new File(filePath);
		HBaseExporter exporter = new HBaseExporter(tableName);
		exporter.exportCSV(csvOutputFile);
	}
}