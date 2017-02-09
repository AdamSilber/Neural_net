package cz.vsb.mro0010.neuralnetworks;

import java.util.ArrayList;
import java.util.Arrays;

public class BPNet extends MultiLayeredNet {

	protected float error;
	
	
	public float getError() {
		return error;
	}


	public void setError(float error) {
		this.error = error;
	}


	protected float tolerance;
	protected String neuronType;
	protected float learnCoeff;
	

	
	public BPNet( float tolerance, int nrOfLayers, int nrOfInputs, ArrayList<Integer> nrOfNeuronsPerLayer, float slope, float learnCoeff ) {
		super(nrOfInputs, nrOfLayers, nrOfNeuronsPerLayer);
		this.neuronType = "SigmoidalNeuron";
		this.tolerance = tolerance;
		this.learnCoeff = learnCoeff;
		
		for (int i = 0; i < nrOfLayers; i++) {
			for (int j = 0; j < nrOfNeuronsPerLayer.get(i); j++) {
				this.neuronLayers.get(i).add(new SigmoidalNeuron(slope));
			}
		}
		for (int i = 0; i < nrOfLayers; i++) {
			this.interconnectionsLayers.add(new InterconnectionsBP(this.learnCoeff));
		}
		for (Neuron neuronIn : this.inputNeuronLayer) {
			for (Neuron neuronFirstLevel : this.neuronLayers.get(0)) {
				this.interconnectionsLayers.get(0).addConnection(new Connection(neuronIn, neuronFirstLevel, (float) (Math.random())));
			}
		}
		for (int i = 1; i < nrOfLayers; i++) {
			for (Neuron neuronIn : this.neuronLayers.get(i-1)) {
				for (Neuron neuronOut : this.neuronLayers.get(i)) {
					this.interconnectionsLayers.get(i).addConnection(new Connection(neuronIn, neuronOut, (float) (Math.random())));
				}
				
			}
		}
		
		
	}
	
	
	public float getTolerance() {
		return tolerance;
	}

	public void setTolerance(float tolerance) {
		this.tolerance = tolerance;
	}

	
	@Override
	public String getNeuronType() {
		return this.neuronType;
	}

	@Override
	public int learn(String trainingSet) {
		boolean learned = false;
		int iter = 0;
		ArrayList<String> trainingElements = new ArrayList<String>(Arrays.asList(trainingSet.split("\n")));
		while(!learned) {
			learned = true;
			this.error = 0;
			for (int i = 0; i < trainingElements.size(); i++) {
				learned &= learnStep(trainingElements.get(i));
			}
			iter++;
//			System.out.println(iter);
		}
		return  iter;
//		System.out.println("Learned in " + iter + " whole training set iterations.");
	}
	
	public boolean learnStep(String trainingElement) {
		// Run training Element
		String[] splitedTrainingElement = trainingElement.split(" ");
		StringBuffer inputString = new StringBuffer();
		for (int i = 0; i < this.nrOfInputs; i++) { //Input values
			inputString.append(splitedTrainingElement[i]);
			inputString.append(" ");
		}
		ArrayList<Float> expectedValues = new ArrayList<Float>();
		for (int i = this.nrOfInputs; i < splitedTrainingElement.length; i++) { //Expected values
			expectedValues.add(Float.parseFloat(splitedTrainingElement[i]));
		}
		this.run(inputString.substring(0, inputString.length() - 1));
		// Calculate error
		float error = 0;
		for (int i = 0; i < expectedValues.size(); i++) {
			float y = this.neuronLayers.get(this.nrOfLayers-1).get(i).getState(); //output of ith neuron
			float o = expectedValues.get(i);
			error += (float)( 0.5 * Math.pow((y-o), 2));
		}
		if (this.error < error) {
			this.error = error;
		}
		if (error > this.tolerance) { //Error is too high -> modify weights
			// Calculate deltas
			for (int i = this.nrOfLayers - 1; i >= 0; i -= 1) {
				for (Neuron n : this.neuronLayers.get(i)) {
					SigmoidalNeuron neuron = (SigmoidalNeuron)n;
					if (i == this.nrOfLayers - 1) { //Top layer
						float y = neuron.getState();
						float o = expectedValues.get(this.neuronLayers.get(i).indexOf(neuron));
						float delta = y - o;
						neuron.setError(delta);
					} else { //Other layers
						ArrayList<Connection> connectionsToUpperLayerFromNeuron = new ArrayList<Connection>();
						// Find all connections, that have "neuron" as input
						for (Connection c : this.interconnectionsLayers.get(i+1).getConnections()) { 
							if (c.getInputNeuron().equals(neuron))
								connectionsToUpperLayerFromNeuron.add(c);
						}
						float delta = 0;
						for (Connection c : connectionsToUpperLayerFromNeuron) {
							float deltaUpper = ((SigmoidalNeuron)c.getOutputNeuron()).getError();
							float lambdaUpper = ((SigmoidalNeuron)c.getOutputNeuron()).getSlope();
							float yUpper = c.getOutputNeuron().getState();
							float w = c.getWeight();
							delta += deltaUpper*lambdaUpper*yUpper*(1-yUpper)*w;
						}
						neuron.setError(delta);
					}
				}
			}
			// Adjust weights
			for (Interconnections interconnectionsLayer : this.interconnectionsLayers) {
				interconnectionsLayer.adjustWeights();
			}
			return false;
		} else {
			return true;
		}
		
		
	}
	
	public String getOutput() {
		StringBuffer output = new StringBuffer();
		ArrayList<Neuron> outputLayer = this.neuronLayers.get(this.nrOfLayers-1);
		for (int i = 0; i < outputLayer.size(); i++) {
			output.append(String.valueOf(outputLayer.get(i).getState()));
			output.append(" ");
		}

		return output.toString();
	}
	



	public void changeSlopeTo(float slope) {
		for (ArrayList<Neuron> neuronLayer : this.neuronLayers) {
			for (Neuron neuron : neuronLayer) {
				((SigmoidalNeuron)neuron).setSlope(slope);
			}
		}
	}


	public void changeLearnCoeffTo(float learnCoeff) {
		for (Interconnections layer : interconnectionsLayers) {
			((InterconnectionsBP)layer).setLearningRate(learnCoeff);
		}
		
	}


	public void resetWeights() {
		for (Interconnections layer : interconnectionsLayers) {
			for (Connection connection : layer.getConnections()) {
				connection.setWeight((float)Math.random());
			}
		}
		
	}
	
	public void addNeuron(int layerIndex, float slope) {
		SigmoidalNeuron newNeuron = new SigmoidalNeuron(slope);
		neuronLayers.get(layerIndex).add(newNeuron);
		if ((layerIndex < nrOfLayers) && (layerIndex >= 0)) {
			Interconnections inputConnectionLayer = this.interconnectionsLayers.get(layerIndex);
			if (layerIndex == 0) {
				ArrayList<InputLayerPseudoNeuron> inputNeurons = this.inputNeuronLayer;
				for (Neuron inputNeuron : inputNeurons) {
					inputConnectionLayer.addConnection(new Connection(inputNeuron, newNeuron, (float)Math.random()));
				}
			} else {
				ArrayList<Neuron> inputNeurons = this.neuronLayers.get(layerIndex - 1);
				for (Neuron inputNeuron : inputNeurons) {
					inputConnectionLayer.addConnection(new Connection(inputNeuron, newNeuron, (float)Math.random()));
				}
			} 
			
			if (layerIndex < nrOfLayers - 1) {
				Interconnections outputConnectionLayer = this.interconnectionsLayers.get(layerIndex + 1);
				ArrayList<Neuron> outputNeurons = this.neuronLayers.get(layerIndex + 1);
				for (Neuron outputNeuron : outputNeurons) {
					outputConnectionLayer.addConnection(new Connection(newNeuron, outputNeuron, (float)Math.random()));
				}
			}
			this.nrOfNeuronsPerLayer.set(layerIndex, this.nrOfNeuronsPerLayer.get(layerIndex) + 1 );
			
			
		}	else {
			
			throw new InvalidLayerNumberException();
			
		}
	}
	
	public void removeNeuron(int layerIndex) {
		int nrOfNeuronsInThisLayer = this.nrOfNeuronsPerLayer.get(layerIndex);
		if ((layerIndex < nrOfLayers) && (layerIndex >= 0)) {
			if (nrOfNeuronsInThisLayer == 1) {
				
				removeNeuronLayer(layerIndex);
				
			} else {
				Neuron removedNeuron = this.neuronLayers.get(layerIndex).get(nrOfNeuronsInThisLayer - 1);
				Interconnections inputConnectionLayer = this.interconnectionsLayers.get(layerIndex);
				ArrayList<Connection> removedConnections = new ArrayList<Connection>();
				for (Connection connection : inputConnectionLayer.getConnections()) {
					if (connection.getOutputNeuron().equals(removedNeuron)) {
						removedConnections.add(connection);
					}
				}
				for (Connection connection : removedConnections) {
					inputConnectionLayer.getConnections().remove(connection);
				}
				removedConnections = new ArrayList<Connection>();
				if (layerIndex < nrOfLayers - 1) {
					Interconnections outputConnectionLayer = this.interconnectionsLayers.get(layerIndex + 1);
					for (Connection connection : outputConnectionLayer.getConnections()) {
						if (connection.getInputNeuron().equals(removedNeuron)) {
							removedConnections.add(connection);
						}
					}
					for (Connection connection : removedConnections) {
						outputConnectionLayer.getConnections().remove(connection);
					}
				}
				
				this.neuronLayers.get(layerIndex).remove(removedNeuron);
				this.nrOfNeuronsPerLayer.set(layerIndex, this.nrOfNeuronsPerLayer.get(layerIndex) - 1 );
			} 
					
		} else {
			throw new InvalidLayerNumberException();
		}
	}
	
	public void addNeuronLayer(int nrOfNeurons, int layerIndex, float slope) {
		if ((layerIndex < nrOfLayers + 1) && (layerIndex >= 0) && (nrOfNeurons > 0)) {
			
			this.nrOfLayers++;
			this.nrOfNeuronsPerLayer.add(layerIndex, nrOfNeurons);
			// new layer creation
			ArrayList<Neuron> newNeuronLayer = new ArrayList<Neuron>();
			for (int i = 0; i < nrOfNeurons; i++) {
				newNeuronLayer.add(new SigmoidalNeuron(slope));
			}
			// old connections removal
			if (layerIndex < nrOfLayers - 1) { // only if inner layer is added
				this.interconnectionsLayers.remove(layerIndex);
			}
			// new layer adding
			this.neuronLayers.add(layerIndex, newNeuronLayer);
			// new connections creation
			// input
			Interconnections inputConnLayer = new InterconnectionsBP(learnCoeff);
			if (layerIndex == 0) {
				ArrayList<InputLayerPseudoNeuron> inputNeurons = this.inputNeuronLayer;
				ArrayList<Neuron> outputNeurons = newNeuronLayer; //Layers already shifted
				for (Neuron inputNeuron : inputNeurons) {
					for (Neuron outputNeuron : outputNeurons) {
						inputConnLayer.addConnection(new Connection(inputNeuron, outputNeuron, (float)Math.random()));
					}
				}
			} else {
				ArrayList<Neuron> inputNeurons = this.neuronLayers.get(layerIndex - 1);
				ArrayList<Neuron> outputNeurons = newNeuronLayer; //Layers already shifted, this is new layer
				for (Neuron inputNeuron : inputNeurons) {
					for (Neuron outputNeuron : outputNeurons) {
						inputConnLayer.addConnection(new Connection(inputNeuron, outputNeuron, (float)Math.random()));
					}
				}
			}
			this.interconnectionsLayers.add(layerIndex, inputConnLayer);
			// output
			Interconnections outputConnLayer = new InterconnectionsBP(learnCoeff);
			if (layerIndex < nrOfLayers - 1) {
				ArrayList<Neuron> inputNeurons = newNeuronLayer;
				ArrayList<Neuron> outputNeurons = this.neuronLayers.get(layerIndex + 1); //Layers already shifted
				for (Neuron inputNeuron : inputNeurons) {
					for (Neuron outputNeuron : outputNeurons) {
						outputConnLayer.addConnection(new Connection(inputNeuron, outputNeuron, (float)Math.random()));
					}
				}
				this.interconnectionsLayers.add(layerIndex + 1, outputConnLayer);
			}
			
			
		} else {
			throw new InvalidLayerNumberException();
		}
		
	}
	
	
	
	public void removeNeuronLayer(int layerIndex) {
		if ((layerIndex < nrOfLayers ) && (layerIndex >= 0) && (nrOfLayers > 1)) {
			// delete output connections
			if (layerIndex < nrOfLayers - 1) {
				this.interconnectionsLayers.remove(layerIndex + 1);
			}
			// delete input connections
			this.interconnectionsLayers.remove(layerIndex);
			// delete neurons on layer
			this.neuronLayers.remove(layerIndex);
			this.nrOfNeuronsPerLayer.remove(layerIndex);
			this.nrOfLayers--;
			// create new connections
			if (layerIndex < nrOfLayers + 1) {
				Interconnections connLayer = new InterconnectionsBP(learnCoeff);
				if (layerIndex == 0) {
					ArrayList<InputLayerPseudoNeuron> inputNeurons = this.inputNeuronLayer;
					ArrayList<Neuron> outputNeurons = this.neuronLayers.get(0);
					for (Neuron inputNeuron : inputNeurons) {
						for (Neuron outputNeuron : outputNeurons) {
							connLayer.addConnection(new Connection(inputNeuron, outputNeuron, (float)Math.random()));
						}
					}
				} else {
					ArrayList<Neuron> inputNeurons = this.neuronLayers.get(layerIndex - 1);
					ArrayList<Neuron> outputNeurons = this.neuronLayers.get(layerIndex); 
					for (Neuron inputNeuron : inputNeurons) {
						for (Neuron outputNeuron : outputNeurons) {
							connLayer.addConnection(new Connection(inputNeuron, outputNeuron, (float)Math.random()));
						}
					}
				}
				this.interconnectionsLayers.add(layerIndex, connLayer);
				
			}
			
		} else {
			throw new InvalidLayerNumberException();
		}
	}
	
	@Override
	public String toString() {
		return getNeuronMap();
	}
	
	public String getNeuronMap() {
		StringBuffer map = new StringBuffer();
		for (int i = 0; i < nrOfLayers; i++) {
			map.append(String.valueOf(nrOfNeuronsPerLayer.get(i)));
			map.append(" ");
		}
		map.deleteCharAt(map.length() - 1);
		return map.toString();
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> nrOfNeuronsPerLayer = new ArrayList<Integer>();
		nrOfNeuronsPerLayer.add(10);
		nrOfNeuronsPerLayer.add(7);
		nrOfNeuronsPerLayer.add(2);
		BPNet net = new BPNet( (float)0.01, 3, 5, nrOfNeuronsPerLayer, (float)1.8, (float)0.7); // bigger slope = better resolution
		
		String trainingSet = "0.4 0.5 1 0.5 1 0 1\n0 0 0 0 0 1 1\n0.1 0.2 0.3 0.4 0.5 0 0\n1 0 1 0 1 1 0\n0.2 0.4 0 0 0.9 0 1";
		net.learn(trainingSet);
		net.run("0.4 0.5 1 0.5 1"); //expected 0 1
		System.out.println(net.getOutput());
		net.run("0 0 0 0 0"); // 1 1
		System.out.println(net.getOutput());
		net.run("0.1 0.2 0.3 0.4 0.5"); // 0 0
		System.out.println(net.getOutput());
		net.run("1 0 1 0 1"); // 1 0
		System.out.println(net.getOutput());
		net.run("0.2 0.4 0 0 0.9"); // 0 1
		System.out.println(net.getOutput());
		
		System.out.println("Not trained elements:");
		net.run("0.9 0.1 0.9 0.1 0.9"); // expected 1 0
		System.out.println(net.getOutput());
		net.run("0.01 0.01 0.01 0.01 0.01"); // expected 1 1
		System.out.println(net.getOutput());
		net.run("0.15 0.15 0.35 0.35 0.5"); // 0 0
		System.out.println(net.getOutput());
		
		System.out.println(net.getNeuronMap());
		net.addNeuron(0, 1.8f);
		System.out.println(net.getNeuronMap());
		net.addNeuron(1, 1.8f);
		System.out.println(net.getNeuronMap());
		net.addNeuron(2, 1.8f);
		System.out.println(net.getNeuronMap());
		net.removeNeuron(0);
		System.out.println(net.getNeuronMap());
		net.removeNeuron(1);
		System.out.println(net.getNeuronMap());
		net.removeNeuron(2);
		System.out.println(net.getNeuronMap());
		
		net.addNeuronLayer(5, 0, 1.8f);
		System.out.println(net.getNeuronMap());
		net.addNeuronLayer(5, 2, 1.8f);
		System.out.println(net.getNeuronMap());
		net.addNeuronLayer(5, 5, 1.8f);
		System.out.println(net.getNeuronMap());
		
		net.removeNeuronLayer(5);
		System.out.println(net.getNeuronMap());
		net.removeNeuronLayer(2);
		System.out.println(net.getNeuronMap());
		net.removeNeuronLayer(0);
		System.out.println(net.getNeuronMap());
		
		net.learn(trainingSet);
		net.run("0.4 0.5 1 0.5 1"); //expected 0 1
		System.out.println(net.getOutput());
		net.run("0 0 0 0 0"); // 1 1
		System.out.println(net.getOutput());
		net.run("0.1 0.2 0.3 0.4 0.5"); // 0 0
		System.out.println(net.getOutput());
		net.run("1 0 1 0 1"); // 1 0
		System.out.println(net.getOutput());
		net.run("0.2 0.4 0 0 0.9"); // 0 1
		System.out.println(net.getOutput());
		
		System.out.println("Not trained elements:");
		net.run("0.9 0.1 0.9 0.1 0.9"); // expected 1 0
		System.out.println(net.getOutput());
		net.run("0.01 0.01 0.01 0.01 0.01"); // expected 1 1
		System.out.println(net.getOutput());
		net.run("0.15 0.15 0.35 0.35 0.5"); // 0 0
		System.out.println(net.getOutput());
	}

}
