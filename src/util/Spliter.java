package util;

import java.util.ArrayList;
import java.util.List;

public class Spliter {

	public static List<String> split(String line, char sep){
		List<String> res = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		
		for(int idx=0 ; idx<line.length() ; idx++) {
			char c = line.charAt(idx);
			
			if (c == sep){
				res.add(sb.toString());
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		res.add(sb.toString());
		
		return res;	
	}
	
}
