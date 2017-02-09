package cz.vsb.mro0010.neuralnetworks;

public class InvalidNeuronTypeException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5354372081840990196L;

	public InvalidNeuronTypeException() {
		super("Wrong Neuron type");
	}
}
