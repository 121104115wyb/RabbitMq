package com.btzh.rabbitmq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.btzh.rabbitmq.service.MqService;
import com.btzh.rabbitmq.view.common.AppConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText send_text;
    Button sendBtn;
    TextView receive_text;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MqService.class);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    void initViews() {
        send_text = findViewById(R.id.send_text);
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        receive_text = findViewById(R.id.receive_text);

    }


    public void sendMes() {
        String sendText = send_text.getText().toString();
        if (TextUtils.isEmpty(sendText)) {
            Toast.makeText(this, "内容不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        MqService.sendMqMessage(sendText, AppConstants.SendDeclareName);
        send_text.setText("");

    }

    @Override
    public void onClick(View v) {
        sendMes();
    }
}
