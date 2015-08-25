package cn.ghh.lib;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 配置文件解析类
 * @author geng
 *
 */
public class DomService {
	/**
	 * 根据文件位置与父节点获取父节点下的所有键值
	 * @param fileLoc
	 * @param parent
	 */
	public HashMap<String, String> getChildsKV(String fileLoc, String parent) throws Exception {
		HashMap<String, String> res =new HashMap<String, String>();
		InputStream input =this.getClass().getClassLoader().getResourceAsStream(fileLoc);
		try {
			res=getChildsKV(input, parent);
			input.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public HashMap<String, String> getChildsKV(InputStream inputStream, String parent) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inputStream);
		Element element = document.getDocumentElement();
		NodeList bookNodes = element.getElementsByTagName(parent);
		for (int i = 0; i < bookNodes.getLength(); i++) {
			Element bookElement = (Element) bookNodes.item(i);
			NodeList childNodes = bookElement.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
					String name=childNodes.item(j).getNodeName();
					String value=childNodes.item(j).getFirstChild().getNodeValue();
					res.put(name, value);
				}
			}
		}
		return res;
	}
	/**
	 * 打印hashmap数据 以 key \t value的形式展示
	 * @param hm
	 */
	public void printHashMap(HashMap<String, String> hm) {
		Set<String> s = hm.keySet();
		Iterator<String> i = s.iterator();
		while (i.hasNext()) {
			String o = i.next();
			if (o.equals("cookie")) {
				System.out.println(o + "\t" + hm.get(o).replaceAll("\"", ""));
			} else {
				System.out.println(o + "\t" + hm.get(o));
			}
		}
	}
	public void test() {
		String file="conf/hbaseconf.xml";
		String parent ="hbaseconfig";
		try {
			printHashMap(getChildsKV(file, parent));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		DomService dm=new DomService();
		dm.test();
	}

}
