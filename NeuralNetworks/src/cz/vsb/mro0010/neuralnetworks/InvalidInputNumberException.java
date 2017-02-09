package cz.vsb.mro0010.neuralnetworks;

public class InvalidInputNumberException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6282750644609100469L;

	public InvalidInputNumberException() {
		super("Number of input values does not correspond with network input size");
	}
	
}
