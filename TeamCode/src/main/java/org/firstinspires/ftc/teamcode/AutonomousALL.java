package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import org.firstinspires.ftc.teamcode.TeamCodeSupport.Point;
import org.firstinspires.ftc.teamcode.TeamCodeSupport.MecanumDrive;
import org.firstinspires.ftc.teamcode.TeamCodeSupport.VuforiaTracker;

/**
 * An autonomous op program to score all of the points that the robot can, including:
 *      - Knocking the correct jewel off of the platform
 *      - Driving to the key, decoding it to find with Vuforia the correct column to put a glyph in
 *      - Placing one extra glyph in the crypto box
 *      - Park in the safe zone
 *
 * @author Gabriel Wong
 * @version 0.3 ALPHA
 */
@Autonomous(name="Score All", group ="Corner Stone")
public class AutonomousALL extends LinearOpMode {
    //Create the object to keep track of Elapsed Time, set the starting location, and keep track of the current location.
    private ElapsedTime runtime = new ElapsedTime();
    private final Point STARTING_LOCATION = new Point(1,1); //TODO: Update starting location
    private Point currentLocation = new Point(STARTING_LOCATION);

    /**
     * Since this is a linear (not iterative) OpMode, there is only one function that will run one
     * time once the play button is pressed by the driver at the pit. It can include loops on the
     * inside, but the entire method will not loop.
     */
    @Override public void runOpMode() {
        //Declare the four motors and create a new Mecanum Drive controller out of them.
        DcMotor leftBack, leftFront, rightBack, rightFront;
        MecanumDrive mecanumDrive;

        //Assign the motors a hardwareMap counterpart
        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        //Create the mecanum drive object
        mecanumDrive = new MecanumDrive(leftBack, leftFront, rightBack, rightFront);

        //Wait for user input before continuing.
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        //Search for the Relic
        searchForRelic();
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