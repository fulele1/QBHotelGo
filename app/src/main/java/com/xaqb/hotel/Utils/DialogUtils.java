package com.xaqb.hotel.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xaqb.hotel.Activity.MainActivity;
import com.xaqb.hotel.R;

/**
 * Created by fl on 2018/6/14.
 */

public class DialogUtils {

    public static void showItemDialog(final Context mContext, String title,final String[] items, final EditText view){

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setIcon(R.mipmap.per)//设置标题的图片
                .setTitle(title)//设置对话框的标题
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(mContext, items[which], Toast.LENGTH_SHORT).show();
                        view.setText(items[which]);
                    }
                })
                .create();
        dialog.show();
    }





}
