package cz.vsb.mro0010.neuralnetworks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
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
public class CarDriverClient {

	private Socket socket; // spojeni
	private BufferedReader in; // cteni se serveru
	private BufferedWriter out; // zapis na server
	private BPNet neuralNetwork;
	
	/**
	 * Pripoji se k zavodu.
	 * 
	 * @param host zavodni server
	 * @param port port serveru
	 * @param raceName nazev zavodu, do nehoz se chce klient pripojit
	 * @param driverName jmeno ridice
	 * @throws java.lang.IOException  problem s pripojenim
	 */
	public CarDriverClient(String host, int port, String raceName, String driverName, String carType, BPNet neuralNetwork) throws IOException  {
		// add neural net
		this.neuralNetwork = neuralNetwork;
		
		// connect to server
		socket = new Socket(host, port);
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

		// connect to race
		out.write("driver\n");                     // protocol specification
		out.write("race:" + raceName + "\n");      // race name
		out.write("driver:" + driverName + "\n");  // driver name
		out.write("color:0000FF\n");               // car color
		if(carType != null){
			out.write("car:" + carType + "\n");  // car type
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
		
		StringBuffer neuralNetInput = new StringBuffer();
		
		
//		float angle = 0;     // uhel k care <0,1>
//		float speed = 0;     // rychlost auta <0,1>
//		float distance0 = 0;  // vzdalenost od cary <0,1>
//		float distance4 = 0; // vzdalenost od cary za 4m<0,1>
//		float distance8 = 0; // vzdalenost od cary za 8m<0,1>
//		float distance16 = 0; // vzdalenost od cary za 16m<0,1>
//		float distance32 = 0; // vzdalenost od cary za 32m<0,1>
//		float friction = 0;
//		float skid = 0;
//		float checkpoint = 0;
//        float sensorFrontLeft = 0;
//        float sensorFrontMiddleLeft = 0;
//        float sensorFrontMiddleRight = 0;
//        float sensorFrontRight = 0;
//        float sensorFrontRightCorner1 = 0;
//        float sensorFrontRightCorner2 = 0;
//        float sensorRight1 = 0;
//        float sensorRight2 = 0;
//        float sensorRearRightCorner2 = 0;
//        float sensorRearRightCorner1 = 0;
//        float sensorRearRight = 0;
//        float sensorRearLeft = 0;
//        float sensorRearLeftCorner1 = 0;
//        float sensorRearLeftCorner2 = 0;
//        float sensorLeft1 = 0;
//        float sensorLeft2 = 0;
//        float sensorFrontLeftCorner1 = 0;
//        float sensorFrontLeftCorner2 = 0;
		
		
		
//		neuralNetInput.append(String.valueOf(angle));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(speed));
//		neuralNetInput.append(" ");
		neuralNetInput.append("0.5 0.5 ");
		neuralNetInput.append(String.valueOf(distance0));
		neuralNetInput.append(" ");
		neuralNetInput.append(String.valueOf(distance4));
		neuralNetInput.append(" ");
		neuralNetInput.append(String.valueOf(distance8));
		neuralNetInput.append(" ");
		neuralNetInput.append(String.valueOf(distance16));
		neuralNetInput.append(" ");
		neuralNetInput.append(String.valueOf(distance32));
		neuralNetInput.append(" ");
		neuralNetInput.append("1 1 0.5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
//		neuralNetInput.append(String.valueOf(friction));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(skid));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(checkpoint));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontLeft));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontMiddleLeft));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontMiddleRight));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontRight));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontRightCorner1));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontRightCorner2));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRight1));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRight2));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRearRightCorner2));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRearRightCorner1));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRearRight));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRearLeft));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRearLeftCorner1));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorRearLeftCorner2));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorLeft1));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorLeft2));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontLeftCorner1));
//		neuralNetInput.append(" ");
//		neuralNetInput.append(String.valueOf(sensorFrontLeftCorner2));
		
		neuralNetwork.run(neuralNetInput.toString());
		
		String output = neuralNetwork.getOutput();
		String[] outputArray = output.split(" ");
		
		wheel = Float.parseFloat(outputArray[0]);
		acc =  Float.parseFloat(outputArray[1]);
		
				
		
		
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
	public static void main(String[] args) throws IOException {
//		String host = "java.cs.vsb.cz";
//		int port = 9460;
		String host = "localhost";
//		int port = 9461; // test
		int port = 9460; // normal
		String raceName = "Zavod";
		String driverName = "basic_client";
		String carType = null;
		if (args.length < 4) {
			// kontrola argumentu programu
			System.err.println("argumenty: server port nazev_zavodu jmeno_ridice [typ_auta]");
			List<String> raceList =  CarDriverClient.listRaces(host, port);
			raceName = raceList.get(new Random().nextInt(raceList.size()));
			List<String> carList =  CarDriverClient.listCars(host, port, raceName);
			carType = carList.get(0);
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
		// vytvoreni neuronove site
		ArrayList<Integer> nrOfNeuronsPerLayer = new ArrayList<Integer>();
		//nrOfNeuronsPerLayer.add(20);
//		nrOfNeuronsPerLayer.add(15);
//		nrOfNeuronsPerLayer.add(10);
//		nrOfNeuronsPerLayer.add(2);
		nrOfNeuronsPerLayer.add(3);
		nrOfNeuronsPerLayer.add(3);
		nrOfNeuronsPerLayer.add(2);
		BPNet neuralNet = new BPNet(0.1f, 3, 28, nrOfNeuronsPerLayer, 1.4f, 0.4f);
		
		FileReader fr = new FileReader(new File("C:\\Users\\Martin\\Desktop\\NSProjekty\\testTrainingSet5.txt"));
		StreamTokenizer tokenizer = new StreamTokenizer(fr);
		/*for (int i = 0; i < 6; i++ )
			tokenizer.nextToken();
		*/
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {} 
		
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		int nrOfLayers = (int)tokenizer.nval;
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		int nrOfInputs = (int)tokenizer.nval;
		tokenizer.nextToken();
		tokenizer.nextToken();
		tokenizer.nextToken();
		tokenizer.nextToken();
		ArrayList<float[]> inputRanges = new ArrayList<float[]>();
		ArrayList<String> inputNames = new ArrayList<String>();
		for (int i = 0; i < nrOfInputs; i++) {
			String inputName = tokenizer.sval;
			inputNames.add(inputName);
			tokenizer.nextToken();
			float[] dims = new float[2];
			dims[0] = (float)tokenizer.nval;
			tokenizer.nextToken();
			dims[1] = (float)tokenizer.nval;
			inputRanges.add(dims);
			tokenizer.nextToken();
		}
		/*for (int i = 0; i < 3; i++ )
			tokenizer.nextToken();*/
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		nrOfNeuronsPerLayer = new ArrayList<Integer>();
		int nrOfOutputs = 0;
		for (int i = 0; i < nrOfLayers; i++) {
			nrOfNeuronsPerLayer.add((int)tokenizer.nval);
			if (i == nrOfLayers - 1) {
				nrOfOutputs = (int)tokenizer.nval;
			}
			tokenizer.nextToken();
		}
		for (int i = 0; i < 3; i++ )
			tokenizer.nextToken();
		ArrayList<String> outputNames = new ArrayList<String>();
		for (int i = 0; i < nrOfOutputs; i++) {
			outputNames.add(tokenizer.sval);
			tokenizer.nextToken();
		}
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		float learnCoeff = (float)tokenizer.nval;
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		float inertiaCoeff = (float)tokenizer.nval;
		/*for (int i = 0; i < 7; i++ )
			tokenizer.nextToken();*/
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		int nrOfTrainingElements = (int)tokenizer.nval;
		/*for (int i = 0; i < 4; i++ )
			tokenizer.nextToken();*/
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nrOfTrainingElements; i++) {
			for (int j = 0; j < nrOfInputs; j++) {
				sb.append(String.valueOf(tokenizer.nval/(inputRanges.get(j)[1]-inputRanges.get(j)[0]) - inputRanges.get(j)[0]/(inputRanges.get(j)[1]-inputRanges.get(j)[0])));
				sb.append(" ");
				tokenizer.nextToken();
			}
			for (int j = 0; j < nrOfOutputs; j++) {
				sb.append(String.valueOf(tokenizer.nval));
				sb.append(" ");
				tokenizer.nextToken();
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\n");
		}
		String trainingData = sb.toString();
		sb = new StringBuffer();
		/*for (int i = 0; i < 5; i++ )
			tokenizer.nextToken();*/
		while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
		int nrOfTestElements = (int)tokenizer.nval;
		String testData;
		/*tokenizer.nextToken();*/
		if (nrOfTestElements > 0) {
			while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
			for (int i = 0; i < nrOfTestElements; i++) {
				for (int j = 0; j < nrOfInputs; j++) {
					sb.append(String.valueOf(String.valueOf(tokenizer.nval/(inputRanges.get(j)[1]-inputRanges.get(j)[0]) - inputRanges.get(j)[0]/(inputRanges.get(j)[1]-inputRanges.get(j)[0]))));
					sb.append(" ");
					tokenizer.nextToken();
				}
				sb.deleteCharAt(sb.lastIndexOf(" "));
				sb.append("\n");
			}
			testData = sb.substring(0,sb.lastIndexOf("\n"));
		} else {
			testData = "";
		}
		fr.close();
		
		String trainingSet = trainingData;
		System.out.println("Learning started.");
		;
		System.out.println("Net learned in " + neuralNet.learn(trainingSet) + " iterations");
		
		// vytvoreni klienta
		CarDriverClient driver = new CarDriverClient(host, port, raceName, driverName, carType, neuralNet);
		// spusteni
		driver.run();
	}
}
