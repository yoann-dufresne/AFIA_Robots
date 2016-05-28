package bluetooth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class BluetoothRobot implements Runnable  {
	private BTConnection btc;
	private boolean ended;
	
	private BufferedReader br;
	private BufferedWriter bw;
	
	public BluetoothRobot() {
		this.ended = true;
	}
	
	@Override
	public void run() {
		this.ended = false;
		
		System.out.println("BT waiting");
        this.btc = Bluetooth.waitForConnection();
		System.out.println("BT connected");
		
        DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
		this.br = new BufferedReader(new InputStreamReader(dis));
		this.bw = new BufferedWriter(new OutputStreamWriter(dos));
		
		while(!this.ended){
			String received = null;
			try {
				 received = this.br.readLine();
				 System.out.println(received);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<String> words = split(received, ';'); 
			String command = words.get(0);
			System.out.println("received command : " + command);
			
			if("DISCOVERED".equals(command)){
				this.discover(words);
			} else if ("PARTIAL".equals(command)){
				this.partial(words);
			} else if ("STOP".equals(command)){
				this.stop(words);
			} else {
				System.out.println("Unknown command");
			}
			
		}
		
		this.btc.close();
	}
	
	private void stop(List<String> words) {
		System.out.println("STOP");
		this.ended = true;
	}

	private void partial(List<String> words) {
		System.out.println("PARTIAL");
	}

	private void discover(List<String> words) {
		System.out.println("DISCOVERED");
	}

	
	public List<String> split(String line, char sep){
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
