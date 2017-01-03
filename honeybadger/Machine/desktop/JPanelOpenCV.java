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
import javax.swing.JPanel;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.videoio.VideoCapture;

import static Machine.Common.Utils.ErrorLog;
import static Machine.Common.Utils.Log;


public class JPanelOpenCV extends JPanel {
    enum BUFFER_TYPE{
        REGULAR,
        CANNY,
        HOUGH,
    }

    static JPanelOpenCV instance;

    static BufferedImage processedImage;

    private static BufferedImage CannyBuffer;

    private static BufferedImage HoughBuffer;

    private static BUFFER_TYPE FrameBufferType = BUFFER_TYPE.REGULAR;

    private static boolean target = false;
    private static boolean blueTarget = false;
    private static int center = 320;

    private static int topWall=0;
    private static int[]topWallAr = {0,0,0,0,0};
    private static int countTop=0;

    private static Scalar lowerBlack = new Scalar(0, 0, 0);
    private static Scalar upperBlack = new Scalar(180, 255, 90);

    private static Scalar lowerBlue = new Scalar(5, 140, 100);
    private static Scalar upperBlue = new Scalar(20, 255, 255);

    private static Scalar lowerGreen = new Scalar(35, 140, 60);
    private static Scalar upperGreen = new Scalar(70, 255, 255);

    private static Scalar lowerYellow = new Scalar(90,150,150);
    private static Scalar upperYellow = new Scalar(110,255,255);

    private static Scalar lowerRed = new Scalar(115,100,100);
    private static Scalar upperRed = new Scalar(140,255,255);


    private int erode = 3;
    private int dilate = 10;

    private static String ConnectURL;
    private static boolean ShouldDraw;

    public static void main(String args[]) throws InterruptedException {
        JPanelOpenCV j1 = new JPanelOpenCV();
        j1.startLoop();
    }

    public static void SetConnectionHost(String host) {
        ConnectURL = String.format("http://%s:8090/?action=stream", host);
        ShouldDraw = true;
    }

    public void setDilate(int d) {
        dilate = d;
    }

    public void setErode(int e) {
        erode = e;
    }

    public static void setGreen(Scalar upper, Scalar lower) {
        lowerGreen = lower;
        upperGreen = upper;
    }

    public static Scalar[] getColorScalars() {
        Scalar[] ar = new Scalar[10];
        ar[0] = lowerGreen;
        ar[1] = upperGreen;
        ar[2] = lowerBlue;
        ar[3] = upperBlue;
        ar[4] = lowerBlack;
        ar[5] = upperBlack;
        ar[6] = lowerRed;
        ar[7] = upperRed;
        ar[8] = lowerYellow;
        ar[9] = upperYellow;
        return ar;
    }

    synchronized public static void renderActive(boolean shouldDraw) {
        ShouldDraw = shouldDraw;
    }

    public boolean isTarget() {
        return target;
    }

    public boolean isBlueTarget() {
        return blueTarget;
    }

    public static void setBlue(Scalar upper, Scalar lower) {
        upperBlue = upper;
        lowerBlue = lower;
    }

    public static void setBlack(Scalar upper, Scalar lower) {
        upperBlack = upper;
        lowerBlack = lower;
    }


    public void startLoop() {
        String arch = System.getProperty("os.arch");
        //System.out.println(arch);
        String openCVLib = arch.contains("x86") ? "opencv_java310" : "opencv_java310_64";
        String ffmpegLib = arch.contains("x86") ? "opencv_ffmpeg310" : "opencv_ffmpeg310_64";
        System.loadLibrary(openCVLib);
        System.loadLibrary(ffmpegLib);

        //TODO: For Joey: you might want to change the IP
        VideoCapture camera = new VideoCapture(ConnectURL);
        //VideoCapture camera = new VideoCapture(1);
        if (!camera.isOpened()) {
            ErrorLog("Error opening stream");
            return;
        }

        Mat original = new Mat();
        Mat frame = new Mat();
        Mat diffFrame = null;
        camera.read(frame);

        if (!camera.isOpened()) {
            Log("Error 1 again");
        }


        Mat resized = new Mat();
        Size outputSize = new Size(640, 480);
        while (ShouldDraw) {
            camera.read(frame);
            processFrame(frame);

            processedImage = MatToBufferedImage(frame);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //@foxtrot94: resize whatever stream to 640x480
            Imgproc.resize(frame, resized, outputSize);
            processedImage = MatToBufferedImage(resized);
            instance.invalidate();
            instance.repaint();
            //@foxtrot94: END
        }

        camera.release();
    }

    public void processFrame(Mat frame) {
        Mat original = frame.clone();

        Mat hsv = original.clone();
        Mat hsv2 = original.clone();
        Mat hsv3 = original.clone();
        Mat hsv4 = original.clone();
        Mat hsv5 = original.clone();
        Mat hough = original.clone();

        Imgproc.cvtColor(original, hsv, Imgproc.COLOR_RGB2HSV);

        hsv2 = hsv.clone();
        hsv3 = hsv.clone();
        hsv4 = hsv.clone();
        hsv5 = hsv.clone();


        Core.inRange(hsv, lowerGreen, upperGreen, hsv);
        Core.inRange(hsv2, lowerBlue, upperBlue, hsv2);
        Core.inRange(hsv3, lowerBlack, upperBlack, hsv3);
        Core.inRange(hsv4, lowerYellow, upperYellow,hsv4);
        Core.inRange(hsv5, lowerRed, upperRed, hsv5);

        hsv = erodeDilate(hsv, dilate, erode);
        hsv2 = erodeDilate(hsv2, dilate, erode);
        hsv3 = erodeDilate(hsv3, dilate, erode);
        hsv4 = erodeDilate(hsv4, dilate, erode);
        hsv5 = erodeDilate(hsv5, dilate, erode);


        frame = searchForMovement(hsv, frame, "green");
        frame = searchForMovement(hsv2, frame, "blue");
        frame = searchForMovement(hsv4,frame, "yellow");
        frame = searchForMovement(hsv5,frame,"red");


        frame = searchLine(hough, frame);
        frame = drawCrosshair(frame, 30, 320, 240, new Scalar(0, 0, 255));
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage frameBuffer;

        switch (FrameBufferType){
            case CANNY:{
                frameBuffer = CannyBuffer;
                break;
            }
            case HOUGH:{
                frameBuffer = HoughBuffer;
                break;
            }
            case REGULAR:
            default:
                frameBuffer = processedImage;
                break;
        }

        g.drawImage(frameBuffer, 0, 0, this);
    }

    //Load an processedImage
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

    //Save an processedImage
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

    public static void processFrame(VideoCapture capture, Mat mRgba, Mat mFGMask, BackgroundSubtractorMOG2 mBGSub) {
        mBGSub.apply(mRgba, mFGMask, 0.005);
        Imgproc.cvtColor(mFGMask, mRgba, Imgproc.COLOR_GRAY2BGRA, 0);

        Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));

        Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));

        Mat openElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(1, 1));

        Mat closeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7), new Point(3, 3));

        Imgproc.threshold(mFGMask, mFGMask, 30, 259, Imgproc.THRESH_BINARY);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, erode);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, dilate);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, openElem);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_CLOSE, closeElem);
    }

    public static Mat searchForMovement(Mat thresholdImage, Mat frame, String color) {

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresholdImage, contours, hierarchy,
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Rect objectBoundingRectangle = new Rect(0, 0, 0, 0);
        target = false;
        blueTarget = false;
        for (int i = 0; i < contours.size(); i++) {
            objectBoundingRectangle = Imgproc.boundingRect(contours.get(i));
            objectBoundingRectangle.width -= 13;
            objectBoundingRectangle.x += 3;
            boolean skinnyRect = false;
            boolean floorPanels =false;
            if (objectBoundingRectangle.width == 0) {
            } else if (objectBoundingRectangle.height / objectBoundingRectangle.width > 3) {
                skinnyRect = true;
            }
            if (color.equals("red")||color.equals("yellow")){floorPanels=true;}

            if (skinnyRect) {
                Imgproc.rectangle(frame, objectBoundingRectangle.tl(), objectBoundingRectangle.br(), new Scalar(0, 255, 0));
                int rectCenter = objectBoundingRectangle.x + objectBoundingRectangle.width / 2;
                if (Math.abs(rectCenter - center) < 5) {
                    target = true;
                    if (color.equals("blue")) {
                        blueTarget = true;
                    }
                }
            }

            if (floorPanels)
            {
                if(objectBoundingRectangle.width*objectBoundingRectangle.height>2000) {
                    Imgproc.rectangle(frame, objectBoundingRectangle.tl(), objectBoundingRectangle.br(), new Scalar(255, 0, 0));

                }
            }
        }
        return frame;
    }

    public static Mat searchCircle(Mat rawMat, Mat out) {
        Mat raw = rawMat;
        Mat greyscale = new Mat();
        Imgproc.cvtColor(raw, greyscale, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(greyscale, greyscale, new Size(9, 9), 2, 2);
        Mat circles = new Mat();

        Imgproc.HoughCircles(greyscale, circles, Imgproc.CV_HOUGH_GRADIENT, 1, (double) greyscale.rows() / 8, 50, 80, 0, 0);
        //System.out.println("got here");
        //System.out.println(circles);
        for (int i = 0; i < circles.cols(); i++) {
            double[] vecCircle = circles.get(0, i);

            int x = (int) vecCircle[0];
            int y = (int) vecCircle[1];
            int r = (int) vecCircle[2];
            Imgproc.circle(out, new Point(x, y), r, new Scalar(0, 0, 255), 10);
            //System.out.println("x detected as " + x);
        }

        return out;
    }

    public static Mat searchLine(Mat rawMat, Mat out) {
        Mat raw = rawMat;
        Mat edges = new Mat();
        Mat greyscale = new Mat();
        Imgproc.cvtColor(raw, greyscale, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(greyscale, greyscale, new Size(10, 10));

        int lowThreshold = 5;
        int ratio = 20;
        Imgproc.Canny(greyscale, edges, lowThreshold, lowThreshold * ratio);

        CannyBuffer = MatToBufferedImage(edges);
        Mat lines = new Mat();
        int threshold = 70;
        int minLineSize = 100;
        int lineGap = 5;

        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        for (int i = 0; i < lines.cols(); i++) {
            double[] val = lines.get(0, i);

            double rise = Math.abs(val[3] - val[1]);
            double run = Math.abs(val[2] - val[0]);
            if ((rise / run) > 0.8) {
            } else {
                Scalar c1 = new Scalar(200,200,0);
                if(val[1]<250){c1= new Scalar(0,0,255);}
                Imgproc.line(out, new Point(val[0], val[1]), new Point(val[2], val[3]), c1, 2);
            }
            if(val[1]<250){
            avgTop((int)val[1]);
            Log(getDistances()+" inches is distance");
            }
        }

        HoughBuffer = MatToBufferedImage(out);

        return out;
    }

    public static Mat erodeDilate(Mat inMat, int dilate, int erode) {
        Mat out = inMat;
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilate, dilate));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erode, erode));

        Imgproc.erode(inMat, out, erodeElement);
        Imgproc.erode(inMat, out, erodeElement);

        Imgproc.dilate(inMat, out, dilateElement);
        Imgproc.dilate(inMat, out, dilateElement);

        return out;

    }

    public static Mat drawCrosshair(Mat inMat, int radius, int centerX, int centerY, Scalar color) {
        Mat cross = inMat;
        Point center = new Point(centerX, centerY);
        Point top = new Point(centerX, centerY + 30);
        Point bot = new Point(centerX, centerY - 30);
        Point left = new Point(centerX - 30, centerY);
        Point right = new Point(centerX + 30, centerY);
        Imgproc.circle(cross, center, radius, color);
        Imgproc.line(cross, center, top, color);
        Imgproc.line(cross, center, bot, color);
        Imgproc.line(cross, center, left, color);
        Imgproc.line(cross, center, right, color);
        return cross;
    }

    synchronized static void setDrawingBuffer(BUFFER_TYPE drawingBuffer){
        FrameBufferType = drawingBuffer;
    }

    public static void avgTop(int x)
    {
        countTop++;
        topWall=((topWallAr[0]+topWallAr[1]+topWallAr[2]+topWallAr[3]+topWallAr[4])/5);
        topWallAr[countTop%5]=x;
    }
    public static double getDistances(){
        double distance=0;
        if (topWall>120){distance = ((topWall-120)/5.45)+25;}
        if (topWall<=120){distance = (topWall/11)+15;}




        return distance;
    }

}