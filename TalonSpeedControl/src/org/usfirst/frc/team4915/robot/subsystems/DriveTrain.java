package org.usfirst.frc.team4915.robot.subsystems;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

// import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team4915.robot.Robot;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.CANTalon;

import java.util.Arrays;
import java.util.List;

import org.usfirst.frc.team4915.robot.commands.ArcadeDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4915.robot.RobotMap;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
    public final static double DEFAULT_SPEED_MAX_OUTPUT = 100.0;         // 100.0 == ~13 ft/sec interpolated from observations
    public final static double DEFAULT_AUTOSPEED_MAX_OUTPUT = 25.0;      // ~3-4 ft/sec
    public final static double MAXIMUM_SPEED_MAX_OUTPUT = 150.0;         // 150.0 == ~20 ft/sec interpolated from observations
    public final static double DEFAULT_PERCENTVBUS_MAX_OUTPUT = 1.0;     // ALWAYS use 1.0 for PercentVBUS mode
            
    public Accelerometer accel;

    private StringBuilder _sb = new StringBuilder();
    private static RobotDrive _robotDrive;
    private double _maxSpeed=0;
    private CANTalon.TalonControlMode _controlMode;
    private double _targetSpeed;
    private boolean _isPercentVbus = false;     // default control mode is Speed
    
    private double _autonomousOutput = DEFAULT_AUTOSPEED_MAX_OUTPUT;
    private double _autonomousDistanceInFeet = 5.0;
    private int _autonomousDistanceInTicks;
        
    // Note: since the motors are grouped together, we only need to control the Masters
    public static List<CANTalon> motors =
            Arrays.asList(RobotMap.leftMaster10, RobotMap.rightMaster12);

    public DriveTrain() {
        _robotDrive = new RobotDrive(RobotMap.leftMaster10, RobotMap.rightMaster12);
        
        accel = new BuiltInAccelerometer(); 
        accel = new BuiltInAccelerometer(Accelerometer.Range.k4G); 
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new ArcadeDrive(CANTalon.TalonControlMode.Speed));
        setDefaultCommand(new ArcadeDrive());
    }
    
    /*
     * Use RobotDrive.arcadeDrive to move the robot using joystick values
     */
    public void arcadeDrive(double driveYstick, double driveXstick) {
        _targetSpeed = driveYstick;
        _robotDrive.arcadeDrive(driveYstick, driveXstick);
    }
    
    /*
     * Zero's encoder values: masters are the only motors w/ encoders!
     */
    public void resetEncoders() {
        RobotMap.leftMaster10.setEncPosition(0);
        RobotMap.rightMaster12.setEncPosition(0);
    }
 
    /* 
     * Methods to get/set autonomous speed and distance values
     */
    public double getAutonomousOutput() {
        return _autonomousOutput;
    }

    public void setAutonomousOutput(double output) {
        _autonomousOutput = output;
        setMaxOutput(output);
    }
    
    public double getAutonomousDistance() {
        return _autonomousDistanceInFeet;
    }
    
    public void setAutonomousDistance(double feet) {
        _autonomousDistanceInFeet = feet;
        _autonomousDistanceInTicks = _feetToTicks(feet);
    }
    
    public int getAutonomousDistanceInTicks() {
        return _autonomousDistanceInTicks;
    }
    
    private int _inchesToTicks(double inches) {
        return (int)(inches * RobotMap.quadTicksPerInch); 
    }
    
    private int _feetToTicks(double feet) {
        return (int)(_inchesToTicks(feet * 12.0)); 
    }
    
    /* Useful utility functions?
    private double _ticksToInches(int ticks) {
        return (double)((double)ticks / RobotMap.quadTicksPerInch);
    }
    
    private double _ticksToFeet(int ticks) {
        return (double)(_ticksToInches(ticks)/12.0);
    }
    */
    
    /*
     * Methods to get/set maximum top speed for our robot 
     */
    public double getMaxOutput() {
        if (_isPercentVbus) {    /* ignore changing max speed for PercentVbus */
            return DEFAULT_PERCENTVBUS_MAX_OUTPUT;
        }
        else {
            if (_maxSpeed == 0) {   /* not initialized, yet */
                return DEFAULT_SPEED_MAX_OUTPUT;
            }
            else {
                return _maxSpeed;
            }
        }
    }
    
    public void setMaxOutput(double maxOutput) {
        if (_isPercentVbus) {    /* ignore changing max speed for PercentVbus */
            _maxSpeed = DEFAULT_PERCENTVBUS_MAX_OUTPUT;
        }
        else {
            if (maxOutput > MAXIMUM_SPEED_MAX_OUTPUT) {
                _maxSpeed = MAXIMUM_SPEED_MAX_OUTPUT;
            }
            else {
                _maxSpeed = maxOutput;
            }
        }

        System.out.println("Setting maxSpeed to: " + _maxSpeed);
        _robotDrive.setMaxOutput(_maxSpeed);
    }
    
    /*
     * Methods to change the control mode of the talons
     */
    public CANTalon.TalonControlMode getControlMode() {
        return _controlMode;
    }
    
    public void setControlMode(CANTalon.TalonControlMode mode) {
        _changeControlMode(mode);
     }
    
    /*
     * Dynamically change control mode of the Talons
     */
    private void _changeControlMode(CANTalon.TalonControlMode newMode) {
        if (newMode == CANTalon.TalonControlMode.PercentVbus) { /* percent voltage mode */
            RobotMap.leftMaster10.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
            RobotMap.rightMaster12.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        }
        else if (newMode == CANTalon.TalonControlMode.Speed) {  /* speed mode */
            RobotMap.leftMaster10.changeControlMode(CANTalon.TalonControlMode.Speed);
            RobotMap.rightMaster12.changeControlMode(CANTalon.TalonControlMode.Speed);
        }
        else {
            System.out.println("DriveTrain::_changeControlMode(): WARNING!! Unknown CANTalon.TalonControlMode is specified!");
        }
    }
    
    /*
     * Used by AutoDriveStraight
     */
    public void driveStraight(double speed) {
        _robotDrive.arcadeDrive(speed, 0);
    }

    /*
     * Demonstration of driving the motors using set() command (currently not used)
     */
    public void talonDrive() {
        double targetSpeed = Robot.oi.getJoystickDrive().getAxis(AxisType.kY) * 100;
        RobotMap.leftMaster10.set(targetSpeed);
        RobotMap.rightMaster12.set(targetSpeed);   
    }
    
    /*
     * Stop motors
     */
    public void stop() {
        _robotDrive.stopMotor();
    }
    
    public boolean isPercentVbus() {
        return _isPercentVbus;
    }
    
    public void setPercentVbus(boolean value) {
        _isPercentVbus = value;
    }
    
    /*
     * Various debug values relating the drive train motors 
     */
    public void smartDashboardDebugDisplay() {
        double motorOutput; 
        
        for (int i=0; i < motors.size(); i++) {
            motorOutput  = motors.get(i).getOutputVoltage() / motors.get(i).getBusVoltage();
               
            _sb.setLength(0);
            _sb.append(String.format("Master[%d] getControlMode: ", motors.get(i).getDeviceID()));
            _sb.append(motors.get(i).getControlMode());
            //System.out.println("Master ControlMode: " + _sb);
            SmartDashboard.putString("Master ControlMode: ", _sb.toString());  
        
            _sb.setLength(0);
            _sb.append(String.format("Master[%d] motorOutput: ", motors.get(i).getDeviceID()));
            _sb.append(String.format("%.2f", motorOutput));
            _sb.append("\tmaxSpeed: ");
            _sb.append(_maxSpeed);
            _sb.append("\ttargetSpeed: ");
            _sb.append(_targetSpeed);
            _sb.append("\tclosedLoopError: ");
            _sb.append(motors.get(i).getClosedLoopError());
            _sb.append("\tgetSpeed(): ");
            _sb.append(String.format("%.2f", motors.get(i).getSpeed())); 
            _sb.append("\tgetSetPoint(): ");
            _sb.append(String.format("%.2f", motors.get(i).getSetpoint()));
            _sb.append("\tget(): ");
            _sb.append(String.format("%.2f", motors.get(i).get()));
            //System.out.println("Speed Info: " + _sb);
            SmartDashboard.putString("Speed Info: ", _sb.toString());  
        }
        _sb.setLength(0);
        _sb.append("LeftFollower11 getControlMode: ");
        _sb.append(RobotMap.leftFollower11.getControlMode());
        _sb.append(" | RightFollower13 getControlMode: ");
        _sb.append(RobotMap.rightFollower13.getControlMode());
        SmartDashboard.putString("Followers ControlMode Info: ", _sb.toString());      
        
        _sb.setLength(0);
        _sb.append("Accel.getX: ");
        _sb.append(String.format("%.3f", accel.getX()));
        _sb.append(" | Accel.getY: ");
        _sb.append(String.format("%.3f", accel.getY()));
        _sb.append(" | Accel.getZ: ");
        _sb.append(String.format("%.3f", accel.getZ()));
        SmartDashboard.putString("Builtin Accelerometer: ", _sb.toString());      
        
    }
}

