package bluetooth;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.TimerTask;

public class PeriodicBt implements Runnable {
	private BufferedWriter bw;

	public PeriodicBt(BufferedWriter bw){
		this.bw = bw;
	}
	
    public void run() {
		for(int i = 0; i < 100; i++){
    		try {
				this.bw.write("nxt periodic : " + i );
				this.bw.flush();
				System.out.println("periodic send : " + i);
				i += 1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 
}
