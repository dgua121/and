package com.baidu.aip.asrwakeup3.uiasr.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.baidu.aip.asrwakeup3.uiasr.R;
import com.baidu.aip.asrwakeup3.uiasr.activity.ActivityCommon;
import com.baidu.aip.asrwakeup3.uiasr.activity.ActivityUiRecog;
import com.baidu.aip.asrwakeup3.uiasr.gj.Test;
import com.baidu.aip.asrwakeup3.uiasr.gongju.HttpUtil;
import com.baidu.aip.asrwakeup3.uiasr.gongju.unitData;
import com.baidu.aip.asrwakeup3.uiasr.save.TestClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

public class callService extends Service {
    int index = 0;
    private final IBinder binder = new callService.MyBinder();
    String session = "";
    String phone;
    String name = "kehu";
    String sort = "C";
    int ccc = 0;
    public boolean kg = true;
    private Context ct;
    private MusicService musicService = null;
    Intent intent3 = new Intent();
    public static Handler handler;
    private SoundPool sp;
    private HashMap<Integer, Integer> hm;
    private float volume;
    private int currentID;
    private int play = 1;

    private void handleMsg(Message msg) {

        if (msg.what == 1111) {
            nextnb();//拨打下一个电话playSound
        }
        //服务器端返回消息
        if (msg.what == 130 && msg.obj != null) {
            String clientInputStr = msg.obj.toString();
            if (clientInputStr.equals("over")) {

                System.out.println("对方电话挂断");
                //电话挂断状态
                over();
                stopService(intent3);
                //结束识别
                ccc = 0;

            } else if (clientInputStr.equals("ALERTING")) {

                //电话呼出成功

            } else if (clientInputStr.equals("ACTIVE")) {
                play = 1;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(4000);
                            open();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        super.run();
                    }
                }.start();

                System.out.println("开始开场白");
            }
        }
        //停止播放音乐
        if (msg.what == 121) {
            System.out.println("中断，，，，，，，，，，，，，");
            if (play != 4 || play != 12) {
                stopService(intent3);
            }

        }
        //本端主动挂断电话
        if (msg.what == 125) {
            ActivityCommon.str = "over";//挂断电话
            ccc = 0;
            // over();
        }

        if (msg.what == 100001) {
            final String ss = msg.obj.toString();
            try {
                new Thread() {
                    public void run() {
                        String talkUrl = "https://aip.baidubce.com/rpc/2.0/unit/bot/chat";
                        String str1 = new unitData().data(ss, phone + index, phone + index, session);
                        String result1 = null;
                        String sss = "";
                        try {
                            result1 = HttpUtil.post(talkUrl, ActivityCommon.tockn, "application/json", str1);
                            JSONObject jsonObject = new JSONObject(result1);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            JSONObject jsonObject11 = new JSONObject(jsonObject1.getString("response"));
                            JSONArray jsonArray2 = new JSONArray(jsonObject11.getString("action_list"));
                            JSONObject jsonObject3 = new JSONObject(jsonArray2.get(0).toString());
                            sss = jsonObject3.getString("say");
                            session = (new JSONObject(jsonObject1.getString("bot_session"))).toString();
                            String aw = jsonObject3.getJSONObject("refine_detail").getString("interact");
                            if (aw.equals("select")) {
                                String aww = jsonObject3.getJSONObject("refine_detail").getJSONArray("option_list").getJSONObject(0).getString("option");
                                String str11 = new unitData().data(aww, phone + index, phone + index, session);
                                result1 = HttpUtil.post(talkUrl, ActivityCommon.tockn, "application/json", str11);
                                JSONObject jsonObject10 = new JSONObject(result1);
                                JSONObject jsonObject110 = new JSONObject(jsonObject10.getString("result"));
                                JSONObject jsonObject111 = new JSONObject(jsonObject110.getString("response"));
                                JSONArray jsonArray21 = new JSONArray(jsonObject111.getString("action_list"));
                                JSONObject jsonObject31 = new JSONObject(jsonArray21.get(0).toString());
                                sss = jsonObject31.getString("say");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (sss.equals("需要多少钱？")) {
                            sort = "B";
                            // playSound(8, 0);
                            play = 8;
                            intent3.putExtra("name", R.raw.kcb8);
                            startService(intent3);
                            System.out.println("开始对话a");

                        } else if (sss.equals("这要根据你资产和负债才能算出来这个没问题的，那你名下有没有按揭房或者保单车子？")) {
                            sort = "B";
                            //playSound(2, 0);
                            play = 2;
                            intent3.putExtra("name", R.raw.kcb2);
                            startService(intent3);
                            System.out.println("开始对话a");

                        } else if (sss.equals("请澄清一下：打工")) {
                            sort = "B";
                            //playSound(3, 0);
                            play = 3;
                            intent3.putExtra("name", R.raw.kcb3);
                            startService(intent3);
                            System.out.println("开始对话a");
                        } else if (sss.equals("没事的可以了解下")) {
                            sort = "C";
                            play = 4;
                            intent3.putExtra("name", R.raw.kcb4);
                            startService(intent3);
                            System.out.println("开始对话a");
                        } else if (sss.equals("利息是根据你资质来定的")) {
                            sort = "A";
                            //playSound(5, 0);
                            play = 5;
                            intent3.putExtra("name", R.raw.kcb5);
                            startService(intent3);
                            System.out.println("开始对话a");
                        } else if (sss.equals("手续费肯定有的我们是按照银行规定收取的")) {
                            sort = "A";
                            //playSound(6, 0);
                            play = 6;
                            intent3.putExtra("name", R.raw.kcb6);
                            startService(intent3);
                            System.out.println("开始对话a");
                        } else if (sss.equals("请澄清一下：打卡工资社保")) {
                            sort = "B";
                            play = 10;
                            intent3.putExtra("name", R.raw.kcb10);
                            startService(intent3);
                            System.out.println("开始对话a");
                        } else if (sss.equals("请澄清一下：营业执照")) {
                            sort = "B";
                            //playSound(11, 0);
                            play = 11;
                            intent3.putExtra("name", R.raw.kcb11);
                            startService(intent3);
                            System.out.println("开始对话a");
                        } else if (sss.equals("嗯你情况我这边也了解了，我让我们经理给你回电话吧，他更加专业")) {
                            sort = "A";
                            play = 12;
                            intent3.putExtra("name", R.raw.kcb12);
                            startService(intent3);
                            System.out.println("开始对话a");
                        } else {
                            ccc++;
                            if (ccc <= 1) {
                                play = 7;
                                intent3.putExtra("name", R.raw.kcb7);
                                startService(intent3);
                                System.out.println("开始对话a 没有听清");
                            } else {
                                // over();
                                play = 12;
                                intent3.putExtra("name", R.raw.kcb12);
                                startService(intent3);
                            }
                        }
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MyBinder extends Binder implements inservice {

        public callService getService() {
            return callService.this;
        }

        @Override
        public void startwork(int a) {
            callService.this.startwork();
        }

        @Override
        public void pausedwork() {
            callService.this.pausedwork();
        }
    }

    public callService() {
    }

    //启动识别
    private void open() {

        System.out.println("启动识别");
        Message msg1 = new Message();
        msg1.what = 999;
        msg1.obj = ActivityUiRecog.STATUS_NONE;
        ActivityUiRecog.handler.sendMessage(msg1);//启动识别引擎
    }

    //关闭识别
    private void over() {
        System.out.println("关闭识别");
        Message msg1 = new Message();
        msg1.what = 999;
        msg1.obj = ActivityUiRecog.STATUS_STOPPED;
        ActivityUiRecog.handler.sendMessage(msg1);//关闭识别引擎
        new Thread() {
            @Override
            public void run() {
                new TestClient(getApplicationContext(), name, sort, phone);
                super.run();
            }
        }.start();
    }

    public void nextnb() {
        index = index + 1;
        sort = "C";
        ccc = 0;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ActivityCommon.str = new Test(getApplication()).queryData2();
                phone = ActivityCommon.str;
                System.out.println(phone);
                session = "";
            }
        }.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ct = getApplication();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }
        };
        //  initSoundPool();
    }

    private void initSoundPool() {
        sp = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        hm = new HashMap<Integer, Integer>();
       /* hm.put(2, sp.load(getApplicationContext(), R.raw.kcb2, 1));
        hm.put(3, sp.load(getApplicationContext(), R.raw.kcb3, 1));
        hm.put(5, sp.load(getApplicationContext(), R.raw.kcb5, 1));
        hm.put(6, sp.load(getApplicationContext(), R.raw.kcb6, 1));
        hm.put(7, sp.load(getApplicationContext(), R.raw.kcb7, 1));
        hm.put(8, sp.load(getApplicationContext(), R.raw.kcb8, 1));
        //hm.put(10, sp.load(getApplicationContext(), R.raw.kcb10, 1));
        hm.put(11, sp.load(getApplicationContext(), R.raw.kcb11, 1));*/
    }

    private void playSound(int num, int loop) {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float currentSound = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = currentSound / maxSound;
        currentID = sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
    }

    private void startwork() {

        intent3.setClass(this, MymusicService.class);
        intent3.setPackage(getPackageName());
        nextnb();
    }

    private void pausedwork() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (kg) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;

    }
}
