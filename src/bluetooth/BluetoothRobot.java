package bluetooth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import util.Spliter;

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
			
			List<String> words = Spliter.split(received, ';'); 
			String command = words.get(0);
			System.out.println("received command : " + command);
			try {
				bw.write("nxt : " +command);
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if("DISCOVERED".equals(command)){
				this.discover(words);
			} else if ("PARTIAL".equals(command)){
				this.partial(words);
			} else if ("STOP".equals(command)){
				this.stop(words);
				System.out.println("endif ended");
			} else {
				System.out.println("Unknown command");
			}
			
		}
		System.out.println("BT ended");
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

}
