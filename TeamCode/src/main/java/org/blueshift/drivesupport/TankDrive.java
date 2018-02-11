package org.blueshift.drivesupport;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
    private Telemetry telemetry;

    static ElapsedTime elapsedTime;

    private int iLeftBack = 0, iLeftFront = 1, iRightBack = 2, iRightFront = 3;

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
        motors[iLeftBack] = leftBack;
        motors[iLeftFront] = leftFront;
        motors[iRightBack] = rightBack;
        motors[iRightFront] = rightFront;

        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);

        for (int i = 0; i < motors.length; i++) {
            motorPowers[i] = 0;
            motors[i].setPower(motorPowers[i]);
        }
    }

    /**
     * Initialize all four motors according to the four parameters so that they can be controlled
     * later. These are, in fact, aliases to the original motors, so setting one's power will also
     * modify the original variable's data. This modified constructor will include the telemetry
     * object from the opmode so that we can output to the phone.
     *
     * @param leftBack   - The motor in the left, back position.
     * @param leftFront  - The motor in the left, front position.
     * @param rightBack  - The motor in the right, back position.
     * @param rightFront - The motor in the right, front position.
     * @param telemetry  - An optional telemetry object to output to.
     */
    public TankDrive(DcMotor leftBack, DcMotor leftFront, DcMotor rightBack, DcMotor rightFront, Telemetry telemetry) {
        this.telemetry = telemetry;

        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);

        motors[iLeftBack] = leftBack;
        motors[iLeftFront] = leftFront;
        motors[iRightBack] = rightBack;
        motors[iRightFront] = rightFront;

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
        motorPowers[iLeftBack] = dSpeed * Math.cos(3 * Math.PI/4) - dRotation;
        motorPowers[iLeftFront] = dSpeed * Math.sin(3 * Math.PI/4) + dRotation;
        motorPowers[iRightBack] = dSpeed * Math.sin(3 * Math.PI/4) - dRotation;
        motorPowers[iRightFront] = dSpeed * Math.cos(3 * Math.PI/4) + dRotation;

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
    public void driveWithEncoders(double dSpeed, int encoderTarget) {
        elapsedTime = new ElapsedTime();
        elapsedTime.reset();

        useEncoders();

        motors[iLeftFront].setTargetPosition(encoderTarget);
        motors[iRightBack].setTargetPosition(encoderTarget);

        motors[iRightFront].setTargetPosition(-encoderTarget);
        motors[iLeftBack].setTargetPosition(-encoderTarget);

        useRunToPosition();
        drive(dSpeed, 0);

        boolean running = true;

        while(running) {
            if (!isBusy()) {
                running = false;
            } else if (elapsedTime.milliseconds() > 6250) {
                running = false;
            }
        }

        elapsedTime = null;
        stop();
        dontUseEncoders();
    }

    /**
     *
     */
    public void useEncoders() {
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /**
     *
     */
    public void useRunToPosition() {
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    /**
     *
     */
    public void dontUseEncoders() {
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public boolean isBusy() {
        return (motors[iLeftBack].isBusy() || motors[iLeftFront].isBusy() || motors[iRightBack].isBusy() || motors[iRightFront].isBusy());
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
