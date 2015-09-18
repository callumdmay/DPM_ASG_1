package wallFollower;


import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class TouchSensorPoller extends Thread{

	private SampleProvider touchSampler;
	private UltrasonicController controller;
	private float[] touchData;

	public TouchSensorPoller(SampleProvider pTouchSampler, float[] pTouchData, UltrasonicController pController)
	{
		touchSampler = pTouchSampler;
		touchData  = pTouchData;
		controller = pController;

	}

	public void run()
	{
		while(true)
		{
			touchSampler.fetchSample(touchData, 0);
			int touch = (int)touchData[0];
			if(touch == 1)
			{
				controller.reverse();
			}
		}
	}


}
