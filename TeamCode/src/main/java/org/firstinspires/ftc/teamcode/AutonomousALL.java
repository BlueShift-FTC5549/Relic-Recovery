package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TeamCodeSupport.Point;
import org.firstinspires.ftc.teamcode.TeamCodeSupport.MecanumDrive;

/**
 * An autonomous op program to score all of the points that the robot can.
 *
 * @author Gabriel Wong
 * @version 0.1 ALPHA
 */
@Autonomous(name="Score All", group ="Corner Stone")
public class AutonomousALL extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront;
    private DcMotor rightBack, rightFront;

    private MecanumDrive mecanumDrive;
    private final Point STARTING_LOCATION = new Point(1,1); //TODO: Update starting location
    private Point currentLocation = new Point(STARTING_LOCATION);

    //TODO: Expand this further than a one second drive
    @Override public void runOpMode() {
        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        mecanumDrive = new MecanumDrive(leftBack, leftFront, rightBack, rightFront);

        mecanumDrive.drive(0, 1, 0, 1000);

        mecanumDrive.drive(0, 1, 0);
        sleep(1000);
        mecanumDrive.stop();
    }
}