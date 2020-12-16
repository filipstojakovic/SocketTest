package com.example.sockettest;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity
{
    public static final String URL = "https://www.blbustracker.com/";
    private Socket mSocket;

    {
        try
        {
            IO.Options opt = new IO.Options();
            opt.reconnection = true;
            opt.forceNew = true;
            opt.transports = new String[]{"websocket"};
            Manager manager = new Manager(new URI(URL), opt);
            mSocket = manager.socket("/locations"); // hostname

        } catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.on(Socket.EVENT_CONNECT, args ->
        {
            System.out.println("connected...");
        }).on("locations", args ->
        {
            System.out.println("locations...");
        }).on("changed", args ->
        {
            JSONObject data = (JSONObject) args[0];
            System.out.println("changed..." + data);
        }).on(Socket.EVENT_CONNECT_ERROR, args ->
        {
            System.out.println("error");

        }).on(Socket.EVENT_DISCONNECT, args ->
        {
            System.out.println("disconnected");
        });

        findViewById(R.id.connect).setOnClickListener(l -> mSocket.connect());
        findViewById(R.id.disconnect).setOnClickListener(l -> mSocket.disconnect());
    }

    @Override
    protected void onDestroy()
    {
        mSocket.disconnect();
        super.onDestroy();
    }
}
