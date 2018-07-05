package com.btzh.rabbitmq.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.btzh.rabbitmq.MainActivity;
import com.btzh.rabbitmq.R;
import com.btzh.rabbitmq.service.LocService;
import com.btzh.rabbitmq.service.MqService;
import com.btzh.rabbitmq.view.common.AppConstants;


public class LaunchActivity extends BaseActivity {

    EditText userNum;
    EditText passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        initViews();
    }

    void initViews() {
        userNum = findViewById(R.id.userNum);
        passWord = findViewById(R.id.passWord);

    }

    public void loginBtn(View view) {
        String userName = userNum.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Boolean enter = false;
        AppConstants.UserName = userNum.getText().toString();
        if (AppConstants.UserNameZhY.equals(userName)) {
            enter = true;
            AppConstants.AppDeclareName = AppConstants.UserNameZhY;
            AppConstants.SendDeclareName = AppConstants.UserNameWYB;
            //张雅的会打开地图定位
            startService(new Intent(this, LocService.class));
        }
        if (AppConstants.UserNameWYB.equals(userName)) {
            enter = true;
            AppConstants.AppDeclareName = AppConstants.UserNameWYB;
            AppConstants.SendDeclareName = AppConstants.UserNameZhY;

        }
        if (enter) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            startService(new Intent(this, MqService.class));
            finish();
        } else {
            Toast.makeText(this, "请输入正确的用户名", Toast.LENGTH_SHORT).show();
            return;
        }
    }


}
