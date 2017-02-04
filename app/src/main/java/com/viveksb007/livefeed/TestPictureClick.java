package com.viveksb007.livefeed;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viveksb007 on 4/2/17.
 */
@SuppressWarnings("deprecation")
public class TestPictureClick extends Activity {

    private final String TAG = "TestPictureClick";
    private Camera mCamera;
    private CameraPreview mCameraPreview;

    @BindView(R.id.camera_preview)
    FrameLayout camPreview;
    @BindView(R.id.btn_trigger)
    Button trigger;

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
        params.setPictureSize(320, 240);
        mCamera.setParameters(params);

        Log.i(TAG,"Jpeg Quality : "+String.valueOf(params.getJpegQuality()));

        List<Camera.Size> sizes = params.getSupportedPictureSizes();

        for(Camera.Size size : sizes){
            Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
        }

        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
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
        mCamera.release();
    }

    public Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            // bytes is image data that needs to transfer to desktop
            if(bytes != null) {
                Log.v(TAG, "Got Picture");
            }else
                Log.v(TAG,"Got No picture");

            mCamera.stopPreview();
            mCamera.startPreview();
        }
    };

}
