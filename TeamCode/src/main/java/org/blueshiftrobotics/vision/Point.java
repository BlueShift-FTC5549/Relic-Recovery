package org.blueshiftrobotics.vision;

/**
 * A Point object created for recording where the coordinates of and orienting the robot during
 * movement. It will allow a method for traveling to a point on the field without writing every
 * individual movement.
 *
 * @author Gabriel Wong
 * @version 1.0
 */

public class Point {
    private double x;
    private double y;
    private final double DEFAULT_VALUE = 0;

    public Point() {
        setX( DEFAULT_VALUE );
        setY( DEFAULT_VALUE );
    }

    public Point(Point point) {
        setX( point.getX() );
        setY( point.getY() );
    }

    public Point(double x, double y) {
        setX(x);
        setY(y);
    }

    public Double getX() {
        return x;
    }
    public Double getY() {
        return y;
    }

    public Point getLocation() {
        return new Point(x, y);
    }
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(double dx, double dy) {
        x = x + dx;
        y = y + dy;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    public boolean equals(Point point) {
        return x == point.getX() && y == point.getY();
    }

    public String toString() {
        return "A point with the coordinates (" + x + ", " + y + ")";
    }
}
