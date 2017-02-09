package cz.vsb.cs.neurace.basicClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;


/**
 * Jednoduchy ukazkovy klient.
 * Pripoji se k zavodnimu serveru a ridi auto.
 * 
 */
public class BasicClient {

	private Socket socket; // spojeni
	private BufferedReader in; // cteni se serveru
	private BufferedWriter out; // zapis na server

	/**
	 * Pripoji se k zavodu.
	 * 
	 * @param host zavodni server
	 * @param port port serveru
	 * @param raceName nazev zavodu, do nehoz se chce klient pripojit
	 * @param driverName jmeno ridice
	 * @throws java.lang.IOException  problem s pripojenim
	 */
	public BasicClient(String host, int port, String raceName, String driverName, String carType) throws IOException  {
		// pripojeni k serveru
		socket = new Socket(host, port);
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

		// pripojeni k zavodu
		out.write("driver\n");                     // specifikace protokolu
		out.write("race:" + raceName + "\n");      // nazev zavodu
		out.write("driver:" + driverName + "\n");  // jmeno ridice
		out.write("color:0000FF\n");               // barva auta
		if(carType != null){
			out.write("car:" + carType + "\n");  // jmeno ridice
		}
		out.write("\n");
		out.flush();

		// precteni a kontrola dopovedi serveru
		String line = in.readLine();
		if (!line.equals("ok")) {
			// pokud se pripojeni nepodari, je oznamena chyba a vyvolana vyjimka
			System.err.println("Chyba: " + line);
			throw new ConnectException(line);
		}
		in.readLine();  // precteni prazdneho radku
	}

	public static List<String> listRaces(String host, int port) throws IOException  {
		// pripojeni k serveru
		Socket socket = new Socket(host, port);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

		// pripojeni k zavodu
		out.write("racelist\n");                     // specifikace protokolu
		out.write("\n");
		out.flush();

		// precteni a kontrola dopovedi serveru
		String line = in.readLine();
		if (!line.equals("ok")) {
			// pokud se pripojeni nepodari, je oznamena chyba a vyvolana vyjimka
			System.err.println("Chyba: " + line);
			throw new ConnectException(line);
		}
		line = in.readLine();  // precteni prazdneho radku
		List<String> racelist = new ArrayList<String>();
		line = in.readLine();
		System.out.println("Races:");
		while(line != null && !"".equals(line)){
			racelist.add(line);
			System.out.println(line);
			line = in.readLine();
		}
		return racelist;
	}
	public static List<String> listCars(String host, int port, String raceName) throws IOException  {
		// pripojeni k serveru
		Socket socket = new Socket(host, port);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

		// pripojeni k zavodu
		out.write("carlist\n");                     // specifikace protokolu
		out.write("race:" + raceName + "\n");
		out.write("\n");
		out.flush();

		// precteni a kontrola dopovedi serveru
		String line = in.readLine();
		if (!line.equals("ok")) {
			// pokud se pripojeni nepodari, je oznamena chyba a vyvolana vyjimka
			System.err.println("Chyba: " + line);
			throw new ConnectException(line);
		}
		line = in.readLine();  // precteni prazdneho radku
		List<String> carList = new ArrayList<String>();
		line = in.readLine();
		System.out.println("cars:");
		while(line != null && !"".equals(line)){
			carList.add(line);
			System.out.println(line);
			line = in.readLine();
		}
		return carList;
	}

	/**
	 * Beh zavodu. Cte data ze serveru. Spousti rizeni auta. 
	 * Ukonci se po ukonceni zavodu.
	 * 
	 * @throws java.io.IOException  problem ve spojeni k serveru
	 */
	public void run() throws IOException {
		while (true) {							// smycka do konce zavodu
			String line = in.readLine();
//			System.out.println(line);
			if (line.equals("round")) {			// dalsi kolo v zavode
				round();
			} else if (line.equals("finish")) {	// konec zavodu konci smucku
				break;
			} else {
				System.err.println("Chyba se serveru: " + line);
			}
		}
	}

	/**
	 * Resi jedno posunuti auta. Precte pozici auta od servru,
	 * vypocte nastaveni rizeni, ktere na server.
	 * 
	 * @throws java.io.IOException   problem ve spojeni k serveru
	 */
	public void round() throws IOException {
		float angle = 0;     // uhel k care <0,1>
		float speed = 0;     // rychlost auta <0,1>
		float distance0 = 0;  // vzdalenost od cary <0,1>
		float distance4 = 0; // vzdalenost od cary za 4m<0,1>
		float distance8 = 0; // vzdalenost od cary za 8m<0,1>
		float distance16 = 0; // vzdalenost od cary za 16m<0,1>
		float distance32 = 0; // vzdalenost od cary za 32m<0,1>
		float friction = 0;
		float skid = 0;
		float checkpoint = 0;
        float sensorFrontLeft = 0;
        float sensorFrontMiddleLeft = 0;
        float sensorFrontMiddleRight = 0;
        float sensorFrontRight = 0;
        float sensorFrontRightCorner1 = 0;
        float sensorFrontRightCorner2 = 0;
        float sensorRight1 = 0;
        float sensorRight2 = 0;
        float sensorRearRightCorner2 = 0;
        float sensorRearRightCorner1 = 0;
        float sensorRearRight = 0;
        float sensorRearLeft = 0;
        float sensorRearLeftCorner1 = 0;
        float sensorRearLeftCorner2 = 0;
        float sensorLeft1 = 0;
        float sensorLeft2 = 0;
        float sensorFrontLeftCorner1 = 0;
        float sensorFrontLeftCorner2 = 0;
		
		// cteni dat ze serveru
		String line = in.readLine();
//		System.out.println(line);
		while (line.length() > 0) {
			String[] data = line.split(":", 2);
			String key = data[0];
			String value = data[1];
			if (key.equals("angle")) {
				angle = Float.parseFloat(value);
			} else if (key.equals("speed")) {
				speed = Float.parseFloat(value);
			} else if (key.equals("distance0")) {
				distance0 = Float.parseFloat(value);
			} else if (key.equals("distance4")) {
				distance4 = Float.parseFloat(value);
			} else if (key.equals("distance8")) {
				distance8 = Float.parseFloat(value);
			} else if (key.equals("distance16")) {
				distance16 = Float.parseFloat(value);
			} else if (key.equals("distance32")) {
				distance32 = Float.parseFloat(value);
			} else if (key.equals("friction")) {
				friction = Float.parseFloat(value);
			} else if (key.equals("skid")) {
				skid = Float.parseFloat(value);
			} else if (key.equals("checkpoint")) {
				checkpoint = Float.parseFloat(value);
			} else if (key.equals("sensorFrontLeft")) {
				sensorFrontLeft = Float.parseFloat(value);
			} else if (key.equals("sensorFrontMiddleLeft")) {
				sensorFrontMiddleLeft = Float.parseFloat(value);
			} else if (key.equals("sensorFrontMiddleRight")) {
				sensorFrontMiddleRight = Float.parseFloat(value);
			} else if (key.equals("sensorFrontRight")) {
				sensorFrontRight = Float.parseFloat(value);
			} else if (key.equals("sensorFrontRightCorner1")) {
				sensorFrontRightCorner1 = Float.parseFloat(value);
			} else if (key.equals("sensorFrontRightCorner2")) {
				sensorFrontRightCorner2 = Float.parseFloat(value);
			} else if (key.equals("sensorRight1")) {
				sensorRight1 = Float.parseFloat(value);
			} else if (key.equals("sensorRight2")) {
				sensorRight2 = Float.parseFloat(value);
			} else if (key.equals("sensorRearRightCorner2")) {
				sensorRearRightCorner2 = Float.parseFloat(value);
			} else if (key.equals("sensorRearRightCorner1")) {
				sensorRearRightCorner1 = Float.parseFloat(value);
			} else if (key.equals("sensorRearRight")) {
				sensorRearRight = Float.parseFloat(value);
			} else if (key.equals("sensorRearLeft")) {
				sensorRearLeft = Float.parseFloat(value);
			} else if (key.equals("sensorRearLeftCorner1")) {
				sensorRearLeftCorner1 = Float.parseFloat(value);
			} else if (key.equals("sensorRearLeftCorner2")) {
				sensorRearLeftCorner2 = Float.parseFloat(value);
			} else if (key.equals("sensorLeft1")) {
				sensorLeft1 = Float.parseFloat(value);
			} else if (key.equals("sensorLeft2")) {
				sensorLeft2 = Float.parseFloat(value);
			} else if (key.equals("sensorFrontLeftCorner1")) {
				sensorFrontLeftCorner1 = Float.parseFloat(value);
			} else if (key.equals("sensorFrontLeftCorner2")) {
				sensorFrontLeftCorner2 = Float.parseFloat(value);
			} else {
				System.err.println("Chyba se serveru: " + line);
			}
			line = in.readLine();
//			System.out.println(line);
		}

		// vypocet nastaveni rizeni, ktery je mozno zmenit za jiny algoritmus
		float acc;	// zrychleni auta <0,1>
		float wheel; // otoceni volantem (kolama) <0,1>
		
		// pokud je v levo jede doprava, jinak do leva
		if (distance0 < 0.5) {
			wheel = 0.8f;
		} else {
			wheel = 0.2f;
		}
		// maximalni zrychleni
		acc = 1f;

		// odpoved serveru
		out.write("ok\n");
		out.write("acc:" + acc + "\n");
		out.write("wheel:" + wheel + "\n");
		out.write("\n");
		out.flush();
	}

	/**
	 * Funkce, ktera vytvari a spousti klienta.
	 * 
	 * @param args pole argumentu: server port nazev_zavodu jmeno_ridice
	 * @throws java.io.IOException problem ve spojeni k serveru, zavodu
	 */
	/*public static void main(String[] args) throws IOException {
//		String host = "java.cs.vsb.cz";
//		int port = 9460;
		String host = "localhost";
		int port = 9460;
		String raceName = "Zavod";
		String driverName = "basic_client";
		String carType = null;
		if (args.length < 4) {
			// kontrola argumentu programu
			System.err.println("argumenty: server port nazev_zavodu jmeno_ridice [typ_auta]");
			List<String> raceList =  BasicClient.listRaces(host, port);
			raceName = raceList.get(new Random().nextInt(raceList.size()));
			List<String> carList =  BasicClient.listCars(host, port, raceName);
			carType = carList.get(new Random().nextInt(carList.size()));
			driverName += "_" + carType;
//			host = JOptionPane.showInputDialog("Host:", host);
//			port = Integer.parseInt(JOptionPane.showInputDialog("Port:", Integer.toString(port)));
//			raceName = JOptionPane.showInputDialog("Race name:", raceName);
//			driverName = JOptionPane.showInputDialog("Driver name:", driverName);
		} else {
			// nacteni parametu
			host = args[0];
			port = Integer.parseInt(args[1]);
			raceName = args[2];
			driverName = args[3];
			if(args.length > 4){
				carType = args[4];
			}
		}
		// vytvoreni klienta
		BasicClient driver = new BasicClient(host, port, raceName, driverName, carType);
		// spusteni
		driver.run();
	}*/
}
