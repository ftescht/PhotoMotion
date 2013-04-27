package com.techq.PhotoMotion.data;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public class CameraHelper {

    private Camera camera;

    public CameraHelper() {
        if (camera != null) {
            return;
        }
        try {
            camera = Camera.open();
            Camera.Parameters camParams = camera.getParameters();

            boolean previewSizeGood = false;
            int minPreviewWidth = 0;
            int minPreviewHeight = 0;
            for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
                if (size.width == Preferences.PREVIEW_WIDTH && size.height == Preferences.PREVIEW_HEIGHT) {
                    previewSizeGood = true;
                    break;
                }
                if (minPreviewWidth == 0 || size.width < minPreviewWidth) {
                    minPreviewWidth = size.width;
                    minPreviewHeight = size.height;
                }
            }
            if (!previewSizeGood) {
                Preferences.PREVIEW_WIDTH = minPreviewWidth;
                Preferences.PREVIEW_HEIGHT = minPreviewHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyCamera() {
        if (camera != null) {
            camera.release();
        }
    }

    public void startCamera(Camera.PreviewCallback previewCallback) {
        camera.setPreviewCallback(previewCallback);
        camera.startPreview();
    }

    public void stopCamera() {
        camera.stopPreview();
        camera.setPreviewCallback(null);
    }

    public void takePicture(Camera.PictureCallback callback) {
        camera.stopPreview();
        camera.takePicture(null, null, callback);
    }

    public void updateCamera() {
        Camera.Parameters cameraParams = camera.getParameters();
        cameraParams.setPreviewSize(Preferences.PREVIEW_WIDTH, Preferences.PREVIEW_HEIGHT);
        cameraParams.setPictureSize(Preferences.PICTURE_WIDTH, Preferences.PICTURE_HEIGHT);
        cameraParams.setJpegQuality(Preferences.PICTURE_COMPRESSION);
        cameraParams.setJpegThumbnailQuality(Preferences.PICTURE_COMPRESSION);
        camera.setParameters(cameraParams);
    }

    public void updatePreviewSize(int width, int height) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(width, height);
        camera.setParameters(parameters);
    }

    public boolean createPreviewDisplay(SurfaceHolder holder) {
        boolean result = false;
        try {
            camera.setPreviewDisplay(holder);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
