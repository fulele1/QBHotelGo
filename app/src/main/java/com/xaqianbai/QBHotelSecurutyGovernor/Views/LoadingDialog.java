package com.xaqianbai.QBHotelSecurutyGovernor.Views;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.R;


/**
 * ToDo 进度加载对话框
 * Author: dylan
 * Date: 2014-12-29
 * Time: 13:34
 */
public class LoadingDialog {
    private Dialog dialog;
    private Context context;
    private TextView tv_text;
    private ImageView img_gif;

    public LoadingDialog(Context context) {
        this.context = context;
    }

    public Dialog show(String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.loading, null);
        tv_text = (TextView) view.findViewById(R.id.tv_text);
        img_gif = (ImageView) view.findViewById(R.id.gif_img);
        Glide.with(context).load(R.drawable.loading).into(img_gif);
        tv_text.setText(message);
        if(dialog==null){
            dialog = new Dialog(context, R.style.transparentFrameWindowStyle);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
        return dialog;
    }

    public void setMessage(String message) {
        if (tv_text != null) {
            tv_text.setText(message);
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }

    }
} 