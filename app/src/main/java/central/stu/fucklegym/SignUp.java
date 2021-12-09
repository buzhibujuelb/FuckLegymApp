package central.stu.fucklegym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.HashMap;

import fucklegym.top.entropy.User;
class LoadActivitiresThread extends Thread{
    private User user;
    private Handler handler;
    public LoadActivitiresThread(User user, Handler handler){
        this.user = user;
        this.handler = handler;
    }
    @Override
    public void run() {
        try {
            user.login();

            HashMap<String,String> acts = (HashMap<String, String>) user.getTodayActivities();
            Message msg = handler.obtainMessage();
            msg.what = SignUp.GETACTIVITIES;
            msg.obj = acts;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class SignThread extends Thread{
    private User user;
    private Handler handler;
    private String name;
    public SignThread(User user,Handler handler,String nme){
        this.user = user;
        this.handler = handler;
        this.name = nme;
    }
    @Override
    public void run() {
        try {
            if(user.getTodayActivities().containsKey(name)){
                user.sign(name);
                handler.sendEmptyMessage(SignUp.UPLOADSUCCESS);
            }else handler.sendEmptyMessage(SignUp.ACTIVITYDOESNOTEXIST);

        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(SignUp.UPLOADFAIL);
        }
    }
}

public class SignUp extends AppCompatActivity {
    public static final int GETACTIVITIES = 0;
    public static final int UPLOADSUCCESS = 1;
    public static final int UPLOADFAIL = 2;
    public static final int ACTIVITYDOESNOTEXIST = 3;
    private TextView textView;
    private User user;
    private EditText editText;
    private HashMap<String,String> activities;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = new User(bundle.getString("username"),bundle.getString("password"));

        this.textView = (TextView)findViewById(R.id.textView_allActivities);
        this.editText = (EditText)findViewById(R.id.editText_activityName);
        handler = new Handler(){
            public void handleMessage(Message msg) {
                // 处理消息
                super.handleMessage(msg);
                switch (msg.what) {
                    case GETACTIVITIES:
                        HashMap<String ,String> acts = (HashMap<String ,String >)msg.obj;
                        StringBuffer buf = new StringBuffer();
                        for(String str:acts.keySet()){
                            buf.append(str+"\n");
                        }
                        textView.setText(buf.toString());
                        break;
                    case UPLOADSUCCESS:
                        Toast.makeText(SignUp.this,"打卡成功",Toast.LENGTH_LONG).show();
                        break;
                    case UPLOADFAIL:
                        Toast.makeText(SignUp.this,"打卡失败",Toast.LENGTH_LONG).show();
                        break;
                    case ACTIVITYDOESNOTEXIST:
                        Toast.makeText(SignUp.this,"活动不存在，请检查名称是否写错",Toast.LENGTH_LONG);
                        break;
                }
            }
        };
        new LoadActivitiresThread(user,handler).start();
        ((Button)findViewById(R.id.button_uploadSign)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new SignThread(user,handler,editText.getText().toString()).start();
            }
        });
    }
}
