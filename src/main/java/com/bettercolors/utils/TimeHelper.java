package com.bettercolors.utils;

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

	/**
	 * @param delay (ms)
	 * @return
	 */
	public boolean isDelayComplete(long delay) {
	    if(stopped) return false;
		if(System.nanoTime() / 1000000L - lastMS >= delay) {
			return true;
		}else if(delay <= 0){
			return true;
		}
		return false;
	}

	public long getCurrentMS(){
		return System.nanoTime() / 1000000L;
	}

	public void reset(){
		this.lastMS = getCurrentMS();
	}
	
}