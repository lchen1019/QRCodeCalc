package clqwq.press.qrcodecalc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import clqwq.press.qrcodecalc.Message;
import com.google.gson.Gson;

public class ResultActivity extends AppCompatActivity {

    private TextView average;
    private TextView total;
    private TextView sum;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        // 初始化变量
        average = findViewById(R.id.average);
        total = findViewById(R.id.total);
        sum = findViewById(R.id.sum);
        // 设置不可编辑
        average.setKeyListener(null);
        total.setKeyListener(null);
        sum.setKeyListener(null);
        // 获取结果并显示
        Intent intent = getIntent();
        Gson gson = new Gson();
        Message message = gson.fromJson(intent.getStringExtra("message"), Message.class);
        // 设置变量
        System.out.println(message.getCurrentResult());
        System.out.println(message.getAddCount());
        average.setText("  " + (double)message.getCurrentResult() / message.getAddCount() + "");
        total.setText("  " + message.getAddCount());
        sum.setText("  " + message.getCurrentResult());
    }

}