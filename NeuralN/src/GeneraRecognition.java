import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GeneraRecognition {
	
	static double[] results = new double[4];
	static JFrame frame=new JFrame("Spectrogram");
	
	public static void main(String[] args) throws Exception {
		 
		frame.setVisible(true);
		JFrame f2 = new JFrame("");
		f2.setLayout(new GridLayout(5,1,5,5));
		f2.setSize(200,200);
		JLabel l1=new JLabel("Classic:"+results[0]);
		JLabel l2=new JLabel("Electronic:"+results[1]);
		JLabel l3=new JLabel("Rap:"+results[2]);
		JLabel l4=new JLabel("Rock:"+results[3]);
		JButton button = new JButton("GO");
		ActionListener buttonAL = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JFrame f1 = new JFrame("");
				f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f1.setSize(1144,512);
				try {
						AudioController audio = new AudioController(f1,f2);
						results=audio.getResults();
						l1.setText("Classic:"+results[0]);
						l2.setText("Electronic:"+results[1]);
						l3.setText("Rap:"+results[2]);
						l4.setText("Rock:"+results[3]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				frame=f1;
			}
			
		};
		button.addActionListener(buttonAL);
		f2.add(button);
		f2.add(l1);
		f2.add(l2);
		f2.add(l3);
		f2.add(l4);
		f2.setLocation(1130,0);
		f2.setVisible(true);


		 /*
		 float[][] train = new float[][]{new float[]{0, 0}, new float[]{0, 1}, new float[]{1, 0}, new float[]{1, 1}};
		 float[][] res = new float[][]{new float[]{0}, new float[]{1}, new float[]{1}, new float[]{0}};
		 MLP mlp = new MLP(audio.magnitudeLength, new int[]{2, 1});
		 mlp.getLayer(1).setIsSigmoid(false);
		 Random r = new Random();
		 int en = 500;
		
		 for (int e = 0; e < en; e++) {
		   for (int i = 0; i < res.length; i++) {
			   int idx = r.nextInt(res.length);
			   mlp.train(train[idx], res[idx], 0.3f, 0.6f);
		   }
		   	if ((e + 1) % 100 == 0) {
		   		System.out.println();
		   		for (int i = 0; i < res.length; i++) {
		   			float[] t = train[i];
		   			System.out.printf("%d epoch\n", e + 1);
		   			System.out.printf("%.1f, %.1f --> %.3f\n", t[0], t[1], mlp.run(t)[0]);
		   		}
		   	}
		}*/
		 
	}
	 
}
