package org.usfirst.frc.team4915.robot;

import edu.wpi.first.wpilibj.Joystick;

import edu.wpi.first.wpilibj.buttons.Button;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {    
    public static final double MIN_THROTTLE_SCALE = 0.5;    // adjust the scale based on minimum driveable value based on observation
    public static final double DRIVE_JOYSTICK_DEADBAND = 0.075;   // absolute values below this will be clipped to 0
    
    // Ports for joysticks
    public static final int DRIVE_STICK_PORT = 0;
    // Create joysticks for driving and aiming the launcher
    private Joystick driveStick;

    public OI() {
        this.driveStick = new Joystick(DRIVE_STICK_PORT);
    }
    
    public Joystick getJoystickDrive() {
        return this.driveStick;
    }

}

