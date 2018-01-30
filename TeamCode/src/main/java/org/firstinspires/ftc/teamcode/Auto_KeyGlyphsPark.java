package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.blueshift.drivesupport.Gyroscope;
import org.blueshift.drivesupport.FieldPoint;
import org.blueshift.drivesupport.TankDrive;
import org.blueshift.vision.VuforiaTracker;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 *
 *
 * @author Gabriel Wong
 * @version 1.0
 */

@Autonomous(name="Corner: Key, 2 Glyphs, Park", group ="Corner")
public class Auto_KeyGlyphsPark extends LinearOpMode {
    private static final FieldPoint  STARTING_LOCATION = new FieldPoint(1,1); //TODO: Update starting location
    private static final double      SEARCHING_DELAY   = 3500; //ms, Pictograph Identification

    private static ElapsedTime runtime = new ElapsedTime();

    private final double OVERSHOOT_CONSTANT = 0.21;

    private Gyroscope gyroscope;
    private double prevHeading;

    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private DcMotor glyphLeft, glyphRight, conveyorLeft, conveyorRight;
    private Servo jewelServo;
    private ColorSensor jewelSensor;

    private TankDrive tankDrive;

    /**
     * Since this is a linear (not iterative) OpMode, there is only one function that will run one
     * time once the play button is pressed by the driver at the pit. It can include loops on the
     * inside, but the entire method will not loop.
     */
    @Override public void runOpMode() {
        initialize();

        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        turnLeft();

    }

    /**
     *
     *
     */
    private void turnLeft() {
        prevHeading = gyroscope.getHeading();

        while (!gyroscope.headingEquals(prevHeading + Math.PI/2 - OVERSHOOT_CONSTANT, Math.PI/64)) {
            tankDrive.drive(0, -0.25);
        }

        tankDrive.drive(0, 0);

        while (opModeIsActive()) {
            telemetry.addData("Heading:", gyroscope.getHeading());
            telemetry.update();
        }
    }

    /**
     *
     *
     */
    private void turnRight() {
        prevHeading = gyroscope.getHeading();

        while (!gyroscope.headingEquals(prevHeading - Math.PI/2 + OVERSHOOT_CONSTANT, Math.PI/64)) {
            tankDrive.drive(0, 0.25);
        }

        tankDrive.drive(0, 0);

        while (opModeIsActive()) {
            telemetry.addData("Heading:", gyroscope.getHeading());
            telemetry.update();
        }
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
        conveyorLeft = hardwareMap.get(DcMotor.class, "conveyorLeft");
        conveyorRight = hardwareMap.get(DcMotor.class, "conveyorRight");


        //Set the direction of each motor
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);

        glyphLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        glyphRight.setDirection(DcMotorSimple.Direction.REVERSE);
        conveyorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        conveyorRight.setDirection(DcMotorSimple.Direction.FORWARD);

        tankDrive = new TankDrive(leftBack, leftFront, rightBack, rightFront);

        gyroscope = new Gyroscope( hardwareMap.get(BNO055IMU.class, "imu") );
        gyroscope.init();

        jewelSensor = hardwareMap.colorSensor.get("jewelSensor");
    }

    /**
     * Output what the camera is seeing to the Robot Controller and initialize the tracking
     * function. Loop until the Vuforia Tracker can see a vuMark that isn't UNKNOWN, then return
     * that VuMark identifier and update the telemetry with that information.
     *
     * This method will only search until a maximmum time is hit. After that, it will return the
     * 'CENTER' vuMark as a default.
     *
     * @return RelicRecoveryVuMark - The VuMark that has been found and the key associated with it.
     */
    private RelicRecoveryVuMark searchForRelic() {
        //Start tracking the time it is taking to find a relic.
        ElapsedTime relicRuntime = new ElapsedTime();
        relicRuntime.reset();

        //Get the RC's camera monitor view hardware map id and make a new vuforiaTracker controller with it. Initialize Tracking as well.
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaTracker vuforiaTracker = new VuforiaTracker(cameraMonitorViewId);
        vuforiaTracker.initTracking();

        //Start the tracking function.
        RelicRecoveryVuMark vuMark = vuforiaTracker.track();

        //Initialize the tracking function and then loop until it finds the VuMark or it hits the maximum time allotted (SEARCHING_DELAY)
        while (vuMark == RelicRecoveryVuMark.UNKNOWN) {
            if (relicRuntime.milliseconds() < SEARCHING_DELAY) {
                telemetry.addData("VuMark", "not visible");
                vuMark = vuforiaTracker.track();
            } else {
                vuMark = RelicRecoveryVuMark.CENTER;
            }
        }

        //Update the telemetry with that found VuMark and return it.
        telemetry.addData("VuMark", "%s visible", vuMark);
        telemetry.update();

        return vuMark;
    }
}
