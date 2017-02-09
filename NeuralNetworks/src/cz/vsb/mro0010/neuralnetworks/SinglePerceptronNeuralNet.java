package cz.vsb.mro0010.neuralnetworks;

import java.util.ArrayList;
import java.util.Arrays;

public class SinglePerceptronNeuralNet extends NeuralNet {

	private Neuron neuron;
	private int nrOfInputs;
	private ArrayList<Connection> connections;
	private ArrayList<InputLayerPseudoNeuron> input;
	private String trainingOutput;
	private float learnCoef;
	
	public SinglePerceptronNeuralNet(Neuron neuron, int nrOfInputs, float learnCoef) {
		super();
		this.neuron = neuron;
		this.nrOfInputs = nrOfInputs;
		this.input = new ArrayList<InputLayerPseudoNeuron>();
		this.connections = new ArrayList<Connection>();
		for (int i = 0; i < this.nrOfInputs; i++) {
			InputLayerPseudoNeuron inputNeuron = new InputLayerPseudoNeuron();
			this.input.add(inputNeuron);
			this.connections.add(new Connection(inputNeuron, neuron, (float)Math.random()));
		}
		this.setTrainingOutput(" ");
		this.learnCoef = learnCoef;
	}
	
	@Override
	public String getNeuronType() {
		return neuron.getClass().getSimpleName();
	}

	@Override
	public int learn(String trainingSet) {
		ArrayList<String> trainingElements = new ArrayList<String>(Arrays.asList(trainingSet.split("\n")));
		boolean learned = false;
	    int iterations = 0;
	    StringBuffer trainingProgress = new StringBuffer();
	    for (Connection c : connections) {
	    trainingProgress.append(String.valueOf(c.getWeight()));
	    trainingProgress.append(" ");
	    }
	    trainingProgress.append(String.valueOf(-neuron.getThreshold()));
	    trainingProgress.append("\n");
		while (!learned) {
			iterations++;
			learned = true;
			for (String element : trainingElements) {
				String[] sa = element.split(" ");
				String expectedOutput = sa[sa.length - 1];
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < sa.length - 1; i++) {
					sb.append(sa[i]);
					sb.append(" ");
				}
				this.run(sb.toString());
				
				if (Float.parseFloat(expectedOutput) != Float.parseFloat(this.getOutput())) {
					learned = false;
					float eo = Float.parseFloat(expectedOutput);
					float ro = Float.parseFloat(this.getOutput());
					neuron.setThreshold(neuron.getThreshold() + learnCoef*-(eo-ro)*1); // w_0 = -threshold
					for (Connection c : connections) {
						c.adjustWeight(learnCoef*(eo-ro)*c.getInputNeuron().getState());
					}
					for (Connection c : connections) {
					    trainingProgress.append(String.valueOf(c.getWeight()));
					    trainingProgress.append(" ");
					}
				    trainingProgress.append(String.valueOf(neuron.getThreshold()));
				    trainingProgress.append("\n");
				}
			}
		}
		//System.out.println("Learned! in " + (iterations-1) + " iterations");
		this.setTrainingOutput(trainingProgress.toString());
		return iterations;
	}

	@Override
	public void run(String inputString) {
		String[] input = inputString.split(" ");
		for (int i = 0; i < input.length; i++) {
			InputLayerPseudoNeuron in = this.input.get(i);
			in.initialize();
			in.adjustPotential(Float.parseFloat(input[i]));
			in.transfer();
		}
		neuron.initialize();
		for (Connection c : connections) {
			c.passSignal();
		}
		neuron.transfer();
		
	}
	
	public String getOutput() {
		String output = String.valueOf(neuron.getState());
		return output;
	}
	
	public String getTrainingOutput() {
		return trainingOutput;
	}

	private void setTrainingOutput(String trainingOutput) {
		this.trainingOutput = trainingOutput;
	}
	
	/*public static void main(String[] args) {
		SinglePerceptronNeuralNet net = new SinglePerceptronNeuralNet(new BinaryNeuron(), 2, (float)0.7);
		net.neuron.setThreshold((float) Math.random());
//		String learnSet = "1 0.5 0\n0.4 0.8 1\n0.1 0.1 0\n0.6 0.9 1\n0.8 0.7 0\n0.4 1.0 1";
//		net.learn(learnSet);
//		net.run("0.7 0.9");
//		System.out.println(net.getOutput());
//		net.run("0.9 0.7");
//		System.out.println(net.getOutput());
//		net.run("0.2 0.2");
//		System.out.println(net.getOutput());
//		net.run("0.1 1.0");
//		System.out.println(net.getOutput());
//		net.run("1.0 0.1");
//		System.out.println(net.getOutput());
		String learnSet = "0.7 0.3 0\n0.2 0.6 1\n0.3 0.4 1\n0.9 0.8 0\n0.1 0.2 1\n0.5 0.6 1";
		net.learn(learnSet);
		net.run("0.7 0.9");
		System.out.println(net.getOutput());
		net.run("0.9 0.7");
		System.out.println(net.getOutput());
		net.run("0.2 0.2");
		System.out.println(net.getOutput());
		net.run("0.1 1.0");
		System.out.println(net.getOutput());
		net.run("1.0 0.1");
		System.out.println(net.getOutput());
		net.run("0.6 0.5");
		System.out.println(net.getOutput());
		net.run("0.5 0.6");
		System.out.println(net.getOutput());
	}*/

	

}
