package com.moonspringstudio.mTouchPad;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by Eric on 14-6-5.
 */
public class FindServer extends Thread implements EventListener {
    private static Context CONTEXT;

    public FindServer(Context context) {
        CONTEXT = context;
    }

    public int DefaultPort = 4321;
    DatagramSocket beacon;

    public void run() {
        // Broadcast ping to look for servers.
        try {
            beacon = new DatagramSocket(null);
            beacon.setBroadcast(true);
            beacon.setSoTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        //InetAddress broadcast = null;
        try {
            // broadcast = getBroadcastAddress();
            byte[] buffer = new byte[]{(byte) 0xEC, (byte) 0xEF, (byte) 0xEE};
            beacon.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), DefaultPort));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //wait for reply.
        byte[] replyData = new byte[3];
        DatagramPacket ack = new DatagramPacket(replyData, replyData.length);
        try {
            beacon.receive(ack);
            if (replyData[0] == TransferProtocal.DataHeader[0] && replyData[1] == TransferProtocal.DataHeader[1]) {
                newServerFoundEventListener.NewServerFound(new NewServerFoundEvent(this, ack.getAddress(), ack.getPort()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CloseSocket() {

        beacon.close();
    }

    private NewServerFoundEventListener newServerFoundEventListener;

    public void setNewServerFoundEventListener(NewServerFoundEventListener eventListener) {
        newServerFoundEventListener = eventListener;
    }

    // Get broadcast address for LAN.
    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) CONTEXT.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


    class NewServerFoundEvent extends EventObject {
        private InetAddress serverAddress;
        private int port;

        public InetAddress getServerAddress() {
            return serverAddress;
        }

        public int getPort() {
            return port;
        }

        public NewServerFoundEvent(Object source, InetAddress addr, int port) {
            super(source);
            serverAddress = addr;
            this.port = port;
        }
    }

    interface NewServerFoundEventListener extends EventListener {
        public void NewServerFound(NewServerFoundEvent event);
    }


}
