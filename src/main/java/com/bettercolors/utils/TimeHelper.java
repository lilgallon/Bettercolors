package com.bettercolors.utils;

public class TimeHelper {
	private long lastMS = 0L;
	private boolean stopped;

	public TimeHelper(){
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

	/**
	 * @param delay (ms)
	 */
	public boolean isDelayComplete(long delay) {
	    if(stopped) return false;
		if(System.nanoTime() / 1000000L - lastMS >= delay) {
			return true;
		}else return delay <= 0;
	}

	private long getCurrentMS(){
		return System.nanoTime() / 1000000L;
	}

	public void reset(){
		this.lastMS = getCurrentMS();
	}
	
}