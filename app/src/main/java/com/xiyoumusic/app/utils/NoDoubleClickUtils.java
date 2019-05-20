package com.xiyoumusic.app.utils;

public class NoDoubleClickUtils {
    private final static int SPACE_TIME = 500;//2次点击的间隔时间，单位ms
    private static long lastClickTime;

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick;
        isClick = currentTime - lastClickTime <= SPACE_TIME;
        lastClickTime = currentTime;
        return isClick;
    }
}
