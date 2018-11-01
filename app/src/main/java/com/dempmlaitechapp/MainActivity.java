package com.dempmlaitechapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)findViewById(R.id.ed_username)).getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                }else  if(!((EditText)findViewById(R.id.ed_username)).getText().toString().equalsIgnoreCase("Metaorigins")){
                    Toast.makeText(MainActivity.this, "Username is not match.", Toast.LENGTH_SHORT).show();
                } else if (((EditText)findViewById(R.id.ed_password)).getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }else if (!((EditText)findViewById(R.id.ed_password)).getText().toString().equalsIgnoreCase("Medlife")){
                    Toast.makeText(MainActivity.this, "Password is not match.", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                }
            }
        });
    }
}
