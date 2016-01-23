package com.uberprinny.camerademo;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraActivityFragment extends Fragment implements SurfaceHolder.Callback {

    public CameraActivityFragment() {
    }

    private ImageView mImageView;

    private Button mShutterButton;

    private Button mGalleryButton;

    private Button mDeleteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        mSurfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mImageView = (ImageView) v.findViewById(R.id.camera_image_view);

        mShutterButton = (Button) v.findViewById(R.id.camera_button_shutter);
        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Add callbacks
                mCamera.takePicture(null, null, null, null);
            }
        });
        mShutterButton.setVisibility(View.VISIBLE);

        mGalleryButton = (Button) v.findViewById(R.id.camera_button_gallery);
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mGalleryButton.setVisibility(View.VISIBLE);

        mDeleteButton = (Button) v.findViewById(R.id.camera_button_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mDeleteButton.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (RuntimeException e) {
        }
    }

    private Camera mCamera;

    private boolean mIsCameraConfigured = false;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private void initPreview(int width, int height) {
        if (mCamera == null || mSurfaceHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {

        }

        if (!mIsCameraConfigured) {
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = getLargestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                mCamera.setParameters(parameters);
                mIsCameraConfigured = true;
            }
        }
    }

    private void startPreview() {
        if (mIsCameraConfigured && mCamera != null) {
            mCamera.startPreview();
        }
    }

    private Camera.Size getLargestPreviewSize(int width, int height, Camera.Parameters parameters) {
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < supportedPreviewSizes.size(); i++) {
            Camera.Size size = supportedPreviewSizes.get(i);
            if (size.width < width && size.height < height) {
                return size;
            }
        }
        return null;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setCameraDisplayOrientation(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initPreview(width, height);
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void onPause() {
        stopPreviewAndRelease();
        super.onPause();
    }

    private void stopPreviewAndRelease() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
