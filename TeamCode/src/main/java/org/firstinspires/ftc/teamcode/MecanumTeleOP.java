package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.blueshift.drivesupport.FieldPoint;
import org.blueshift.drivesupport.MecanumDrive;
import org.blueshift.drivesupport.TankDrive;

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
 * @version 1.4
 */

@TeleOp(name="Mecanum Drive", group="Main OPMode")
public class MecanumTeleOP extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private DcMotor glyphLeft, glyphRight, liftMotor;

    //Servo Stuff
    private ElapsedTime servoRuntime = new ElapsedTime();
    private Servo bucketServo, jewelServo;
    private double bucketServoPosition;
    private double servoDelta = 0.01;
    private double servoDelayTime = 0.05; //Seconds

    private final FieldPoint STARTING_LOCATION = new FieldPoint(0,0);
    private final double CONTROLLER_TOLERANCE = 0.10;

    private final double OUTTAKE_POWER = 0.7;
    private final double BUCKET_POWER = 1.0;

    private MecanumDrive mecanumDrive;
    private TankDrive tankDrive;

    /**
     * Run once when the 'init' button is pressed. The motor objects are set to their named hardware
     * map counterparts in the phone configuration.
     */
    @Override public void init() {
        //Assign the motors a hardwareMap counterpart
        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        glyphLeft = hardwareMap.get(DcMotor.class, "glyphLeft");
        glyphRight = hardwareMap.get(DcMotor.class, "glyphRight");

        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        bucketServo = hardwareMap.get(Servo.class, "bucketServo");
        jewelServo = hardwareMap.get(Servo.class, "jewelServo");

        bucketServoPosition = bucketServo.getPosition();

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
        tankDrive    = new TankDrive(leftBack, leftFront, rightBack, rightFront);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    @Override public void init_loop() {

    }

    @Override public void start() {
        runtime.reset();
    }

    /**
     * The loop function is run continuously from when the driver presses 'play' to when he presses
     * 'stop' or aborts the program. If the user is moving the right stick of gamepad 1, then the
     * mecanum drive will be controlled, and the left stick's x component is used for rotation. If
     * the user is moving the left stick of gamepad 1, then the tank drive will be controlled with
     * a single stick configuration. If both are pressed, mecanum will be the default drive.
     *
     * The two glyph intake controls are controlled with the bumpers of gamepad 1, and the lift
     * motor is controlled with the triggers. The button to move the servo down is 'a', and the
     * button to move it up is 'b'.
     *
     * The Mecanum angle is the angle that the stick is pushed at, and the speed is how hard the
     * stick is pushed.
     */
    @Override public void loop() {
        double dAngle, dSpeed, dRotation;

        /* ** ** ** ** ** ** ** ** **/
        /*     Drive Train Code     */
        /* ** ** ** ** ** ** ** ** **/
        if (gamepad1.right_stick_x != 0 || gamepad1.right_stick_y != 0) {
            if ((CONTROLLER_TOLERANCE > gamepad1.right_stick_x && gamepad1.right_stick_x > -CONTROLLER_TOLERANCE) && (CONTROLLER_TOLERANCE > gamepad1.right_stick_y && gamepad1.right_stick_y > -CONTROLLER_TOLERANCE)) { //If the sticks are within a certain value, then it is basically zero.
                dAngle = 0.0;
                dSpeed = 0.0;
            } else if (gamepad1.right_stick_x != 0 && gamepad1.right_stick_y != 0) {
                dAngle = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x);

                dSpeed = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
            } else if (gamepad1.right_stick_y != 0) {
                if (-gamepad1.right_stick_y > 0) {
                    dAngle = Math.PI / 2;
                } else {
                    dAngle = 3 * Math.PI / 2;
                }

                dSpeed = Math.abs(gamepad1.right_stick_y);
            } else {
                if (gamepad1.right_stick_x > 0) {
                    dAngle = 0.0;
                } else {
                    dAngle = Math.PI;
                }

                dSpeed = Math.abs(gamepad1.right_stick_x);
            }

            if (gamepad1.a) {

            } else if (gamepad1.b) {

            }

            //Make all angles positive.
            if (dAngle < 0) {
                dAngle += 2 * Math.PI;
            }

            //When using the Mecanum drive, set the rotation to the x value of the left stick.
            dRotation = gamepad1.left_stick_x;

            //While the right stick button is pressed, increase the driver precision by scaling down power and rotation multipliers.
            if (gamepad1.right_stick_button) {
                dSpeed /= 10;
                dRotation /= 10;
            }

            mecanumDrive.drive(dAngle, dSpeed, dRotation);

            //Make the angle a multiple of pi for displaying purposes, and make speed a percentage.
            double dAngleDisplay = dAngle / Math.PI;
            double dSpeedPercent = dSpeed * 100;

            telemetry.addData("Motors", "Angle (%.2f)pi, Speed (%.2f) Percent", dAngleDisplay, dSpeedPercent);
            telemetry.addData("Rotation", "Rotating at (%.2f)", dRotation);
        } else if (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0) {
            //Use the tank drive if Mecanum is not being used
            tankDrive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x);
        } else {
            mecanumDrive.stop();
            tankDrive.stop();
        }



        /* ** ** ** ** ** ** ** ** **/
        /*      Auxiliary Code      */
        /* ** ** ** ** ** ** ** ** **/

        //Front intake controls
        if (gamepad1.right_bumper) { //OUT
            glyphRight.setPower(-OUTTAKE_POWER);
            glyphLeft.setPower(-OUTTAKE_POWER);
        } else if (gamepad1.right_trigger != 0) {
            glyphRight.setPower(gamepad1.right_trigger);
            glyphLeft.setPower(gamepad1.right_trigger);
        } else {
            glyphRight.setPower(0.0);
            glyphLeft.setPower(0.0);
        }

        //Glyph Lift Controls
        if (gamepad1.left_trigger > 0) {
            liftMotor.setPower(-gamepad1.left_trigger / 2);
        } else if (gamepad1.left_bumper) {
            liftMotor.setPower(1.00);
        } else {
            liftMotor.setPower(0.0);
        }

        //On A -> Drop the glyph. On B -> Return to the normal configuration
        if (gamepad1.y) {
            bucketServoPosition = 0.5;
            bucketServo.setPosition(bucketServoPosition);
        } else if (gamepad1.a) {
            if (servoRuntime.time() > servoDelayTime) {
                bucketServoPosition -= servoDelta;
                bucketServoPosition = Range.clip(bucketServoPosition, 0, 1);
                bucketServo.setPosition(bucketServoPosition);
                servoRuntime.reset();
            }
        } else if (gamepad1.b) {
            if (servoRuntime.time() > servoDelayTime) {
                bucketServoPosition += servoDelta;
                bucketServoPosition = Range.clip(bucketServoPosition, 0.1, 0.9);
                bucketServo.setPosition(bucketServoPosition);
                servoRuntime.reset();
            }
        }

        //Generate telemetry with the run time.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override public void stop() {
        //Stop all motors
        mecanumDrive.stop();
        tankDrive.stop();

        mecanumDrive = null;
        tankDrive = null;
    }
}
