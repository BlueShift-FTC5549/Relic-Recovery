package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.blueshift.drivesupport.Gyroscope;
import org.blueshift.drivesupport.FieldPoint;
import org.blueshift.drivesupport.MecanumDrive;
import org.blueshift.vision.VuforiaTracker;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * An autonomous op program to score all of the points that the robot can, including:
 *      - Knocking the correct jewel off of the platform
 *      - Driving to the key, decoding it to find with Vuforia the correct column to put a glyph in
 *      - Placing one extra glyph in the crypto box
 *      - Park in the safe zone
 *
 * @author Gabriel Wong
 * @version 0.4 ALPHA
 */
@Autonomous(name="Score All", group ="Corner Stone")
public class AutonomousALL extends LinearOpMode {
    //Create the object to keep track of Elapsed Time, set the starting location, and keep track of the current location.
    private static       ElapsedTime runtime = new ElapsedTime();
    private static final FieldPoint  STARTING_LOCATION = new FieldPoint(1,1); //TODO: Update starting location
    private              Gyroscope   gyroscope = new Gyroscope( hardwareMap.get(BNO055IMU.class, "imu") );

    //Declare the motors and servo
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private DcMotor glyphLeft, glyphRight, liftMotor;
    private Servo bucketServo;

    //Placeholder for the Mecanum Drive object
    private MecanumDrive mecanumDrive;

    /**
     * Since this is a linear (not iterative) OpMode, there is only one function that will run one
     * time once the play button is pressed by the driver at the pit. It can include loops on the
     * inside, but the entire method will not loop.
     */
    @Override public void runOpMode() {
        initialize();

        //Wait for user input before continuing.
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        //Search for the Relic
        RelicRecoveryVuMark vuMark = searchForRelic();

        if (vuMark == RelicRecoveryVuMark.CENTER) {
            telemetry.addData(">", "CENTER");
        } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
            telemetry.addData(">", "RIGHT");
        } else if (vuMark == RelicRecoveryVuMark.LEFT) {
            telemetry.addData(">", "LEFT");
        }

        telemetry.update();
    }

    /**
     * Initialize all hardware components such as the four drive motors of the Mecanum train, the
     * two motors used for glyph/relic intake, and also the singular servo that controls the glyph
     * bucket. Initializing the motors includes setting their directions to the desired direction of
     * spin (it compensates for gear-reversals and different motor orientations).
     */
    private void initialize() {
        //Assign the motors a hardwareMap counterpart
        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        glyphLeft = hardwareMap.get(DcMotor.class, "glyphLeft");
        glyphRight = hardwareMap.get(DcMotor.class, "glyphRight");

        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        bucketServo = hardwareMap.get(Servo.class, "bucketServo");


        //Set the direction of each motor
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);

        glyphLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        glyphRight.setDirection(DcMotorSimple.Direction.REVERSE);

        liftMotor.setDirection(DcMotorSimple.Direction.FORWARD);


        //Create the two different drive objects
        mecanumDrive = new MecanumDrive(leftBack, leftFront, rightBack, rightFront);
    }

    /**
     * Output what the camera is seeing to the Robot Controller and initialize the tracking
     * function. Loop until the Vuforia Tracker can see a vuMark that isn't UNKNOWN, then return
     * that VuMark identifier and update the telemetry with that information.
     *
     * @return RelicRecoveryVuMark - The VuMark that has been found and the key associated with it.
     */
    private RelicRecoveryVuMark searchForRelic() {
        //Get the RC's camera monitor view hardware map id and make a new vuforiaTracker controller with it.
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaTracker vuforiaTracker = new VuforiaTracker(cameraMonitorViewId);

        vuforiaTracker.initTracking();

        RelicRecoveryVuMark vuMark = vuforiaTracker.track();

        //Initialize the tracking function and then loop until it finds the VuMark.
        while (vuMark == RelicRecoveryVuMark.UNKNOWN) {
            telemetry.addData("VuMark", "not visible");
            vuMark = vuforiaTracker.track();
        }

        //Update the telemetry with that found VuMark and return it.
        telemetry.addData("VuMark", "%s visible", vuMark);
        telemetry.update();

        return vuMark;
    }
}