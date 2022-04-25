package clqwq.press.qrcodecalc;

import lombok.Data;

/**
 * 传递过程中的消息，会被解析为JSON，生成二维码传输
 */
/*
二维码传递格式规范:
* json: {
*   taskId:         // 唯一表示ID
*   currentResult:  // 当前结果
*   AddCounter:     // 当前有多少个人完成了相加
* }
* */
public class Message {
    private String taskID;
    private int currentResult;
    private int addCount;       // 求平均数的时候会用到的总共人数
    private String owner;       // 通过手机串号唯一标识发起者
    private int round;          // 所属者

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public int getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(int currentResult) {
        this.currentResult = currentResult;
    }

    public int getAddCount() {
        return addCount;
    }

    public void setAddCount(int addCount) {
        this.addCount = addCount;
    }
}
