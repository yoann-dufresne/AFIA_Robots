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
import model.Grid;
import model.Position;
import model.Tile;
import util.Spliter;

public class BluetoothRobot implements Runnable  {
	private Position position;
	private Grid grid;
	
	private BTConnection btc;
	private boolean ended;

	private BufferedReader br;
	private BufferedWriter bw;

	private List<String> inbox;

	public BluetoothRobot(Position pos, Grid grid) {
		this.ended = true;
		this.inbox = new ArrayList<String>();
		
		this.position = pos;
		this.grid = grid;
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
			while (this.btc.available() > 0) {
				String received = null;
				try {
					 received = this.br.readLine();
					 System.out.println(received);
				} catch (IOException e) {
					e.printStackTrace();
					this.ended = true;
				}

				List<String> words = Spliter.split(received, ';');
				String command = words.get(0);
				System.out.println("received command : " + command);
				try {
					this.bw.write("nxt : " +command);
					this.bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
					this.ended = true;
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

			// Envoie les infos de base
			Tile current = this.grid.getTile(this.position.getPoint());
			this.inbox.add("UPDATE;" +
					current.getLine() + ";" + current.getCol() + ";" + this.position.getDirection() +
					";" + current.north.toString() +
					";" + current.east.toString() +
					";" + current.south.toString() +
					";" + current.west.toString()
			);
			
			// Vide la inbox
			while (this.inbox.size() > 0){
				String msg = null;
				synchronized (this.inbox) {
					msg = this.inbox.remove(0);
				}

				try {
					this.bw.write(msg+'\n');
					this.bw.flush();
					Thread.sleep(20);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		System.out.println("BT ended");
		this.btc.close();
	}
	
	public void stop () {
		this.ended = true;
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

	public void send(String msg){
		synchronized (this.inbox) {
			this.inbox.add(msg);
		}
	}

}
