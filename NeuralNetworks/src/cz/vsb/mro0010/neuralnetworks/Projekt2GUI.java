package cz.vsb.mro0010.neuralnetworks;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JPanel;

import com.thoughtworks.xstream.XStream;

import java.awt.GridLayout;

public class Projekt2GUI {

	private JFrame frmBPnet;
	private BPNet neuralNet;
	private File dataFile;
	private String trainingData;
	private String testData;
	
	private int nrOfInputs;
	private int nrOfOutputs;
	private int nrOfLayers;
	private float maxError;
	private float slope;
	private float inertiaCoeff;
	private ArrayList<Integer> nrOfNeuronsPerLayer;
	private ArrayList<String> inputNames;
	private ArrayList<String> outputNames;
	
	
	private ArrayList<float[]> inputRanges;
	private float learnCoeff;
	
	
	private int nrOfTrainingElements;
	private int nrOfTestElements;
	
	//Swing components
	private JButton btnLearn;
	private JTable tableLearn;
	private JTable tableTest;
	private JScrollPane scrollPaneLearn;
	private JScrollPane scrollPaneTest;
	private JButton btnTestData;
	private JButton btnDoSpecifiedLearn;
	private JSpinner spinnerLearnSteps;
	private JTextField textFieldIterations;
	private JLabel lblLearned;
	private JLabel lblLearnCoeff;
	private JLabel lblSlopeLambda;
	private JSpinner spinnerLearnCoeff;
	private JSpinner spinnerSlope;
	private JLabel lblMaxError;
	private JSpinner spinnerError;
	private JLabel lblCurentError;
	private JTextField textFieldCurrentError;
	private JTextField textFieldTestElement;
	private JTextField textFieldTestOutput;
	private JButton btnTestElement;
	private JButton btnResetWeights;
	private JPanel panelTopology;
	private JButton btnAddLayer;
	private JSpinner spinnerLayer;
	private JSpinner spinnerLayerNeurons;
	private JMenuItem mntmSaveNeuralNet;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Projekt2GUI window = new Projekt2GUI();
					window.frmBPnet.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Projekt2GUI() {
		initialize();
	}

	
	private void changeAfterLearn() {
		btnLearn.setEnabled(false);
		btnTestData.setEnabled(true);
		btnDoSpecifiedLearn.setEnabled(false);
		lblLearned.setText("Learned");
		lblLearned.setForeground(Color.GREEN);
		spinnerLearnSteps.setEnabled(false);
		spinnerError.setEnabled(false);
        spinnerLearnCoeff.setEnabled(false);
        spinnerSlope.setEnabled(false);
        textFieldCurrentError.setText(String.valueOf(neuralNet.getError()));
        btnTestElement.setEnabled(true);
        textFieldTestElement.setEnabled(true);
        textFieldTestOutput.setEnabled(true);
        btnResetWeights.setEnabled(false);
        btnAddLayer.setEnabled(false);
        spinnerLayer.setEnabled(false);
        spinnerLayerNeurons.setEnabled(false);
        frmBPnet.getContentPane().remove(panelTopology);
        frmBPnet.revalidate();
        frmBPnet.repaint();
        mntmSaveNeuralNet.setEnabled(true);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//Default values
		slope = (float)1.1; 
		maxError = (float)0.1;
		
		
		frmBPnet = new JFrame();
		frmBPnet.setTitle("Backpropagation network");
		frmBPnet.setBounds(100, 100, 778, 562);
		frmBPnet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBPnet.getContentPane().setLayout(null);
		
		
		
		btnLearn = new JButton("Quick Learn");
		btnLearn.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				
				int iterations = neuralNet.learn(trainingData);
				changeAfterLearn();
				textFieldIterations.setText(String.valueOf(iterations));
				//JOptionPane.showMessageDialog(null, "Neural Net learned in " + iterations + " iterations.");
			
			}
		});
		btnLearn.setEnabled(false);
		btnLearn.setBounds(10, 188, 174, 23);
		frmBPnet.getContentPane().add(btnLearn);
		
		btnTestData = new JButton("Test data");
		btnTestData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTestData.setEnabled(false);
				
				
				String[] columnNames = new String[nrOfInputs + nrOfOutputs];
				for (int i = 0; i < nrOfInputs; i++) {
					columnNames[i] = inputNames.get(i);
					
				}
				for (int i = 0; i < nrOfOutputs; i++) {
					columnNames[nrOfInputs + i] = outputNames.get(i);
				}
		        Float[][] fDataTable = new Float[nrOfTestElements][nrOfInputs + nrOfOutputs];
		        String[] rows = testData.split("\n");
		        for (int i = 0; i < nrOfTestElements; i++) {
		        	neuralNet.run(rows[i]);
		        	String output = neuralNet.getOutput();
		        	String[] cells = (rows[i] + " " + output).split(" ");
					for (int j = 0; j < nrOfInputs + nrOfOutputs; j++) {
						fDataTable[i][j] = Float.valueOf(cells[j]);
					}
				}
		        tableTest = new JTable( fDataTable, columnNames);
		        tableTest.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		        scrollPaneTest.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		        scrollPaneTest.setViewportView(tableTest);
		     

			}
		});
		btnTestData.setEnabled(false);
		btnTestData.setBounds(10, 222, 174, 23);
		frmBPnet.getContentPane().add(btnTestData);
		
		scrollPaneLearn = new JScrollPane();
		scrollPaneLearn.setBounds(10, 25, 368, 156);
		frmBPnet.getContentPane().add(scrollPaneLearn);
		
		scrollPaneTest = new JScrollPane();
		scrollPaneTest.setBounds(10, 267, 368, 160);
		frmBPnet.getContentPane().add(scrollPaneTest);
		
		JLabel lblNewLabel = new JLabel("Training data");
		lblNewLabel.setBounds(10, 11, 116, 14);
		frmBPnet.getContentPane().add(lblNewLabel);
		
		JLabel lblTestData = new JLabel("Test data");
		lblTestData.setBounds(10, 252, 103, 14);
		frmBPnet.getContentPane().add(lblTestData);
		
		JLabel lblTrainingProcess = new JLabel("Training modification");
		lblTrainingProcess.setBounds(386, 11, 132, 14);
		frmBPnet.getContentPane().add(lblTrainingProcess);
		
		btnDoSpecifiedLearn = new JButton("Do specified learn steps");
		btnDoSpecifiedLearn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean learned = false;
				int iter = 0;
				int maxIterations = (int)spinnerLearnSteps.getModel().getValue();
				ArrayList<String> trainingElements = new ArrayList<String>(Arrays.asList(trainingData.split("\n")));
//				float maxError = 0;
				while(!learned  && (iter < maxIterations)) {
					neuralNet.setError(0);
					learned = true;
					for (int i = 0; i < trainingElements.size(); i++) {
						learned &= neuralNet.learnStep(trainingElements.get(i));
//						if (neuralNet.getError() > maxError) {
//							maxError = neuralNet.getError();
//						}
					}
					iter++;
					textFieldCurrentError.setText(String.valueOf(neuralNet.getError()));
//					System.out.println(iter);
				}
				if (learned) {
					changeAfterLearn();
				}
				int currentIter = Integer.parseInt(textFieldIterations.getText());
				textFieldIterations.setText(String.valueOf(currentIter + iter));
				
				
			}
		});
		btnDoSpecifiedLearn.setEnabled(false);
		btnDoSpecifiedLearn.setBounds(194, 188, 184, 23);
		frmBPnet.getContentPane().add(btnDoSpecifiedLearn);
		
		spinnerLearnSteps = new JSpinner();
		spinnerLearnSteps.setModel(new SpinnerNumberModel(1, 1, 100000, 1));
		spinnerLearnSteps.setEnabled(false);
		spinnerLearnSteps.setBounds(194, 223, 88, 20);
		frmBPnet.getContentPane().add(spinnerLearnSteps);
		
		textFieldIterations = new JTextField();
		textFieldIterations.setEnabled(false);
		textFieldIterations.setText("0");
		textFieldIterations.setBounds(292, 223, 86, 20);
		frmBPnet.getContentPane().add(textFieldIterations);
		textFieldIterations.setColumns(10);
		
		lblLearned = new JLabel("Not Learned");
		lblLearned.setForeground(Color.RED);
		lblLearned.setBackground(Color.LIGHT_GRAY);
		lblLearned.setBounds(514, 143, 74, 14);
		frmBPnet.getContentPane().add(lblLearned);
		
		lblLearnCoeff = new JLabel("Learn coeff");
		lblLearnCoeff.setBounds(388, 39, 79, 14);
		frmBPnet.getContentPane().add(lblLearnCoeff);
		
		lblSlopeLambda = new JLabel("Slope - lambda");
		lblSlopeLambda.setBounds(388, 64, 89, 14);
		frmBPnet.getContentPane().add(lblSlopeLambda);
		
		
		
		spinnerLearnCoeff = new JSpinner();
		spinnerLearnCoeff.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				learnCoeff = (float)spinnerLearnCoeff.getModel().getValue();
				if (neuralNet != null)
					neuralNet.changeLearnCoeffTo(learnCoeff);
			}
		});
		spinnerLearnCoeff.setEnabled(false);
		spinnerLearnCoeff.setModel(new SpinnerNumberModel(new Float(0.5), new Float(0.05), new Float(1), new Float(0.05)));
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinnerLearnCoeff.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(5);
		spinnerLearnCoeff.setBounds(499, 36, 74, 20);
		frmBPnet.getContentPane().add(spinnerLearnCoeff);
		
		slope = (float)1.1;
		spinnerSlope = new JSpinner();
		spinnerSlope.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				slope = (float)spinnerSlope.getModel().getValue();
				if (neuralNet != null)
					neuralNet.changeSlopeTo(slope);
			}
		});
		spinnerSlope.setEnabled(false);
		spinnerSlope.setBounds(499, 61, 74, 20);
		spinnerSlope.setModel(new SpinnerNumberModel(new Float(slope), new Float(0.05), null, new Float(0.05)));
		editor = (JSpinner.NumberEditor)spinnerSlope.getEditor();
        format = editor.getFormat();
        format.setMinimumFractionDigits(5);
		frmBPnet.getContentPane().add(spinnerSlope);
		
		lblMaxError = new JLabel("Max error");
		lblMaxError.setBounds(388, 89, 67, 14);
		frmBPnet.getContentPane().add(lblMaxError);
		
		
		maxError = (float)0.1;
		spinnerError = new JSpinner();
		spinnerError.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				maxError = (float)spinnerError.getModel().getValue();
				if (neuralNet != null)
					neuralNet.setTolerance(maxError);
			}
		});
		spinnerError.setEnabled(false);
		spinnerError.setBounds(499, 86, 74, 20);
		spinnerError.setModel(new SpinnerNumberModel(new Float(maxError), new Float(0.00001), new Float(100), new Float(0.00001)));
		editor = (JSpinner.NumberEditor)spinnerError.getEditor();
        format = editor.getFormat();
        format.setMinimumFractionDigits(5);
		frmBPnet.getContentPane().add(spinnerError);
		
		lblCurentError = new JLabel("Current Error");
		lblCurentError.setBounds(388, 115, 79, 14);
		frmBPnet.getContentPane().add(lblCurentError);
		
		textFieldCurrentError = new JTextField();
		textFieldCurrentError.setEnabled(false);
		textFieldCurrentError.setBounds(487, 112, 86, 20);
		frmBPnet.getContentPane().add(textFieldCurrentError);
		textFieldCurrentError.setColumns(10);
		
		JLabel lblChangeNetworkTopology = new JLabel("Change network topology");
		lblChangeNetworkTopology.setBounds(389, 168, 184, 14);
		frmBPnet.getContentPane().add(lblChangeNetworkTopology);
		
		textFieldTestElement = new JTextField();
		textFieldTestElement.setEnabled(false);
		textFieldTestElement.setBounds(10, 438, 272, 20);
		frmBPnet.getContentPane().add(textFieldTestElement);
		textFieldTestElement.setColumns(10);
		
		btnTestElement = new JButton("Run input");
		btnTestElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = textFieldTestElement.getText();
				try {
					neuralNet.run(input);
				}
				catch(InvalidInputNumberException exception) {
					JOptionPane.showMessageDialog(null, "Invalid Input");
				}
				finally {
					String output = neuralNet.getOutput();
					textFieldTestOutput.setText(output);
				}
			}
		});
		btnTestElement.setEnabled(false);
		btnTestElement.setBounds(289, 437, 89, 23);
		frmBPnet.getContentPane().add(btnTestElement);
		
		textFieldTestOutput = new JTextField();
		textFieldTestOutput.setEnabled(false);
		textFieldTestOutput.setBounds(49, 471, 329, 20);
		frmBPnet.getContentPane().add(textFieldTestOutput);
		textFieldTestOutput.setColumns(10);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setBounds(10, 474, 46, 14);
		frmBPnet.getContentPane().add(lblOutput);
		
		btnResetWeights = new JButton("Reset weights");
		btnResetWeights.setEnabled(false);
		btnResetWeights.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				neuralNet.resetWeights();
			}
		});
		btnResetWeights.setBounds(388, 138, 116, 23);
		frmBPnet.getContentPane().add(btnResetWeights);
		
		panelTopology = new JPanel();
		panelTopology.setBounds(386, 213, 366, 214);
		frmBPnet.getContentPane().add(panelTopology);
		
		btnAddLayer = new JButton("Add layer");
		btnAddLayer.setEnabled(false);
		btnAddLayer.setBounds(386, 188, 89, 23);
		frmBPnet.getContentPane().add(btnAddLayer);
		
		spinnerLayer = new JSpinner();
		spinnerLayer.setEnabled(false);
		spinnerLayer.setBounds(499, 189, 40, 20);
		frmBPnet.getContentPane().add(spinnerLayer);
		
		spinnerLayerNeurons = new JSpinner();
		spinnerLayerNeurons.setEnabled(false);
		spinnerLayerNeurons.setBounds(571, 189, 40, 20);
		frmBPnet.getContentPane().add(spinnerLayerNeurons);
		
		JLabel lblTo = new JLabel("to");
		lblTo.setBounds(483, 192, 46, 14);
		frmBPnet.getContentPane().add(lblTo);
		
		JLabel lblWith = new JLabel("with");
		lblWith.setBounds(542, 192, 46, 14);
		frmBPnet.getContentPane().add(lblWith);
		
		JLabel lblNeurons = new JLabel("neurons");
		lblNeurons.setBounds(621, 192, 74, 14);
		frmBPnet.getContentPane().add(lblNeurons);
		
		
		
		JMenuBar menuBar = new JMenuBar();
		frmBPnet.setJMenuBar(menuBar);
		
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmLoadData = new JMenuItem("Load data");
		mntmLoadData.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
			    fc.setDialogType(JFileChooser.OPEN_DIALOG);
			    FileFilter filter = new FileFilter() {
					
					@Override
					public String getDescription() {
						return "Txt files";
					}
					
					@Override
					public boolean accept(File f) {
						return (f.getName().endsWith(".txt") || f.isDirectory());
					}
				};
			    fc.setFileFilter(filter);
			    
			    
		        
			    if (fc.showOpenDialog(frmBPnet) == JFileChooser.APPROVE_OPTION) {
			    	dataFile = fc.getSelectedFile();
			    	FileReader fr;
					try {
						
						spinnerLearnSteps.setEnabled(true);
				        spinnerError.setEnabled(true);
				        spinnerLearnCoeff.setEnabled(true);
				        spinnerSlope.setEnabled(true);
						spinnerLearnCoeff.setValue(new Float((float)spinnerLearnCoeff.getValue()));
						spinnerSlope.setValue(spinnerSlope.getValue());
						spinnerError.setValue(spinnerError.getValue());
						
						//Parse data file
						
						
						
						fr = new FileReader(dataFile);
						StreamTokenizer tokenizer = new StreamTokenizer(fr);
						/*for (int i = 0; i < 6; i++ )
							tokenizer.nextToken();
						*/
						while(true) {
							tokenizer.nextToken();
							if ((tokenizer.nextToken() == StreamTokenizer.TT_WORD) && tokenizer.sval.equals("vrstev")) {
								tokenizer.nextToken();
								break;
							}
						}
						nrOfLayers = (int)tokenizer.nval;
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						nrOfInputs = (int)tokenizer.nval;
						tokenizer.nextToken();
						tokenizer.nextToken();
						tokenizer.nextToken();
						tokenizer.nextToken();
						inputRanges = new ArrayList<float[]>();
						inputNames = new ArrayList<String>();
						for (int i = 0; i < nrOfInputs; i++) {
							String inputName = tokenizer.sval;
							inputNames.add(inputName);
							tokenizer.nextToken();
							float[] dims = new float[2];
							dims[0] = (float)tokenizer.nval;
							tokenizer.nextToken();
							dims[1] = (float)tokenizer.nval;
							inputRanges.add(dims);
							tokenizer.nextToken();
						}
						/*for (int i = 0; i < 3; i++ )
							tokenizer.nextToken();*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						nrOfNeuronsPerLayer = new ArrayList<Integer>();
						for (int i = 0; i < nrOfLayers; i++) {
							nrOfNeuronsPerLayer.add((int)tokenizer.nval);
							if (i == nrOfLayers - 1) {
								nrOfOutputs = (int)tokenizer.nval;
							}
							tokenizer.nextToken();
						}
						for (int i = 0; i < 3; i++ )
							tokenizer.nextToken();
						outputNames = new ArrayList<String>();
						for (int i = 0; i < nrOfOutputs; i++) {
							outputNames.add(tokenizer.sval);
							tokenizer.nextToken();
						}
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						learnCoeff = (float)tokenizer.nval;
						spinnerLearnCoeff.getModel().setValue(new Float(learnCoeff));
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						inertiaCoeff = (float)tokenizer.nval;
						/*for (int i = 0; i < 7; i++ )
							tokenizer.nextToken();*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						nrOfTrainingElements = (int)tokenizer.nval;
						/*for (int i = 0; i < 4; i++ )
							tokenizer.nextToken();*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < nrOfTrainingElements; i++) {
							for (int j = 0; j < nrOfInputs; j++) {
								sb.append(String.valueOf(tokenizer.nval/(inputRanges.get(j)[1]-inputRanges.get(j)[0]) - inputRanges.get(j)[0]/(inputRanges.get(j)[1]-inputRanges.get(j)[0])));
								sb.append(" ");
								tokenizer.nextToken();
							}
							for (int j = 0; j < nrOfOutputs; j++) {
								sb.append(String.valueOf(tokenizer.nval));
								sb.append(" ");
								tokenizer.nextToken();
							}
							sb.deleteCharAt(sb.length() - 1);
							sb.append("\n");
						}
						trainingData = sb.toString();
						sb = new StringBuffer();
						/*for (int i = 0; i < 5; i++ )
							tokenizer.nextToken();*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						nrOfTestElements = (int)tokenizer.nval;
						/*tokenizer.nextToken();*/
						if (nrOfTestElements > 0) {
							while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
							for (int i = 0; i < nrOfTestElements; i++) {
								for (int j = 0; j < nrOfInputs; j++) {
									sb.append(String.valueOf(String.valueOf(tokenizer.nval/(inputRanges.get(j)[1]-inputRanges.get(j)[0]) - inputRanges.get(j)[0]/(inputRanges.get(j)[1]-inputRanges.get(j)[0]))));
									sb.append(" ");
									tokenizer.nextToken();
								}
								sb.deleteCharAt(sb.lastIndexOf(" "));
								sb.append("\n");
							}
							testData = sb.substring(0,sb.lastIndexOf("\n"));
						} else {
							testData = "";
						}
						fr.close();
						
						
						
						neuralNet = new BPNet(maxError, nrOfLayers, nrOfInputs, nrOfNeuronsPerLayer, slope, learnCoeff);
						spinnerError.getModel().setValue(maxError);
						btnLearn.setEnabled(true);
						//Show learn table
						String[] columnNames = new String[nrOfInputs + nrOfOutputs];
						for (int i = 0; i < nrOfInputs; i++) {
							columnNames[i] = inputNames.get(i);
							
						}
						for (int i = 0; i < nrOfOutputs; i++) {
							columnNames[nrOfInputs + i] = outputNames.get(i);
						}
				        Float[][] fDataTable = new Float[nrOfTrainingElements][nrOfInputs + nrOfOutputs];
				        String[] rows = trainingData.split("\n");
				        for (int i = 0; i < nrOfTrainingElements; i++) {
				        	String[] cells = rows[i].split(" ");
							for (int j = 0; j < nrOfInputs + nrOfOutputs; j++) {
								fDataTable[i][j] = Float.valueOf(cells[j]);
							}
						}
				        tableLearn = new JTable( fDataTable, columnNames);
				        tableLearn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				        scrollPaneLearn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				        scrollPaneLearn.setViewportView(tableLearn);
				        //Show test table
				        columnNames = new String[nrOfInputs];
						for (int i = 0; i < nrOfInputs; i++) {
							columnNames[i] = inputNames.get(i);
						}
				        fDataTable = new Float[nrOfTestElements][nrOfInputs];
				        rows = testData.split("\n");
				        for (int i = 0; i < nrOfTestElements; i++) {
				        	String[] cells = rows[i].split(" ");
							for (int j = 0; j < nrOfInputs; j++) {
								fDataTable[i][j] = Float.valueOf(cells[j]);
							}
						}
				        tableTest = new JTable( fDataTable, columnNames);
				        tableTest.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				        scrollPaneTest.setViewportView(tableTest);
				        btnResetWeights.setEnabled(true);
				        btnDoSpecifiedLearn.setEnabled(true);
				        btnAddLayer.setEnabled(true);
				        spinnerLayer.setEnabled(true);
				        spinnerLayer.setModel(new SpinnerNumberModel(0, 0, neuralNet.getNrOfLayers(), 1));
				        spinnerLayerNeurons.setEnabled(true);
				        spinnerLayerNeurons.setModel(new SpinnerNumberModel(1, 1, null, 1));
				        
				        btnAddLayer.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								neuralNet.addNeuronLayer((Integer)spinnerLayerNeurons.getValue(), (Integer)spinnerLayer.getValue(), slope);
								refreshPanelTopology();
							}
						});
				        refreshPanelTopology();
				        
						
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error: File not found");
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "IOException");
					}
					
			    	
			    }
				
			}

			private void refreshPanelTopology() {
				panelTopology.setLayout(new GridLayout(neuralNet.getNrOfLayers() + 1, 4));
				panelTopology.removeAll();
				nrOfLayers = neuralNet.getNrOfLayers();
				String map = neuralNet.getNeuronMap();
				String[] layers = map.split(" ");
				spinnerLayer.setModel(new SpinnerNumberModel(0, 0, neuralNet.getNrOfLayers() - 1, 1));
		        spinnerLayerNeurons.setModel(new SpinnerNumberModel(1, 1, null, 1));
				for (int i = 0; i < nrOfLayers; i++) {
					JLabel label = new JLabel(layers[nrOfLayers - 1 - i]);
					panelTopology.add(label);
					if (i > 0) {
					JButton btn1 = new JButton("Rmv neuron");
					JButton btn2 = new JButton("Rmv layer");
					JButton btn3 = new JButton("Add neuron");
					btn1.setName(String.valueOf(nrOfLayers - 1 - i));
					btn2.setName(String.valueOf(nrOfLayers - 1 - i));
					btn3.setName(String.valueOf(nrOfLayers - 1 - i));
					btn1.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							String name = ((JButton)e.getSource()).getName();
							neuralNet.removeNeuron(Integer.parseInt(name));
							refreshPanelTopology();
						}
					});
					btn2.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							String name = ((JButton)e.getSource()).getName();
							neuralNet.removeNeuronLayer(Integer.parseInt(name));
							refreshPanelTopology();
						}
					});
					btn3.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							String name = ((JButton)e.getSource()).getName();
							neuralNet.addNeuron(Integer.parseInt(name), slope);
							refreshPanelTopology();
							
						}
					});
					panelTopology.add(btn1);
					panelTopology.add(btn2);
					panelTopology.add(btn3);
					} else {
						panelTopology.add(new JLabel(" "));
						panelTopology.add(new JLabel(" "));
						panelTopology.add(new JLabel(" "));
					}
				}
				panelTopology.add(new JLabel("Inputs"));
				panelTopology.add(new JLabel(String.valueOf(neuralNet.getNrOfInputs())));
				panelTopology.add(new JLabel(" "));
				panelTopology.add(new JLabel(" "));
				frmBPnet.revalidate();
			}
		});
		mnFile.add(mntmLoadData);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmBPnet.dispatchEvent(new WindowEvent(frmBPnet, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		mntmSaveNeuralNet = new JMenuItem("Save Neural Net");
		mntmSaveNeuralNet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { 
					File address = null;
					JFileChooser fc = new JFileChooser();
					FileFilter filter = new FileFilter() {
						
						@Override
						public String getDescription() {
							return "Xml files";
						}
						
						@Override
						public boolean accept(File f) {
							return (f.getName().endsWith(".xml") || f.isDirectory());
						}
					};
				    fc.setFileFilter(filter);
					fc.setCurrentDirectory(new java.io.File("."));
					//fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
					if (fc.showSaveDialog(frmBPnet) == JFileChooser.APPROVE_OPTION) {
				    	address = fc.getSelectedFile();
				    	XStream xstream = new XStream();
				    	String xml = xstream.toXML(neuralNet);
				    	BufferedWriter out = new BufferedWriter(new FileWriter(address));
				    	out.write(xml);
				    	out.close();
//						BPNet testNet = (BPNet)xstream.fromXML(xml);
				    	
						JOptionPane.showMessageDialog(null, "Hotovo");
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.getMessage());
				}
				
			}
		});
		mntmSaveNeuralNet.setEnabled(false);
		mnFile.add(mntmSaveNeuralNet);
		mnFile.add(mntmExit);
	}
}
