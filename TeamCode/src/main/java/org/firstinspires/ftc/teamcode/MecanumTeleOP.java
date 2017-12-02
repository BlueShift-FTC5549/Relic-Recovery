package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.blueshiftrobotics.driveSupport.FieldPoint;
import org.blueshiftrobotics.driveSupport.MecanumDrive;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode". An OpMode is a 'program'
 * that runs in either the autonomous or the teleop period of an FTC match. The names of OpModes
 * appear on the menu of the FTC Driver Station. When an selection is made from the menu, the
 * corresponding OpMode class is instantiated on the Robot Controller and executed. This OpMode is
 * for a 4 wheeled mecanum drive.
 *
 * The Driver-Controller TeleOP period controls the movement of the robot with the left stick and
 * the two bumpers of gamepad 1. The left stick controllers both direction and speed (direction is
 * the angle formed by the stick, and speed is the distance it is pushed from the center).
 *
 * The autonomous period uses the FieldPoint object to record its location and travels to different areas
 * on the field with this coordinate system. It uses the camera to find the locations of cubes
 * (Cyphers) and travels to their locations; the robot grabs the cubes and transports it to the
 * cypher box location.
 *
 * @author Gabriel Wong
 * @version 1.2
 */

@TeleOp(name="Mecanum Drive", group="Main OPMode")
public class MecanumTeleOP extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront, rightBack, rightFront;

    private final FieldPoint STARTING_LOCATION = new FieldPoint(0,0);
    private final double CONTROLLER_TOLERANCE = 0.10;

    MecanumDrive mecanumDrive;

    /**
     * Run once when the 'init' button is pressed. The motor objects are set to their named hardware
     * map counterparts in the phone configuration.
     */
    @Override public void init() {
        telemetry.addData("Status", "Initialized");

        //Declare the four motors and create a new Mecanum Drive controller out of them.
        DcMotor leftBack, leftFront, rightBack, rightFront;


        //Assign the motors a hardwareMap counterpart
        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        //Create the mecanum drive object
        mecanumDrive = new MecanumDrive(leftBack, leftFront, rightBack, rightFront, STARTING_LOCATION);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /**
     * The init_loop method runs continuously after the driver hits 'init' but before he or she hits
     * 'play'.
     */
    @Override public void init_loop() {

    }

    /**
     * Run once when the 'play' button is pressed.
     */
    @Override public void start() {
        runtime.reset();
    }

    /**
     * The loop function is run continuously from when the driver presses 'play' to when he presses
     * 'stop' or aborts the program. This method continuously updates the mecanum drives according
     * to the left joystick and the two top bumpers.
     */
    @Override public void loop() {
        //TODO: actually test and research this mecanum control function
        double dAngle, dSpeed, dRotation;

        if ( (CONTROLLER_TOLERANCE > gamepad1.left_stick_x && gamepad1.left_stick_x  > -CONTROLLER_TOLERANCE) && (CONTROLLER_TOLERANCE > gamepad1.left_stick_y && gamepad1.left_stick_y  > -CONTROLLER_TOLERANCE)) { //If the sticks are within a certain value, then it is basically zero.
            dAngle = 0.0;
            dSpeed = 0.0;
        } else if (gamepad1.left_stick_x != 0 && gamepad1.left_stick_y != 0) {
            dAngle = Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x);

            dSpeed = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
        } else if (gamepad1.left_stick_y != 0) {
            if (-gamepad1.left_stick_y > 0) {
                dAngle = Math.PI/2;
            } else {
                dAngle = 3 * Math.PI/2;
            }

            dSpeed = Math.abs(gamepad1.left_stick_y);
        } else if (gamepad1.left_stick_x != 0) {
            if (gamepad1.left_stick_x > 0) {
                dAngle = 0.0;
            } else {
                dAngle = Math.PI;
            }

            dSpeed = Math.abs(gamepad1.left_stick_x);
        } else {
            dAngle = 0.0;
            dSpeed = 0.0;
        }

        //Set the rotation factor to the left_trigger's value, or set it to the right_trigger's value if left_trigger is zero.
        if ((gamepad1.left_trigger > 0) && (gamepad1.right_trigger > 0)) {
            dRotation = 0;
        } else if (gamepad1.left_trigger > 0) {
            dRotation = -gamepad1.left_trigger;
        } else if (gamepad1.right_trigger > 0) {
            dRotation = gamepad1.right_trigger;
        } else {
            dRotation = 0;
        }

        //Make all angles positive.
        if (dAngle < 0) {
            dAngle += 2*Math.PI;
        }

        mecanumDrive.drive(dAngle, dSpeed, dRotation);

        //Make the angle a multiple of pi for displaying purposes, and make speed a percentage.
        double dAngleDisplay = dAngle / Math.PI;
        double dSpeedPercent = dSpeed * 100;
        //Generate Telemetry
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "Angle (%.2f)pi, Speed (%.2f) Percent", dAngleDisplay, dSpeedPercent);
        telemetry.addData("Rotaton", "Rotating at (%.2f)", dRotation);
        telemetry.addData("Controller", "Controller (x,y) = (%.2f), (%.2f)", gamepad1.left_stick_x, gamepad1.left_stick_y);
    }

    @Override public void stop() {
        mecanumDrive.stop();
    }
}
