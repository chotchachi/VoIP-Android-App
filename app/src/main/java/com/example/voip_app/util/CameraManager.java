package com.example.voip_app.util;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CameraManager {
    private static int WIDTH = 640;
    private static int HEIGHT = 480;
    private static final int frameRate = 30;
    
    private Camera mCamera;
    private SurfaceHolder surfaceHolder;
    private OnFrameCallback onFrameCallback;

    public CameraManager(SurfaceView mSurfaceView) {
        initSurface(mSurfaceView);
    }

    public void setOnFrameCallback(OnFrameCallback onFrameCallback) {
        this.onFrameCallback = onFrameCallback;
    }

    private void initSurface(SurfaceView mSurfaceView) {
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    private void initCamera() {
        if (mCamera == null) {
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(90);
            Camera.Parameters p = mCamera.getParameters();

            Camera.Parameters camParams = mCamera.getParameters();
            List<Camera.Size> sizes = camParams.getSupportedPreviewSizes();
            Collections.sort(sizes, (a, b) -> a.width * a.height - b.width * b.height);

            for (int i = 0; i < sizes.size(); i++) {
                if ((sizes.get(i).width >= WIDTH && sizes.get(i).height >= HEIGHT) || i == sizes.size() - 1) {
                    WIDTH = sizes.get(i).width;
                    HEIGHT = sizes.get(i).height;
                    Log.v("cameraManager", "Changed to supported resolution: " + WIDTH + "x" + HEIGHT);
                    break;
                }
            }

            p.setPreviewSize(WIDTH, HEIGHT);
            p.setPreviewFrameRate(frameRate);
            p.set("rotation", 90);
            mCamera.setParameters(p);
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
            mCamera.setPreviewCallback((data, camera) -> onFrameCallback.onCameraFrame(data));
        }
    }

    public interface OnFrameCallback {
        void onCameraFrame(byte[] data);
    }
}
