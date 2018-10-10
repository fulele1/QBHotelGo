package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.fantasy.doubledatepicker.DoubleDateSelectDialog;


/**
 * Created by lenovo on 2018/8/24.
 */

public class DoubleDateUtil {


    public static void show(Context context, final EditText edit_time){
        DoubleDateSelectDialog mDoubleTimeSelectDialog = null;
        String nowTime = DateUtil.getNowTime();
        if (mDoubleTimeSelectDialog == null) {
            mDoubleTimeSelectDialog = new DoubleDateSelectDialog(context, "1990-01-01",nowTime, nowTime);
            LogUtils.e("newTime"+DateUtil.getNowTime());
            mDoubleTimeSelectDialog.setOnDateSelectFinished(new DoubleDateSelectDialog.OnDateSelectFinished() {
                @Override
                public void onSelectFinished(String startTime, String endTime) {
                    edit_time.setText(startTime.replace("-", "-") + "--->" + endTime.replace("-", "-"));
                }
            });

            mDoubleTimeSelectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
        }if (!mDoubleTimeSelectDialog.isShowing()) {
            mDoubleTimeSelectDialog.show();
        }

    }


}
