package cz.vsb.mro0010.neuralnetworks;

public class InputLayerPseudoNeuron extends Neuron {

	public InputLayerPseudoNeuron() {
		super();
	}
	
	@Override
	public void transfer() {
		this.setState(this.getPotential());
	}

}
