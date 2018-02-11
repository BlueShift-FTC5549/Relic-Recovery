package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.blueshift.vision.JewelSense_Vuforia;
import org.blueshift.vision.JewelState;
import org.blueshift.vision.VuforiaTracker;

/**
 *
 *
 */

@Autonomous(name="DEBUG Jewel Sensing", group ="DEBUG")
public class DEBUG_JewelSensing extends LinearOpMode {
    private static ElapsedTime runtime = new ElapsedTime();

    VuforiaTracker jewelSense;

    @Override public void runOpMode() {
        initialize();

        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        telemetry.clear();
        telemetry.addData("Status", "Started");
        telemetry.update();

        while (opModeIsActive()) {
            String jewelString;
            JewelState sensedJewel = jewelSense.jewelSearch_BottomLeft();

            if (sensedJewel == JewelState.BLUE) {
                jewelString = "BLUE";
            } else if (sensedJewel == JewelState.RED) {
                jewelString = "RED";
            } else {
                jewelString = "UNKNOWN";
            }

            telemetry.addData( "Jewel", jewelString );
            telemetry.update();

            sleep(100);
        }
    }

    private void initialize() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        jewelSense = new VuforiaTracker(cameraMonitorViewId);

        //Notify the Driver Station
        telemetry.addData("Initialized: ", runtime.seconds() + " seconds");
        telemetry.update();
    }
}
