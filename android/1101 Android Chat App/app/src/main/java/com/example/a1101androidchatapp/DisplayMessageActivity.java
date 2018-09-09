package com.example.a1101androidchatapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;


public class DisplayMessageActivity extends AppCompatActivity {
    private WebSocket web_socket;
    private String userName;
    private String room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        room = intent.getStringExtra(MainActivity.ROOM);
        userName = intent.getStringExtra(MainActivity.USERNAME);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.outputRoom);
        String roomView = "Room: " + room;
        textView.setText(roomView);

        AsyncHttpClient.getDefaultInstance().websocket("http://10.0.2.2:8080", "my-protocol", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                web_socket = webSocket;
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                webSocket.send("join " + room);
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    String currentText = "";
                    public void onStringAvailable(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            currentText += jsonObject.getString("user") + ": " + jsonObject.getString("message") + "\n";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final Handler handler = new Handler(DisplayMessageActivity.this.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView1 = findViewById(R.id.outputText);
                                textView1.setText(currentText);
                            }
                        });
                    }
                });
            }
        });
    }

    public void sendMessage(View view){
        EditText editTextMsg = (EditText) findViewById(R.id.inputText);
        String message = editTextMsg.getText().toString();
        web_socket.send(userName + " " + message);
    }
}
