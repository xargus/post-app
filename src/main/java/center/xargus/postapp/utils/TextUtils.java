package center.xargus.postapp.utils;

public class TextUtils {
	
	public static boolean isEmpty(String content) {
		if (content == null || "".equals(content)) {
			return true;
		}
		
		return false;
	}
}
