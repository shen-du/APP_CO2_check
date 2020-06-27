package com.example.CO2_check;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.CO2_check.R;
import com.example.CO2_check.util.WaveUtil;
import com.example.CO2_check.view.WaveShowView;

public class CO2_check extends Activity {
    /*接收发送定义的常量*/
    private String mIp = "192.168.4.1";
    private int mPort = 8080;
    private SendThread sendthread;
    public TextView tv_co2;
    public TextView tv_co2_limit;
    public EditText et_imput_limit;
    public Button bt_set;
    public int CO2,CO2_Limit=1000;
    private String data,CO2_PPM,CO2_Limit_PPM;
    private WaveUtil waveUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_co2_check);
        waveUtil = new WaveUtil();
        WaveShowView waveShowView = findViewById(R.id.waveview);
        waveUtil.showWaveData(waveShowView);

        tv_co2=findViewById(R.id.tv_co2);
        tv_co2_limit=findViewById(R.id.tv_co2_limit);
        et_imput_limit=findViewById(R.id.et_imput_limit);
        bt_set=findViewById(R.id.bt_set);
        CO2_Limit_PPM=CO2_Limit_PPM.format("%dppm",CO2_Limit);
        tv_co2_limit.setText(CO2_Limit_PPM);
        /***************连接*****************/
        sendthread = new SendThread(mIp, mPort, mHandler);
        Thread1();
        new Thread().start();
        /**********************************/
    }
    /*接收线程*******************************************************************************/
    /**
     * 开启socket连接线程
     */
    void Thread1(){
        //        sendthread = new SendThread(mIp, mPort, mHandler);
        new Thread(sendthread).start();//创建一个新线程
    }
    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 0x00) {
                Log.i("mr_收到的数据： ", msg.obj.toString());
                data=msg.obj.toString();
                CO2 = Integer.valueOf(data.substring(data.indexOf("CO2:") + "CO2:".length(), data.indexOf("ppm")));
                CO2_PPM=CO2_PPM.format("%dppm",CO2);
                tv_co2.setText(CO2_PPM);
                waveUtil.co2=CO2;
                if(CO2>CO2_Limit)tv_co2.setBackgroundColor(Color.RED);
                else tv_co2.setBackgroundColor(Color.alpha(0xFFEB3B));

            }
        }
    };

    public void SetLimit(View view) {
        final String limit = et_imput_limit.getText().toString().trim();
        if (TextUtils.isEmpty(limit)) {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            CO2_Limit_PPM=CO2_Limit_PPM.format("%sppm",limit);
            tv_co2_limit.setText(CO2_Limit_PPM);
            CO2_Limit=Integer.valueOf(limit);
        }
    }
}
