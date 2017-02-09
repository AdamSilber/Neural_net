package cz.vsb.mro0010.neuralnetworks;

public class Connection {

	private Neuron inputNeuron;
	private Neuron outputNeuron;
	private float weight;
	
	public Connection(Neuron inputNeuron, Neuron outputNeuron, float weight) {
		this.setInputNeuron(inputNeuron);
		this.setOutputNeuron(outputNeuron);
		this.setWeight(weight);
	}
	
	protected Neuron getInputNeuron() {
		return inputNeuron;
	}
	
	protected void setInputNeuron(Neuron inputNeuron) {
		this.inputNeuron = inputNeuron;
	}

	protected Neuron getOutputNeuron() {
		return outputNeuron;
	}

	protected void setOutputNeuron(Neuron outputNeuron) {
		this.outputNeuron = outputNeuron;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public void adjustWeight(float value) {
		this.weight += value;
	}
	
	public void passSignal() {
		outputNeuron.adjustPotential(inputNeuron.getState()*this.getWeight());
	}
	
	@Override
	public String toString() {
		return "Weight: " + this.getWeight();
	}
	
}
