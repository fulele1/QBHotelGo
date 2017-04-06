package com.xaqb.unlock.Activity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xaqb.unlock.R;

import java.io.File;


/**
 * Created by chengeng on 2016/12/2.
 * 待发数据页面
 */
public class WillSendActivity extends BaseActivity {
    private WillSendActivity instance;
    private String[] aFile;
    private ListView oList;
    private int dialogType;
    private ArrayAdapter<String> oAdapter;

    @Override
    public void initTitleBar() {
        setTitle("待发数据");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.will_send_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {

    }

    @Override
    public void initData() {
        try {
            File[] oFiles = new File(appPath()).listFiles();
            int i, iLen = 0;
            for (i = 0; i < oFiles.length; i++) {
                if (oFiles[i].getName().startsWith("Unlock"))
                    iLen++;
            }
            if (iLen > 0) {
                aFile = new String[iLen];
                iLen = 0;
                for (i = 0; i < oFiles.length; i++) {
                    if (oFiles[i].getName().startsWith("Unlock")) {
                        aFile[iLen] = oFiles[i].getName();
                        iLen++;
                    }
                }
                oList = (ListView) this.findViewById(R.id.lv_will_send);
                oAdapter = new ArrayAdapter<>(this, R.layout.will_send_list, aFile);
                oList.setAdapter(oAdapter);
            } else {
                dialogType = 2;
                showDialog("提示信息", "没有将要发送的数据", "确定", "", 0);
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
