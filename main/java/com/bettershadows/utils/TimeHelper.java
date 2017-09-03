package com.bettershadows.utils;

public class TimeHelper {
	private long lastMS = 0L;
	private long prevMS;
	private boolean stopped;
	
	public TimeHelper(){
		this.prevMS = 0L;
		stopped = true;
	}
	
	public void stop(){
		stopped = true;
		reset();
	}
	
	public void start(){
		stopped = false;
		reset();
	}
	
	public boolean isStopped(){
		return stopped;
	}
	
	public boolean isDelayComplete(long delay) {
		if(System.nanoTime() / 1000000L - lastMS >= delay) {
			return true;
		}
		
		return false;
	}
	
	public long getCurrentMS(){
		return System.nanoTime() / 1000000L;
	}
	
	public void setLastMS(long lastMS) {
		this.lastMS = lastMS;
	}
	
	public void setLastMS() {
		this.lastMS = System.nanoTime() / 1000000L;
	}
	
	public int convertToMS(int d) {
		return 1000 /d;
	}
	
	public boolean hasReached(float f){
		return (float) (getCurrentMS() - this.lastMS) >= f; 
	}

	public void reset(){
		this.lastMS = getCurrentMS();
	}
	
	public boolean delay(float milliSec){
		return (float)(getTime() - this.prevMS) >= milliSec;
	}
	
	private long getTime(){
		return System.nanoTime() / 1000000L;
	}
	
}