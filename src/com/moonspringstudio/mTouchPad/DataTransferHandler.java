package com.moonspringstudio.mTouchPad;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Eric on 14-2-28.
 */
public class DataTransferHandler extends Thread {
    private final InetAddress serverAddress;
    private int serverPort;
    private DatagramSocket beacon;
    private byte[] dataBuffer;

    public DataTransferHandler(InetAddress serverAddress, int port) throws SocketException {
        this.serverAddress = serverAddress;
        this.serverPort = port;
        final InetAddress addr = serverAddress;
    }

    public void run() {
        try {
            beacon = new DatagramSocket(24632);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (dataBuffer == null)
                continue;
            try {
                beacon.send(new DatagramPacket(dataBuffer, dataBuffer.length, serverAddress, serverPort));
                dataBuffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void SendCommand(byte[] data) {
        SendData(WrapData(data));
    }

    private byte[] WrapData(byte[] logicBytes) {
        int finalBytesLength = logicBytes.length + 2;
        byte[] finalBytes = new byte[finalBytesLength];
        System.arraycopy(TransferProtocal.DataHeader, 0, finalBytes, 0, TransferProtocal.DataHeader.length);
        System.arraycopy(logicBytes, 0, finalBytes, 2, logicBytes.length);
        return finalBytes;
    }

    private void SendData(final byte[] finalBytes) {
        dataBuffer = finalBytes;
    }


}

