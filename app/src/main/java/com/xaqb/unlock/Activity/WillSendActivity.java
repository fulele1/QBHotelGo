package com.xaqb.unlock.Activity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.SDCardUtils;

import java.io.File;


/**
 * Created by chengeng on 2016/12/2.
 * 待发数据页面
 */
public class WillSendActivity extends BaseActivityNew {
    private WillSendActivity instance;
    private String[] aFile;
    private ListView oList;
    private TextView tvTitle;
    private int dialogType;
    private ArrayAdapter<String> oAdapter;

    @Override
    public void initViews() {
        StatusBarUtil.setTranslucent(this,0);
        setContentView(R.layout.will_send_activity);
        instance = this;
        assignViews();
        tvTitle.setText("待发数据");
    }

    private void assignViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void initData() {
        try {
            File[] oFiles = new File(appPath()).listFiles();
            int i, iLen = 0;
            for (i = 0; i < oFiles.length; i++) {
                if (oFiles[i].getName().startsWith("咚咚开锁"))
                    iLen++;
            }
            if (iLen > 0) {
                aFile = new String[iLen];
                iLen = 0;
                for (i = 0; i < oFiles.length; i++) {
                    if (oFiles[i].getName().startsWith("咚咚开锁")) {
                        aFile[iLen] = oFiles[i].getName();
                        LogUtils.e(instance.getFilesDir().getAbsolutePath()+"/"+oFiles[i]+".txt");
                        SDCardUtils.deletFile(instance.getFilesDir().getAbsolutePath()+"/"+oFiles[i]+".txt");
                        iLen++;
                    }
                }
                oList = (ListView) this.findViewById(R.id.lv_will_send);
                oAdapter = new ArrayAdapter<>(this, R.layout.will_send_list, aFile);

                oList.setAdapter(oAdapter);
            } else {
                dialogType = 2;
                showAdialog(this, "提示", "没有将要发送的数据", "确定", View.GONE);
            }
        } catch (Exception e) {
            showDialog("", e.getMessage(), "", "", 0);
        }
    }

    @Override
    public void addListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    protected void dialogOk() {
        switch (dialogType) {
            case 2:
                finish();
                break;
        }
    }
}
