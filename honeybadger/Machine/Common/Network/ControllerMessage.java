package Machine.Common.Network;

import Machine.rpi.Badger;

import java.io.Serializable;

/**
 * Created by Javier Fajardo on 2016-12-20.
 */
public class ControllerMessage extends BaseMsg {
    public interface ControllerAction{
        void Do(Badger badger);
    }

    public static class MoveForward implements ControllerAction, Serializable{
        float throttle;

        public MoveForward(float throttle){
            this.throttle=throttle;
        }

        public void Do(Badger badger){
            badger.moveForward(throttle);
        }

        @Override
        public String toString(){
            return "Pressed A";
        }
    }

    private ControllerAction Action;

    public ControllerMessage(ControllerAction someAction){
        Action = someAction;
        payload = someAction.toString();
    }

    @Override
    public void Execute(Object context) {
        Badger badger = (Badger) context;
        if(badger==null){
            //TODO: Send some error message back
            return;
        }

        // Execute
        Action.Do(badger);
    }
}
