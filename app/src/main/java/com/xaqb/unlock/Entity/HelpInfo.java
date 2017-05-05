package com.xaqb.unlock.Entity;

/**
 * Created by lenovo on 2017/5/5.
 * 使用帮助实体类
 */
public class HelpInfo extends Entity {
    //使用帮助图片
    private int imageResource;
    //使用帮助文字说明
    private String captions;

    public HelpInfo() {
    }

    public HelpInfo(int imageResource, String captions) {
        this.imageResource = imageResource;
        this.captions = captions;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getCaptions() {
        return captions;
    }

    public void setCaptions(String captions) {
        this.captions = captions;
    }

    @Override
    public String toString() {
        return "HelpInfo{" +
                "captions='" + captions + '\'' +
                ", imageResource=" + imageResource +
                '}';
    }
}
