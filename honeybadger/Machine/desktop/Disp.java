package Machine.desktop;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Machine.desktop.JPanelOpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
public class Disp extends JPanel{
	BufferedImage image;
	VideoCapture cmr;
	public Disp(BufferedImage img)
	{
		image = img;
	}
	public void setImage(BufferedImage img){image = img;}
	
	public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
}