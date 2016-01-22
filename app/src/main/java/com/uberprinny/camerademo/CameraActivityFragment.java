package com.uberprinny.camerademo;

import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
        } catch (Exception e) {
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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
