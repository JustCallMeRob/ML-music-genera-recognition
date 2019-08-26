import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.neuroph.core.data.DataSet;

public class AudioController extends JPanel {

	static byte[] b;
	boolean sampleReading = false;
	double[] finalResults = new double[4];

	public AudioController(JFrame f1, JFrame f2) throws Exception {

		AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

		try {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			final SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(info);
			speakers.open();

			info = new DataLine.Info(TargetDataLine.class, format);
			final TargetDataLine microphone = AudioSystem.getTargetDataLine(format);
			microphone.open();

			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
			}

			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			// thread for reading data from microphone
			Thread targetThread = new Thread() {
				@Override
				public void run() {
					microphone.start();
					byte[] data = new byte[microphone.getBufferSize() / 5];
					int readBytes;
					while (true) {
						readBytes = microphone.read(data, 0, data.length);
						out.write(data, 0, readBytes);
						if (readBytes > 0) {
							b = out.toByteArray();
						}
					}
				}
			};

			// thread for playing back audio that has been recorded
			Thread sourceThread = new Thread() {
				@Override
				public void run() {
					speakers.start();
					while (true) {
						speakers.write(out.toByteArray(), 0, out.toByteArray().length);
					}
				}
			};

			if (!sampleReading) {
				targetThread.start();
				System.out.println("Started Recording");
				Thread.sleep(3500);
				microphone.stop();
				microphone.close();
			} else {
				readSample("songsamples\\rock5.wav");
			}

			for (int i = 0; i < b.length - 1; i++) {
				b[i] = (byte) (b[i] * 30);
			}
			System.out.println("Ended Recording");

			Complex[][] results = callFFT(b);
			double[][] magnitude = new double[572][256];
			double max = 0, min = 5000.00;
			for (int x = 0; x < 572; x++) {
				for (int y = 0; y < 256; y++) {
					magnitude[x][y] = round(results[x][y].abs(), 2);
					if (max < magnitude[x][y]) {
						max = magnitude[x][y];
					}
					if (min > magnitude[x][y]) {
						min = magnitude[x][y];
					}
				}
			}
			for (int x = 0; x < 572; x++) {
				for (int y = 0; y < 256; y++) {
					magnitude[x][y] = round(normalize(max, min, magnitude[x][y]), 2);
				}
			}

			ArrayList<Double> localMaxima = runMask(magnitude);
			GeneraRecognitionNetwork net = new GeneraRecognitionNetwork();
			if (sampleReading) {
				for (int x = 0; x < localMaxima.size(); x++) {
					DataSet data = new DataSet(10, 4);
					double[] currentInput = new double[10];
					try {
						for (int y = 0; y < 10; y++) {
							currentInput[y] = localMaxima.get(x + y);
						}
					} catch (IndexOutOfBoundsException e) {
						Arrays.fill(currentInput, 0.0);
					}
					x += 10;
					data.addRow(currentInput, new double[] { 0, 0, 0, 1 });
					net.trainNetwork(currentInput);
				}
			} else {
				for (int x = 0; x < localMaxima.size(); x++) {
					DataSet data = new DataSet(10, 4);
					double[] currentInput = new double[10];
					double[] currentOutput = new double[10];
					try {
						for (int y = 0; y < 10; y++) {
							currentInput[y] = localMaxima.get(x + y);
						}
					} catch (IndexOutOfBoundsException e) {
						Arrays.fill(currentInput, 0.0);
					}
					x += 10;
					data.addRow(currentInput, new double[] { 0, 0, 0, 0 });
					currentOutput = net.testNetwork(data);
					this.finalResults[0] += currentOutput[0];
					this.finalResults[1] += currentOutput[1];
					this.finalResults[2] += currentOutput[2];
					this.finalResults[3] += currentOutput[3];
				}
			}

			Paint paint = new Paint(results);
			f1.add(paint);
			f1.setVisible(true);
			Paint.saveScreenShot(paint, "current.png");

			System.out.println("Started Playback");
			sourceThread.start();
			speakers.start();
			Thread.sleep(3000);
			speakers.stop();
			speakers.close();
			System.out.println("Ended Playback");

		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
	}

	public Complex[][] callFFT(byte b[]) {
		final int totalSize = b.length;
		int freq = 512;
		int totalAmount = totalSize / freq;
		// Need complex numbers for converting into frequency domain
		Complex[][] results = new Complex[totalAmount][];
		// For all the chunks:
		for (int times = 0; times < totalAmount; times++) {
			Complex[] complex = new Complex[freq];
			for (int i = 0; i < freq; i++) {
				// Put the time domain data into a complex number with imaginary
				// part as 0:
				complex[i] = new Complex(b[(times * freq) + i], 0);
			}
			// Perform FFT analysis on the chunk:
			results[times] = FFT.fft(complex);
		}
		return results;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public double normalize(double max, double min, double x) {
		return ((x - min) / (max - min));
	}

	public ArrayList<Double> runMask(double[][] data) {
		int xCounter = 0, yCounter = 0, xMask = ((int) data.length * 20 / 100),
				yMask = ((int) data[0].length * 20 / 100);
		int xOffsetAdder = (data.length * 10 / 100), yOffsetAdder = ((int) data.length * 10 / 100);
		int xOffset = 0, yOffset = 0;
		long counter = 0;
		ArrayList<Double> localMaxima = new ArrayList<Double>();
		double[][] mask = new double[xMask][yMask];

		while (xCounter < data.length) {
			while (yCounter < data[0].length) {
				try {
					for (int x = 0; x < xMask; x++) {
						for (int y = 0; y < yMask; y++) {
							mask[x][y] = data[x + xOffset][y + yOffset];
						}
					}
					localMaxima.add(getLocalMaxima(mask));
					yOffset += yOffsetAdder;
				} catch (ArrayIndexOutOfBoundsException e) {
					yOffset = 0;
					counter++;
				}
				yCounter++;
			}
			xOffset += xOffsetAdder;
			yCounter = 0;
			xCounter++;
		}

		System.out.println("Number of masks:" + counter);
		return localMaxima;
	}

	private static double getLocalMaxima(double[][] mask) {
		double max = 0;
		for (int x = 0; x < mask.length; x++) {
			for (int y = 0; y < mask[0].length; y++) {
				if (max < mask[x][y]) {
					max = mask[x][y];
				}
			}
		}
		return max;
	}

	public static void readSample(String filePath) {
		File file = new File(filePath);
		InputStream fis;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			try {
				fis.read(buffer, 0, buffer.length);
				b = buffer;
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public double[] getResults() {
		return this.finalResults;
	}

}
