package org.blueshiftrobotics.driveSupport;

import java.lang.Math;
import java.lang.reflect.Field;

/**
 * A FieldPoint object created for recording where the coordinates of and orienting the robot during
 * movement. It will allow a method for traveling to a point on the field without writing every
 * individual movement.
 *
 * @author Gabriel Wong
 * @version 1.2
 */

public class FieldPoint {
    private double x;
    private double y;
    private final double DEFAULT_VALUE = 0;

    /**
     * Initialize both the x and y coordinate to zero when no values are given for the constructor.
     */
    public FieldPoint() {
        setX( DEFAULT_VALUE );
        setY( DEFAULT_VALUE );
    }

    /**
     * Initialize the point to the parameters of another point.
     *
     * @param fieldPoint - the point to set the new point equal to
     */
    public FieldPoint(FieldPoint fieldPoint) {
        setX( fieldPoint.getX() );
        setY( fieldPoint.getY() );
    }

    /**
     * Initialize the point to two values, x and y, given for the constructor. This uses the setX()
     * and setY() functions over just natively setting the variables x and y to centralize all of
     * the variable changes.
     *
     * @param x - The x value of the new point
     * @param y - the y value of the new point
     */
    public FieldPoint(double x, double y) {
        setX(x);
        setY(y);
    }

    public FieldPoint getLocation() {
        return new FieldPoint(x, y);
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(double dx, double dy) {
        x = x + dx;
        y = y + dy;
    }
    public Double getX() {
        return x;
    }
    public Double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    public boolean equals(FieldPoint fieldPoint) {
        return x == fieldPoint.getX() && y == fieldPoint.getY();
    }

    /**
     * Check if the given point matches the working point (this) within a certain tolerance given
     * by the function call. This is used to compensate for the impreciseness of driving using
     * encoders and other position tracking methods.
     *
     * @param fieldPoint - The given point
     * @param tolerance  - The given tolerance
     * @return boolean - whether the two points are relatively equal or not.
     */
    public boolean equalsWithTol(FieldPoint fieldPoint, double tolerance) {
        return (Math.abs(fieldPoint.getX() - getX()) < tolerance) && (Math.abs(fieldPoint.getY() - getY()) < tolerance);
    }


    /**
     * Find the distance from the current point to the given point.
     *
     * @param fieldPoint - The point to find the distance to.
     * @return (double)  - The distance between the two points.
     */
    public double distanceToPoint(FieldPoint fieldPoint) {
        double xDifference = Math.abs(getX() + fieldPoint.getX());
        double yDifference = Math.abs(getY() + fieldPoint.getY());

        return Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
    }

    /**
     * Find the angle needed to travel from one point to another.
     *
     * @param fieldPoint - The point to be traveled to.
     * @return [0, 2pi] - The angle needed to travel.
     */
    public double angleToPoint(FieldPoint fieldPoint) {
        double xDifference = Math.abs(getX() + fieldPoint.getX());
        double yDifference = Math.abs(getY() + fieldPoint.getY());

        double angle = Math.atan(yDifference/xDifference);

        if (xDifference < 0) {
            angle += Math.PI;
        }

        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }

        return angle;
    }

    public String toString() {
        return "A point with the coordinates (" + x + ", " + y + ")";
    }
}
