package clqwq.press.qrcodecalc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import clqwq.press.qrcodecalc.utils.IdUtils;
import clqwq.press.qrcodecalc.utils.QRCodeMaker;


public class MainActivity extends AppCompatActivity {

    private Button send;
    private ImageView QRCodeImage;
    private RadioGroup choice;
    private int status = Params.METHOD_SEND;
    private int curNumber;
    private EditText input;
    private static String Device_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化组件
        QRCodeImage =  findViewById(R.id.QRCode);
        send = findViewById(R.id.send);
        input = findViewById(R.id.curNumber);
        choice = findViewById(R.id.choice);

        addUUID();
        addListener();
    }


    // 初始时，为设备提供唯一的ID
    private void addUUID() {
        // 首先获取一下，如果为空就存储
        SharedPreferences sharedPreferences = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Device_ID = sharedPreferences.getString("DEVICE_ID", "tag");
        if (Device_ID.equals("tag")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Device_ID = "DEVICE" + IdUtils.getIdByTime();
            editor.putString("DEVICE_ID", Device_ID);
            editor.apply();
        }
    }



    // 注册监听
    private void addListener() {
        // 传递按钮
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入数A
                try {
                    curNumber = Integer.parseInt(String.valueOf(input.getText()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"数据格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 根据状态判断执行是扫码传递，还是发起
                if (status == Params.METHOD_INIT) {
                    // 初始发送者，初始化信息
                    // 生成传递二维码，并显示
                    String json = new InitSender(curNumber, MainActivity.this).initSend();
                    System.out.println(json);
                    Bitmap mBitmap = QRCodeMaker.createQRCodeBitmap(json, 800, 800);
                    QRCodeImage.setImageBitmap(mBitmap);
                } else {
                    // 传递者，打开扫码请求
                    // 创建IntentIntegrator对象
                    IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                    // 开始扫描
                    intentIntegrator.initiateScan();
                }
            }
        });
        // 选择框监听状态的改变
        choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkID) {
                if (checkID == R.id.sender) {
                    status = Params.METHOD_SEND;
                } else {
                    status = Params.METHOD_INIT;
                }
                System.out.println(status);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "取消扫描", Toast.LENGTH_SHORT).show();
            } else {
                String json = result.getContents();
                Gson gson = new Gson();
                System.out.println(json);
                Message message = gson.fromJson(json, Message.class);
                // 生成传递二维码，并显示
                Message newMessage = new PassSender(curNumber, message, this).passSend();
                if (newMessage.getRound() == 3) {
                    Toast.makeText(this, "运算结束", Toast.LENGTH_LONG).show();
                    // 表明已经完成，显示结果即可
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("message", gson.toJson(message));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "传递成功，请继续传递", Toast.LENGTH_LONG).show();
                    // 显示传递二维码
                    Bitmap mBitmap = QRCodeMaker.createQRCodeBitmap(gson.toJson(message), 800, 800);
                    QRCodeImage.setImageBitmap(mBitmap);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}