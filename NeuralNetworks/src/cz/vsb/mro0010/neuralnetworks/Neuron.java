package cz.vsb.mro0010.neuralnetworks;

public abstract class Neuron {
	
	private float potential; // inner potential
	private float state; // excitation state
	private float threshold; // threshold of excitation
	
	
	public Neuron() {
		this(0, 0, 0);
	}
	
	public Neuron(float potential, float state, float threshold) {
		this.setPotential(potential);
		this.setState(state);
		this.setThreshold(threshold);
	}
	
	public void initialize() {
		this.setPotential(0);
		this.setState(0);
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getState() {
		return state;
	}

	protected void setState(float state) {
		this.state = state;
	}

	protected float getPotential() {
		return this.potential;
	}
	
	private void setPotential(float potential) {
		this.potential = potential;
	}
	
	public void adjustPotential(float value) {
		this.potential += value;
	}
	
	@Override
	public String toString() {
		return "Pot.: " + this.potential + ", State: " + this.state + ", Thr.: " + this.threshold;
	}
	
	public abstract void transfer();
}
