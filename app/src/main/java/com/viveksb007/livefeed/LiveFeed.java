package com.viveksb007.livefeed;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

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

    private final String TAG = "LiveFeed";
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    ServerSocket serverSocket;
    byte[] imageData;

    @BindView(R.id.camera_preview)
    FrameLayout camPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mCamera.release();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            // bytes is image data that needs to transfer to desktop
            imageData = bytes;
        }
    };

    public class ServerSocketThread extends Thread {
        static final int SOCKET_SERVER_PORT = 8080;

        @Override
        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SOCKET_SERVER_PORT);
                while (true) {
                    socket = serverSocket.accept();
                    SendFrame sendFrame = new SendFrame(socket);
                    sendFrame.start();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class SendFrame extends Thread {
        Socket socket;

        SendFrame(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                mCamera.takePicture(null, mPicture, null);
                outputStream.writeObject(imageData);
                outputStream.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
