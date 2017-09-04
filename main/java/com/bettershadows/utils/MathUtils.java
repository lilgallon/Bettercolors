package com.bettershadows.utils;

import java.util.Random;

public class MathUtils {
	
	public static float wrapAngleTo180_float(float p_76142_0_)
	{
		p_76142_0_ %= 360.0F;

		if (p_76142_0_ >= 180.0F)
		{
			p_76142_0_ -= 360.0F;
		}

		if (p_76142_0_ < -180.0F)
		{
			p_76142_0_ += 360.0F;
		}

		return p_76142_0_;
	}
	
	public static int random(int min, int max){
        return new Random().nextInt((max - min) + 1) + min;
    }
}
