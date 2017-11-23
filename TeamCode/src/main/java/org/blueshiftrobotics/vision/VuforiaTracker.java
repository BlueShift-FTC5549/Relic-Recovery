package org.blueshiftrobotics.vision;

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

    /**
     * This constructor initializes all of the Vuforia necessities. It activities the program with
     * our team's vuforia license key, outputs the view to the RC, and tells the program what object
     * it should be looking for from the Assets folder.
     *
     * @param cameraMonitorViewId - The Camera Monitor (On the RC) hardware map ID.
     */
    public VuforiaTracker(int cameraMonitorViewId) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "ATsODcD/////AAAAAVw2lR...d45oGpdljdOh5LuFB9nDNfckoxb8COxKSFX";

        //Indicate what camera to use
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
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
        parameters.vuforiaLicenseKey = "ATsODcD/////AAAAAVw2lR...d45oGpdljdOh5LuFB9nDNfckoxb8COxKSFX";

        //Indicate what camera to use
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");
    }

    /**
     * The one time activation code necessary for Vuforia to track.
     */
    public void initTracking() {
        relicTrackables.activate();
    }

    /**
     * Meant to run in a loop, it will check to see if any instances of relicTemplate are currently
     * visible. RelicRecoveryVuMark is an enumeration which can have the following values: UNKNOWN,
     * LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than UNKNOWN will be
     * returned by the RelicRecoveryVuMark#from(VuforiaTrackable)}.
     *
     * @return RelicRecoveryVuMark - the vuMark that the tracker can currently see.
     */
    public RelicRecoveryVuMark track() {
        return RelicRecoveryVuMark.from(relicTemplate);
    }
}