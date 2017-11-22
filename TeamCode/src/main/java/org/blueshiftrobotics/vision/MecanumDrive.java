package org.blueshiftrobotics.vision;

import com.qualcomm.robotcore.hardware.DcMotor;
import java.lang.Math;

/**
 * A class for easily driving and controlling a robot using four mecanum wheels on four distinct
 * motors. This is intended for coding convenience with a TeleOP program, making the code more
 * readable and easier to follow.
 *
 * @author Gabriel Wong
 * @version 1.0
 */
public class MecanumDrive {
    private DcMotor leftBack, leftFront, rightBack, rightFront;
    private double leftBackPower, leftFrontPower, rightBackPower, rightFrontPower;

    /**
     * Initialize all four motors according to the four parameters so that they can be controlled
     * later. These are, in fact, aliases to the original motors, so setting one's power will also
     * modify the original variable's data.
     *
     * @param leftBack   - The motor in the left, back position.
     * @param leftFront  - The motor in the left, front position.
     * @param rightBack  - The motor in the right, back position.
     * @param rightFront - The motor in the right, front position.
     */
    public MecanumDrive(DcMotor leftBack, DcMotor leftFront, DcMotor rightBack, DcMotor rightFront) {
        this.leftBack = leftBack;
        this.leftFront = leftFront;
        this.rightBack = rightBack;
        this.leftFront = rightFront;

        leftBackPower   = 0;
        leftFrontPower  = 0;
        rightBackPower  = 0;
        rightFrontPower = 0;

        leftBack.setPower(leftBackPower);
        leftFront.setPower(leftFrontPower);
        rightBack.setPower(rightBackPower);
        rightFront.setPower(rightFrontPower);
    }

    /**
     * This drives the robot with the three given parameters, accounting for the differing way that
     * the mecanum wheels are driven compared to conventional wheels.
     *
     * @param dAngle     - [0, 2pi], The desired angle for the robot to travel at.
     * @param dSpeed     - [-1, 1], The desired speed for the robot to travel at.
     * @param dRotation  - [-1, 1], The desired speed of rotation for the robot.
     */
    public void drive(double dAngle, double dSpeed, double dRotation) {
        leftBackPower = dSpeed * Math.cos(dAngle + Math.PI/4) + dRotation;
        leftFrontPower = dSpeed * Math.sin(dAngle + Math.PI/4) + dRotation;
        rightBackPower = dSpeed * Math.sin(dAngle + Math.PI/4) - dRotation;
        rightFrontPower = dSpeed * Math.cos(dAngle + Math.PI/4) - dRotation;

        leftBack.setPower(leftBackPower);
        leftFront.setPower(leftFrontPower);
        rightBack.setPower(rightBackPower);
        rightFront.setPower(rightFrontPower);
    }

    /**
     * This drives the robot with the three given parameters, accounting for the differing way that
     * the mecanum wheels are driven compared to conventional wheels.
     *
     * @param dAngle     - [0, 2pi], The desired angle for the robot to travel at.
     * @param dSpeed     - [-1, 1] , The desired speed for the robot to travel at.
     * @param dRotation  - [-1, 1] , The desired speed of rotation for the robot.
     * @param duration   - millis  , The desired wait time in milliseconds before stopping the bot.
     */
    public void drive(double dAngle, double dSpeed, double dRotation, int duration)  {
        try {
            leftBackPower = dSpeed * Math.cos(dAngle + Math.PI / 4) + dRotation;
            leftFrontPower = dSpeed * Math.sin(dAngle + Math.PI / 4) + dRotation;
            rightBackPower = dSpeed * Math.sin(dAngle + Math.PI / 4) - dRotation;
            rightFrontPower = dSpeed * Math.cos(dAngle + Math.PI / 4) - dRotation;

            leftBack.setPower(leftBackPower);
            leftFront.setPower(leftFrontPower);
            rightBack.setPower(rightBackPower);
            rightFront.setPower(rightFrontPower);

            wait(duration);

            stop();
        } catch (InterruptedException e) {
            stop();
        }
    }

    /**
     * This function will stop the robot by setting all of the motor's powers to zero.
     */
    public void stop() {
        leftBackPower   = 0;
        leftFrontPower  = 0;
        rightBackPower  = 0;
        rightFrontPower = 0;

        leftBack.setPower(leftBackPower);
        leftFront.setPower(leftFrontPower);
        rightBack.setPower(rightBackPower);
        rightFront.setPower(rightFrontPower);
    }
}
