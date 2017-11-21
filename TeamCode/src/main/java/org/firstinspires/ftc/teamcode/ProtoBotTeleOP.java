package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Prototype Bot", group="Iterative Opmode")
public class ProtoBotTeleOP extends OpMode {
    // Declare Instance Variables - Variables that can be accessed throughout the entire class.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront = null;
    private DcMotor rightBack, rightFront = null;



    private double oldDrive, oldTurn;
    private double oldTime;
    private boolean isPerformingAction = false;

    private double leftPower  = 0.0;
    private double rightPower = 0.0;

    //Runs once when the driver hits 'init'
    @Override public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftBack  = hardwareMap.get(DcMotor.class, "left_back");
        leftFront = hardwareMap.get(DcMotor.class, "left_front");

        rightBack  = hardwareMap.get(DcMotor.class, "right_back");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
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

    //Runs repetedly after the driver hits 'play' and before he or she hits 'stop'
    @Override public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double drive = -gamepad1.left_stick_y;
        double turn  =  gamepad1.left_stick_x;

        if (drive == 0 && oldDrive != 0 && !isPerformingAction) {
            leftPower  = Range.clip(-2 * oldDrive - oldTurn, -1.0, 1.0);
            rightPower = Range.clip(-2 * oldDrive + oldTurn, -1.0, 1.0);

            leftFront.setPower(leftPower);
            leftBack.setPower(leftPower);

            rightFront.setPower(rightPower);
            rightBack.setPower(rightPower);
        } else if (!isPerformingAction){
            leftPower  = Range.clip(drive + turn, -1.0, 1.0);
            rightPower = Range.clip(drive - turn, -1.0, 1.0);

            leftFront.setPower(leftPower);
            leftBack.setPower(leftPower);

            rightFront.setPower(rightPower);
            rightBack.setPower(rightPower);
        }


        if (gamepad1.y) {
            isPerformingAction = false;

            leftBack.setPower(0.0);
            leftFront.setPower(0.0);

            rightBack.setPower(0.0);
            rightFront.setPower(0.0);
        } else if (gamepad1.x) {
            oldTime = runtime.milliseconds();
            isPerformingAction = true;

            leftBack.setPower(1.0);
            leftFront.setPower(1.0);

            rightBack.setPower(-1.0);
            rightFront.setPower(-1.0);
        }

        if (oldTime + 5000.0 >= runtime.milliseconds() && isPerformingAction) {
            isPerformingAction = false;

            leftBack.setPower(0.0);
            leftFront.setPower(0.0);

            rightBack.setPower(0.0);
            rightFront.setPower(0.0);
        }

        oldDrive = drive;
        oldTurn = turn;

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
    }

    //Runs once the driver hits stop
    @Override public void stop() {
        leftFront.setPower(0);
        leftBack.setPower(0);

        rightFront.setPower(0);
        rightBack.setPower(0);
    }
}
