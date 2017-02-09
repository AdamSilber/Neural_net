package cz.vsb.mro0010.neuralnetworks;

import java.util.ArrayList;

public abstract class MultiLayeredNet extends NeuralNet {

	protected ArrayList<ArrayList<Neuron>> neuronLayers;
	protected ArrayList<InputLayerPseudoNeuron> inputNeuronLayer;
	protected int nrOfInputs;
	protected int nrOfLayers;
	protected ArrayList<Integer> nrOfNeuronsPerLayer;
	
	public MultiLayeredNet(int nrOfInputs, int nrOfLayers, ArrayList<Integer> nrOfNeuronsPerLayer) {
		super();
		this.nrOfInputs = nrOfInputs;
		this.nrOfLayers = nrOfLayers;
		this.nrOfNeuronsPerLayer = nrOfNeuronsPerLayer;
		neuronLayers = new ArrayList<ArrayList<Neuron>>(nrOfLayers);
		inputNeuronLayer = new ArrayList<InputLayerPseudoNeuron>(nrOfInputs);
		for (int i = 0; i < nrOfLayers; i++) {
			neuronLayers.add(new ArrayList<Neuron>(nrOfNeuronsPerLayer.get(i)));
		}
		for (int i = 0; i < nrOfInputs; i++) {
			inputNeuronLayer.add(new InputLayerPseudoNeuron());
		}
	}
	
	public MultiLayeredNet() {
		this(0,0,null);
	}
	
	public int getNrOfInputs() {
		return nrOfInputs;
	}

	public int getNrOfLayers() {
		return nrOfLayers;
	}

	@Override
	public void run(String input) {
		String[] inputValues = input.split(" ");
		if (inputValues.length != nrOfInputs) 
			throw new InvalidInputNumberException();
		for (int i = 0; i < nrOfInputs; i++) {
			InputLayerPseudoNeuron in = this.inputNeuronLayer.get(i);
			in.initialize();
			in.adjustPotential(Float.parseFloat(inputValues[i]));
			in.transfer();
		}
			
		for (int i = 0; i < nrOfLayers; i++) {
			Interconnections interconnectionsLayer = interconnectionsLayers.get(i);
			interconnectionsLayer.passSignal();
		}
	}

}
