package org.blueshift.drivesupport;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

/**
 *
 * @author Gabriel Wong
 * @version 0.1 ALPHA
 */

public class TankDrive {
    private DcMotor[] motors = {null, null, null, null};
    private double[] motorPowers = {0.0, 0.0, 0.0, 0.0};
    double[] encoderPositions    = {0.0, 0.0, 0.0, 0.0};

    private FieldPoint currentLocation;
    private double referenceHeading;
    private double heading;

    private Gyroscope gyroscope;

    private static final double COUNTS_PER_MOTOR_REV  = 28.0;
    private static final double DRIVE_GEAR_REDUCTION  = 40.0;
    private static final double WHEEL_DIAMETER_INCHES = 4.0 ;
    private static final double COUNTS_PER_INCH       = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

    private static final double ENCODER_DRIVE_SPEED = 0.5;
    private static final double INCHES_PER_FEET     = 12.0;

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
    public TankDrive(DcMotor leftBack, DcMotor leftFront, DcMotor rightBack, DcMotor rightFront, FieldPoint startLocation) {
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
     * Initialize all four motors according to the four parameters so that they can be controlled
     * later. These are, in fact, aliases to the original motors, so setting one's power will also
     * modify the original variable's data. This alternate constructor also sets up a gyroscope for
     * use in heading,  etc.
     *
     * @param leftBack   - The motor in the left, back position.
     * @param leftFront  - The motor in the left, front position.
     * @param rightBack  - The motor in the right, back position.
     * @param rightFront - The motor in the right, front position.
     * @param gyroscope  - The gyroscope to use.
     */
    public TankDrive(DcMotor leftBack, DcMotor leftFront, DcMotor rightBack, DcMotor rightFront, FieldPoint startLocation, Gyroscope gyroscope) {
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

        this.gyroscope = gyroscope;
        updateHeading();
        referenceHeading = heading;
    }

    /**
     *
     *
     * @param dSpeed     - [-1, 1], The desired speed for the robot to travel at.
     * @param dRotation  - [-1, 1], The desired speed of rotation for the robot.
     */
    public void drive(double dSpeed, double dRotation) {
        useEncoders(false);
        motorPowers[0] = dSpeed * Math.cos(3 * Math.PI/4) - dRotation;
        motorPowers[1] = dSpeed * Math.sin(3 * Math.PI/4) + dRotation;
        motorPowers[2] = dSpeed * Math.sin(3 * Math.PI/4) - dRotation;
        motorPowers[3] = dSpeed * Math.cos(3 * Math.PI/4) + dRotation;

        for (int i = 0; i < motors.length; i++) {
            motors[i].setPower(motorPowers[i]);
        }
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
                motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        } else {
            for (int i = 0; i < motors.length; i++) {
                motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
    }

    /**
     * Get the heading given by the gyroscope and update the local variable with it. This is used to
     * keep the mecanum drive going straight without drifting as it drives autonomously.
     */
    public void updateHeading() {
        gyroscope.updateHeading();
        heading = this.gyroscope.getHeading() - referenceHeading;
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
