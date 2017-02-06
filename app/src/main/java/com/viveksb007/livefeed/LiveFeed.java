package com.viveksb007.livefeed;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viveksb007 on 28/1/17.
 */

@SuppressWarnings("deprecation")
public class LiveFeed extends Activity {

    private Activity activity;
    private final String TAG = "LiveFeed";
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    ServerSocket serverSocket;
    DataOutputStream oStream = null;
    Socket mSocket = null;
    boolean hasConnection = false;

    @BindView(R.id.camera_preview)
    FrameLayout camPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.live_feed);
        ButterKnife.bind(this);

        Log.v("IP_ADDRESS", getIpAddress());

        ServerSocketThread startServer = new ServerSocketThread();
        startServer.start();

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        camPreview.addView(mCameraPreview);

        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setPictureSize(320, 240);
        params.setPreviewFrameRate(30);
        mCamera.setParameters(params);
        mCamera.setPreviewCallback(mPreview);
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
        if (mSocket != null) {
            try {
                oStream.close();
                mSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
    }

    public Camera.PreviewCallback mPreview = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            int format = mCamera.getParameters().getPreviewFormat();
            if (format == ImageFormat.NV21 || format == ImageFormat.NV16 || format == ImageFormat.YUY2) {
                int w = mCamera.getParameters().getPreviewSize().width;
                int h = mCamera.getParameters().getPreviewSize().height;
                YuvImage yuvImage = new YuvImage(bytes, format, w, h, null);
                Rect rect = new Rect(0, 0, w, h);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(rect, 85, outputStream);
                byte[] imageData = outputStream.toByteArray();
                if ((oStream != null) && (hasConnection)) {
                    try {
                        oStream.write(imageData);
                        oStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public class ServerSocketThread extends Thread {
        static final int SOCKET_SERVER_PORT = 8080;

        @Override
        public void run() {

            try {
                serverSocket = new ServerSocket(SOCKET_SERVER_PORT);
                mSocket = serverSocket.accept();
                if (mSocket != null) {
                    oStream = new DataOutputStream(mSocket.getOutputStream());
                    hasConnection = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Got a Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
