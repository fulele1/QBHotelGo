package com.xaqb.unlock.Utils;

import java.util.Map;

/**
 * Created by lenovo on 2017/4/20.
 * 网络访问回调方法
 */
public abstract class QBCallback  {

    public  abstract void doWork(Map<?, ?> map);//请求成功后的操作

    public  abstract void doError(Exception e);//请求返回失败

    public abstract void reDoWork();//刷新token后执行的方法
}
