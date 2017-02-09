package cz.vsb.mro0010.neuralnetworks;

public class InterconnectionsBP extends InterconnectionsMultiLayer {

	public InterconnectionsBP(float learningRate) {
		super(learningRate);
	}
	
	public void setLearningRate(float learningRate) {
		this.learningRate = learningRate;
	}
	
	@Override
	public void adjustWeights() { // backPropagation - set new weights !after! all deltas are calculated
		for (Connection connection : this.connections) {
			float delta = ((SigmoidalNeuron)connection.getOutputNeuron()).getError();
			float lambda = ((SigmoidalNeuron)connection.getOutputNeuron()).getSlope();
			float y = connection.getOutputNeuron().getState();
			float x = connection.getInputNeuron().getState();
			float errorDerivative = delta*lambda*y*(1-y)*x;
			connection.adjustWeight(-learningRate*errorDerivative);
		}
	}

}
