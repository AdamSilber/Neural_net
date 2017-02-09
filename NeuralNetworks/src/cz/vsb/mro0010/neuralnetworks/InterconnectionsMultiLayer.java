package cz.vsb.mro0010.neuralnetworks;

public abstract class InterconnectionsMultiLayer extends Interconnections {

	protected float learningRate; //eta
	
	public InterconnectionsMultiLayer(float learningRate) {
		this.learningRate = learningRate;
	}

}
