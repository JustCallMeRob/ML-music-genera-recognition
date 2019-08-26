import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class test {

	
	public test(){
		System.out.println("Starting");
		
		try{
			//to know how to format the audio data
			AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100,16,2,4,44100,false);
			//data line info is how we get data from the computers audio system
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			//check to see if line supported
			if(!AudioSystem.isLineSupported(info)){
				System.out.println("Line not supported");
			}
			//where we get the audio data from
			TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
			//allocates system resources to be able to record
			targetLine.open();
			
			System.out.println("Started rec.");
			//engage dataIO, gets data from mic
			targetLine.start();
			
			//Anonymous thread in class
			Thread thread = new Thread(){
				@Override public void run(){
					//writer that will write the audio data to file
					AudioInputStream audioStream = new AudioInputStream(targetLine);
					//file where to write
					File audioFile = new File("test.wav");
					//keeps writing bytes until targetdataline keeps getting data from mic
					try{
						AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE,audioFile);
					}catch(IOException e2){
						e2.printStackTrace();
					}
					System.out.println("Stopped rec.");
				}
			};
			
			thread.start();
			Thread.sleep(2000);
			//stop line from recording
			targetLine.stop();
			//realease all system reasources
			targetLine.close();
			
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		catch(LineUnavailableException e2){
			e2.printStackTrace();
		}
	}
	
	public static void processAudioData(){
		
	}
	
	
}
