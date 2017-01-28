package com.viveksb007.livefeed;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viveksb007 on 28/1/17.
 */

@SuppressWarnings("deprecation")
public class LiveFeed extends Activity {

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    @BindView(R.id.camera_preview)
    FrameLayout camPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.live_feed);
        ButterKnife.bind(this);

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        camPreview.addView(mCameraPreview);

        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(params);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.stopPreview();
    }
}
