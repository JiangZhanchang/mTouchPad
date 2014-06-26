package com.moonspringstudio.mTouchPad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by Eric on 14-2-24.
 */
public class MainPageActivity extends Activity {
    private Button mTouchAreaButton;
    private Button mLeftButton;
    private Button mRightButton;
    private DataTransferHandler beacon;

    public void MainPageActivity() {

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        Intent args = getIntent();
        String serverAdd = args.getStringExtra(WelcomePageActivity.SERVER_IP);
        int serverPort = args.getIntExtra(WelcomePageActivity.SERVER_PORT, 0);
        ((TextView) findViewById(R.id.pc_Name)).setText(serverAdd + ":" + serverPort);
        InitTransfer(serverAdd, serverPort);

        bindButtonCommand();
    }

    /**
     * Find buttons and set command.
     */
    private void bindButtonCommand() {
        mTouchAreaButton = (Button) findViewById(R.id.btn_TouchAreaButton);
        mTouchAreaButton.setOnClickListener(buttonClicked(true));
        mTouchAreaButton.setOnTouchListener(touchAreaTouched);

        mLeftButton = (Button) findViewById(R.id.btn_LeftButton);
        mLeftButton.setOnClickListener(buttonClicked(true));

        mRightButton = (Button) findViewById(R.id.btn_RightButton);
        mRightButton.setOnClickListener(buttonClicked(false));
    }

    private float touchDownX, touchDownY;

    View.OnTouchListener touchAreaTouched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    return false;

                case MotionEvent.ACTION_MOVE:
                    if (event.getHistorySize() == 0)
                        return false;
                    mouseMove(event.getX() - event.getHistoricalX(0), event.getY() - event.getHistoricalY(0));
                    return true;

                case MotionEvent.ACTION_UP:
                    float offsetX = event.getX() - touchDownX;
                    float offsetY = event.getY() - touchDownY;
                    if (FloatMath.sqrt(offsetX * offsetX + offsetY * offsetY) < 1)
                        return false;
                    else return true;
            }
            return false;
        }
    };

    private void mouseMove(float x, float y) {
        Log.d("Mouse move:", "X:" + x + " Y:" + y);
        byte[] mouseMoveBytes = new byte[9];
        int dataIndex = 0;
        mouseMoveBytes[dataIndex++] = (byte) TransferProtocal.CommandType.MouseMove.getValue();
        byte[] mouseXBytes = float2ByteArray(x);
        byte[] mouseYBytes = float2ByteArray(y);
        System.arraycopy(mouseXBytes, 0, mouseMoveBytes, dataIndex, mouseXBytes.length);
        dataIndex += 4;
        System.arraycopy(mouseYBytes, 0, mouseMoveBytes, dataIndex, mouseXBytes.length);
        beacon.SendCommand(mouseMoveBytes);
    }

    /**
     * Trigger when button is clicked.
     *
     * @param isLeft Is left button or not.
     * @return View.OnClickListener.
     */
    View.OnClickListener buttonClicked(final boolean isLeft) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLeft)
                    beacon.SendCommand(new byte[]{(byte) TransferProtocal.CommandType.MouseLeftButtonClick.getValue()});
                else
                    beacon.SendCommand(new byte[]{(byte) TransferProtocal.CommandType.MouseRightButtonClick.getValue()});
            }
        };
    }


    private void InitTransfer(String serverAdd, int port) {
        try {
            InetAddress serverAddress = InetAddress.getByName(serverAdd);
            beacon = new DataTransferHandler(serverAddress, port);
            beacon.start();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static byte[] float2ByteArray(float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }
}
