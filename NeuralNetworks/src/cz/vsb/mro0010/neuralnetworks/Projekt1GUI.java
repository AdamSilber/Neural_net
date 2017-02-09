package cz.vsb.mro0010.neuralnetworks;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

public class Projekt1GUI {

	private JFrame frmPerceptron;
	private SinglePerceptronNeuralNet neuralNet;
	private File dataFile;
	private String trainingData;
	private String testData;
	private int nrOfInputs;
	private ArrayList<float[]> inputRanges;
	private float learnCoeff;
	private int nrOfTrainingElements;
	private int nrOfTestElements;
	private String trainingOutput;
	private int nrOfTrainingIterations;
	
	//Swing components
	private JButton btnLearn;
	private JTable tableLearn;
	private JTable tableTest;
	private JTable tableTrainingProcess;
	private JScrollPane scrollPaneLearn;
	private JScrollPane scrollPaneTest;
	private JScrollPane scrollPaneTrainingProcess;
	private JButton buttonBackward;
	private JButton buttonForward;
	private JButton btnTestData;
	
	//Chart components
	private XYSeriesCollection dataset;
	private ChartPanel pnlChart;
	private XYLineAndShapeRenderer renderer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Projekt1GUI window = new Projekt1GUI();
					window.frmPerceptron.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Projekt1GUI() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPerceptron = new JFrame();
		frmPerceptron.setTitle("Perceptron");
		frmPerceptron.setBounds(100, 100, 652, 498);
		frmPerceptron.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPerceptron.getContentPane().setLayout(null);
		
		
		
		btnLearn = new JButton("Learn");
		btnLearn.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				btnLearn.setEnabled(false);
				btnTestData.setEnabled(true);
				neuralNet.learn(trainingData);
				trainingOutput = neuralNet.getTrainingOutput();
				
				//Show training process table
				String[] columnNames = new String[nrOfInputs + 1];
				for (int i = 0; i < nrOfInputs; i++) {
					columnNames[i] = "w" + String.valueOf(i+1);
				}
				columnNames[nrOfInputs] = "Threshold";
				String[] rows = trainingOutput.split("\n");
				nrOfTrainingIterations = rows.length;
		        Float[][] fDataTable = new Float[nrOfTrainingIterations][nrOfInputs + 1];
		        for (int i = 0; i < nrOfTrainingIterations; i++) {
		        	String[] cells = rows[i].split(" ");
					for (int j = 0; j < nrOfInputs + 1; j++) {
						fDataTable[i][j] = Float.valueOf(cells[j]);
					}
				}
		        tableTrainingProcess = new JTable( fDataTable, columnNames);
		        tableTrainingProcess.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			        public void valueChanged(ListSelectionEvent event) {
			         
			            if (tableTrainingProcess.getSelectedRow() == 0) {
			            	buttonForward.setEnabled(true);
			            	buttonBackward.setEnabled(false);
			            }
			            else if (tableTrainingProcess.getSelectedRow() == tableTrainingProcess.getRowCount()-1) {
			            	buttonBackward.setEnabled(true);
			            	buttonForward.setEnabled(false);
			            }
			            else {
			            	buttonBackward.setEnabled(true);
			            	buttonForward.setEnabled(true);
			            }
			            
			            //redraw chart in 2D
						if ((nrOfInputs == 2) && (dataset != null)) {
							float xMin = inputRanges.get(0)[0];
			    	        float xMax = inputRanges.get(0)[1];
			    	        float yMin = inputRanges.get(1)[0];
			    	        float yMax = inputRanges.get(1)[1];
			    	        
			    	        int selectedRow = tableTrainingProcess.getSelectedRow();
			    	        float w0 = -(float)tableTrainingProcess.getModel().getValueAt(selectedRow, 2);
			    	        float w1 = (float)tableTrainingProcess.getModel().getValueAt(selectedRow, 0);
			    	        float w2 = (float)tableTrainingProcess.getModel().getValueAt(selectedRow, 1);
			    	        float step = (float)0.01;
			    	        
							boolean containSeries = false;
			        		String key = "Line";
			        		for (Object obj : dataset.getSeries()) {
			        			if (obj instanceof XYSeries) {
			        				XYSeries xys = (XYSeries)obj;
			        				if (xys.getKey().equals(key)) {
			        					containSeries = true;
			        				}
			        			}
			        		}
			        		if (!containSeries) {
			        		XYSeries series = new XYSeries(key);
			    	        dataset.addSeries(series);
			        		}
			        		for (Object obj : dataset.getSeries()) {
			        			if (obj instanceof XYSeries) {
			        				XYSeries xys = (XYSeries)obj;
			        				if (xys.getKey().equals(key)) {
			        					int index = dataset.getSeries().indexOf(xys);
			        					xys.clear();
			        					for (float x = xMin; x < xMax; x += step) {
						    	        	float y = -w1/w2 * x - w0/w2;
						    	        	if ( (yMin <= y) && (y <= yMax)) {
						    	        		xys.add(x, y);
						    	        	}
						    	        }
			        					renderer.setSeriesPaint(index, Color.RED);
			        				}
			        			}
			        		}
		        		}
			        }
			    });
		        scrollPaneTrainingProcess.setViewportView(tableTrainingProcess);
		        tableTrainingProcess.setRowSelectionInterval(0, 0);
		        if (nrOfTrainingIterations > 1)
		        	buttonForward.setEnabled(true);
		        
		        // in 2D case draw graph
		        if (nrOfInputs == 2) {
		        	//Create a chart
	    	        XYSeries series = new XYSeries("Line");
	    	        float xMin = 0;//inputRanges.get(0)[0];
	    	        float xMax = 1;//inputRanges.get(0)[1];
	    	        float yMin = 0;//inputRanges.get(1)[0];
	    	        float yMax = 1;//inputRanges.get(1)[1];
	    	        
	    	        int selectedRow = tableTrainingProcess.getSelectedRow();
	    	        float w0 = -(float)tableTrainingProcess.getModel().getValueAt(selectedRow, 2);
	    	        float w1 = (float)tableTrainingProcess.getModel().getValueAt(selectedRow, 0);
	    	        float w2 = (float)tableTrainingProcess.getModel().getValueAt(selectedRow, 1);
	    	        float step = (float)0.01;
	    	        for (float x = xMin; x < xMax; x += step) {
	    	        	float y = -w1/w2 * x - w0/w2;
	    	        	if ( (yMin <= y) && (y <= yMax)) {
	    	        		series.add(x, y);
	    	        	}
	    	        }
	    	        	
	    	        XYSeries seriesLearnNeg = new XYSeries("LN");
	    	        XYSeries seriesLearnPoz = new XYSeries("LP");
	    	        String[] trainingRows = trainingData.split("\n");
	    	        for (int i = 0; i < nrOfTrainingElements; i++) {
	    	        	String[] trainingElement = trainingRows[i].split(" ");
	    	        	if (Float.valueOf(trainingElement[2]) == 1) {
	    	        		seriesLearnPoz.add(Float.valueOf(trainingElement[0]), Float.valueOf(trainingElement[1]));
	    	        	} else {
	    	        		seriesLearnNeg.add(Float.valueOf(trainingElement[0]), Float.valueOf(trainingElement[1]));
	    	        	}
	    	        }
	    	        
	    	        
	    	        dataset = new XYSeriesCollection();
	    	        dataset.addSeries(series);
	    	        dataset.addSeries(seriesLearnPoz);
	    	        dataset.addSeries(seriesLearnNeg);
	    	        
	    	        //Create chart with name , axis names and dataset
	    	        JFreeChart chart = ChartFactory.createXYLineChart("", "x1", "x2", dataset);
	    	        if ((pnlChart != null) && (pnlChart.getParent() == frmPerceptron.getContentPane()))
	    	        	frmPerceptron.getContentPane().remove(pnlChart);
	    	        
	    	        //Change plot properties
	    	        
	    	        XYPlot plot = (XYPlot) chart.getPlot();
	    	        plot.setBackgroundPaint(Color.white);
	    	        plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
	    	        plot.setDomainGridlinesVisible(false);
	    	        plot.setDomainGridlinePaint(Color.lightGray);
	    	        plot.setRangeGridlinePaint(Color.white);
	    	        //Set axes range
	    	        //x
	    	        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
	    	        domain.setRange(xMin, xMax);
	    	        //y
	    	        NumberAxis yRange = (NumberAxis) plot.getRangeAxis();
	    	        yRange.setRange(yMin, yMax);
	    	        
	    	        //Set renderer
	    	        
	    	        renderer = new XYSplineRenderer();
	    	        renderer.setSeriesShapesVisible(0, false);
	    	        renderer.setSeriesShapesVisible(1, true);
	    	        renderer.setSeriesShape(1, ShapeUtilities.createUpTriangle(4));
	    	        renderer.setSeriesShapesVisible(2, true);
	    	        renderer.setSeriesShape(2, ShapeUtilities.createDownTriangle(4));
	    	        renderer.setSeriesPaint(0, Color.RED);
	    	        renderer.setSeriesPaint(1, Color.BLUE);
	    	        renderer.setSeriesPaint(2, Color.BLUE);
	    	        renderer.setSeriesLinesVisible(0, true);
	    	        renderer.setSeriesLinesVisible(1, false);
	    	        renderer.setSeriesLinesVisible(2, false);
	    	        plot.setRenderer(renderer);
	    	        pnlChart = new ChartPanel(chart);
	    	        pnlChart.setBounds(309, 267, 273, 150);
	    	        pnlChart.setDomainZoomable(false);
	    	        pnlChart.setRangeZoomable(false);
	    	        pnlChart.getChart().removeLegend();
	    	        frmPerceptron.getContentPane().add(pnlChart);
	    	        frmPerceptron.repaint();
		        } else {
		        	if (pnlChart != null) {
		        		frmPerceptron.getContentPane().remove(pnlChart);
		        		frmPerceptron.repaint();
					}
		        }
			}
		});
		btnLearn.setEnabled(false);
		btnLearn.setBounds(10, 188, 89, 23);
		frmPerceptron.getContentPane().add(btnLearn);
		
		btnTestData = new JButton("Test data");
		btnTestData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTestData.setEnabled(false);
				String[] columnNames = new String[nrOfInputs + 1];
				for (int i = 0; i < nrOfInputs; i++) {
					columnNames[i] = "x" + String.valueOf(i+1);
				}
				columnNames[nrOfInputs] = "y";
		        Float[][] fDataTable = new Float[nrOfTestElements][nrOfInputs + 1];
		        String[] rows = testData.split("\n");
		        for (int i = 0; i < nrOfTestElements; i++) {
		        	String[] cells = rows[i].split(" ");
					for (int j = 0; j < nrOfInputs; j++) {
						fDataTable[i][j] = Float.valueOf(cells[j]);
					}
					neuralNet.run(rows[i]);
					String y = neuralNet.getOutput();
					fDataTable[i][nrOfInputs] = Float.valueOf(y);
				}
		        tableTest = new JTable( fDataTable, columnNames);
		        scrollPaneTest.setViewportView(tableTest);
		        // in 2D case redraw graph
		        if (nrOfInputs == 2) {
		        	XYSeries seriesTestNeg = new XYSeries("TN");
	    	        XYSeries seriesTestPoz = new XYSeries("TP");
	    	        String[] testRows = testData.split("\n");
	    	        for (int i = 0; i < nrOfTestElements; i++) {
	    	        	String[] testElement = testRows[i].split(" ");
	    	        	neuralNet.run(testRows[i]);
						String y = neuralNet.getOutput();
	    	        	if (Float.valueOf(y) == 1) {
	    	        		seriesTestPoz.add(Float.valueOf(testElement[0]), Float.valueOf(testElement[1]));
	    	        	} else {
	    	        		seriesTestNeg.add(Float.valueOf(testElement[0]), Float.valueOf(testElement[1]));
	    	        	}
	    	        }
	    	        dataset.addSeries(seriesTestPoz);
	    	        dataset.addSeries(seriesTestNeg);
	    	        
	    	        renderer.setSeriesShapesVisible(3, true);
	    	        renderer.setSeriesShape(3, ShapeUtilities.createUpTriangle(6));
	    	        renderer.setSeriesShapesVisible(4, true);
	    	        renderer.setSeriesShape(4, ShapeUtilities.createDownTriangle(6));
	    	        renderer.setSeriesPaint(3, Color.GREEN);
	    	        renderer.setSeriesPaint(4, Color.GREEN);
	    	        renderer.setSeriesLinesVisible(3, false);
	    	        renderer.setSeriesLinesVisible(4, false);
	    	       
		        }
			}
		});
		btnTestData.setEnabled(false);
		btnTestData.setBounds(10, 222, 89, 23);
		frmPerceptron.getContentPane().add(btnTestData);
		
		scrollPaneLearn = new JScrollPane();
		scrollPaneLearn.setBounds(10, 25, 283, 156);
		frmPerceptron.getContentPane().add(scrollPaneLearn);
		
		scrollPaneTest = new JScrollPane();
		scrollPaneTest.setBounds(10, 267, 283, 160);
		frmPerceptron.getContentPane().add(scrollPaneTest);
		
		JLabel lblNewLabel = new JLabel("Training data");
		lblNewLabel.setBounds(10, 11, 116, 14);
		frmPerceptron.getContentPane().add(lblNewLabel);
		
		JLabel lblTestData = new JLabel("Test data");
		lblTestData.setBounds(10, 252, 103, 14);
		frmPerceptron.getContentPane().add(lblTestData);
		
		scrollPaneTrainingProcess = new JScrollPane();
		scrollPaneTrainingProcess.setBounds(303, 25, 283, 156);
		frmPerceptron.getContentPane().add(scrollPaneTrainingProcess);
		
		JLabel lblTrainingProcess = new JLabel("Training process");
		lblTrainingProcess.setBounds(303, 11, 97, 14);
		frmPerceptron.getContentPane().add(lblTrainingProcess);
		
		buttonBackward = new JButton("<<");
		buttonBackward.setEnabled(false);
		buttonBackward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = tableTrainingProcess.getSelectedRow();
				int tableRows = tableTrainingProcess.getRowCount();
				if (row == tableRows - 1) {
					buttonForward.setEnabled(true);
				}
				if (row == 1) {
					buttonBackward.setEnabled(false);
				}
				tableTrainingProcess.setRowSelectionInterval(row-1, row-1);
				Rectangle rect = tableTrainingProcess.getCellRect(row-1, 0, true);
				tableTrainingProcess.scrollRectToVisible(rect);
			}
		});
		buttonBackward.setBounds(348, 188, 89, 23);
		frmPerceptron.getContentPane().add(buttonBackward);
		
		buttonForward = new JButton(">>");
		buttonForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableTrainingProcess.getSelectedRow();
				int tableRows = tableTrainingProcess.getRowCount();
				if (row == 0) {
					buttonBackward.setEnabled(true);
				}
				if (row == tableRows - 2) {
					buttonForward.setEnabled(false);
				}
				tableTrainingProcess.setRowSelectionInterval(row+1, row+1);
				Rectangle rect = tableTrainingProcess.getCellRect(row+1, 0, true);
				tableTrainingProcess.scrollRectToVisible(rect);
				
				
			}
		});
		buttonForward.setEnabled(false);
		buttonForward.setBounds(447, 188, 89, 23);
		frmPerceptron.getContentPane().add(buttonForward);
		
		JLabel lbldView = new JLabel("2D View");
		lbldView.setBounds(312, 226, 46, 14);
		frmPerceptron.getContentPane().add(lbldView);
		
		JMenuBar menuBar = new JMenuBar();
		frmPerceptron.setJMenuBar(menuBar);
		
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
						// TODO Auto-generated method stub
						return "Txt files";
					}
					
					@Override
					public boolean accept(File f) {
						// TODO Auto-generated method stub
						return (f.getName().endsWith(".txt") || f.isDirectory());
					}
				};
			    fc.setFileFilter(filter);
			    
			    
		        
			    if (fc.showOpenDialog(frmPerceptron) == JFileChooser.APPROVE_OPTION) {
			    	dataFile = fc.getSelectedFile();
			    	FileReader fr;
					try {
						//Parse data file
						fr = new FileReader(dataFile);
						StreamTokenizer tokenizer = new StreamTokenizer(fr);
						/*for (int i = 0; i < 6; i++ )
							tokenizer.nextToken();
						*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						nrOfInputs = (int)tokenizer.nval;
						/*tokenizer.nextToken();
						tokenizer.nextToken();
						tokenizer.nextToken();
						tokenizer.nextToken();*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						inputRanges = new ArrayList<float[]>();
						for (int i = 0; i < nrOfInputs; i++) {
							float[] dims = new float[2];
							dims[0] = (float)tokenizer.nval;
							tokenizer.nextToken();
							dims[1] = (float)tokenizer.nval;
							inputRanges.add(dims);
							tokenizer.nextToken();
							tokenizer.nextToken();
						}
						/*for (int i = 0; i < 3; i++ )
							tokenizer.nextToken();*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						learnCoeff = (float)tokenizer.nval;
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
							sb.append(String.valueOf(tokenizer.nval));
							sb.append("\n");
							tokenizer.nextToken();
						}
						trainingData = sb.toString();
						sb = new StringBuffer();
						/*for (int i = 0; i < 5; i++ )
							tokenizer.nextToken();*/
						while(tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {}
						nrOfTestElements = (int)tokenizer.nval;
						/*tokenizer.nextToken();*/
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
						fr.close();
						neuralNet = new SinglePerceptronNeuralNet(new BinaryNeuron(), nrOfInputs, learnCoeff);
						btnLearn.setEnabled(true);
						//Show learn table
						String[] columnNames = new String[nrOfInputs + 1];
						for (int i = 0; i < nrOfInputs; i++) {
							columnNames[i] = "x" + String.valueOf(i+1);
						}
						columnNames[nrOfInputs] = "y";
				        Float[][] fDataTable = new Float[nrOfTrainingElements][nrOfInputs + 1];
				        String[] rows = trainingData.split("\n");
				        for (int i = 0; i < nrOfTrainingElements; i++) {
				        	String[] cells = rows[i].split(" ");
							for (int j = 0; j < nrOfInputs + 1; j++) {
								fDataTable[i][j] = Float.valueOf(cells[j]);
							}
						}
				        tableLearn = new JTable( fDataTable, columnNames);
				        scrollPaneLearn.setViewportView(tableLearn);
				        //Show test table
				        columnNames = new String[nrOfInputs];
						for (int i = 0; i < nrOfInputs; i++) {
							columnNames[i] = "x" + String.valueOf(i+1);
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
				        scrollPaneTest.setViewportView(tableTest);
						
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error: File not found");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
			    	
			    }
				
			}
		});
		mnFile.add(mntmLoadData);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmPerceptron.dispatchEvent(new WindowEvent(frmPerceptron, WindowEvent.WINDOW_CLOSING));
			}
		});
		mnFile.add(mntmExit);
	}
}
