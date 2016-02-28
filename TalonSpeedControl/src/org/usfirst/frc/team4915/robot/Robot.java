
package org.usfirst.frc.team4915.robot;

import org.usfirst.frc.team4915.robot.commands.AutoDriveStraight;
import edu.wpi.first.wpilibj.Preferences;
import org.usfirst.frc.team4915.robot.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {    
	public static OI oi;
    public static DriveTrain driveTrain;
    
    Preferences prefs;
	
	int _loops = 0;
		
    Command autonomousCommand;
    SendableChooser chooser;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        RobotMap.init();                // Step 1. initialize robot parts & pieces
        driveTrain = new DriveTrain();  // Step 2. initialize subsystems      
		oi = new OI();	                // Step 3. initialize OI	
		
		// Read preferences for driveTrain functions
	    prefs = Preferences.getInstance();
	    prefs.putDouble("MaxSpeed", driveTrain.getMaxOutput());
	    prefs.putBoolean("PercentVbus", driveTrain.isPercentVbus());
	    prefs.putDouble("Autonomous Speed",  driveTrain.getAutonomousOutput());
	    prefs.putDouble("Autonomous Distance", driveTrain.getAutonomousDistance());
	    
	    // autonomous selection
	    chooser = new SendableChooser();
	    chooser.addDefault("Default Auto", new AutoDriveStraight());
	    SmartDashboard.putData("Auto chooser", chooser);
     }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {
        // TODO: FIX defect: autonomous correctly runs every other autonomous cycle
        autonomousCommand = (Command) chooser.getSelected();
        // Note: In autonomous, we only use Speed mode 
        System.out.println("--------------Robot::autonomousInit() Setting autoMaxOutput & autoDistance via preferences----------");
        driveTrain.setAutonomousOutput(prefs.getDouble("Autonomous Speed",  driveTrain.getAutonomousOutput()));
        driveTrain.setAutonomousDistance(prefs.getDouble("Autonomous Distance", driveTrain.getAutonomousDistance()));
            	
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        
        if(++_loops >= 10 || _loops > 9) {
            _loops = 0;
            driveTrain.smartDashboardDebugDisplay();          
        }
    }

    public void teleopInit() { 
        System.out.println("Setting maxOutput via preferences----");
        driveTrain.setPercentVbus(prefs.getBoolean("PercentVbus", driveTrain.isPercentVbus()));
        driveTrain.setMaxOutput(prefs.getDouble("MaxSpeed", driveTrain.getMaxOutput()));
    }
    
    /**
     * This function is called periodically during operator control
     */
   public void teleopPeriodic() {
       
        // Makes sure that the autonomous stops running when teleop starts
        if (autonomousCommand != null) autonomousCommand.cancel();

        Scheduler.getInstance().run();
             
        if(++_loops >= 10 || _loops > 9) {
            _loops = 0;
            driveTrain.smartDashboardDebugDisplay();          
        }
    }
   
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
