package com.moonspringstudio.mTouchPad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WelcomePageActivity extends Activity {
    public static final String SERVER_IP = "server_ip";
    public static final String SERVER_PORT = "server_port";
    // static final private int DefaultPort = 8999;

    private View searchServersButton;
    private ListView listServers;
    List<InetAddress> serverAddress;
    ArrayAdapter<String> arrayServers;

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomepage);

        listServers = (ListView) this.findViewById(R.id.list_ServerList);
        serverAddress = new ArrayList<>();
        arrayServers = new ArrayAdapter<String>(this, R.layout.server_item, R.id.list_Server_PcName);
        listServers.setAdapter(arrayServers);

        searchServersButton = this.findViewById(R.id.btn_SearchServers);
        searchServersButton.setOnClickListener(searchServerListener);
    }

    private FindServer findServer;
    View.OnClickListener searchServerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (findServer != null)
                findServer.CloseSocket();

            findServer = new FindServer(v.getContext());
            findServer.setNewServerFoundEventListener(newServerFoundEventListener);
            findServer.start();
            serverAddress.clear();
            arrayServers.clear();
        }
    };

    FindServer.NewServerFoundEventListener newServerFoundEventListener = new FindServer.NewServerFoundEventListener() {
        @Override
        public void NewServerFound(FindServer.NewServerFoundEvent event) {
            final InetAddress serverAdar = event.getServerAddress();
            final int severPort = event.getPort();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverAddress.add(serverAdar);
                    arrayServers.add(serverAdar.getHostAddress().toString() + ":" + severPort);
                }
            });

        }
    };


    public void btn_ConnectClicked(View view) {
        TextView pcAddressText = (TextView) findViewById(R.id.list_Server_PcName);
        String[] serverAddr = pcAddressText.getText().toString().split(":");
        if (serverAddr == null || serverAddr.length != 2)
            return;

        findServer.CloseSocket();
        connectToServer(serverAddr[0], Integer.parseInt(serverAddr[1]));
    }

    public void connectToServer(String serverIp, int serverPort) {
        Intent mainActivityIntent = new Intent(this, MainPageActivity.class);
        mainActivityIntent.putExtra(SERVER_IP, serverIp);
        mainActivityIntent.putExtra(SERVER_PORT, serverPort);
        startActivity(mainActivityIntent);
    }

}
