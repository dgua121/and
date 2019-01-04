package com.baidu.aip.asrwakeup3.uiasr.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;

import com.baidu.aip.asrwakeup3.core.recog.IStatus;
import com.baidu.aip.asrwakeup3.uiasr.R;
import com.baidu.aip.asrwakeup3.uiasr.gj.Test;
import com.baidu.aip.asrwakeup3.uiasr.params.AllRecogParams;
import com.baidu.aip.asrwakeup3.uiasr.params.CommonRecogParams;
import com.baidu.aip.asrwakeup3.uiasr.params.NluRecogParams;
import com.baidu.aip.asrwakeup3.uiasr.params.OfflineRecogParams;
import com.baidu.aip.asrwakeup3.uiasr.params.OnlineRecogParams;
import com.baidu.aip.asrwakeup3.uiasr.setting.AllSetting;
import com.baidu.aip.asrwakeup3.uiasr.setting.NluSetting;
import com.baidu.aip.asrwakeup3.uiasr.setting.OfflineSetting;
import com.baidu.aip.asrwakeup3.uiasr.setting.OnlineSetting;
import com.baidu.aip.asrwakeup3.uiasr.view.Dgua11;
import com.baidu.aip.asrwakeup3.uiasr.view.DguaPagerView;
import com.baidu.aip.asrwakeup3.uiasr.view.view1.DguaViewBackground1;
import com.baidu.aip.asrwakeup3.uiasr.view.FragAdapter;
import com.baidu.aip.asrwakeup3.uiasr.view.view1.TestFragment;
import com.baidu.aip.asrwakeup3.uiasr.view.view1.TestFragment1;
import com.baidu.aip.asrwakeup3.uiasr.view.view1.TestFragment2;
import com.baidu.aip.asrwakeup3.uiasr.view.view1.Top;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Map;

/**
 * 识别的基类Activity。 ActivityCommon定义了通用的UI部分
 * 封装了识别的大部分逻辑，包括MyRecognizer的初始化，资源释放
 * <p>
 * <p>
 * 集成流程代码，只需要一句： myRecognizer.start(params);具体示例代码参见startRough()
 * =》.实例化 myRecognizer   new MyRecognizer(this, listener);
 * =》 实例化 listener  new MessageStatusRecogListener(null);
 * </p>
 * 集成文档： http://ai.baidu.com/docs#/ASR-Android-SDK/top 集成指南一节
 * demo目录下doc_integration_DOCUMENT
 * ASR-INTEGRATION-helloworld  ASR集成指南-集成到helloworld中 对应 ActivityMiniRecog
 * ASR-INTEGRATION-TTS-DEMO ASR集成指南-集成到合成DEMO中 对应 ActivityUiRecog
 * <p>
 * 大致流程为
 * 1. 实例化MyRecognizer ,调用release方法前不可以实例化第二个。参数中需要开发者自行填写语音识别事件的回调类，实现开发者自身的业务逻辑
 * 2. 如果使用离线命令词功能，需要调用loadOfflineEngine。在线功能不需要。
 * 3. 根据识别的参数文档，或者demo中测试出的参数，组成json格式的字符串。调用 start 方法
 * 4. 在合适的时候，调用release释放资源。
 * <p>
 */

public abstract class ActivityUiRecog extends ActivityCommon implements IStatus {

    public static Mymessage mm;
    public static InterstitialAd mInterstitialAd;
    private AdView mAdView;
    /*
     * Api的参数类，仅仅用于生成调用START的json字符串，本身与SDK的调用无关
     */
    private final CommonRecogParams apiParams;

    public static Class settingActivityClass;

    public class Mymessage extends Handler {

        public Mymessage(ActivityUiRecog mainActivity, Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == 888) {
                pager.setCurrentItem(0);
            }
            if (message.what == 355) {
                super.handleMessage(message);
            } else {
                super.handleMessage(message);
            }
        }
    }

    /**
     * 控制UI按钮的状态
     */
    public int status;

    /**
     * 日志使用
     */
    private static final String TAG = "ActivityUiRecog";


    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    //protected abstract void start();


    /**
     * 开始录音后，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用。
     */
    //protected  void stop(){
    //    session="";
    //  }

    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    protected abstract void cancel();

    public static boolean running = false;

    public ActivityUiRecog(int textId) {
        super(textId);
        String className = getClass().getSimpleName();
        if (className.equals("ActivityOnlineRecog") || className.equals("ActivityUiDialog")) {
            settingActivityClass = OnlineSetting.class;
            apiParams = new OnlineRecogParams();
        } else if (className.equals("ActivityOfflineRecog")) {
            settingActivityClass = OfflineSetting.class;
            apiParams = new OfflineRecogParams();
        } else if (className.equals("ActivityNlu")) {
            settingActivityClass = NluSetting.class;
            apiParams = new NluRecogParams();
        } else if (className.equals("ActivityAllRecog")) {
            settingActivityClass = AllSetting.class;
            apiParams = new AllRecogParams();
        } else {
            throw new RuntimeException("PLEASE DO NOT RENAME DEMO ACTIVITY, current name:" + className);
        }
    }

    protected Map<String, Object> fetchParams() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        Map<String, Object> params = apiParams.fetch(sp);
        //  集成时不需要上面的代码，只需要params参数。
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiParams.initSamplePath(this);

        t1 = (Top) findViewById(R.id.activitymainTop111);
        t1.setText("新消息", "");
        t1.setNU(0);
        bGround1 = (DguaViewBackground1) findViewById(R.id.activitymainDguaViewBackground111);
        bGround1.mydgua1_1 = new Dgua11(this, R.drawable.lx1, R.drawable.lx2) {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    ActivityUiRecog.t1.setText("配置页", "设置");
                    ActivityUiRecog.bGround1.setViewColor(1);
                    pager.setCurrentItem(0);

                    return true;
                } else if (motionEvent.getAction() != 1) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        bGround1.mydgua1_1.setText("配置页");
        bGround1.addView(bGround1.mydgua1_1);
        bGround1.mydgua1_3 = new Dgua11(this, R.drawable.ms1, R.drawable.ms2) {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    ActivityUiRecog.bGround1.setViewColor(3);
                    pager.setCurrentItem(1);

                    ActivityUiRecog.t1.setText("发短信", "任务");
                    return true;
                } else if (motionEvent.getAction() != 1) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        bGround1.mydgua1_3.setText("发短信");
        bGround1.addView(bGround1.mydgua1_3);
        bGround1.mydgua1_2 = new Dgua11(this, R.drawable.hh1, R.drawable.hh2) {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    ActivityCommon.bGround1.setViewColor(2);
                    pager.setCurrentItem(2);

                    ActivityUiRecog.t1.setText("打电话", "任务");
                    return true;
                } else if (motionEvent.getAction() != 1) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        bGround1.mydgua1_2.setText("打电话");
        bGround1.addView(bGround1.mydgua1_2);
        bGround1.mydgua1_4 = new Dgua11(this, R.drawable.aaa, R.drawable.aaa2) {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    ActivityCommon.bGround1.setViewColor(4);

                    return true;
                } else if (motionEvent.getAction() != 1) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        bGround1.mydgua1_4.setText("百宝箱");
        bGround1.addView(bGround1.mydgua1_4);
        tf1 = TestFragment.newInstance(R.layout.fglayout, R.id.testText);
        fgs[0] = tf1;
        Bundle bundle2 = new Bundle();
        bundle2.putInt("id", 0);
        fgs[0].setArguments(bundle2);

        tf11 = TestFragment1.newInstance(R.layout.fglayout2, R.id.message111);
        fgs[1] = tf11;
        bundle2 = new Bundle();
        bundle2.putInt("id", 1);
        fgs[1].setArguments(bundle2);

        tf22 = TestFragment2.newInstance(R.layout.fglayout3, R.id.fglayout3DguaCtV1);
        fgs[2] = tf22;
        bundle2 = new Bundle();
        bundle2.putInt("id", 2);
        fgs[2].setArguments(bundle2);

        ActivityCommon.bGround1.setViewColor(3);
        adapter = new FragAdapter(getSupportFragmentManager(), fgs);
        pager = (DguaPagerView) findViewById(R.id.pager1);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);

        MobileAds.initialize(this, "ca-app-pub-1912776650962590~4969958173");//app-key
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1912776650962590/4523004104");//chayeguanggao
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);// hengfuguanggao

    }

    @Override
    protected void initView() {
        super.initView();
    }

    protected void handleMsg(Message msg) {
        super.handleMsg(msg);
        switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1) {
//                    txtResult.setText(msg.obj.toString());
                }
                status = msg.what;
                updateBtnTextByStatus();
                break;
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                updateBtnTextByStatus();
                break;
            default:
                break;
        }
        if (msg.what==999){
            status=(int)msg.obj;
            //status = STATUS_NONE;
            switch (status) {
                case STATUS_NONE: // 初始状态
                    start();
                    status = STATUS_WAITING_READY;
                    updateBtnTextByStatus();
                    break;
                case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
                case STATUS_READY: // 引擎准备完毕。
                case STATUS_SPEAKING: // 用户开始讲话
                case STATUS_FINISHED: // 一句话识别语音结束
                case STATUS_RECOGNITION: // 识别中
                    stop();
                    status = STATUS_STOPPED; // 引擎识别中
                    updateBtnTextByStatus();
                    break;
                case STATUS_LONG_SPEECH_FINISHED: // 长语音识别结束
                case STATUS_STOPPED: // 引擎识别中
                    cancel();
                    status = STATUS_NONE; // 识别结束，回到初始状态
                    updateBtnTextByStatus();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                // btn.setText("开始录音");
                // btn.setEnabled(true);
                // setting.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                // btn.setText("停止录音");
                // btn.setEnabled(true);
                  //setting.setEnabled(false);
                break;
            case STATUS_LONG_SPEECH_FINISHED:
            case STATUS_STOPPED:
                // btn.setText("取消整个识别过程");
                // btn.setEnabled(true);
                // setting.setEnabled(false);
                break;
            default:
                break;
        }
    }

}
