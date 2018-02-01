package org.blueshift.drivesupport;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;

/**
 * A class to store the gyroscope as an object that can be manipulated with custom functions. This
 * class is a bridge between the native functionality of the BNO055IMU inside of the REV Expansion
 * hub and the TeleOP program. This class can return the current heading of the bot (radians), and
 * also the current acceleration felt by the robot.
 *
 * @author Gabriel Wong
 * @version 1.0
 */

public class Gyroscope {
    private Orientation orientation;
    private Acceleration gravity;
    private BNO055IMU gyroscope;

    private double heading;

    public Gyroscope(BNO055IMU gyroscope) {
        this.gyroscope = gyroscope;
    }

    /**
     * Setup the parameters of the gyroscope given in the constructor including units and  the
     * calibration file.
     */
    public void init() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        gyroscope.initialize(parameters);
    }

    public void calibrate() {
        // Get the calibration data
        BNO055IMU.CalibrationData calibrationData = gyroscope.readCalibrationData();

        // Save the calibration data to a file. You can choose whatever file
        // name you wish here, but you'll want to indicate the same file name
        // when you initialize the IMU in an opmode in which it is used. If you
        // have more than one IMU on your robot, you'll of course want to use
        // different configuration file names for each.
        String filename = "AdafruitIMUCalibration.json";
        File file = AppUtil.getInstance().getSettingsFile(filename);
        ReadWriteFile.writeFile(file, calibrationData.serialize());
    }

    public void updateOrientation() {
        orientation = gyroscope.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
    }

    /**
     * Update the heading of the robot according to the rotation around the Z axis. Also, find the
     * acceleration due to gravity and the full orientation from thr gyro.
     */
    public void updateHeading() {
        updateOrientation();
        heading = orientation.firstAngle;
        gravity = gyroscope.getGravity();
    }

    /** @return The heading as a double value in radians. */
    public double getHeading() {
        updateHeading();
        return heading;
    }

    /**
     * For checking the current heading against a previous heading/target with a tolerance. The
     * tolerance is necessary because readings of the IMU Gyroscope are neither taken often enough
     * nor are they precise enough to have one set value - there must be a tolerance that changes
     * with robot rotation speed.
     *
     * @param target - The target or previous heading to check the current one against.
     * @param tolerance - This dictates what values are acceptable for 'equal'.
     * @return Boolean - If they are equal (within tolerances) or not.
     */
    public Boolean headingEquals(double target, double tolerance) {
        return (target - tolerance < getHeading()) && (getHeading() < target + tolerance);
    }

    public Acceleration getGravity() {
        return gravity;
    }

    public String getSystemStatus() { return gyroscope.getSystemStatus().toShortString(); }

    public String getCalibrationStatus() { return gyroscope.getCalibrationStatus().toString(); }
}
