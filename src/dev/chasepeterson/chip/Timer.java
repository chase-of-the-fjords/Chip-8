package dev.chasepeterson.chip;

public class Timer {
	
	public long clockRate;
	
	public long completedIterations;
	
	public long startTime;
	
	public short target;
	
	public boolean active = false;
	
	public Timer(int clockRate) {
		this.clockRate = clockRate;
		this.completedIterations = 0;
	}
	
	public void start(short time) {
		this.target = time;
		this.startTime = System.nanoTime();
		this.completedIterations = 0;
		this.active = true;
	}
	
	public short getValue() {
		return (short) (this.target - this.completedIterations);
	}
	
	public void stop() {
		this.active = false;
	}
	
	public boolean iterate() {
		if (completedIterations == target) {
			this.stop();
			return false;
		}
		
		long timeSinceStart = System.nanoTime() - startTime;
		long expectedIterations = timeSinceStart * clockRate / 1000000000;
		
		if (expectedIterations > completedIterations) {
			completedIterations++;
			return true;
		}
		
		return false;
	}
	
}
