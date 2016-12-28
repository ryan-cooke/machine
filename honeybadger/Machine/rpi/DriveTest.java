package Machine.rpi;

public class DriveTest {
    public static void main(String[] args) throws Exception {
        HoneybadgerV6 testBadger = HoneybadgerV6.getInstance();

//        testBadger.moveForward(100);

        testBadger.STOP();
        Thread.sleep(3000);

        testBadger.moveForward(80);

        Thread.sleep(3000);

        testBadger.STOP();
        Thread.sleep(3000);

        testBadger.moveBackward(80);

        Thread.sleep(13000);
//
//        sopl("Moving forward at 50%");
//        testBadger.moveForward(50);
//        Thread.sleep(5000);
//        sopl("Moving backward at 50%");
//        testBadger.moveBackward(50);
//        Thread.sleep(5000);
//        sopl("Strafing left at 50%");
//        testBadger.strafeLeft(50);
//        Thread.sleep(5000);
//        sopl("Strafing right at 50%");
//        testBadger.strafeRight(50);
//        Thread.sleep(5000);
    }
    private static void sopl(Object ob) {
        System.out.println(ob);
    }
}
