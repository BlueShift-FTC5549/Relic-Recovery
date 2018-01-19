package org.blueshift.drivesupport;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

/**
 *
 *
 * @author Gabriel Wong
 * @version 1.0
 */

public class TankDrive {
    private DcMotor[] motors = {null, null, null, null};
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
     *
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
     * This function will stop the robot by setting all of the motor's powers to zero.
     */
    public void stop() {
        for (int i = 0; i < motors.length; i++) {
            motorPowers[i] = 0;
            motors[i].setPower(motorPowers[i]);
        }
    }
}
