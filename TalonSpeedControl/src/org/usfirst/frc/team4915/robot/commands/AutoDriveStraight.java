package org.usfirst.frc.team4915.robot.commands;

import edu.wpi.first.wpilibj.CANTalon;
import org.usfirst.frc.team4915.robot.RobotMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4915.robot.subsystems.DriveTrain;
import org.usfirst.frc.team4915.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class AutoDriveStraight extends Command {

    private StringBuilder _sb = new StringBuilder();

    public AutoDriveStraight() {
        requires(Robot.driveTrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {        
        System.out.println("Autonomous output : " + Robot.driveTrain.getAutonomousOutput());
        System.out.println("Autonomous distance: " + Robot.driveTrain.getAutonomousDistance());
        System.out.println("Autonomous distance in ticks: " + Robot.driveTrain.getAutonomousDistanceInTicks());

        // Only run in Speed mode -- speed was set during Robot::autonomousInit() from prefs
        Robot.driveTrain.setControlMode(CANTalon.TalonControlMode.Speed);            
        // initialize the encoders to 0
        Robot.driveTrain.resetEncoders();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if (Robot.driveTrain.getAutonomousDistanceInTicks() != 0) {
            Robot.driveTrain.driveStraight(Robot.driveTrain.getAutonomousOutput());
            // This debugging will slow down updates to the motors... May result in motors not updated often enough.
            for (int i=0; i < DriveTrain.motors.size(); i++) {
                _sb.setLength(0);
                _sb.append("Motor[");
                _sb.append(DriveTrain.motors.get(i).getDeviceID()); 
                _sb.append("] : ");
                _sb.append(DriveTrain.motors.get(i).getEncPosition());
                SmartDashboard.putString("Encoder value: ", _sb.toString());
            }                    
        }
        else {
            SmartDashboard.putString("Encoder value: ", "No Ticks");
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    // We are finished when either encoder has moved the requested autonomous distance (in ticks)
    // TODO: It may be wise to set a timer to time out in case our encoders are broken or our drivetrain is stuck?
    //       Maybe look for a few seconds of time in which no encoder changes happen? Then return true to stop us...
    protected boolean isFinished() {
        if ((Robot.driveTrain.getAutonomousDistanceInTicks() == 0) ||
                (Math.abs(RobotMap.leftMaster10.getEncPosition()) >= Math.abs(Robot.driveTrain.getAutonomousDistanceInTicks())) ||
                (Math.abs(RobotMap.rightMaster12.getEncPosition()) >= Math.abs(Robot.driveTrain.getAutonomousDistanceInTicks()))) {
            return true;
        }
        else {
            return false;
        }
    }

    // Called once after isFinished returns true
    protected void end() {
        Robot.driveTrain.stop();
        System.out.println("AutoDriveStraight::end() -- autonomousCommand drive forward complete!");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
