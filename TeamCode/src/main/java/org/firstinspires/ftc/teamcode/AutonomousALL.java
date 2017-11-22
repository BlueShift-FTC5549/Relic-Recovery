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
 *      - Driving to the key, decoding it to find the correct column to place a glyph into
 *      - Placing one extra glyph in the crypto box
 *      - Park in the safe zone
 *
 * @author Gabriel Wong
 * @version 0.2 ALPHA
 */
@Autonomous(name="Score All", group ="Corner Stone")
public class AutonomousALL extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private final Point STARTING_LOCATION = new Point(1,1); //TODO: Update starting location
    private Point currentLocation = new Point(STARTING_LOCATION);

    @Override public void runOpMode() {
        //    Declare the four motors and create a new Mecanum Drive controller out of them.

        DcMotor leftBack, leftFront, rightBack, rightFront;
        MecanumDrive mecanumDrive;

        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        mecanumDrive = new MecanumDrive(leftBack, leftFront, rightBack, rightFront);

        // Some sample testing code{
        mecanumDrive.drive(0, 1, 0, 1000);

        mecanumDrive.drive(0, 1, 0);
        sleep(1000);
        mecanumDrive.stop();
        //}

        //Get the RC's camera monitor view hardware map id and make a new vuforiaTracker controller with it.
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaTracker vuforiaTracker = new VuforiaTracker(cameraMonitorViewId);

        //Wait for user input before continuing.
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        //Initialize the tracking function and then loop until it finds the VuMark.
        vuforiaTracker.initTracking();

        RelicRecoveryVuMark vuMark = vuforiaTracker.track();

        while (vuMark == RelicRecoveryVuMark.UNKNOWN) {
            telemetry.addData("VuMark", "not visible");
            vuMark = vuforiaTracker.track();
        }

        telemetry.addData("VuMark", "%s visible", vuMark);
    }
}