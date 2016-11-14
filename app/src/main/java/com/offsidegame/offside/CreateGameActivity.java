package com.offsidegame.offside;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CreateGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
    }

    void ShowToast(String message){
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);

    }
}
