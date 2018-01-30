package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.blueshift.drivesupport.Gyroscope;
import org.blueshift.drivesupport.MecanumDrive;
import org.blueshift.drivesupport.TankDrive;

/**
 *
 * @author Gabriel Wong
 * @version 1.0
 */

@TeleOp(name="Mecanum DEBUG", group="Debug")
public class Debug_MecanumTeleOP extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private DcMotor glyphLeft, glyphRight, conveyorLeft, conveyorRight;

    private final double CONTROLLER_TOLERANCE = 0.10;

    private double SPEED_MULTIPLIER = 1.0;
    private double MECANUM_ROUND_ANGLE = Math.PI/36; //5 Degrees

    private final double OUTTAKE_POWER = 1.0;
    private final double CONVEYOR_OUTTAKE_POWER = 0.7;

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

        //Create the two different drive objects
        mecanumDrive = new MecanumDrive(leftBack, leftFront, rightBack, rightFront);
        tankDrive    = new TankDrive(leftBack, leftFront, rightBack, rightFront);
        mecanumDrive.useEncoders(true);

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

            if (gamepad1.a) {

            } else if (gamepad1.b) {

            }

            //Make all angles positive.
            if (dAngle < 0) {
                dAngle += 2 * Math.PI;
            }

            /*
            //Round the mecanum angle to the nearest n degrees
            dAngle = MECANUM_ROUND_ANGLE * Math.round(dAngle/MECANUM_ROUND_ANGLE);
            */

            //When using the Mecanum drive, set the rotation to the x value of the left stick.
            dRotation = gamepad1.left_stick_x;

            mecanumDrive.drive(dAngle, dSpeed * SPEED_MULTIPLIER, dRotation);
        } else if (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0) {
            //Use the tank drive if Mecanum is not being used
            tankDrive.drive(-gamepad1.left_stick_y * SPEED_MULTIPLIER, gamepad1.left_stick_x);
        } else {
            mecanumDrive.stop();
            tankDrive.stop();
        }

        telemetry.addData("Motors", "Angle (%.2f)pi ((%.2f) degrees), Speed (%.2f) Percent", dAngle / Math.PI, dAngle / Math.PI * 180, dSpeed * 100);
        telemetry.addData("Rotation", "Rotating at (%.2f)", dRotation);


        /* ** ** ** ** ** ** ** ** **/
        /*      Auxiliary Code G2   */
        /* ** ** ** ** ** ** ** ** **/
        //Front intake controls
        if (gamepad2.left_stick_y != 0) {
            glyphLeft.setPower(-gamepad2.left_stick_y);
        } else {
            glyphLeft.setPower(0);
        }

        if (gamepad2.right_stick_y != 0) {
            glyphRight.setPower(-gamepad2.right_stick_y);
        } else {
            glyphRight.setPower(0);
        }

        //Conveyor Controls
        if (gamepad2.right_trigger != 0) {
            conveyorRight.setPower(gamepad2.right_trigger);
        } else {
            conveyorRight.setPower(0);
        }

        if (gamepad2.left_trigger != 0) {
            conveyorLeft.setPower(gamepad2.left_trigger);
        } else {
            conveyorLeft.setPower(0);
        }

        if (gamepad2.right_bumper) {
            conveyorRight.setPower(CONVEYOR_OUTTAKE_POWER);
        } else {
            conveyorRight.setPower(0);
        }

        if (gamepad2.left_bumper) {
            conveyorLeft.setPower(CONVEYOR_OUTTAKE_POWER);
        } else {
            conveyorLeft.setPower(0);
        }


        /* ** ** ** ** ** ** ** ** **/
        /*      Auxiliary Code G1   */
        /* ** ** ** ** ** ** ** ** **/

        //Front intake controls
        if (gamepad1.left_trigger != 0) {
            glyphRight.setPower(gamepad1.left_trigger);
            glyphLeft.setPower(gamepad1.left_trigger);
        } else if (gamepad1.left_bumper) { //OUT
            glyphRight.setPower(-OUTTAKE_POWER);
            glyphLeft.setPower(-OUTTAKE_POWER);
        } else if (gamepad2.left_stick_y == 0 && gamepad2.right_stick_y == 0){
            glyphRight.setPower(0.0);
            glyphLeft.setPower(0.0);
        }

        //Conveyor Controls
        if (gamepad1.right_trigger != 0) {
            conveyorLeft.setPower(gamepad1.right_trigger);
            conveyorRight.setPower(gamepad1.right_trigger);
        } else if (gamepad1.right_bumper) {
            conveyorLeft.setPower(-OUTTAKE_POWER);
            conveyorRight.setPower(-OUTTAKE_POWER);
        } else if (gamepad2.left_trigger == 0 && gamepad2.left_trigger == 0) {
            conveyorLeft.setPower(0);
            conveyorRight.setPower(0);
        }

        //Speed Controls
        if (gamepad1.dpad_up) {
            SPEED_MULTIPLIER = SPEED_MULTIPLIER * -1;
        }

        if (gamepad1.dpad_down) {
            if (SPEED_MULTIPLIER < 1.0) {
                SPEED_MULTIPLIER = 1;
            } else {
                SPEED_MULTIPLIER = 0.5;
            }
        }

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
