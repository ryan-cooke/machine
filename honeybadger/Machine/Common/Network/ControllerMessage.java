package Machine.Common.Network;

import Machine.desktop.Controller;
import Machine.rpi.Badger;

/**
 * Created by Javier Fajardo on 2016-12-20.
 */
public class ControllerMessage extends BaseMsg {

    ControllerMessage(){

    }

    @Override
    public void Execute(Object context) {
        //This looks like a bad idea. Not final.
        Badger badger = (Badger) context;
        if(badger==null){
            return;
        }
    }
}
