package cz.vsb.mro0010.neuralnetworks;

public class BinaryNeuron extends Neuron {

	@Override
	public void transfer() {
		if (this.getPotential() > this.getThreshold()) {
			this.setState(1);
		} else {
			this.setState(0);
		}

	}

}
