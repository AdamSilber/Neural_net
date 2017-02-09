package cz.vsb.mro0010.neuralnetworks;

import java.util.ArrayList;

public abstract class Interconnections {

	protected ArrayList<Connection> connections;
	
	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public Interconnections() {
		this.connections = new ArrayList<Connection>();
	}
	
	public void addConnection(Connection connection) {
		this.connections.add(connection);
	}
	
//	public void passSignal() {
//		for (Connection c : this.connections) {
//			
//			Neuron n = c.getOutputNeuron();
//			n.initialize();
//			for (Connection cn : this.connections) {
//				if (cn.getOutputNeuron().equals(n)) {
//					cn.passSignal();
//				}
//			}
//			n.transfer();
//		}
//	}
	
	public void passSignal() { // Faster version
		ArrayList<Neuron> processedNeurons = new ArrayList<Neuron>();
		for (Connection c : this.connections) {
			
			Neuron n = c.getOutputNeuron();
			if (!processedNeurons.contains(n)) {
				processedNeurons.add(n);
				n.initialize();
				for (Connection cn : this.connections) {
					if (cn.getOutputNeuron().equals(n)) {
						cn.passSignal();
					}
				}
				n.transfer();
			}
		}
	}
	
	public abstract void adjustWeights();
}
