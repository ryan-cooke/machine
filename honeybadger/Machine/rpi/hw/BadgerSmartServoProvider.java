package Machine.rpi.hw;

import com.pi4j.io.serial.*;

import static Machine.Common.Utils.Log;

/**
 * Class for providing the RobotXYZ Smart Servo implementation
 */
public class BadgerSmartServoProvider {
    public static class SerialListener implements SerialDataEventListener {
        protected SerialDataEvent lastReceivedEvent;

        public SerialListener(){
        }

        @Override
        public void dataReceived(SerialDataEvent event) {
            lastReceivedEvent = event;
            //TODO: handle receiving the ServoStatus
        }

        public SerialDataEvent getLastReceivedEvent(){ return lastReceivedEvent; }
    }

    public static byte SERVO_A = 0x1;
    public static byte SERVO_B = 0x2;
    public static byte SERVO_C = 0x3;

    public static byte MASK = (byte)0xFF;

    protected Serial serialBus;
    protected String serialPort;
    protected SerialConfig config;
    protected SerialListener serialListener;

    protected int ServoAPosition;
    protected int ServoBPosition;
    protected int ServoCPosition;

    public BadgerSmartServoProvider(){
        Log("Preconfiguring Serial devices");
        serialBus = SerialFactory.createInstance();
        serialListener = new SerialListener();
        serialBus.addListener(serialListener);
        config = new SerialConfig();
        try {
            serialPort = "/dev/serial0";
            config.device(serialPort)
                    .baud(Baud._115200)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1);
        }catch (Exception e){e.printStackTrace();}

        Log("Opening Serial Device");

        try {
            serialBus.open(config);
        }catch (Exception e){
            e.printStackTrace();
            Log(String.format("Unable to open %s",serialPort));
            return;
        }

        Log("Serial device ready.");
    }

    public void SetPosition(byte servoID, int flightTime, int position){
        //Verify inputs
        flightTime = flightTime/10;
        if(flightTime<0||flightTime>0xFF){
            return;
        }
        if(position<0||position>1023){
            return;
        }

        //We'll do an independent move "I_JOG"
        SetPosition(servoID,(byte)0x05,(byte)(flightTime&0xFF),position);
    }

    /**
     * Private method to set the position correctly.
     * Ported to Java and Pi4J. Original C implementation is available for download at XYZrobot's site.
     * Copyright claim is quoted, as is, below.
     *
     *   A1-16.h - Modified for XYZrobot ATmega 1280 control board.
     *   Copyright (c) 2015 Wei-Shun You. XYZprinting Inc.  All right reserved.
     *
     * @param servoID The registered ID of the servo that should move
     * @param commandByte Byte of the command (can be I_JOG or S_JOG)
     * @param flightTime Time (in 10s of ms) that the servo should be in flight
     * @param position The position, between 0 and 1023, that the servo should move to.
     */
    private void SetPosition(byte servoID, byte commandByte,  byte flightTime, int position){
        //_pID -> SERVO ID to execute this command
        //_CMD -> The SET Position command itself
        //_playtime -> Time in flight.
        //_position -> expected position from 1023 to 0

        //  From the above, _position=420 is the highest and 0 is the lowest it should go
        //  _playtime should be about 100ms (byte 0x32)
        //  _CMD=I_JOG=0x05
        byte[] _data = new byte[5];
        byte packetSize = (byte) 0x0C;

        _data[0] = (byte)(position & MASK);
        _data[1] = (byte)((position & 0xFF00)>>8);
        _data[2] = 0;					//set:0(position control), 1(speed control), 2(torque off), 3(position servo on)
        _data[3] = servoID;
        _data[4] = flightTime;

        //Calc checksum 1
        byte checksum_1 = (byte)((packetSize)^servoID^commandByte);
        for (int i = 0; i < 5; i++) {
            checksum_1 ^= _data[i];
        }
        checksum_1 &= 0xFE;

        //Calc checksum 2
        byte checksum_2 = (byte)((~checksum_1)&0xFE);
        try {
            //0xff 0xff 0x0c <PID> <CMD> <CHK1> <CHK2> <DATA>
            serialBus.write(MASK);
            serialBus.write(MASK);
            serialBus.write(packetSize);
            serialBus.write(servoID);
            serialBus.write(commandByte);
            serialBus.write(checksum_1);
            serialBus.write(checksum_2);

            serialBus.write(_data);
        } catch (Exception e){
            e.printStackTrace();
        }
        Log(String.format("Wrote to serial\n\tHEADER:%02x %02x %02x %02x %02x %02x %02x | DATA:%x %x %x %x %x",
                MASK,MASK,packetSize,servoID,commandByte,checksum_1,checksum_2,
                _data[0],_data[1],_data[2],_data[3],_data[4]));
    }

    private void ReadServoStatus(byte servoID){
        //TODO: port from code below
        //Original method call A1_16_ReadData(id, _CMD=CMD_RAM_READ, _addr_start=RAM_Joint_Position, _data_length=0x02)
//        while(Serial1.read() != -1);
//        checksum_1 = (9^_pID^_CMD^_addr_start^_data_length)&0xfe;
//        checksum_2 = (~checksum_1)&0xfe;
//        Serial1.write(0xff);
//        Serial1.write(0xff);
//        Serial1.write(0x09);						//packet size
//        Serial1.write(_pID);
//        Serial1.write(_CMD);
//        Serial1.write(checksum_1);
//        Serial1.write(checksum_2);
//        Serial1.write(_addr_start);
//        Serial1.write(_data_length);			//length of data
//        int value = A1_16_ReadPacket(_data_length);
//        return value;
    }
}
