# 二维码接力计算实验报告

## 1. 原理分析

通过二维码传递的方式，可以比较安全的泄露一个密码。计算方式，是通过两圈的互相扫码，第一次加上这个人的打分，和一个随机生成数，第二圈传递减去这个生成数。这样，一个人如果想知道另一个人的打的分数，必定要知道这个人的上家和下家，通过两轮的差值计算出打分。

### 2.1 第一圈

<img src="https://s2.loli.net/2022/04/25/z3QEarUs6p4H5Om.png" >

### 2.2 第二圈

<img src="D:\software\Microsoft_office\OneDrive\桌面\第二圈.png" alt="第二圈" style="zoom:60%;" />

## 2. 代码实现

### 2.1 传递的消息 (JSON格式)

```json
message: {
   taskId:         // 唯一表示ID
   currentResult:  // 当前结果
   AddCounter:     // 当前有多少个人完成了相加
   owner:		   // 计算的发起者，就是秘书
   round:		   // 当前是第几圈，标识加A或减B
}
```

### 2.2 JSON解析(GSON)

```groovy
// GSON依赖
implementation group: 'com.google.code.gson', name: 'gson', version: '2.8.6'
```

```java
// 简单使用方式
Gson gson = new Gson();
String json = gson.toJson(message);
Message message = gson.fromJson(json, Message.class);
```

### 2.3 如何唯一标识不同的用户

#### 2.3.1 通过设备串号

```java
TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
String DEVICE_ID = tm.getDeviceId();  
```

设备串号是厂商提供的唯一标识手机的ID，有良好的唯一性（过多的手机生产商可能无法保障）。

但是现在Android10以上已经无法获取到了，开发者无法获取到所需要的权限。

<strong>自 Android 10（API 级别 29）起，您的应用必须是[设备或个人资料所有者应用](https://source.android.google.cn/devices/tech/admin/managed-profiles#device_administration)，具有[特殊运营商许可](https://source.android.google.cn/devices/tech/config/uicc)，或具有 `READ_PRIVILEGED_PHONE_STATE` 特权，才能访问不可重置的设备标识符。</strong>

#### 2.3.2 通过时间戳 + 随机后缀

在用户第一次创建应用时候，生成标识其的唯一ID，并写入到内存。

这样做的一个缺点是，用户可以通过卸载应用来获取重新的标识，更好的做法是通过获取电话号码，将其写入到我们的后台，这样在重新安装的时候，首先检索我们的后台。

### 2.4 Android提供的一种快速的键值对存储

一种方式是通过，<b>SQLite数据库</b>。种轻量级嵌入式数据库引擎，它的运算速度非常快，占用资源很少，常用来存储大量复杂的关系数据；

另一种方式通过，<b>sharedpreferences</b>，sharedpreferences是一种基于XML存储的方式。

```java
SharedPreferences sharedPreferences = activity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
//获取操作SharedPreferences实例的编辑器（必须通过此种方式添加数据）
SharedPreferences.Editor editor = sharedPreferences.edit();
editor.putString(key, value);	//添加数据
editor.apply();					// 类似数据库的commit，保证原子性
// 获取数据
String value = sharedPreferences.getString(key, "tag");		// tag是找不到的时候的返回值，可以修改
```

### 2.5 二维码生成功能

```java
private ImageView QRCodeImage;
// QRcodeMaker是在网上找的，封装成了一个类方便调用
Bitmap mBitmap = QRCodeMaker.createQRCodeBitmap(字符串, width, height);
QRCodeImage.setImageBitmap(mBitmap);
```

### 2.6 二维码扫描功能

```java
// 创建IntentIntegrator对象
IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
// 开始扫描
intentIntegrator.initiateScan();

// 重写一个页面的生命周期函数，因为设计到跳转
 @Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // 获取解析结果
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (result != null) {
        // 扫描成功
        if (result.getContents() == null) {
            Toast.makeText(this, "取消扫描", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
        }
    } else {
        Toast.makeText(this, "扫描失败", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
```

```xml
<!-- 调整二维码扫描界面为竖屏 -->
<activity
          android:name="com.journeyapps.barcodescanner.CaptureActivity"
          android:screenOrientation="fullSensor"
          tools:replace="screenOrientation" />
```

## 3. 结果展示

1. 参与人数3：分别输入1234，10086，3783

2. 发起者（秘书）选择发起模式，会生成二维码，如下P1

3. 然后每个人都选择传递模式，轮流扫码

4. 当秘书第二次扫码的时候，其扫码结果将会跳转到P3，也就是结果的显示页面

5. 每个人都会扫上家的码两次，都会两次展示二维码给下家

   <b>通过产生的设备ID唯一标识用户，可以识别出发起者</b>

<img src="C:\Users\PC\Documents\Tencent Files\1966069940\FileRecv\Screenshot_20220425_101031_clqwq.press.qrcodecalc.jpg" alt="Screenshot_20220425_101031_clqwq.press.qrcodecalc" style="zoom:25%; float:left;" /><img src="C:\Users\PC\Documents\Tencent Files\1966069940\FileRecv\Screenshot_20220425_101018_clqwq.press.qrcodecalc.jpg" alt="Screenshot_20220425_101031_clqwq.press.qrcodecalc" style="zoom:25%; float:left;" /><img src="C:\Users\PC\Documents\Tencent Files\1966069940\FileRecv\Screenshot_20220424_231415_clqwq.press.qrcodecalc.jpg" alt="Screenshot_20220424_231415_clqwq.press.qrcodecalc" style="zoom:25%; float:left" />
