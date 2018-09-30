package com.bettercolors.utils;

import java.util.Random;

public class MathUtils {

	/**
	 * @param min the minimum value.
	 * @param max the maximum value.
	 * @return a random number between min and max included.
	 */
	public static int random(int min, int max){
        return new Random().nextInt((max - min) + 1) + min;
    }
}
