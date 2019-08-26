import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Paint extends JPanel {

	Complex[][] data;

	public Paint(Complex[][] data) {
		setData(data);
	}

	private void setData(Complex[][] data) {
		this.data = data;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.BLACK);
		for (int x = 0; x < 1144; x++) {
			for (int y = 0; y < 512; y++) {
				double magnitude = data[x / 2][y / 2].abs() * 15;
				Color color = new Color((int) magnitude);
				g.setColor(color);
				g.fillRect(x, y, 1, 1);
			}
		}

	}

	public void repaint(Graphics g) {
		super.repaint();
		this.setBackground(Color.BLACK);
		for (int x = 0; x < 1000; x++) {
			for (int y = 0; y < 640; y++) {
				double magnitude = Math.log(data[x][y / 10].abs() + 1);
				Color color = new Color(0, (int) magnitude * 10, (int) magnitude * 20);
				g.setColor(color);
				g.fillRect(x, y, 2, 2);
			}
		}
	}

	public void initiateCanvas(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.BLACK);
	}

	public static BufferedImage getScreenShot(Component component) {
		BufferedImage image = new BufferedImage(1144, 512, BufferedImage.TYPE_INT_RGB);
		component.paint(image.getGraphics());
		return image;
	}

	public static void saveScreenShot(Component component, String fileName) throws Exception {
		BufferedImage img = getScreenShot(component);
		ImageIO.write(img, "png", new File(fileName));
	}

}
