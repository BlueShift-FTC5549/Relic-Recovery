package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.blueshift.drivesupport.MecanumDrive;
import org.blueshift.drivesupport.TankDrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode". An OpMode is a 'program'
 * that runs in either the autonomous or the teleop period of an FTC match. The names of OpModes
 * appear on the menu of the FTC Driver Station. When an selection is made from the menu, the
 * corresponding OpMode class is instantiated on the Robot Controller and executed. This OpMode is
 * for a 4-wheeled mecanum-based drive.
 *
 * If the user is moving the right stick of gamepad 1, then the
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
 *
 * @author Gabriel Wong
 * @version 2.0
 */

@SuppressWarnings("FieldCanBeLocal")
@TeleOp(name="Mecanum Drive", group="Main OPMode")
public class Driver_MecanumTeleOP extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private DcMotor glyphLeft, glyphRight, conveyorLeft, conveyorRight;
    private Servo jewelServoR, jewelServoL;

    private double CONTROLLER_TOLERANCE;

    private double OUTTAKE_POWER;
    private double CONVEYOR_OUTTAKE_POWER;

    private double speedMultiplier = 1.0;

    private boolean SQUARE_TRIGGERS, SQUARE_STICKS;

    private MecanumDrive mecanumDrive;
    private TankDrive tankDrive;

    /**
     * Run once when the 'init' button is pressed. The motor objects are set to their named hardware
     * map counterparts in the phone configuration.
     */
    @Override public void init() {
        try {
            File file = new File("driversupport.xml");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            OUTTAKE_POWER = Double.parseDouble(properties.getProperty("outtake_power"));
            CONVEYOR_OUTTAKE_POWER = Double.parseDouble(properties.getProperty("conveyor_outtake_power"));
            CONTROLLER_TOLERANCE = Double.parseDouble(properties.getProperty("controller_tolerance"));
            SQUARE_TRIGGERS = Boolean.parseBoolean(properties.getProperty("square_triggers"));
            SQUARE_STICKS = Boolean.parseBoolean(properties.getProperty("square_sticks"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            OUTTAKE_POWER = 1.0;
            CONVEYOR_OUTTAKE_POWER = 0.7;
            CONTROLLER_TOLERANCE = 0.05;
            SQUARE_TRIGGERS = true;
            SQUARE_STICKS = true;
        } catch (IOException e) {
            e.printStackTrace();

            OUTTAKE_POWER = 1.0;
            CONVEYOR_OUTTAKE_POWER = 0.7;
            CONTROLLER_TOLERANCE = 0.05;
            SQUARE_TRIGGERS = true;
            SQUARE_STICKS = true;
        }

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

        jewelServoR = hardwareMap.get(Servo.class, "jewelServoR");
        jewelServoL = hardwareMap.get(Servo.class, "jewelServoL");

        jewelServoR.setPosition(0.0);
        jewelServoL.setPosition(1.0);

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
        //TODO: Square (^2) the stick magnitude and then clip to make the controller more sensitive on the inside and not on the outside
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

            if (SQUARE_STICKS) {
                dSpeed *= dSpeed;
            }

            mecanumDrive.drive(dAngle, dSpeed * speedMultiplier, dRotation);
        } else if (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0) {
            //Use the tank drive if Mecanum is not being used
            tankDrive.drive(-gamepad1.left_stick_y * speedMultiplier, gamepad1.left_stick_x);
        } else {
            mecanumDrive.stop();
            tankDrive.stop();
        }


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

        double leftTrigger1 = gamepad1.left_trigger;
        double rightTrigger1 = gamepad1.right_trigger;

        if (SQUARE_TRIGGERS) {
            leftTrigger1 *= leftTrigger1;
            rightTrigger1 *= rightTrigger1;
        }

        //Front intake controls
        if (leftTrigger1 != 0) {
            glyphRight.setPower( Math.pow(leftTrigger1, 2) );
            glyphLeft.setPower( Math.pow(leftTrigger1, 2) );
        } else if (gamepad1.left_bumper) { //OUT
            glyphRight.setPower(-OUTTAKE_POWER);
            glyphLeft.setPower(-OUTTAKE_POWER);
        } else if (gamepad2.left_stick_y == 0 && gamepad2.right_stick_y == 0){
            glyphRight.setPower(0.0);
            glyphLeft.setPower(0.0);
        }

        //Conveyor Controls
        if (rightTrigger1 != 0) {
            conveyorLeft.setPower( Math.pow(rightTrigger1, 2) );
            conveyorRight.setPower( Math.pow(rightTrigger1, 2) );
        } else if (gamepad1.right_bumper) {
            conveyorLeft.setPower(-OUTTAKE_POWER);
            conveyorRight.setPower(-OUTTAKE_POWER);
        } else if (gamepad2.left_trigger == 0 && gamepad2.left_trigger == 0) {
            conveyorLeft.setPower(0);
            conveyorRight.setPower(0);
        }

        //Speed Controls
        if (gamepad1.dpad_up) {
            speedMultiplier = speedMultiplier * -1;
        }

        if (gamepad1.dpad_down) {
            if (speedMultiplier < 1.0) {
                speedMultiplier = 1;
            } else {
                speedMultiplier = 0.5;
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
