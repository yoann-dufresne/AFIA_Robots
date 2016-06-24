package bluetooth;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import api.Observable;
import api.Observer;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import main.AbstractMain;
import main.MainExploitation;
import main.MainExplorer;
import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;
import util.Spliter;

public class BluetoothRobot extends Observable implements Runnable  {
	public static BluetoothRobot bt;
	private boolean started;
	
	private Position position;
	private Grid grid;
	public Point destination;
	public boolean conflict;
	
	private BTConnection btc;
	private boolean ended;
	private boolean lock; 
	
	private BufferedReader br;
	private BufferedWriter bw;

	private String command;
	private List<String> inbox;
	public AbstractMain main;

	public int id;
	public Position otherPosition;
	
	public Point beginningCorner;
	
	public BluetoothRobot(Position pos, Grid grid) {
		BluetoothRobot.bt = this;
		this.started = false;
		this.ended = true;
		this.inbox = new ArrayList<String>();
		
		this.position = pos;
		this.otherPosition = new Position(0, 0, Direction.NORTH);
		this.grid = grid;
		this.main = new MainExploitation(grid, pos);
		
		this.beginningCorner = new Point(0,0);
		this.destination = new Point(-1, -1);
		
		this.conflict = false;
		this.lock= true;
		
		System.out.println("BT waiting");
		this.btc = Bluetooth.waitForConnection();
		System.out.println("BT connected");

        DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
		this.br = new BufferedReader(new InputStreamReader(dis));
		this.bw = new BufferedWriter(new OutputStreamWriter(dos));
	}

	@Override
	public void run() {
		this.ended = false;

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
				} else if ("START".equals(command)){
					this.start(words);
				} else if ("STOP".equals(command)){
					this.stop(words);
				} else if ("INIT".equals(command)){
					this.init(words);
					System.out.println(command);
				} else if ("SETID".equals(command)){
					this.setID(words);
				} else if ("LOCK".equals(command)){
					this.lock(words);
				} else if ("CONFLICT".equals(command)){
					this.conflict(words);
				} else if ("NEXT_POS".equals(command)){
					this.majPosition(words);
				} else {
					System.out.println("Unknown command");
				}
			}

			// Envoie les infos de base
			Tile current = this.grid.getTile(this.position.getPoint());
			String up = "UPDATE;" +
					current.getLine() + ";" + current.getCol() + ";" + this.position.getDirection().toString() +
					";" + current.north.toString() +
					";" + current.east.toString() +
					";" + current.south.toString() +
					";" + current.west.toString();
				
			synchronized (this.inbox) {
				this.inbox.add(up);
			
				// Vide la inbox
				while (this.inbox.size() > 0){
					String msg = null;
					msg = this.inbox.remove(0);
	
					try {
						this.bw.write(msg+'\n');
						this.bw.flush();
						Thread.sleep(5);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				Thread.sleep(100);
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
	
	private void init(List<String> words) {
		int width = new Integer(words.get(2));
		int height = new Integer(words.get(3));
		this.grid = new Grid(height, width);
		
		int line = new Integer(words.get(4));
		int col = new Integer(words.get(5));
		Direction dir = Direction.values()[new Integer(words.get(6))];
		
		this.position = new Position(line + 0.5, col + 0.5, dir);
		this.beginningCorner = new Point(line,col); 
				
		int idMain = new Integer(words.get(1));
		System.out.println("Main " + idMain);
		this.main = idMain == 0 ? new MainExplorer(this.grid, this.position) : new MainExploitation(this.grid, this.position);
	}

	private void start(List<String> words) {
		this.started = true;
		System.out.println("Go !!!");
	}
	
	private void stop(List<String> words) {
		System.out.println("STOP");
		this.ended = true;
	}

	private void partial(List<String> words) {
		String word = words.get(0);
		if (word.startsWith("?"))
		this.destination = new Point(new Integer(words.get(1)), new Integer(words.get(2)));
		this.command = "PARTIAL";
		this.notifyObservers();
		
		System.out.println("PARTIAL");
	}

	private void discover(List<String> words) {
		int x = new Integer(words.get(1));
		int y = new Integer(words.get(2));
		Direction dir = Direction.values()[new Integer(words.get(3))];
		WallState state = WallState.values()[new Integer(words.get(4))];
		int quality = new Integer(words.get(5));
		
		try {
			int proba = this.grid.getProba(x, y, dir);
			if (quality > proba)
				return;
			
			if (quality == proba && state == WallState.Wall)
				return;
			
			this.grid.setState(x, y, dir, state);
		} catch (IllegalArgumentException e) {};
	}
	
	private void conflict(List<String> words) {
		this.conflict = true;
		this.notifyObservers();
		System.out.println("CONFLICT");
	}

	private void majPosition(List<String> words) {
		this.otherPosition.setX(new Integer(words.get(1)));
		this.otherPosition.setY(new Integer(words.get(2)));
	}

	private void setID(List<String> words){
		this.id= new Integer(words.get(1));
		System.out.println("IDDDDDDDDD: "+this.id);
	}
	
	private void lock(List<String> words){
		if (this.id = new Integer(words.get(1))){
			this.lock = new Boolean(words.get(2).toLowerCase());
		}
	}
	
	
	public void send(String msg){
		synchronized (this.inbox) {
			this.inbox.add(msg);
		}
	}
	

	public boolean started() {
		return this.started;
	}

	@Override
	public void notifyObservers() {
		for (Observer obs : this.observers)
			obs.update(this, this.command);
	}

}
