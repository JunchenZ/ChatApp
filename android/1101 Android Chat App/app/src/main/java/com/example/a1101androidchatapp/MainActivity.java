package com.example.a1101androidchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String ROOM = "com.example.myfirstapp.ROOM";
    public static final String USERNAME = "com.example.myfirstapp.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void sendRoomInfo(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editTextRoom = (EditText) findViewById(R.id.inputRoom);
        String room = editTextRoom.getText().toString();
        intent.putExtra(ROOM, room);

        EditText editTextUser = (EditText) findViewById(R.id.inputUsername);
        String userName = editTextUser.getText().toString();
        intent.putExtra(USERNAME, userName);

        startActivity(intent);
    }
}
