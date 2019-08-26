import java.util.Arrays;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;

public class GeneraRecognitionNetwork {

	static NeuralNetwork GeneraRecognitionNet;

	public GeneraRecognitionNetwork(int dataSetInputSize) {
		// create training set to used to initialize the network (needs to be called once)
		DataSet trainingSet = new DataSet(dataSetInputSize, 4);
		trainingSet.addRow(new DataSetRow(new double[] { 0, 0 }, new double[] { 0 }));
		trainingSet.addRow(new DataSetRow(new double[] { 0, 1 }, new double[] { 1 }));
		trainingSet.addRow(new DataSetRow(new double[] { 1, 0 }, new double[] { 1 }));
		trainingSet.addRow(new DataSetRow(new double[] { 1, 1 }, new double[] { 0 }));
		// create multi layer perceptron
		MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.TANH, 2, 3, 1);
		// learn the training set
		myMlPerceptron.learn(trainingSet);
	}

	public GeneraRecognitionNetwork() {
		// load saved neural network
		GeneraRecognitionNet = NeuralNetwork.createFromFile("NeuralN\\GeneraRecognitionNet.nnet");
	}

	public void saveNetwork() {
		// save trained neural network
		GeneraRecognitionNet.save("GeneraRecognitionNet.nnet");
		System.out.println("Neural network has been saved");
	}

	public double[] testNetwork(DataSet testSet) {
		// test the neural network
		System.out.println("Testing trained neural network");
		double[] finalResults = new double[4];
		finalResults = testNeuralNetwork(GeneraRecognitionNet, testSet);
		return finalResults;
	}

	public void trainNetwork(double[] data) {
		// train the neural network
		// create training set to initialize the network (needs to be called once)
		DataSet trainingSet = new DataSet(data.length, 4);
		trainingSet.addRow(new DataSetRow(data, new double[] { 1, 0, 0, 0 }));
		GeneraRecognitionNet.learn(trainingSet);
		saveNetwork();
	}

	public static double[] testNeuralNetwork(NeuralNetwork nnet, DataSet testSet) {
		for (DataSetRow dataRow : testSet.getRows()) {
			nnet.setInput(dataRow.getInput());
			nnet.calculate();
			double[] networkOutput = nnet.getOutput();
			System.out.print("Input: " + Arrays.toString(dataRow.getInput()));
			System.out.println(" Output: " + Arrays.toString(networkOutput));
			return networkOutput;
		}
		return null;
	}

}