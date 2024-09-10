package dev.chasepeterson.chip;

import java.awt.Toolkit;

public class Clock {
	
	public long clockRate;
	
	public long completedIterations;
	
	public long startTime;
	
	public Clock(int clockRate) {
		this.clockRate = clockRate;
		this.completedIterations = 0;
	}
	
	public void start() {
		this.startTime = System.nanoTime();
		this.completedIterations = 0;
	}
	
	public boolean iterate() {
		long timeSinceStart = System.nanoTime() - startTime;
		long expectedIterations = timeSinceStart * clockRate / 1000000000;
		
		if (expectedIterations > completedIterations) {
			completedIterations++;
			return true;
		}
		
		return false;
	}
	
}
