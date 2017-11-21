/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.lang.Math;

import org.firstinspires.ftc.teamcode.TeamCodeSupport.Point;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode". An OpMode is a 'program'
 * that runs in either the autonomous or the teleop period of an FTC match. The names of OpModes
 * appear on the menu of the FTC Driver Station. When an selection is made from the menu, the
 * corresponding OpMode class is instantiated on the Robot Controller and executed. This OpMode is
 * for a 4 wheeled mecanum drive.
 *
 * The Driver-Controller TeleOP period controls the movement of the robot with the left stick and
 * the two bumpers of gamepad 1. The left stick controllers both direction and speed (direction is
 * the angle formed by the stick, and speed is the distance it is pushed from the center).
 *
 * The autonomous period uses the Point object to record its location and travels to different areas
 * on the field with this coordinate system. It uses the camera to find the locations of cubes
 * (Cyphers) and travels to their locations; the robot grabs the cubes and transports it to the
 * cypher box location.
 *
 * @author Gabriel Wong
 * @version 1.0
 */

@Disabled
@TeleOp(name="Mecanum Drive", group="Iterative Opmode")
public class MecanumTeleOP extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack, leftFront;
    private DcMotor rightBack, rightFront;

    private final Point STARTING_LOCATION = new Point(0,0);
    private Point currentLocation = new Point(STARTING_LOCATION);

    /**
     * Run once when the 'init' button is pressed. The motor objects are set to their named hardware
     * map counterparts in the phone configuration.
     */
    @Override public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftBack  = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /**
     * The init_loop method runs continuously after the driver hits 'init' but before he or she hits
     * 'play'.
     */
    @Override public void init_loop() {

    }

    /**
     * Run once when the 'play' button is pressed.
     */
    @Override public void start() {
        runtime.reset();
    }

    /**
     * The loop function is run continuously from when the driver presses 'play' to when he presses
     * 'stop' or aborts the program. This method continuously updates the mecanum drives according
     * to the left joystick and the two top bumpers.
     */
    @Override public void loop() {
        double dAngle, dSpeed, dRotation;
        int leftBumperValue, rightBumperValue;

        if (gamepad1.left_bumper) { leftBumperValue = 1; } else { leftBumperValue = 0; }
        if (gamepad1.right_bumper) { rightBumperValue = 1; } else { rightBumperValue = 0; }

        // Create the three Mecanum control variables.
        if (gamepad1.left_stick_x != 0) {
            if (gamepad1.left_stick_x > 0) {
                dAngle = Math.atan(gamepad1.left_stick_y / gamepad1.left_stick_x);
            } else {
                dAngle = -Math.atan(gamepad1.left_stick_y / gamepad1.left_stick_x);
            }

            dSpeed = Range.clip(gamepad1.left_stick_y / gamepad1.left_stick_x, -1.0, 1.0);
        } else {
            if (gamepad1.left_stick_y < 0) {
                dAngle = 3 * Math.PI/2;
            } else {
                dAngle = Math.PI/2;
            }

            dSpeed = Range.clip(gamepad1.left_stick_y, -1.0, 1.0);
        }

        //Set whether to rotate or not by the bumper values.
        dRotation = rightBumperValue - leftBumperValue;

        //Calculate the Voltage Multipliers For The Motors and Set Them
        //Left Motors
        leftFront.setPower(dSpeed * Math.sin(dAngle + Math.PI/4) + dRotation); // Front V = dSpeed * sin(dAngle + pi/4) + v0
        leftBack.setPower(dSpeed * Math.cos(dAngle + Math.PI/4) + dRotation); // Back V = dSpeed * cos(dAngle + pi/4) + v0

        //Right Motors
        rightFront.setPower(dSpeed * Math.cos(dAngle + Math.PI/4) - dRotation); // Front V = dSpeed * cos(dAngle + pi/4) - v0
        rightBack.setPower(dSpeed * Math.sin(dAngle + Math.PI/4) - dRotation); // Front V = dSpeed * cos(dAngle + pi/4) - v0

        //Generate Telemetry
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "Angle (%.2f), Speed (%.2f)", dAngle, dSpeed);
        telemetry.addData("Rotaton", "Rotating at", dRotation);
    }

    @Override public void stop() {
        leftFront.setPower(0.0);
        leftBack.setPower(0.0);
        rightFront.setPower(0.0);
        rightBack.setPower(0.0);
    }
}