package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.blueshift.drivesupport.MecanumDrive;
import org.blueshift.drivesupport.TankDrive;

/**
 *
 *
 */

@TeleOp(name="DEBUG Mecanum Enoders", group="Debug")
public class Debug_MecanumEncoders extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private Servo jewelServoR, jewelServoL;

    private double CONTROLLER_TOLERANCE = 0.01;

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

        //Set the direction of each motor
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);

        //Create the two different drive objects
        mecanumDrive = new MecanumDrive(leftBack, leftFront, rightBack, rightFront);
        tankDrive    = new TankDrive(leftBack, leftFront, rightBack, rightFront);

        tankDrive.useEncoders();

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized in " + runtime.seconds() + " seconds");
        telemetry.update();
    }

    @Override public void init_loop() {

    }

    @Override public void start() {
        telemetry.clear();
        runtime.reset();
    }

    /**
     * The loop function is run continuously from when the driver presses 'play' to when he presses
     * 'stop' or aborts the program. If the user is moving the right stick of gamepad 1, then the
     * mecanum drive will be controlled, where the left stick's x component is used for rotation. If
     * the user is moving the left stick of gamepad 1, then the tank drive will be controlled with
     * a single stick configuration. If both are pressed, mecanum will be the default drive.
     *
     * The glyph intake controls are controlled with the gamepad 1 triggers, and the outtake
     * controls are used with the gamepad 1 bumpers. This includes both the ground intake and the
     * conveyor belt system.
     *
     * The Mecanum angle is the angle that the stick is pushed at, and the speed is how hard the
     * stick is pushed.
     */
    @Override public void loop() {
        double dAngle = 0;
        double dSpeed = 0;
        double dRotation = 0;

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

            //Make all angles positive.
            if (dAngle < 0) {
                dAngle += 2 * Math.PI;
            }

            //When using the Mecanum drive, set the rotation to the x value of the left stick.
            dRotation = gamepad1.left_stick_x;

            mecanumDrive.drive(dAngle, dSpeed, dRotation);
        } else if (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0) {
            //Use the tank drive if Mecanum is not being used
            tankDrive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x);
        } else {
            mecanumDrive.stop();
            tankDrive.stop();
        }

        telemetry.addData("Right Front", rightFront.getCurrentPosition());
        telemetry.addData("Right Rear ", rightBack.getCurrentPosition());
        telemetry.addData("Left Front",  leftFront.getCurrentPosition());
        telemetry.addData("Left Rear ",  leftBack.getCurrentPosition());

        //Generate telemetry with the run time.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.update();
    }

    @Override public void stop() {
        //Stop all motors
        mecanumDrive.stop();
        tankDrive.stop();

        mecanumDrive = null;
        tankDrive = null;
    }
}
