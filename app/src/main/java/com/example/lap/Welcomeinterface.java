package com.example.lap;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Welcomeinterface extends AppCompatActivity implements View.OnClickListener {

    private int reclen = 3;  //跳过倒计时三秒
    private TextView countdown;
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag,flag);
        setContentView(R.layout.activity_welcomeinterface);
        initView();
        timer.schedule(task,1000,1000);//等待时间一秒，停顿时间一秒
        /*
        正常情况不点击跳过
         */
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                //从闪屏界面跳转到首界面
                Intent intent = new Intent(Welcomeinterface.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

        //设置字体（字体没有改变，原因不明，可能字体包？）
        TextView tx = (TextView)findViewById(R.id.welcome);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/BLKCHCRY.TTF");
        tx.setTypeface(custom_font);
    }

    private void initView(){
        countdown = (TextView) findViewById(R.id.countdown);
        countdown.setOnClickListener(this);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reclen--;
                    countdown.setText("跳过" + reclen + "s");
                    if (reclen < 0){
                        timer.cancel();
                        countdown.setVisibility(View.GONE);//倒计时到0隐藏字体
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.countdown:
                Intent intent = new Intent(Welcomeinterface.this,MainActivity.class);
                startActivity(intent);
                finish();
                if (runnable != null){
                    handler.removeCallbacks(runnable);
                }
                break;
            default:
                break;
        }
    }
}
