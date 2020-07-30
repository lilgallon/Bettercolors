/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.nero.bettercolors.engine.utils;

public class TimeHelper {
	private long lastMS = 0L;
	private boolean stopped;

	/**
	 * The timer is stopped by default.
	 */
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

	/**
	 * It resets the timer to 0
	 */
	public void reset(){
		this.lastMS = getCurrentMS();
	}

	/**
	 * @return true whether the timer is stopped or not
	 */
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

	/**
	 * @return current system milliseconds
	 */
	private long getCurrentMS(){
		return System.nanoTime() / 1000000L;
	}
}