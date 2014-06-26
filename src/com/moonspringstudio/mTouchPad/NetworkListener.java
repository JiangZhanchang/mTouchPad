package com.moonspringstudio.mTouchPad;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Eric on 14-2-28.
 */
public class NetworkListener implements Runnable {
    private final Queue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();


    public void sendData(byte[] dataToSend) {
        sendQueue.add(dataToSend);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            byte[] dataSend = sendQueue.poll();
            if (dataSend == null)
                continue;

        }
    }
}
