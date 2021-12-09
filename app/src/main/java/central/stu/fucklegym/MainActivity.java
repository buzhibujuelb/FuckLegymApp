package central.stu.fucklegym;

import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContentInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import fucklegym.top.entropy.*;
class Jump extends Thread{
    private Activity cont;
    public Jump(Activity con){
        this.cont = con;
    }

    @Override
    public void run() {
        EditText username = (EditText)cont.findViewById(R.id.editText_username);
        EditText password = (EditText)cont.findViewById(R.id.editText_password);
        Intent intent = new Intent(cont,FreeRun.class);
        intent.putExtra("username",username.getText().toString());
        intent.putExtra("password",password.getText().toString());
        cont.startActivity(intent);
        cont.finish();
    }
}
class SignJump extends Thread{
    private Activity cont;
    public SignJump(Activity con){
        this.cont = con;
    }
    @Override
    public void run() {
        EditText username = (EditText)cont.findViewById(R.id.editText_username);
        EditText password = (EditText)cont.findViewById(R.id.editText_password);
        Intent intent = new Intent(cont,SignUp.class);
        intent.putExtra("username",username.getText().toString());
        intent.putExtra("password",password.getText().toString());
        cont.startActivity(intent);
        cont.finish();
    }
}
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button but = (Button)findViewById(R.id.button_freeRun);
        but.setOnClickListener(this);
        ((Button)findViewById(R.id.button_signup)).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_freeRun){
            jumpFreeRun();
        }else{
            jumpSignUp();
        }
    }
    private void jumpFreeRun(){
        Jump jmp = new Jump(this);
        jmp.start();
        Button but = (Button)findViewById(R.id.button_freeRun);
        but.setText("Waiting for jumping pages");
        but.setEnabled(false);
    }
    private void jumpSignUp(){
        SignJump jmp = new SignJump(this);
        jmp.start();
        Button but = (Button)findViewById(R.id.button_signup);
        but.setText("Waiting for jumping pages");
        but.setEnabled(false);
    }
}