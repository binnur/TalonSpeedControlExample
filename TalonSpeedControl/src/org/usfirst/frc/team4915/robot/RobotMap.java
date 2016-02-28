package org.usfirst.frc.team4915.robot;

import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.CANTalon;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // For example to map the left and right motors, you could define the
    // following variables to use with your drivetrain subsystem.
    // public static int leftMotor = 1;
    // public static int rightMotor = 2;
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static int rangefinderPort = 1;
    // public static int rangefinderModule = 1;
     
    // Define channels for the drive train motors
    private static final int DRIVE_TRAIN_LEFT_MASTER_10 = 10;       // w/ encoder
    private static final int DRIVE_TRAIN_RIGHT_MASTER_12 = 12;      // w/ encoder
    private static final int DRIVE_TRAIN_LEFT_FOLLOWER_11 = 11;
    private static final int DRIVE_TRAIN_RIGHT_FOLLOWER_13 = 13;

    // CAN motor controllers for the driveTrain
    public static CANTalon leftMaster10;
    public static CANTalon rightMaster12;
    public static CANTalon leftFollower11;
    public static CANTalon rightFollower13;
    
    // Drive train QuadEncoder calibration 
    /**
     * Wheel is connected to 48T sprocket, chain driven by 15T sprocket.
     * 15T sprocket is connected to encoder via 3:1 geartrain (1 turn of 15T sprocket is 3 turns of encoder).
     * Encoder is 256 cycles per revolution. Multiply by 4 to get 1024 counts per revolution at Talon.
     * 
     * 1 wheel rotation = (48 / 15) * 3 * 1024 encoder ticks = 9830.4 encoder ticks per wheel revolution
     * 
     * Verified experimentally by rotating a wheel 360 degrees and comparing before and after tick counts.
     */
    public static final int quadTicksPerWheelRev = 9830;
    public static final double wheelDiameterInInches = 14.0;
    public static final double wheelCircumferenceInInches = wheelDiameterInInches * Math.PI;
    public static final double quadTicksPerInch = quadTicksPerWheelRev / wheelCircumferenceInInches; 

    public static void init() {
        if (true) {         // was: the check for the Module Manager
            // STEP 1: instantiate the motor controllers
            leftMaster10 = new CANTalon(DRIVE_TRAIN_LEFT_MASTER_10);
            rightMaster12 = new CANTalon(DRIVE_TRAIN_RIGHT_MASTER_12);
            leftFollower11 = new CANTalon(DRIVE_TRAIN_LEFT_FOLLOWER_11);
            rightFollower13 = new CANTalon(DRIVE_TRAIN_RIGHT_FOLLOWER_13);
                        
            // STEP 2: If needed invert motors -- use the roborio-4915-frc.local for testing
            // Not valid for Speed mode: make sure to invert the motor values given
            // Example: someMotor.setInverted(true) 
            
            // Configure the follower Talons: left & right back motors
            leftFollower11.changeControlMode(CANTalon.TalonControlMode.Follower);
            leftFollower11.set(leftMaster10.getDeviceID());
            
            rightFollower13.changeControlMode(CANTalon.TalonControlMode.Follower);
            rightFollower13.set(rightMaster12.getDeviceID());
                       
            // STEP 3: Setup speed control mode for the master Talons
            leftMaster10.changeControlMode(CANTalon.TalonControlMode.Speed);
            rightMaster12.changeControlMode(CANTalon.TalonControlMode.Speed);
            
            // STEP 4: Indicate the feedback device used for closed-loop
            // For speed mode, indicate the ticks per revolution
            leftMaster10.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            rightMaster12.setFeedbackDevice(FeedbackDevice.QuadEncoder);
            leftMaster10.configEncoderCodesPerRev(quadTicksPerWheelRev);
            rightMaster12.configEncoderCodesPerRev(quadTicksPerWheelRev);            
            
            // STEP 5: Set PID values & closed loop error
            leftMaster10.setPID(0.22, 0, 0); 
            rightMaster12.setPID(0.22, 0, 0); 
            //leftMaster10.setAllowableClosedLoopErr(10);
            //rightMaster12.setAllowableClosedLoopErr(10);
            
            // STEP 6: configure voltage input
            //leftMaster10.configNominalOutputVoltage(+0.0f,  -0.0f);
            //rightMaster12.configNominalOutputVoltage(+0.0f, -0.0f);
            //leftMaster10.configPeakOutputVoltage(+12.0f, -12.0f);
            //rightMaster12.configPeakOutputVoltage(+12.0f, -12.0f);
            
            // STEP 6: Add SmartDashboard controls for testing
            // Add SmartDashboard live window
            LiveWindow.addActuator("Drive Train", "Left Master 10", leftMaster10);
            LiveWindow.addActuator("Drive Train", "Right Master 12", rightMaster12);
                   
            System.out.println("ModuleManager RobotMap Initialized: DriveTrain!");
        }
    }

}
