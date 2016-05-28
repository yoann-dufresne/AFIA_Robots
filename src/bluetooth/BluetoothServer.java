package bluetooth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import lejos.pc.comm.NXTConnector;

public class BluetoothServer implements Runnable {

	private boolean stopped;
	private String addr;
	private int id;
	
	private NXTConnector conn;
	private BufferedWriter bw;
	private BufferedReader br;

	public BluetoothServer(String addr, int id) {
		this.addr = addr;
		this.conn = new NXTConnector();
		this.id = id;
		this.stopped = true;
	}
	
	@Override
	public void run() {
		this.stopped = false;
		
		boolean connected = this.conn.connectTo("btspp://" + this.addr);
		
		if (!connected)
			return;
		
		System.out.println("Robot " + this.id + " connected");
		
		this.bw = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(this.conn.getOutputStream())));
		this.br = new BufferedReader(new InputStreamReader(new DataInputStream(this.conn.getInputStream())));
		
		while (!this.stopped) {
			
		}
		
		try {
			this.br.close();
			this.bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop () {
		this.stopped = true;
	}

}
