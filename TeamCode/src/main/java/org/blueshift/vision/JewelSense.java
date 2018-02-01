package org.blueshift.vision;

import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.hardware.ColorSensor;
import org.blueshift.vision.JewelState;

/**
 * Class for the Color Sensor meant for sensing Jewels (red or blue). When constructed, this class
 * will use the color sensor's color value output and convert it to HSV values for easier use when
 * programming. The main functions will turn the LED on or off, display the color on the robot
 * controller phone's screen, and/or return the Jewel that the sensor is currently seeing.
 * {@link org.blueshift.vision.JewelState}
 *
 * @author Gabriel Wong
 * @version 0.2 ALPHA
 */

public class JewelSense {
    ColorSensor colorSensor;

    private float hsvValues[] = {0F, 0F, 0F};
    private final float values[] = hsvValues;
    private final double SCALE_FACTOR = 255;

    private View relativeLayout;

    /**
     * @param colorSensor - the color sensor to be used with these functions.
     */
    public JewelSense(ColorSensor colorSensor) {
        this.colorSensor = colorSensor;
        enableLed(true);
    }

    /**
     * A constructor that both passes in the color sensor to be used with these functions and
     * enables the functionality (debugging purposes) that shows the viewed color on the robot
     * controller's screen.
     *
     * @param colorSensor    - the color sensor to be used with these functions.
     * @param relativeLayout - the robot controller screen that the color will be displayed on.
     */
    public JewelSense(ColorSensor colorSensor, View relativeLayout) {
        this.colorSensor = colorSensor;
        this.relativeLayout = relativeLayout;
        enableLed(true);
    }

    /**
     * Turn the LED on or off.
     *
     * @param state - the preferred state of the LED.
     */
    public void enableLed(boolean state) { colorSensor.enableLed(state); }

    /** Update the array tables that hold the HSV color values with a modified RGB table. */
    private void readColors() {
        Color.RGBToHSV((int) (colorSensor.red() * SCALE_FACTOR),
                (int) (colorSensor.green() * SCALE_FACTOR),
                (int) (colorSensor.blue() * SCALE_FACTOR),
                hsvValues);
    }

    /** Display the viewed color on the robot controller. */
    public void showColor() {
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
            }
        });
    }

    /**
     * Update the colors with the readColors() method and then compare the outputs with eachother to
     * find whether the robot is looking at a red or blue jewel.
     *
     * @return JewelState - the jewel the robot is looking at.
     */
    public JewelState lookingAt() {
        readColors();
        if (hsvValues[0] > hsvValues[2] + 10) {
            return JewelState.RED;
        } else if (hsvValues[0] + 10 < hsvValues[2]) {
            return JewelState.BLUE;
        } else {
            return JewelState.UNKNOWN;
        }
    }
}