package org.blueshift.drivesupport;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

/**
 * A class for easily driving and controlling a robot using four mecanum wheels on four distinct
 * motors. This is intended for coding convenience with a TeleOP program, making the code more
 * readable and easier to follow. It is different frm the regular MecanumDrive functionality because
 * this class removes the angular strafing features, etc of the Mecanum drive and makes it a sort of
 * tank drive in practice. This is far more precise when using an autonomous program.
 *
 * @author Gabriel Wong
 * @version 1.0
 */

public class TankDrive {
    private DcMotor[] motors = new DcMotor[4];
    private double[] motorPowers = {0.0, 0.0, 0.0, 0.0};

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
    public TankDrive(DcMotor leftBack, DcMotor leftFront, DcMotor rightBack, DcMotor rightFront) {
        motors[0] = leftBack;
        motors[1] = leftFront;
        motors[2] = rightBack;
        motors[3] = rightFront;

        motors[0].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[1].setDirection(DcMotorSimple.Direction.FORWARD);
        motors[2].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[3].setDirection(DcMotorSimple.Direction.FORWARD);

        for (int i = 0; i < motors.length; i++) {
            motorPowers[i] = 0;
            motors[i].setPower(motorPowers[i]);
        }
    }

    /**
     * Drive normally as a tank drive robot. The robot can go forwards, backwards, and rotate in
     * place using this function.
     *
     * @param dSpeed     - [-1, 1], The desired speed for the robot to travel at.
     * @param dRotation  - [-1, 1], The desired speed of rotation for the robot.
     */
    public void drive(double dSpeed, double dRotation) {
        motorPowers[0] = dSpeed * Math.cos(3 * Math.PI/4) - dRotation;
        motorPowers[1] = dSpeed * Math.sin(3 * Math.PI/4) + dRotation;
        motorPowers[2] = dSpeed * Math.sin(3 * Math.PI/4) - dRotation;
        motorPowers[3] = dSpeed * Math.cos(3 * Math.PI/4) + dRotation;

        for (int i = 0; i < motors.length; i++) {
            motors[i].setPower(motorPowers[i]);
        }
    }

    /**
     * An extension of the drive(...) method that uses encoders and drives certain distances.
     *
     * @param dSpeed        - [-1, 1], The desired speed for the robot to travel at.
     * @param encoderTarget - The values the encoders should be equal to before stopping the robot.
     */
    public void driveWithEncoders(double dSpeed, double encoderTarget) {
        useEncoders(true);

        while (!(getEncoders() >= encoderTarget)) {
            drive(dSpeed, 0);
        }

        drive(0,0);

        useEncoders(false);
    }

    /**
     * Turn on the encoders to the RUN_TO_POSITION mode (encoderState = true) or disable the
     * encoders (encoderState = false).
     *
     * @param encoderState - Whether the encoders should be turned on or not.
     */
    public void useEncoders(boolean encoderState) {
        if (encoderState) {
            for (int i = 0; i < motors.length; i++) {
                motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        } else {
            for (int i = 0; i < motors.length; i++) {
                motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
    }

    /**
     * @return the average encoder values of the two front motors
     */
    public double getEncoders() {
        return (Math.abs(motors[1].getCurrentPosition()) - Math.abs(motors[3].getCurrentPosition()))/2;
    }

    /**
     * This function will stop the robot by setting all of the motor's powers to zero.
     */
    public void stop() {
        for (int i = 0; i < motors.length; i++) {
            motorPowers[i] = 0;
            motors[i].setPower(motorPowers[i]);
        }
    }
}
