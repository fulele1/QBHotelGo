package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.xaqianbai.QBHotelSecurutyGovernor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fl on 2017/5/11.
 * 曲线图
 */

public class ChartUtil {

    static Context mContext;
    /**
     * 返回曲线上的数据以及完整的曲线
     *
     * @param count      显示x轴的个数
     * @param dv         第一条曲线的数据
     * @param keys       显示到x轴上的信息
     * @return
     */
    public static LineData makeLineData(Context context,int count, List<Double> dv,
                                        List<String> keys) {
        mContext = context;
        List<String> x = keys;
        //y轴的数据
        ArrayList<Entry> y = new ArrayList<>();
        double dVal1 = 0.0d;
        for (int i = 0; i < count; i++) {
            dVal1 = dv.get(i);
            Entry entry = new Entry((int) dVal1, i);
            y.add(entry);
        }

        ArrayList<LineDataSet> mLineDataSets = new ArrayList<>();
        mLineDataSets.add(setchar(y, "入住量", context.getResources().getColor(R.color.LINE)));
//        mLineDataSets.add(setchar(y2, linedes2, lineColor2));
        LineData mLineData = new LineData(x, mLineDataSets);
        return mLineData;
    }

    /**
     * @param y
     * @param describ
     * @param lineColor
     * @return
     */
    public static LineDataSet setchar(ArrayList<Entry> y, String describ, int lineColor) {
        LineDataSet mLineDataSet = new LineDataSet(y, describ);// y轴数据集
        // 用y轴的集合来设置参数
        mLineDataSet.setLineWidth(3.0f);// 线宽

        mLineDataSet.setCircleSize(3.0f);// 显示的圆形大小

        mLineDataSet.setColor(lineColor);// 折线的颜色
        //mLineDataSet2.setCircleColor(Color.GREEN);// 圆球的颜色

        // 设置mLineDataSet.setDrawHighlightIndicators(false)后，
        // Highlight的十字交叉的纵横线将不会显示，
        // 同时，mLineDataSet.setHighLightColor(Color.CYAN)失效。
        mLineDataSet.setDrawHighlightIndicators(true);

        mLineDataSet.setHighLightColor(Color.CYAN);// 按击后，十字交叉线的颜色

        mLineDataSet.setValueTextSize(10.0f);// 设置这项上显示的数据点的字体大小。

        // mLineDataSet.setDrawCircleHole(true);//设置圆圈中心有小圈

        mLineDataSet.setDrawCubic(true);// 改变折线样式，用曲线。
        // 默认是直线
        mLineDataSet.setCubicIntensity(0.2f);// 曲线的平滑度，值越大越平滑。

        // 填充曲线下方的区域，红色，半透明。
         mLineDataSet.setDrawFilled(true);
         mLineDataSet.setFillAlpha(128);
         mLineDataSet.setFillColor(mContext.getResources().getColor(R.color.bagkk));

        mLineDataSet.setCircleColorHole(Color.YELLOW);// 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
        mLineDataSet.setValueFormatter(new ValueFormatter() {// 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。

            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int n = (int) value;
                String s = "" + n;
                return s;
            }
        });
        return mLineDataSet;
    }


    /**
     * 设置chart显示的样式
     *
     * @param mLineChart
     * @param lineData
     * @param color
     */
    public static void setChartStyle(LineChart mLineChart, LineData lineData, int color) {

        mLineChart.setDrawBorders(false);// 是否在折线图上添加边框
        mLineChart.setDescription("日期(单位:日)");// 数据描述
        mLineChart.setNoDataTextDescription("如果传给MPAndroidChart的数据为空，那么你将看到这段文字。@Zhang Phil"); // 如果没有数据的时候，会显示这个，类似listview的emtpyview

        // 是否绘制背景颜色。
        // 如果mLineChart.setDrawGridBackground(false)，
        // 那么mLineChart.setGridBackgroundColor(Color.CYAN)将失效;
        mLineChart.setDrawGridBackground(false);
        mLineChart.setGridBackgroundColor(Color.CYAN);
        mLineChart.setTouchEnabled(true);// 触摸
        mLineChart.setDragEnabled(true);// 拖拽
        mLineChart.setScaleEnabled(true);// 缩放
        mLineChart.setPinchZoom(false);
        mLineChart.getAxisRight().setEnabled(false);// 隐藏右边 的坐标轴
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);// 让x轴在下面
        mLineChart.getXAxis().setLabelRotationAngle(-60);
        // // 隐藏左边坐标轴横网格线
        // mLineChart.getAxisLeft().setDrawGridLines(false);
        // // 隐藏右边坐标轴横网格线
        // mLineChart.getAxisRight().setDrawGridLines(false);
        // // 隐藏X轴竖网格线
        // mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.setBackgroundColor(color);// 设置背景
        mLineChart.setData(lineData);// 设置x,y轴的数据
        Legend mLegend = mLineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLACK);// 颜色
        mLegend.setEnabled(false);
        mLineChart.animateX(2000);// 沿x轴动画，时间2000毫秒。
    }
}
