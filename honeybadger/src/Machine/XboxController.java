
/**
 * Created by Alex on 2016-11-16.
 */

import ch.aplu.xboxcontroller.*;

public class Main {
    String path = "path\\to\\xboxcontroller.dll";

    private class MyXboxControllerAdapter extends XboxControllerAdapter
    {
        public void buttonA(boolean pressed)
        {
            if(pressed){

            }
        }

        public void buttonB(boolean pressed)
        {
            if(pressed){

            }
        }

        public void buttonX(boolean pressed)
        {
            if(pressed){

            }
        }

        public void buttonY(boolean pressed)
        {
            if (pressed){

            }
        }

        public void back(boolean pressed)
        {
            if (pressed){

            }
        }

        public void start(boolean pressed)
        {
            if (pressed){

            }
        }

        public void leftShoulder(boolean pressed)
        {
            if (pressed){

            }
        }

        public void rightShoulder(boolean pressed)
        {
            if (pressed){

            }
        }

        public void leftThumb(boolean pressed)
        {
            if (pressed){

            }
        }

        public void rightThumb(boolean pressed)
        {
            if (pressed){

            }
        }

        public void dpad(int direction, boolean pressed) {
            if (pressed) {
                switch (direction) {
                    case 0:
                        // N
                        break;
                    case 1:
                        // NE
                        break;
                    case 2:
                        // E
                        break;
                    case 3:
                        // SE
                        break;
                    case 4:
                        // S
                        break;
                    case 5:
                        // SW
                        break;
                    case 6:
                        // W
                        break;
                    case 7:
                        // NW
                        break;
                }
            }
        }

        public void leftTrigger(double value)
        {
            //value is how hard you press. Between 0-1.0
        }

        public void rightTrigger(double value)
        {
            //value is how hard you press. Between 0-1.0
        }

        public void leftThumbMagnitude(double magnitude)
        {
            //magnitude is how hard you press. Between 0-1.0
        }

        public void leftThumbDirection(double direction)
        {
            //direction is angle. Between 0-360.0, at top
        }

        public void rightThumbMagnitude(double magnitude)
        {
            //magnitude is how hard you press. Between 0-1.0
        }

        public void rightThumbDirection(double direction)
        {
            //direction is angle. Between 0-360.0, at top
        }

        public void isConnected(boolean connected)
        {
            if (connected)
                System.out.println(" - Controller connected");
            else
                System.out.println(" - Controller disconnected");
        }
    }

    public Main()
    {
        XboxController xc = new XboxController(path ,1,50,50);
        if(xc.isConnected()){
            System.out.println("Controller connected!");
        }
        xc.addXboxControllerListener(new MyXboxControllerAdapter());
        xc.setLeftThumbDeadZone(0.2);
        xc.setRightThumbDeadZone(0.2);
    }

    public static void main(String[] args) {
        new Main();
    }
}
