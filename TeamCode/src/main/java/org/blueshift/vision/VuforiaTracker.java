package org.blueshift.vision;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * This is the VuForia tracking mechanism for a VuMark completely rolled into one function. To use
 * this, a team MUST enter in its Vuforia License Key to continue. This class is a compressed
 * version of the VuMark tracking sample code. It uses the "RelicVumark" files in the Assets section
 * of the project and looks for them, returning either CENTER, LEFT, RIGHT, or UNKNOWN values using
 * the enum {@link RelicRecoveryVuMark}.
 *
 * @author Gabriel Wong
 * @version 1.0
 */

public class VuforiaTracker {
    private VuforiaLocalizer vuforia;

    private VuforiaTrackables relicTrackables;
    private VuforiaTrackable relicTemplate;

    private int leftRed = 0, leftBlue = 0, rightRed = 0, rightBlue = 0;

    private String vuforiaLicenseKey = "AQfdha3/////AAAAGZL0n1bAsE6PhPRk2ZsrU28GbeRb+R9mOTvsv5dv8xLA+Fi/2ZqLOjcq93npWWM6jcqlyPmp76pka6/mCQEqGXEWZ2hHSaTOB8/XSgSao69oTendDtVCQjMY0ewFVhgXlg63pNtHIbq1OTchsAiuyfPIaTWs3Mii/JTqZEpNdlCSS1mshiYXH2fxUwmC/U5onbwMr3VNejuJS60rYacIzSkeYIRbMeeYNhxfJej4nNxrVPkCKcgkw10CHeKlnNLD9LuzSB6u/5dl1mSXsDh4P8gj02JjVYtWmEqvVRWC744RK97jA3KEemN9ALm6owsXtrCVN1CWnkOdlPJWFGlGZILEx7rxb9/st5hqUweC8iLs";

    /**
     * This constructor initializes all of the Vuforia necessities. It activities the program with
     * our team's vuforia license key, outputs the view to the RC, and tells the program what object
     * it should be looking for from the Assets folder.
     *
     * @param cameraMonitorViewId - The Camera Monitor (On the RC) hardware map ID.
     */
    public VuforiaTracker(int cameraMonitorViewId) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = vuforiaLicenseKey;

        //Indicate what camera to use
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");
    }

    /**
     * This constructor initializes all of the Vuforia necessities. It activities the program with
     * our team's vuforia license key and tells the program what object it should be looking for
     * from the Assets folder. This constructor DOES NOT include a visual output.
     */
    public VuforiaTracker() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = vuforiaLicenseKey;

        //Indicate what camera to use
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");
    }

    /**
     * The one time activation code necessary for Vuforia to trackPictograph. This is for the glyph pictokey.
     */
    public void initPictographTracking() {
        relicTrackables.activate();
    }

    public void quitPictographTracking() {
        relicTrackables.deactivate();
    }

    /**
     *
     */
    public void initJewelTracking() {
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true); //enables RGB565 format for the image

        vuforia.setFrameQueueCapacity(1); //tells VuforiaLocalizer to only store one frame at a time
    }

    /**
     * Meant to run in a loop, it will check to see if any instances of relicTemplate are currently
     * visible. RelicRecoveryVuMark is an enumeration which can have the following values: UNKNOWN,
     * LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than UNKNOWN will be
     * returned by the RelicRecoveryVuMark#from(VuforiaTrackable)}.
     *
     * @return RelicRecoveryVuMark - the vuMark that the tracker can currently see.
     */
    public RelicRecoveryVuMark trackPictograph(int timeout) {
        ElapsedTime relicRuntime = new ElapsedTime();
        relicRuntime.reset();

        initPictographTracking();

        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);

        boolean stillRunning = true;

        while (stillRunning) {
            if (relicRuntime.milliseconds() < timeout && (vuMark == RelicRecoveryVuMark.UNKNOWN)) {
                vuMark = RelicRecoveryVuMark.from(relicTemplate);
            } else {
                stillRunning = false;
            }
        }

        return vuMark;
    }

    /**
     * Grab the image frame from the front camera through VuForia's engine.
     *
     * @return Image - the frame as an RGB565 image.
     */
    private Image getImage() {
        Image rgb = null;

        try {
            //Take a frame from the frame queue - there where only be one due to configurations.
            VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take();

            long numImages = frame.getNumImages();

            //Count through every image (1) and set rgb equal to it. (RGB565)
            for (int i = 0; i < numImages; i++) {
                if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    rgb = frame.getImage(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rgb;
    }

    /**
     * Convert the input RGB565 image into Android's BitMap format. This will be used for getting
     * certain pixel values later in the countPixels() method.
     *
     * @param rgb - An RGB565 image usually gotten through the getImage() method.
     * @return Bitmap - the input RGB565 image in Android's BitMap form.
     */
    private Bitmap processImage(Image rgb) {
        Bitmap bitmap = null;

        try {
            bitmap = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);
            bitmap.copyPixelsFromBuffer(rgb.getPixels());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * LEFT is the top side of the bear. To count the pixels, this method takes every second pixel
     * given in the Bitmap image in the specified quadrant of the camera (CameraSide) by looping
     * through each case and comparing the RGB(565) values with each other. For example, comparing
     * a pixel's RGB values includes one color being over the other two. If the Red value is greater
     * than Blue, then the pixel is red. If the Blue value is greater than Red, then the pixel is
     * blue. This continues on until the entire quadrant has been run through.
     *
     * @param side - The side of the Camera that the jewels should be searched on.
     * @param colorOption - What color (Red or Blue) that should be counted.
     * @return int - The number of pixels of the color (colorOption).
     */
    private int countPixels(CameraSide side, RGBColor colorOption) {
        Bitmap bitmap = processImage(getImage());

        //Return -1 if the input bitmap is 1
        if (bitmap == null) { return -1; }

        //Declare the initial counting variables
        int redCount = 0, blueCount = 0;
        int pixel, redValue, blueValue, greenValue;

        /**
         * For whatever quadrant specified in the function call, run a FOR loop for every second
         * pixel in the photo's quadrant. Comparing a pixel's values runs as specified above.
         */
        if (side == CameraSide.LEFT) {
            //The major for loop will run through every x value of the given frame
            for (int x = 0; x < bitmap.getWidth() / 2; x += 2) {
                //The minor for loop will run through every y value of the given frame
                for (int y = 0; y < bitmap.getHeight(); y += 2) {
                    //Grab the pixel from the coordinate
                    pixel = bitmap.getPixel(x, y);

                    //Grab the red/blue values from the pixel
                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);

                    //Compare the two values
                    if (redValue > blueValue) {
                        redCount++;
                    } else if (blueValue > redValue) {
                        blueCount++;
                    }
                } //y
            } //x
        } else if (side == CameraSide.RIGHT){
            for (int x = bitmap.getWidth() / 2; x < bitmap.getWidth(); x += 2) {
                for (int y = 0; y < bitmap.getHeight(); y += 2) {
                    pixel = bitmap.getPixel(x, y);

                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);
                    greenValue = Color.green(pixel);

                    if (redValue > blueValue) {
                        redCount++;
                    } else if (blueValue > redValue) {
                        blueCount++;
                    }
                }
            }
        } else if (side == CameraSide.BOTTOMLEFTLEFT) {
            for (int x = 0; x < bitmap.getWidth() / 4; x += 2) {
                for (int y = 0; y < bitmap.getHeight() / 3; y += 2) {
                    pixel = bitmap.getPixel(x, y);

                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);
                    greenValue = Color.green(pixel);

                    if (redValue > blueValue) {
                        redCount++;
                    } else if (blueValue > redValue) {
                        blueCount++;
                    }
                }
            }
        } else if (side == CameraSide.BOTTOMLEFTRIGHT) {
            for (int x = bitmap.getWidth() / 4; x < bitmap.getWidth() / 2; x += 2) {
                for (int y = 0; y < bitmap.getHeight() / 3; y += 2) {
                    pixel = bitmap.getPixel(x, y);

                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);
                    greenValue = Color.green(pixel);

                    if (redValue > blueValue) {
                        redCount++;
                    } else if (blueValue > redValue) {
                        blueCount++;
                    }
                }
            }
        } else {
            throw new NullPointerException();
        }

        //Return the requested value.
        if (colorOption == RGBColor.RED) {
            return redCount;
        } else if (colorOption == RGBColor.BLUE) {
            return blueCount;
        } else {
            return 0;
        }
    }

    /**
     * Search for the Jewel across the entire camera screen using countPixels().
     *
     * @return JewelState - the jewel at LEFT (The top of the camera).
     */
    public JewelState jewelSearch_Full() {
        int leftRed = countPixels(CameraSide.LEFT, RGBColor.RED);
        int leftBlue = countPixels(CameraSide.LEFT, RGBColor.BLUE);

        int rightRed = countPixels(CameraSide.RIGHT, RGBColor.RED);
        int rightBlue = countPixels(CameraSide.RIGHT, RGBColor.BLUE);


        if (leftRed > leftBlue && rightBlue > rightRed) {
            return JewelState.RED;
        } else if (leftBlue > leftRed && rightRed > rightBlue) {
            return JewelState.BLUE;
        } else {
            return JewelState.UNKNOWN;
        }
    }

    /**
     * Search for the Jewel across the Bottom Left quadrant of the phone screen.
     *
     * @return JewelState - the jewel closer to the top of the camera.
     */
    public JewelState jewelSearch_BottomLeft() {
        leftRed = countPixels(CameraSide.BOTTOMLEFTLEFT, RGBColor.RED);
        leftBlue = countPixels(CameraSide.BOTTOMLEFTLEFT, RGBColor.BLUE);

        rightRed = countPixels(CameraSide.BOTTOMLEFTRIGHT, RGBColor.RED);
        rightBlue = countPixels(CameraSide.BOTTOMLEFTRIGHT, RGBColor.BLUE);

        //If any of the values are -1 (An error value) then return null
        if (leftRed == -1 || leftBlue == -1 || rightRed == -1 || rightBlue == -1) {
            return null;
        }

        //Return the enumeration (JewelState) value for the sensed jewel.
        if (leftRed > leftBlue && rightBlue > rightRed) {
            return JewelState.RED;
        } else if (leftBlue > leftRed && rightRed > rightBlue) {
            return JewelState.BLUE;
        } else {
            return JewelState.UNKNOWN;
        }
    }

    public int getLeftRed() { return leftRed; }
    public int getLeftBlue() { return leftBlue; }
    public int getRightRed() { return rightRed; }
    public int getRightBlue() { return rightBlue; }
}
