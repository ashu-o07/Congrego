package com.example.hppc.agoravideocalling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {
    Button b; EditText f;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        b=(Button)findViewById(R.id.button);
        f=(EditText)findViewById(R.id.editText);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String room = f.getText().toString();
                Intent i = new Intent(MainActivity.this,call.class);
                i.putExtra("room", room);
                startActivity(i);


            }
        });



    }
}
