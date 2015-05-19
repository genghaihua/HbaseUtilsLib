import java.util.ArrayList;
import java.util.List;


public class study {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> values = new ArrayList<String>();
		values.add("aaa");
		values.add("bbb");
	}

	static String getStrFromList(ArrayList<String> arr){
		String res="";
		for (String str : arr) {
			res+=str+"\001";
		}
		res=res.substring(0,res.lastIndexOf("\001"));
		return res;
	}
}
