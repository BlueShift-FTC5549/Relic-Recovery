package org.blueshift.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;

import com.vuforia.CameraDevice;
import com.vuforia.CameraField;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;

import java.nio.ByteBuffer;

/**
 *
 *
 * @author Gabriel Wong
 * @version 0.1 ALPHA
 */

public class JewelSense_Vuforia {
    private String vuforiaLicenseKey = "AQfdha3/////AAAAGZL0n1bAsE6PhPRk2ZsrU28GbeRb+R9mOTvsv5dv8xLA+Fi/2ZqLOjcq93npWWM6jcqlyPmp76pka6/mCQEqGXEWZ2hHSaTOB8/XSgSao69oTendDtVCQjMY0ewFVhgXlg63pNtHIbq1OTchsAiuyfPIaTWs3Mii/JTqZEpNdlCSS1mshiYXH2fxUwmC/U5onbwMr3VNejuJS60rYacIzSkeYIRbMeeYNhxfJej4nNxrVPkCKcgkw10CHeKlnNLD9LuzSB6u/5dl1mSXsDh4P8gj02JjVYtWmEqvVRWC744RK97jA3KEemN9ALm6owsXtrCVN1CWnkOdlPJWFGlGZILEx7rxb9/st5hqUweC8iLs";
    private VuforiaLocalizer vuforia;

    public JewelSense_Vuforia(int cameraMonitorViewId) {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        params.vuforiaLicenseKey = vuforiaLicenseKey;
        params.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;

        vuforia = ClassFactory.createVuforiaLocalizer(params);

        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true); //enables RGB565 format for the image

        vuforia.setFrameQueueCapacity(1); //tells VuforiaLocalizer to only store one frame at a time
    }

    public JewelSense_Vuforia() {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters();
        params.vuforiaLicenseKey = vuforiaLicenseKey;
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.createVuforiaLocalizer(params);

        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true); //enables RGB565 format for the image

        vuforia.setFrameQueueCapacity(1); //tells VuforiaLocalizer to only store one frame at a time
    }

    private Image getImage() {
        Image rgb = null;

        try {
            VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take();

            long numImages = frame.getNumImages();

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

    private Bitmap processImage(Image rgb) {
        Bitmap bitmap = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);
        bitmap.copyPixelsFromBuffer(rgb.getPixels());

        return bitmap;
    }

    //LEFT = The Top Side of the Phone
    private int countPixels(CameraSide side, RGBColor colorOption) {
        Bitmap bitmap = processImage(getImage());

        int redCount = 0, blueCount = 0;

        int pixel, redValue, blueValue, greenValue;

        if (side == CameraSide.LEFT) {
            for (int x = 0; x < bitmap.getWidth() / 2; x += 2) {
                for (int y = 0; y < bitmap.getHeight(); y += 2) {
                    pixel = bitmap.getPixel(x, y);

                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);
                    greenValue = Color.green(pixel);

                    if (redValue > blueValue && redValue > greenValue) {
                        redCount++;
                    } else if (blueValue > redValue && blueValue > greenValue) {
                        blueCount++;
                    }
                }
            }
        } else if (side == CameraSide.RIGHT){
            for (int x = bitmap.getWidth() / 2; x < bitmap.getWidth(); x += 2) {
                for (int y = 0; y < bitmap.getHeight(); y += 2) {
                    pixel = bitmap.getPixel(x, y);

                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);
                    greenValue = Color.green(pixel);

                    if (redValue > blueValue && redValue > greenValue) {
                        redCount++;
                    } else if (blueValue > redValue && blueValue > greenValue) {
                        blueCount++;
                    }
                }
            }
        } else if (side == CameraSide.BOTTOMLEFTLEFT) {
            for (int x = 0; x < bitmap.getWidth() / 4; x += 2) {
                for (int y = 0; y < bitmap.getHeight() / 2; y += 2) {
                    pixel = bitmap.getPixel(x, y);

                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);
                    greenValue = Color.green(pixel);

                    if (redValue > blueValue && redValue > greenValue) {
                        redCount++;
                    } else if (blueValue > redValue && blueValue > greenValue) {
                        blueCount++;
                    }
                }
            }
        } else if (side == CameraSide.BOTTOMLEFTRIGHT) {
            for (int x = bitmap.getWidth() / 4; x < bitmap.getWidth() / 2; x += 2) {
                for (int y = 0; y < bitmap.getHeight() / 2; y += 2) {
                    pixel = bitmap.getPixel(x, y);

                    redValue = Color.red(pixel);
                    blueValue = Color.blue(pixel);
                    greenValue = Color.green(pixel);

                    if (redValue > blueValue && redValue > greenValue) {
                        redCount++;
                    } else if (blueValue > redValue && blueValue > greenValue) {
                        blueCount++;
                    }
                }
            }
        } else {
            throw new NullPointerException();
        }

        if (colorOption == RGBColor.RED) {
            return redCount;
        } else if (colorOption == RGBColor.BLUE) {
            return blueCount;
        } else {
            return 0;
        }
    }

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

    public JewelState jewelSearch_BottomLeft() {
        int leftRed = countPixels(CameraSide.BOTTOMLEFTLEFT, RGBColor.RED);
        int leftBlue = countPixels(CameraSide.BOTTOMLEFTLEFT, RGBColor.BLUE);

        int rightRed = countPixels(CameraSide.BOTTOMLEFTRIGHT, RGBColor.RED);
        int rightBlue = countPixels(CameraSide.BOTTOMLEFTRIGHT, RGBColor.BLUE);


        if (leftRed > leftBlue && rightBlue > rightRed) {
            return JewelState.RED;
        } else if (leftBlue > leftRed && rightRed > rightBlue) {
            return JewelState.BLUE;
        } else {
            return JewelState.UNKNOWN;
        }
    }
}
