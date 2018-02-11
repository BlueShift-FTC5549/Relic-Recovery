package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.blueshift.drivesupport.Gyroscope;
import org.blueshift.drivesupport.TankDrive;
import org.blueshift.vision.JewelState;
import org.blueshift.vision.VuforiaTracker;

/**
 * Autonomous TeleOP for the FTC (FIRST Tech Challenge) 2017-2018 tournament "Relic Recovery". The
 * goal of this program is to knock off the correct jewel during autonomous only.
 *
 * Starts at BLUE.
 *
 * @author Gabriel Wong
 * @version 1.0
 */

@SuppressWarnings("FieldCanBeLocal")
@Autonomous(name="Blue: Jewel", group ="Blue")
public class Auto_BJewel extends LinearOpMode {
    //Elapsed time of the OpMode
    private static ElapsedTime runtime = new ElapsedTime();

    //Instance variables set in the setDefaultFunctions() function
    private double LSERVO_DOWN_POSITION, LSERVO_UP_POSITION, RSERVO_UP_POSITION;
    private int slightTurnTimeout, SEARCHING_DELAY;

    //Gyroscope object and working information
    private Gyroscope gyroscope;
    private double prevHeading;
    private int gyroscopeToUse = 1;

    //All motors and servos used in autonomous.
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private DcMotor glyphLeft, glyphRight, conveyorLeft, conveyorRight;
    private Servo jewelServoR, jewelServoL;

    //The tracker that will find the jewel and the pictograph
    private VuforiaTracker vuforiaTracker;

    //Keeping track of the pictograph and the jewel states
    private JewelState jewelAtFront;

    //The tank drive object. The core driving function of the autonomous program.
    private TankDrive tankDrive;

    /**
     * Since this is a linear (not iterative) OpMode, there is only one function that will run one
     * time once the play button is pressed by the driver at the box. It can include loops on the
     * inside, but the entire method will not loop.
     */
    @Override public void runOpMode() {
        //Run the initialization function,
        initialize();

        //Inform the driver that intiialization has competleted and he/she can start on the OpMode.
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        //Power both servos so that they both stay upright during movement init.
        jewelServoL.setPosition(LSERVO_UP_POSITION);
        jewelServoR.setPosition(RSERVO_UP_POSITION);

        telemetry.addData("Status", "Starting Searches");
        telemetry.update();

        //Find the Pictograph and Jewel with the phone's camera (See VuforiaTracker)
        searchForJewel();

        telemetry.clear();
        telemetry.addData("Jewel", jewelAtFront);
        telemetry.update();

        jewelServoL.setPosition(LSERVO_DOWN_POSITION);
        sleep(400);

        //SWitch through the different jewel states and perform the necessary actions when it finds it.
        switch (jewelAtFront) {
            case RED: //Knock off back jewel.
                slightTurn(1);
                jewelServoL.setPosition(LSERVO_UP_POSITION);
                sleep(200);
                slightTurn(-1);
                break;
            case BLUE: //Knock off the front jewel.
                slightTurn(-1);
                jewelServoL.setPosition(LSERVO_UP_POSITION);
                sleep(200);
                slightTurn(1);
                break;
            default: //Assume that the 'unseen' jewel is blue. Knock off the front jewel.
                slightTurn(-1);
                jewelServoL.setPosition(LSERVO_UP_POSITION);
                sleep(250);
                slightTurn(1);
                break;
        }
    }

    /**
     * Turn the robot a value of (PI/20) degrees to either the right or the left, depending on the
     * given direction on function call. This is used when knocking off the glyph.
     *
     * @param direction [-1] || [1] - Turn left or right
     */
    private void slightTurn(int direction) {
        //Keep track of time so that it can time out after a certain time (slightTurnTimeout)
        ElapsedTime elapsedTime = new ElapsedTime();
        elapsedTime.reset();

        //The previous heading to compare against
        prevHeading = gyroscope.getHeading();

        //Make sure the range doesn't exceed an abs value of 1
        direction = Range.clip(direction, -1, 1);

        while (!gyroscope.headingEquals(prevHeading - (direction*Math.PI/20), Math.PI/90) && opModeIsActive() && elapsedTime.milliseconds() <= slightTurnTimeout) {
            tankDrive.drive(0, 0.19 * direction);
        }

        tankDrive.stop();
    }

    /**
     * Set all of the default values. This function was created so that variable sets can be
     * interchanged between different OpModes.
     */
    private void setupDefaultValues() {
        LSERVO_UP_POSITION = 1.0;
        LSERVO_DOWN_POSITION = 0.4;
        RSERVO_UP_POSITION = 0.0;

        SEARCHING_DELAY = 3600;
        slightTurnTimeout = 2500;
    }

    /**
     * Initialize all hardware components such as the four drive motors of the Mecanum train, the
     * four motors used for glyph/relic intake, and both servos used for the two different jewel
     * arms.
     */
    private void initialize() {
        setupDefaultValues();
        telemetry.update();

        //Assign the motors a hardwareMap counterpart
        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        glyphLeft = hardwareMap.get(DcMotor.class, "glyphLeft");
        glyphRight = hardwareMap.get(DcMotor.class, "glyphRight");
        conveyorLeft = hardwareMap.get(DcMotor.class, "conveyorLeft");
        conveyorRight = hardwareMap.get(DcMotor.class, "conveyorRight");


        //Set the direction of each motor
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);

        glyphLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        glyphRight.setDirection(DcMotorSimple.Direction.REVERSE);
        conveyorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        conveyorRight.setDirection(DcMotorSimple.Direction.FORWARD);


        //Declare Servos
        jewelServoL = hardwareMap.get(Servo.class, "jewelServoL");
        jewelServoR = hardwareMap.get(Servo.class, "jewelServoR");

        if (gyroscopeToUse == 0) { gyroscope = new Gyroscope(hardwareMap.get(BNO055IMU.class, "imu"));
        } else { gyroscope = new Gyroscope(hardwareMap.get(BNO055IMU.class, "imu1")); }
        gyroscope.init();

        //Declare the object combinations
        tankDrive = new TankDrive(leftBack, leftFront, rightBack, rightFront);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        vuforiaTracker = new VuforiaTracker(cameraMonitorViewId);

        //Notify the Driver
        telemetry.addData("Status", "Init in " +  runtime.seconds() + " seconds");
        telemetry.addData("Jewel Status", jewelAtFront);
        telemetry.update();
    }

    /**
     * Use the vuforia tracker to find the pictograph and the jewel. An error-checking timeout is
     * built into the jewel finding function incase the camera fails.
     */
    public void searchForJewel() {
        vuforiaTracker.initJewelTracking();
        jewelAtFront = vuforiaTracker.jewelSearch_BottomLeft();

        if (jewelAtFront == null) {
            sleep(1000);
            jewelAtFront = vuforiaTracker.jewelSearch_BottomLeft();
        }
    }
}
