package org.usfirst.frc.team4915.robot.commands;

import org.usfirst.frc.team4915.robot.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.CANTalon;
import org.usfirst.frc.team4915.robot.OI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import org.usfirst.frc.team4915.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ArcadeDrive extends Command {
    private double _driveYstick;
    private double _driveXstick;
    private double _scaledThrottle;
    // private double _throttleScale;
    private StringBuilder _sb = new StringBuilder();
    
    // Use requires() here to declare subsystem dependencies
    public ArcadeDrive() {
        requires(Robot.driveTrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        System.out.println("Setting maxOutput via ArcadeDrive----");
        if (Robot.driveTrain.isPercentVbus()) {
            Robot.driveTrain.setControlMode(CANTalon.TalonControlMode.PercentVbus);
            Robot.driveTrain.setMaxOutput(DriveTrain.DEFAULT_PERCENTVBUS_MAX_OUTPUT);
        } 
        else {
            Robot.driveTrain.setControlMode(CANTalon.TalonControlMode.Speed);            
            Robot.driveTrain.setMaxOutput(Robot.driveTrain.getMaxOutput());            
        }
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        // Note: multiplying -1 to reverse the joystick direction for forward/reverse and left/right drive motion
        _driveYstick = Robot.oi.getJoystickDrive().getAxis(AxisType.kY) * -1;   // Get Y axis value and reverse it
        _driveXstick = Robot.oi.getJoystickDrive().getAxis(AxisType.kX) * -1;   // Get X axis value and reverse it
        _scaledThrottle = _scaleThrottle(Robot.oi.getJoystickDrive().getAxis(AxisType.kThrottle));    // Get and scale the throttle value
        
        // Beware that debugging will slow motor updates, and may result in output not updated often enough
        smartDashboardDebugDisplay();
        
        // apply deadband to joystick (makes sure motors are off if joystick does not zero properly)
        if (Math.abs(_driveYstick) < OI.DRIVE_JOYSTICK_DEADBAND) {
            _driveYstick = 0;
        }
        if (Math.abs(_driveXstick) < OI.DRIVE_JOYSTICK_DEADBAND) {
            _driveXstick = 0;
        }

        Robot.driveTrain.arcadeDrive(_driveYstick * _scaledThrottle, _driveXstick * _scaledThrottle);
    }
    
    /*
     * Returns a scaled value between MIN_THROTTLE_SCALE and 1.0
     * MIN_THROTTLE_SCALE must be set to the lowest useful scale value through experimentation
     * Scale the joystick values by throttle before passing to the driveTrain
     *     +1=bottom position; -1=top position
     */
    private double _scaleThrottle(double raw_throttle_value) {
        /**
         * Throttle returns a double in the range of -1 to 1. We would like to change that to a range of MIN_THROTTLE_SCALE to 1.
         * First, multiply the raw throttle value by -1 to reverse it (makes "up" maximum (1), and "down" minimum (-1))
         * Then, add 1 to make the range 0-2 rather than -1 to +1
         * Then multiply by ((1-MIN_THROTTLE_SCALE)/2) to change the range to 0-(1-MIN_THROTTLE_SCALE)
         * Finally add MIN_THROTTLE_SCALE to change the range to MIN_THROTTLE_SCALE to 1
         * 
         * Check the results are in the range of MIN_THROTTLE_SCALE to 1, and clip it in case the math went horribly wrong.
         */
        double scale = ((raw_throttle_value * -1) + 1) * ((1-OI.MIN_THROTTLE_SCALE) / 2) + OI.MIN_THROTTLE_SCALE;

        if (scale < OI.MIN_THROTTLE_SCALE) {
            // Somehow our math was wrong. Our value was too low, so force it to the minimum
            scale = OI.MIN_THROTTLE_SCALE;            
        } 
        else if (scale > 1) {
            // Somehow our math was wrong. Our value was too high, so force it to the maximum
            scale = 1.0;
        }
        return scale;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
    
    public void smartDashboardDebugDisplay() {
        _sb.setLength(0);
        _sb.append("Y value: ");
        _sb.append(String.format("%.3f", _driveYstick));
        _sb.append("| X value: ");
        _sb.append(String.format("%.3f", _driveXstick));
        _sb.append("| throttle value: ");
        _sb.append(String.format("%.2f", _scaledThrottle));
        SmartDashboard.putString("Drive Joystick Info: ", _sb.toString());     
    }

}
