package com.cjw.rxjavademo.utils;

import java.security.SecureRandom;

/**
 * 随机类
 * Created by cjw on 16-11-21.
 */
public class RandomUtils {

    private static SecureRandom mRandom;

    private RandomUtils() {
        mRandom = new SecureRandom();
    }

    public static double getDoubleNumber() {
        return mRandom.nextDouble();
    }

    public static int getIntegerNumber() {
        return mRandom.nextInt();
    }

    public static int getIntegerNumber(int num) {
        return mRandom.nextInt(num);
    }
}
