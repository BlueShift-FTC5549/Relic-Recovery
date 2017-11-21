package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode class is instantiated on the
 * Robot Controller and executed.
 *
 * This OPMode contains code for Team 5549 (Blue Shift)'s Protobot.
 *
 * @author Gabriel Wong
 * @version 1.1
 */

@TeleOp(name="Prototype Bot", group="Iterative Opmode")
public class ProtoBotTeleOP extends OpMode {
    //Create a reference for determining the Elapsed Time.
    private ElapsedTime runtime = new ElapsedTime();

    //Declare all four DCMotors.
    private DcMotor leftBack, leftFront;
    private DcMotor rightBack, rightFront;

    //Two variables used later to brake the robot.
    private double oldDrive, oldTurn;

    @Override public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftBack  = hardwareMap.get(DcMotor.class, "left_back");
        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");

        // Reverse the left motors so that it can drive forward.
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    //Runs repeatedly after the driver hits 'init'
    @Override public void init_loop() {
    }

    //Runs once the driver hits 'play'
    @Override public void start() {
        runtime.reset();
    }

    //Runs repeatedly after the driver hits 'play' and before he or she hits 'stop'
    @Override public void loop() {
        double leftPower;
        double rightPower;

        // Setup a variable for each drive wheel to save power level for telemetry
        double drive = -gamepad1.left_stick_y;
        double turn  =  gamepad1.left_stick_x;

        //If the stick is no longer being moved but it was a second ago, then apply brakes. Else, run the robot per usual.
        if (drive == 0 && oldDrive != 0) {
            leftPower  = Range.clip(-2 * oldDrive - oldTurn, -1.0, 1.0);
            rightPower = Range.clip(-2 * oldDrive + oldTurn, -1.0, 1.0);

            leftFront.setPower(leftPower);
            leftBack.setPower(leftPower);

            rightFront.setPower(rightPower);
            rightBack.setPower(rightPower);
        } else {
            leftPower  = Range.clip(drive + turn, -1.0, 1.0);
            rightPower = Range.clip(drive - turn, -1.0, 1.0);

            leftFront.setPower(leftPower);
            leftBack.setPower(leftPower);

            rightFront.setPower(rightPower);
            rightBack.setPower(rightPower);
        }

        //If the x button is pressed, spin the robot around.
        if (gamepad1.x) {
            leftBack.setPower(1.0);
            leftFront.setPower(1.0);

            rightBack.setPower(-1.0);
            rightFront.setPower(-1.0);
        }

        //Set the old values for use in the braking method.
        oldDrive = drive;
        oldTurn = turn;

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
    }

    //Runs once the driver hits stop
    @Override public void stop() {
        //Set all motors to 0 power.
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
    }
}
