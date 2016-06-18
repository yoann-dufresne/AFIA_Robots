package util;

public class Debug {

	public static void log (String msg) {
		Debug.log(msg, 1000);
	}
	
	public static void log (String msg, long duration) {
		System.out.println(msg);
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
