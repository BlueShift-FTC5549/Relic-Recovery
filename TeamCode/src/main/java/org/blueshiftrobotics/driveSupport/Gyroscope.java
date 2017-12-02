package org.blueshiftrobotics.driveSupport;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
/**
 * A class to store the gyroscope as an object that can be manipulated with custom functions.
 *
 * @author Gabriel Wong
 * @version 0.1 ALPHA
 */

public class Gyroscope {
    Orientation orientation;
    Acceleration gravity;
    BNO055IMU gyroscope;

    double heading;

    public Gyroscope(BNO055IMU gyroscope) {
        this.gyroscope = gyroscope;
    }

    /**
     * Setup the parameters of the gyroscope given in the constructor including units and  the
     * calibration file.
     */
    public void initGyroscope() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        gyroscope.initialize(parameters);
    }

    /**
     * Update the heading of the robot according to the rotation around the Z axis.lso, find the
     * acceleration due to gravity and the full orientation from thr gyro.
     */
    public void updateHeading() {
        orientation = gyroscope.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        heading = orientation.firstAngle;
        gravity = gyroscope.getGravity();
    }

    /**
     * @return The heading as a double value in radians.
     */
    public double getHeading() {
        return heading;
    }

    public Acceleration getGravity() {
        return gravity;
    }
}
