package cn.ghh.hbase.export;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.ghh.lib.DomService;
import cn.ghh.lib.TimeStampTransform;

/**
 * 导出hbase数据
 * 
 * @author geng
 *
 */
public class DataExportFromHbase {
	private DomService dm = new DomService();

	public static class ExportMapper extends TableMapper<Text, NullWritable> {
		@Override
		protected void map(
				ImmutableBytesWritable key,
				Result value,
				org.apache.hadoop.mapreduce.Mapper<ImmutableBytesWritable, Result, Text, NullWritable>.Context context)
				throws IOException, InterruptedException {
			String uid=Bytes.toString(value.getRow());
			for (Cell cell : value.rawCells()) {
				String content = Bytes.toString(CellUtil.cloneQualifier(cell));
				String val=Bytes.toString(CellUtil.cloneValue(cell));
				String time=Long.toString(cell.getTimestamp());
				time=TimeStampTransform.timestamp2Date(time);
				context.write(new Text(uid+"\001"+time+"\001"+content+"\001"+val), NullWritable.get());
			}
		}
	}

	public void run(String args[]) throws IOException,
			InterruptedException, ClassNotFoundException {
		Configuration conf = HBaseConfiguration.create();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: <OUT> <TableName> ");
			return ;
		}
		String output = args[0];
		String table = args[1];
		HashMap<String, String> hbaseconMap = new HashMap<String, String>();
		try {
			hbaseconMap = dm.getChildsKV("conf/hbaseconf.xml","hbaseconfig");
			Iterator<Entry<String, String>> iter = hbaseconMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String val = entry.getValue();
				conf.set(key, val);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Job job =Job.getInstance(conf, "ExportDataFromHBaseTable "+table);
		job.setNumReduceTasks(0);
		job.setJarByClass(DataExportFromHbase.class);
		Scan scan=new Scan();
		scan.addFamily("info".getBytes());
		scan.setCaching(200);
		TableMapReduceUtil.initTableMapperJob(table, scan,
				ExportMapper.class, Text.class, NullWritable.class, job);
		Path outPath = new Path(output);
		FileOutputFormat.setOutputPath(job, outPath);// 输出结果
		job.waitForCompletion(true);
	}

	public static void main(String args[]) throws IOException,
			InterruptedException, ClassNotFoundException {
		DataExportFromHbase dataExportFromHbase = new DataExportFromHbase();
		dataExportFromHbase.run(args);
	}

}
