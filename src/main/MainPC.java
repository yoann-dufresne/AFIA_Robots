package main;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

/**
 * This is a PC sample. It connects to the NXT, and then
 * sends an integer and waits for a reply, 100 times.
 *
 * Run the program by:
 *
 *   make runPC
 *
 * Your NXT should be running a sample such as BTReceive or
 * SignalTest. Run the NXT program first until it is
 * waiting for a connection, and then run the PC program.
 *
 * @author Lawrie Griffiths
 *
 */
public class MainPC {
	public static void main(String[] args) {
		NXTConnector conn0 = new NXTConnector();
		NXTConnector conn2 = new NXTConnector();

		String macNxt0 = "00:16:53:13:EF:A9";
		String macNxt2 = "00:16:53:0F:F5:A9";

		conn0.addLogListener(new NXTCommLogListener(){

			public void logEvent(String message) {
				System.out.println("BTSend Log.listener: "+message);

			}

			public void logEvent(Throwable throwable) {
				System.out.println("BTSend Log.listener - stack trace: ");
				throwable.printStackTrace();

			}

		}
				);
		// Connect to any NXT over Bluetooth
		boolean connected0 = conn0.connectTo("btspp://" + macNxt0);
		boolean connected2 = conn2.connectTo("btspp://" + macNxt2);

		BufferedWriter bw0 = null;
		BufferedWriter bw2 = null;

		DataOutputStream dos0 = null, dos2 = null;
		DataInputStream dis0 = null, dis2 = null;

		if (!connected0) {
			System.err.println("Failed to connect to: " + macNxt0);
			//System.exit(1);
		}
		else{
			dos0= new DataOutputStream(conn0.getOutputStream());
			bw0 = new BufferedWriter(new OutputStreamWriter(dos0));
			dis0 = new DataInputStream(conn0.getInputStream());

		}

		if (!connected2) {
			System.err.println("Failed to connect to: "  + macNxt2);
			//System.exit(1);
		}
		else{
			dos2 = new DataOutputStream(conn2.getOutputStream());
			bw2 = new BufferedWriter(new OutputStreamWriter(dos2));
			dis2 = new DataInputStream(conn2.getInputStream());
		}



		try {
			if (connected0) {
				bw0.write("DISCOVERED\n");
				bw0.flush();
			}

			if (connected2) {
				bw2.write("DISCOVERED\n");
				bw2.flush();
			}

			System.out.println("wrote discovered");
			Thread.sleep(1000);


			if (connected0) {
				bw0.write("PARTIAL\n");
				bw0.flush();
			}

			if (connected2) {
				bw2.write("PARTIAL\n");
				bw2.flush();
			}

			System.out.println("wrote partiak");
			Thread.sleep(1000);

			if (connected0) {
				bw0.write("plop\n");
				bw0.flush();
			}
			if (connected2) {
				bw2.write("plop\n");
				bw2.flush();
			}
			System.out.println("wrote plop");
			Thread.sleep(1000);

			if (connected0) {
				bw0.write("STOP\n");
				bw0.flush();
			}

			if (connected2) {
				bw2.write("STOP\n");
				bw2.flush();
			}
			System.out.println("wrote stop");
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		try {
			if (connected0) {
				dis0.close();
				dos0.close();
				conn0.close();
			}

			if (connected2) {
				dis2.close();
				dos2.close();
				conn2.close();
			}

		} catch (IOException ioe) {
			System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
	}
}
