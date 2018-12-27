package com.baidu.aip.asrwakeup3.uiasr.Dailog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import com.baidu.aip.asrwakeup3.uiasr.R;
import com.baidu.aip.asrwakeup3.uiasr.model.DguaModel;
import com.baidu.aip.asrwakeup3.uiasr.view.view1.phoneDguaCtSubV;
import com.baidu.aip.asrwakeup3.uiasr.view.DguaDgV;
import com.baidu.aip.asrwakeup3.uiasr.view.view2.DguaViewBackground;
import com.baidu.aip.asrwakeup3.uiasr.view.view1.Top;


public class PhoneMenuDialog extends Dialog {
    public String a1="";
    public String a2="";
    public String a3;
    public Context cctt;
    public phoneDguaCtSubV dcsv;

    public PhoneMenuDialog( Context context, int i, int i2, int i3, int i4) {
        super(context, i4);
        setContentView(i3);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        getDensity(context);
        attributes.width = i;
        attributes.height = i2;
        attributes.gravity = 80;
        attributes.windowAnimations = i4;
        window.setAttributes(attributes);

        DguaDgV dguaDgV = (DguaDgV) findViewById(R.id.layoutdialogDguaDgV);
        dguaDgV.ds1.setText(this.a1);
        dguaDgV.ds2.setText(this.a2);
        ((DguaViewBackground) findViewById(R.id.layoutdialogDguaViewBackground21)).mydgua1_1.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    return true;
                }
                if (motionEvent.getAction() != 1) {
                    return false;
                }

                dismiss();
                return true;
            }
        });
    }

    private float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
