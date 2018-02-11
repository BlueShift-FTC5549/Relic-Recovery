package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.blueshift.drivesupport.Gyroscope;
import org.blueshift.drivesupport.FieldPoint;
import org.blueshift.drivesupport.MecanumDrive;
import org.blueshift.drivesupport.TankDrive;
import org.blueshift.vision.VuforiaTracker;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * An autonomous op program to drive in a straight line, drop off a pre-loaded glyph, and back off.
 * The column that the robot will drop the glyph into is decided by its positioning on the balancing
 * stone, and the key is scored by random chance.
 *
 * @author Gabriel Wong
 * @version 1.0
 */
@Autonomous(name="Forward and Drop", group ="All")
public class Auto_ForwardDrop extends LinearOpMode {
    //Create the object to keep trackPictograph of Elapsed Time and keep trackPictograph of the current location.
    private static       ElapsedTime runtime = new ElapsedTime();

    //Declare the motors
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private DcMotor glyphLeft, glyphRight;

    //Placeholder for the Tank Drive object
    private TankDrive tankDrive;


    /**
     * Since this is a linear (not iterative) OpMode, there is only one function that will run one
     * time once the play button is pressed by the driver at the pit. It can include loops on the
     * inside, but the entire method will not loop.
     */
    @Override public void runOpMode() {
        initialize();

        //Wait for user input before continuing.
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        tankDrive.drive(0.5, 0);
        sleep(1700);
        tankDrive.drive(0,0);

        glyphLeft.setPower(-0.8);
        glyphRight.setPower(-0.8);

        sleep(450);

        glyphLeft.setPower(0);
        glyphRight.setPower(0);

        sleep(100);

        tankDrive.drive(-0.5, 0);
        sleep(200);
        tankDrive.drive(0,0);

        telemetry.update();
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

        //Set the direction of each motor
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);

        glyphLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        glyphRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //Create the two different drive objects
        tankDrive = new TankDrive(leftBack, leftFront, rightBack, rightFront);
    }
}