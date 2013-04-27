package com.techq.PhotoMotion.data;

import java.util.concurrent.atomic.AtomicBoolean;

public class Detector {

    private native boolean Compare(byte[] first, byte[] second, int width, int height, int pixel_threshold, int picture_threshold);
    private native int CompareAndResult(byte[] first, byte[] second, int width, int height, int pixel_threshold);
    static {
        System.loadLibrary("image-processing");
    }

    private static volatile AtomicBoolean isProcessing = new AtomicBoolean(false);
    private int width;
    private int height;
    private int pixThreshold;
    private int picThreshold;
    private byte[] previousData = null;

    public Detector(int width, int height, int pixThreshold, int picThreshold) {
        this.width = width;
        this.height = height;
        this.pixThreshold = pixThreshold;
        this.picThreshold = picThreshold;
    }

    public void UpdateSettings(int width, int height, int pixThreshold, int picThreshold) {
        isProcessing.set(true);

        this.width = width;
        this.height = height;
        this.pixThreshold = pixThreshold;
        this.picThreshold = picThreshold;

        this.previousData = null;

        isProcessing.set(false);
    }

    public void setSize(int width, int height) {
        isProcessing.set(true);
        this.width = width;
        this.height = height;
        isProcessing.set(false);
    }

    public void setPixelThreshold(int pixelThreshold) {
        isProcessing.set(true);
        this.pixThreshold = pixelThreshold;
        isProcessing.set(false);
    }

    public void setPictureThreshold(int pictureThreshold) {
        isProcessing.set(true);
        this.picThreshold = pictureThreshold;
        isProcessing.set(false);
    }

    public boolean Compare(byte[] currentData) {
        if (currentData == null) {
            return false;
        }
        if (previousData == null || previousData.length != currentData.length) {
            previousData = currentData;
            return false;
        }

        boolean result = false;

        if (isProcessing.compareAndSet(false, true)) {
            try {
                if (Compare(previousData, currentData, width, height, pixThreshold, picThreshold)) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isProcessing.set(false);
            }
        }

        previousData = currentData;

        return result;
    }

    public int CompareAndResult(byte[] currentData) {
        if (currentData == null) {
            return 0;
        }
        if (previousData == null || previousData.length != currentData.length) {
            previousData = currentData;
            return 0;
        }

        int result = 0;

        if (isProcessing.compareAndSet(false, true)) {
            try {
                result = CompareAndResult(previousData, currentData, width, height, pixThreshold);
            } catch (Exception e) {
                result = 0;
                e.printStackTrace();
            }
            isProcessing.set(false);
        }

        previousData = currentData;

        return result;
    }
}
