package com.viveksb007.livefeed;

import android.app.Activity;
import android.graphics.ImageFormat;
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

    @BindView(R.id.camera_preview)
    FrameLayout camPreview;
    @BindView(R.id.btn_trigger)
    Button trigger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.live_feed);
        ButterKnife.bind(this);

        Log.v("IP_ADDRESS", getIpAddress());

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        camPreview.addView(mCameraPreview);

        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setPictureSize(320, 240);
        mCamera.setParameters(params);

        ServerSocketThread temp = new ServerSocketThread();
        temp.start();
        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendFrame sendFrame = new SendFrame(mSocket);
                sendFrame.start();
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
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCamera.stopPreview();
        mCamera.release();
    }

    public Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            // bytes is image data that needs to transfer to desktop
            mCamera.stopPreview();
            mCamera.startPreview();

            if (bytes != null) {
                Log.v(TAG, "Got Picture");
                try {
                    oStream.write(bytes);
                    oStream.flush();
                    oStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                Log.v(TAG, "Got No picture");
        }
    };


    Socket mSocket = null;

    public class ServerSocketThread extends Thread {
        static final int SOCKET_SERVER_PORT = 8080;

        @Override
        public void run() {

            try {
                serverSocket = new ServerSocket(SOCKET_SERVER_PORT);

            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    DataOutputStream oStream;

    public class SendFrame extends Thread {

        SendFrame(Socket socket) {
        }

        @Override
        public void run() {
            try {
                mSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Got a Connection", Toast.LENGTH_SHORT).show();
                }
            });
            try {
                oStream = new DataOutputStream(mSocket.getOutputStream());
                mCamera.takePicture(null, null, mPicture);

            } catch (IOException e) {
                e.printStackTrace();
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
