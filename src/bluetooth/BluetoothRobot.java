package bluetooth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class BluetoothRobot implements Runnable  {
	private BufferedReader br;
	
	@Override
	public void run() {
		System.out.println("BT waiting");
        BTConnection btc = Bluetooth.waitForConnection();
		System.out.println("BT connected");
		
        DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
		this.br = new BufferedReader(new InputStreamReader(dis));
		
		while(true){
			String recepted = null;
			try {
				 recepted = this.br.readLine();
			} catch (IOException e) {
//				System.out.println("erreur readline :(");
				e.printStackTrace();
			}
			System.out.println(recepted);
			
			List<String> words=null;
			try{
			words = split(recepted, ';'); 
			} catch (Exception e) {
				e.printStackTrace();				
			}
			String command = words.get(0);
			System.out.println("recepted command : " + command);
			
			if("DISCOVERED".equals(command)){
				System.out.println("DISCOVERED");

			} else if ("PARTIAL".equals(command)){
				System.out.println("PARTIAL");

			} else if ("STOP".equals(command)){
				System.out.println("STOP");
				btc.close();
				break;
			} else {
				System.out.println("Unknown");
			}
			
		}
		System.out.println("stopped bluetooth thread");
	}
	
	public List<String> split(String recepted, char sep){
		System.out.println("start split");
		List<String> res = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		System.out.println("split created objects");
		
		for(char c: (recepted+'\n').toCharArray()){
			System.out.println("split : for, c = " + c);
			System.out.println("split : for, free memory" + Runtime.getRuntime().freeMemory());
			
			if (c == sep || c == '\n'){
				System.out.println("split : for, extend res");
				res.add(sb.toString());
				sb.delete(0, sb.length());
			}
			else {
				System.out.println("split : for, append to string buf");
				sb.append(c);
			}
			System.out.println(sb.toString());
		}
		System.out.println("split : end for");
		
		return res;
		
	}

}
