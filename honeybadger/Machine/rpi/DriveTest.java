package Machine.rpi;

public class DriveTest {
    public static void main(String[] args) throws Exception {
        Badger testBadger = new Badger();

        sopl("Moving forward at 50%");
        testBadger.moveForward(50);
        Thread.sleep(500);
        sopl("Moving backward at 50%");
        testBadger.moveBackward(50);
        Thread.sleep(500);
        sopl("Strafing left at 50%");
        testBadger.strafeLeft(50);
        Thread.sleep(500);
        sopl("Strafing right at 50%");
        testBadger.strafeRight(50);
        //Thread.sleep(500);
    }
    private static void sopl(Object ob) {
        System.out.println(ob);
    }
}
