package wallFollower;

import java.util.ArrayList;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private final int FILTER_OUT = 20;
	private int distance;
	private int filterControl;
	private ArrayList<Integer> distanceArray = new ArrayList<Integer>();
	private int dynamicDistanceError = 5;
	private int currentDistanceAverage = 0;
	
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
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int pDistance) {
		
		// rudimentary filter - toss out invalid samples corresponding to null signal.

		if (pDistance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (pDistance == 255){
			// true 255, therefore set distance to 255
			distance = pDistance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			distance = pDistance;
		}


		int distanceError = distance - bandCenter;
		
		//Correct distance to wall
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
		else if (distanceError < bandwidth*(-1))
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
	
	
	//This method filters out sporadic jumps in the distance
	//It keeps a running average of the current distance, and filters
	//out values that are much larger than the dynamic error.
	//The dynamic error is in place such that if a bad value is initially
	//read, the system can eventually correct itself
	public void calculateCurrentDistance(int pDistance)
	{

		//If the array is not full, always add the values
		if(distanceArray.size() <3)
		{
			distanceArray.add(pDistance);
		}
		
		//The pDistance value must be within the error of the currentdistanceAverage
		if(pDistance < currentDistanceAverage + dynamicDistanceError && pDistance > currentDistanceAverage - dynamicDistanceError)
		{
			distanceArray.remove(0);
			distanceArray.add(pDistance);
			dynamicDistanceError = 4;
		}
		else
		{
			//Exponentially increase the error so that if a incorrect value is initially read, the system can correct itself
			dynamicDistanceError *=2;
		}
		
		//Reset the average before calculating the new average
		currentDistanceAverage = 0;
		
		//Sum the values in the array
		for( int x : distanceArray)
		{
		currentDistanceAverage +=x;	
		}
		
		distance = Math.round(currentDistanceAverage/3);
		
	}
}
