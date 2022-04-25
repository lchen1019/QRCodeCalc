package clqwq.press.qrcodecalc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * 传递过程中的人，第一圈和第二圈
 * 第一圈：
 *      A. 每人加上自己的数
 *      B. 所有者修改为第二圈
 * 第二圈：
 *      A. 每个人减去自己的数
 *      B. 所有者标识结束，跳转到结果显示页面
*/
public class PassSender {

    private Message message;
    private String Device_ID;
    private AppCompatActivity activity;
    private int curNumber;

    @SuppressLint("HardwareIds")
    public PassSender(int curNumber, Message message, AppCompatActivity activity ) {
        this.curNumber = curNumber;
        this.message = message;
        this.activity = activity;
        SharedPreferences sharedPreferences = activity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Device_ID = sharedPreferences.getString("DEVICE_ID", "tag");
    }

    // 在第一圈
    private Message roundOne() {
        if (Device_ID.equals(message.getOwner())) {
            message.setRound(message.getRound() + 1);
        } else {
            // 随机产生数B
            int randomB = new Random().nextInt(Params.MOD);
            // 将键值对写入内存中
            SharedPreferences sharedPreferences = activity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
            //获取操作SharedPreferences实例的编辑器（必须通过此种方式添加数据）
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //添加数据
            editor.putString(message.getTaskID(), randomB + "");
            editor.apply();
            // 添加用户A的数字
            message.setCurrentResult(message.getCurrentResult() + (randomB + curNumber));
            // 改变总人数
            message.setAddCount(message.getAddCount() + 1);
        }
        return message;
    }

    // 第二圈
    private Message roundTwo() {
        int numberB = 0;
        //取出数据
        try {
            //获取SharedPreference实例
            SharedPreferences sharedPreferences = activity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
            // 未找到表明，第一轮不在里面，为了增强鲁棒性，就减0
            numberB = Integer.parseInt(sharedPreferences.getString(message.getTaskID(), "0"));
            // 修改result
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        if (Device_ID.equals(message.getOwner())) {
            // 再次扫到发起者，表明已经结束了
            message.setRound(message.getRound() + 1);
        }
        message.setCurrentResult(message.getCurrentResult() - numberB);
        return message;
    }

    public Message passSend() {
        if (message.getRound() == 1) {
            return roundOne();
        } else {
            return roundTwo();
        }
    }
}
