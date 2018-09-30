package com.bettercolors.utils;

public class TimeHelper {
	private long lastMS = 0L;
	private boolean stopped;

	public TimeHelper(){
		stopped = true;
	}

    /**
     * It stops and reset the timer.
     */
	public void stop(){
		stopped = true;
		reset();
	}

    /**
     * It starts and reset the timer.
     */
	public void start(){
		stopped = false;
		reset();
	}

    public void reset(){
	    this.lastMS = getCurrentMS();
    }

	public boolean isStopped(){
		return stopped;
	}

	/**
	 * @param delay the delay in milliseconds.
	 * @return true if the delay has been reached and the timer is not stopped.
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
}