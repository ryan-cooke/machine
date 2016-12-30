package Machine.desktop;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
    
public class JPanelOpenCV extends JPanel{
    public static BufferedImage image;

    public static void main (String args[]) throws InterruptedException{
        JPanelOpenCV t = new JPanelOpenCV();
       
       // VideoCapture camera = new VideoCapture();
        VideoCapture camera = new VideoCapture("http://96.10.1.168/mjpg/1/video.mjpg");        //VideoCapture camera = new VideoCapture("http://150.214.93.55:8090/?action=stream.mjpg");
        //camera.open("http://192.168.2.86:8090/?action=stream");
       // VideoCapture camera = new VideoCapture("http://192.168.2.86:8090/?action=stream.mjpg");
       // camera.open(192.168.2.86:8090/?action=stream?dummy=param.mjpg);
        //VideoCapture camera = new VideoCapture(0);
        //VideoCapture camera = new VideoCapture("C:\\Users\\josep\\Desktop\\OpenCv\\10fpsStreettest2.avi");
        Disp j = new Disp(image);
        
        Mat original = new Mat();
        Mat frame = new Mat();
        Mat diffFrame = null;
        camera.read(frame); 
        //connected components
        
        
        
        
        //Video Writer
       // String fileName = "C:\\Users\\josep\\Desktop\\OpenCv\\10fpsStreettest3.avi";
        int fourcc = VideoWriter.fourcc('I','Y','U','V');
        double fps = 10;
        
        Size s = new Size(640,480);
        System.out.println("the fourcc code it "+fourcc);
        //VideoWriter rec = new VideoWriter();
       // if(rec.open(fileName, fourcc, fps, s)){System.out.println("true");}else{System.out.println("false");}
        //rec.open(fileName, -1, fps, s);
        
        //if(!rec.isOpened()){System.out.println("something wrong with video file");}
        
        if(!camera.isOpened()){
            System.out.println("Error 1 again");
        }
        else { 
        	
        	
        	
        	
        	
            
          /*  while(true){      	  
            	 
                if (camera.read(frame)){
                	BufferedImage image = t.MatToBufferedImage(frame);
                	t.window(image, "Original Image", 0, 0);
                    System.out.println(image.getWidth());
                    System.out.println(image.getHeight());
                   
                 //   t.window(t.grayscale(image), "Processed Image", 40, 60);
                    //t.window(t.loadImage("ImageName"), "Image loaded", 0, 0);
                   break;
                }
                
            }   */
        }
        
        JFrame frameb = new JFrame();
        Disp b = new Disp(MatToBufferedImage(frame));
        frameb.getContentPane().add(b);
        frameb.setTitle("streamB");
        frameb.setSize(640, 480 + 30);
        frameb.setLocation(0, 0);
        frameb.setVisible(true);
        
        JFrame framea = new JFrame();
        Disp a = new Disp(MatToBufferedImage(frame));
        framea.getContentPane().add(a);
        framea.setTitle("streamA");
        framea.setSize(640, 480 + 30);
        framea.setLocation(0, 0);
        framea.setVisible(true);
        
        
        
        BackgroundSubtractorMOG2 mBGSub = Video.createBackgroundSubtractorMOG2();
        int count =0;
        double rstTime = System.currentTimeMillis();
        int secondsRec=300;
        while(true)
        {
        	
        	camera.read(frame);
        //	rec.write(frame);
        	original = frame.clone();
        	
        	//-------BackGroudSubtraction---------//
        	diffFrame = new Mat(frame.size(), CvType.CV_8UC1);
        	//processFrame(camera, frame,diffFrame,mBGSub);
        	//frame = diffFrame.clone();
        	//-----search-----------//
        	//original = searchForMovement(diffFrame, original);
        	//-----search for circles-----//
        	//original = searchCircle(original,original);
        	//-----end----//
        	if(System.currentTimeMillis()-rstTime>1000){System.out.println(count);rstTime=System.currentTimeMillis();count=0;secondsRec--;}
        	
        	image = t.MatToBufferedImage(frame);
        	a.setImage(MatToBufferedImage(original));
        	b.setImage(MatToBufferedImage(frame));
        	
        	
        	framea.getContentPane().add(a);
        	frameb.getContentPane().add(b);
        	
        	Thread.sleep(10);
        	count++;
        	//System.out.println("tick");
        	framea.invalidate();
        	framea.validate();
        	framea.repaint();
        	
        	frameb.invalidate();
        	frameb.validate();
        	frameb.repaint();
        	if(secondsRec<0){break;}
        	 
        	
        }
        
       // rec.release();
        camera.release();
        System.exit(0);
        
       // camera.release();
    }
    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
    public JPanelOpenCV() {
    }
    public JPanelOpenCV(BufferedImage img) {
        image = img;
    }   
    //Show image on window
    public void window(BufferedImage img, String text, int x, int y) {
        JFrame frame0 = new JFrame();
        frame0.getContentPane().add(new JPanelOpenCV(img));
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame0.setTitle(text);
        frame0.setSize(img.getWidth(), img.getHeight() + 30);
        frame0.setLocation(x, y);
        frame0.setVisible(true);
    }
    //Load an image
    public BufferedImage loadImage(String file) {
        BufferedImage img;
        try {
            File input = new File(file);
            img = ImageIO.read(input);
            return img;
        } catch (Exception e) {
            System.out.println("erro");
        }
        return null;
    }
    //Save an image
    public void saveImage(BufferedImage img) {        
        try {
            File outputfile = new File("Images/new.png");
            ImageIO.write(img, "png", outputfile);
        } catch (Exception e) {
            System.out.println("error");
        }
    }
    //Grayscale filter
    public BufferedImage grayscale(BufferedImage img) {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color c = new Color(img.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor =
                        new Color(
                        red + green + blue,
                        red + green + blue,
                        red + green + blue);
                img.setRGB(j, i, newColor.getRGB());
            }
        }
        return img;
    }
    public static BufferedImage MatToBufferedImage(Mat frame) {
        //Mat() to BufferedImage
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);
        return image;
    }
    
    public static void processFrame(VideoCapture capture, Mat mRgba, Mat mFGMask,BackgroundSubtractorMOG2 mBGSub)
    {
    	mBGSub.apply(mRgba, mFGMask,0.005);
    	Imgproc.cvtColor(mFGMask, mRgba, Imgproc.COLOR_GRAY2BGRA,0);
    	
    	Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2));
    	
    	Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2));
    	
    	Mat openElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3), new Point(1,1));
    	Mat closeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(7, 7), new Point(3, 3));
    	
    	Imgproc.threshold(mFGMask, mFGMask, 30, 259, Imgproc.THRESH_BINARY);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, erode);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, dilate);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, openElem);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_CLOSE, closeElem);
    }
    
    public static Mat searchForMovement(Mat thresholdImage, Mat frame)
	{
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(thresholdImage, contours, hierarchy,
			Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		Rect objectBoundingRectangle = new Rect(0, 0, 0, 0);
		for (int i = 0; i < contours.size(); i++)
		{
			objectBoundingRectangle = Imgproc.boundingRect(contours.get(i));
			if(objectBoundingRectangle.area()>500)
			Imgproc.rectangle(frame, objectBoundingRectangle.tl(), objectBoundingRectangle.br(), new Scalar(0,255,0));
			
		}
		return frame;
		//drawnWindow.showImage(frame);
	}
    
    public static Mat searchCircle(Mat rawMat, Mat out)
    
    {
    	Mat raw = rawMat;
    	Mat greyscale = new Mat();
    	Imgproc.cvtColor(raw, greyscale, Imgproc.COLOR_BGR2GRAY);
    	Imgproc.GaussianBlur(greyscale, greyscale, new Size(9, 9), 2, 2 );
    	Mat circles = new Mat();
    	
    	 Imgproc.HoughCircles(greyscale, circles, Imgproc.CV_HOUGH_GRADIENT, 1, (double)greyscale.rows()/8, 50, 80,0,0);
    	 System.out.println("got here");
    	 System.out.println(circles);
         for (int i = 0; i < circles.cols(); i++){	
        	 double[] vecCircle = circles.get(0, i);
        	 
        	 int x = (int) vecCircle[0];
             int y = (int) vecCircle[1];
             int r = (int) vecCircle[2];
             Imgproc.circle(out, new Point(x,y), r, new Scalar(0,0,255),10);
             System.out.println("x detected as "+ x);
         }
    	 
    	 return out;
    }
}