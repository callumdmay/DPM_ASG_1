package wallFollower;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private final int 
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int pDistance) {
		distance = pDistance;
		
		int distanceError = distance - bandCenter;
		
		if(Math.abs(distanceError) <= bandwidth)
		{
			leftMotor.setSpeed(motorHigh);				// Start robot moving forward
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();
		}
		//Too far from wall
		else if (distanceError > bandwidth)
		{
			leftMotor.setSpeed(motorLow);				// Start robot moving forward
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();
		}
		//Too close to wall
		else if (distanceError < Math.negateExact(bandwidth))
		{
			leftMotor.setSpeed(motorHigh);				// Start robot moving forward
			rightMotor.setSpeed(motorLow);
			leftMotor.forward();
			rightMotor.forward();
		}
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
