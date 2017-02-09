package cz.vsb.mro0010.neuralnetworks;

public class SigmoidalNeuron extends Neuron {

	private float error; //delta
	private float slope; //lambda
	
	


	public void setSlope(float slope) {
		this.slope = slope;
	}


	public SigmoidalNeuron(float slope) {
		this.slope = slope;
		this.error = 0;
	}
	
	
	@Override
	public void transfer() {
		float z = this.getPotential();
		float y = (float) (1.0/(1.0 + Math.exp(-slope*z)));
		this.setState(y);
	}


	public float getSlope() {
		return slope;
	}
	
	public float getError() {
		return error;
	}


	public void setError(float error) {
		this.error = error;
	}

	
	public static void main(String args[]) {
		SigmoidalNeuron neuron = new SigmoidalNeuron((float)0.5);
		for (int i = -10; i <= 10; i++) {
			neuron.initialize();
			neuron.adjustPotential(i);
			neuron.transfer();
			System.out.println(neuron.getState());
		}
	}
}
