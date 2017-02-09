package cz.vsb.mro0010.neuralnetworks;

public class InvalidLayerNumberException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1366940285989358521L;

	public InvalidLayerNumberException() {
		super("Number of layer does not correspond with network");
	}
}
