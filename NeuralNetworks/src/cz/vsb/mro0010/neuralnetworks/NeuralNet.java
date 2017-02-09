package cz.vsb.mro0010.neuralnetworks;

import java.util.ArrayList;

public abstract class NeuralNet {

	protected ArrayList<Interconnections> interconnectionsLayers;
	
	public NeuralNet(ArrayList<Interconnections> interconnectionsLayers) {
		this.interconnectionsLayers = interconnectionsLayers;
	}
	
	public NeuralNet() {
		this(new ArrayList<Interconnections>());
	}

	public abstract String getNeuronType();
	public abstract int learn(String trainingSet);
	public abstract void run(String input);
}
