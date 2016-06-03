package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import util.Spliter;
import bluetooth.BluetoothServer;

public class Server {
	
	public static final String[] macAddresses = {
		"00:16:53:13:EF:A9",
		"00:16:53:00:00:00",
		"00:16:53:0F:F5:A9",
		"00:16:53:00:00:00"
	};
	private static int port;
	
	private BluetoothServer[] btServs;
	private ServerSocket tcp;
	private boolean stopped;
	
	public Server() {
		this.stopped = true;
		this.btServs = new BluetoothServer[Server.macAddresses.length];
		
		for (int idx=0 ; idx<macAddresses.length ; idx++) {
			String mac = Server.macAddresses[idx];
			this.btServs[idx] = new BluetoothServer(mac, idx);
		}
		
		try {
			this.tcp = new ServerSocket(Server.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start () {
		this.stopped = false;
		for (BluetoothServer serv : this.btServs)
			new Thread(serv).start();
		
		while (!stopped) {
			try {
				Socket sock = this.tcp.accept();
				new SocketHandler(sock, this).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void stop() {
		this.stopped = true;
	}
	
	
	private class SocketHandler extends Thread {
		
		private Socket sock;
		private Server server;
		private BufferedReader br;

		public SocketHandler(Socket sock, Server serv) {
			this.sock = sock;
			this.server = serv;
			
			try {
				this.br = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			String line = null;
			try {
				while (true) {
					line = this.br.readLine();
					List<String> split = Spliter.split(line, ';');
					
					if (line.startsWith("STOP")) {
						this.stop(split);
						break;
					}
				}
				
				this.br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void stop(List<String> words) {
			this.server.stop();
		}
	}
	
	
	public static void main(String[] args) {
		Server serv = new Server();
		serv.start();
	}
	
}
