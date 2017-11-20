package com.xaqb.unlock.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.xaqb.unlock.R;
import com.xaqb.unlock.Views.LoadingDialog;


/**
 * Created by fl on 2017/5/18.
 */

public class BaseFragment extends Fragment {

    private Context mContext;
    private View view;
    private Intent mIntent;
    private Toast mToast;
    private AlertDialog.Builder mDialog;//对话框
    public LoadingDialog mLoadingDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pay_activity,null);
        mContext = this.getContext();
        mLoadingDialog = new LoadingDialog(mContext);
        return view;
    }


    /**
     * 普通的界面的跳转
     *
     * @param context
     * @param cla
     */
    public void intentB(Context context, Class<?> cla) {
        mIntent = new Intent(context, cla);
        context.startActivity(mIntent);
    }

    /**
     * 携带参数界面的跳转
     *
     * @param context
     * @param cla
     */
    public void intentB(Context context, Class<?> cla, String string) {
        mIntent = new Intent(context, cla);
        mIntent.putExtra("intentData", string);
        context.startActivity(mIntent);
    }



    /**
     * 需要请求结果的界面的跳转
     * @param context
     * @param cla
     * @param code
     */
    public void intentB(Context context, Class<?> cla, int code){
        mIntent = new Intent(context, cla);
        startActivityForResult(mIntent,code);
    }

    public String readConfig(String sName) {

        SharedPreferences oConfig = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE);
        return oConfig.getString(sName, "");
    }

    public void writeConfig(String sName, String sValue) {
        SharedPreferences oConfig = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor oEdit = oConfig.edit();//获得编辑器
        oEdit.putString(sName, sValue);
        oEdit.commit();//提交内容

    }



    /**
     * 携带请求数据跳转界面
     * @param context
     * @param cla
     * @param key
     * @param value
     */
//    public void intentB(Context context,Class<?> cla,String key,String value){
//        mIntent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putString(key,value);
//        mIntent.putExtras(bundle);
//        setResult(RESULT_OK,mIntent);
//        finish();
//    }


    /**
     * 相机
     * @param code
     */
    public void intentB(int code){
        mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(mIntent,code);
    }

    /**
     * 图库
     * @param code
     * @param a
     */
    public void intentB(int code,String a){
        mIntent = new Intent(Intent.ACTION_PICK);
        mIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(mIntent,code);
    }


    /**
     * 弹出对话框
     */
    public void showToastB(String str) {
        if (null == mToast) {
            mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    /**
     * 弹出普通对话框
     *
     * @param tit
     * @param icon
     * @param mes
     * @param ok
     * @param no
     */
    public AlertDialog.Builder showDialogB(Context context, String tit, int icon, String mes, String ok, String no) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setIcon(icon);
        mDialog.setTitle(tit);
        mDialog.setMessage(mes);
        mDialog.setPositiveButton(ok, new BaseFragment.DialogEvent());
        mDialog.setNegativeButton(no, new BaseFragment.DialogEvent());
        mDialog.show();
        return mDialog;
    }


    class DialogEvent implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    dialogOkB();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    dialogNoB();
                    break;
            }
        }
    }


    public AlertDialog.Builder showDialogB(Context context, String[] item){
        mDialog = new AlertDialog.Builder(context);
        mDialog.setItems(item,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogItemEventB(which);

            }
        });
        mDialog.show();
        return mDialog;
    }

    /**
     * 对话框的子条目的点击事件
     * @param witch
     */
    public void dialogItemEventB(int witch){
    }

    /**
     * 对话框确定键
     */
    public void dialogOkB() {
    }

    /**
     * 对话框取消键
     */
    public void dialogNoB() {
    }



    AlertDialog alertDialog;

    /**
     * 自定义对话框
     * @param context
     * @param title
     * @param message
     * @param ok
     * @param
     * @return
     */
    public AlertDialog showAdialog(final Context context, String title, String message, String ok,int view){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.loading_my_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        TextView tvMessage = (TextView) window.findViewById(R.id.tv_dialog_message);
        tvMessage.setText(message);
        Button btOk = (Button) window.findViewById(R.id.btn_dia_ok);
        btOk.setText(ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOk();

            }
        });
        Button btNo = (Button) window.findViewById(R.id.btn_dia_no);
        btNo.setVisibility(view);
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }

    //对话框单击确定按钮处理
    protected void dialogOk() {

    }


}
