package clqwq.press.qrcodecalc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Random;

import clqwq.press.qrcodecalc.utils.IdUtils;

public class InitSender {

    private int curNumber;
    private AppCompatActivity activity;
    
    public InitSender(int numberA, AppCompatActivity activity) {
        curNumber = numberA;
        this.activity = activity;
    }

    // 初始化传递请求
    public String initSend() {
        Message message = new Message();
        // 生成ID
        String ID = "TASK" + IdUtils.getIdByTime();
        message.setTaskID(ID);
        // 随机产生数B
        int randomB = new Random().nextInt(Params.MOD);
        // 将键值对写入内存中
        SharedPreferences sharedPreferences = activity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        //获取操作SharedPreferences实例的编辑器（必须通过此种方式添加数据）
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //添加数据
        editor.putString(ID, randomB + "");
        editor.apply();
        // 添加用户A的数字
        message.setCurrentResult(randomB + curNumber);
        // 添加发起者
        // 获取用户的唯一ID
        String Device_ID = sharedPreferences.getString("DEVICE_ID", "tag");
        message.setOwner(Device_ID);
        // 标识回合
        message.setRound(1);
        message.setAddCount(1);
        // 生成JSON字符串并返回
        Gson gson = new Gson();
        return gson.toJson(message);
    }
    
}
