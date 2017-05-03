package com.xaqb.unlock.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

/**
 * 动画工具类
 * Created by chengeng on 16/03/08.
 */
public class AnimationUtil {
    /**
     * 先变小再变大再回到初始大小
     */
    public static void playButtonAnimation(View view) {
        /**
         * params1 执行动画的view控件
         * params2 补间动画类型
         * params3 初始值
         * params4 最终值
         */
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",
                1.0f, 0.8f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",
                1.0f, 0.8f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "scaleX",
                0.8f, 1.2f);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(view, "scaleY",
                0.8f, 1.2f);
        ObjectAnimator anim5 = ObjectAnimator.ofFloat(view, "scaleX",
                1.2f, 1f);
        ObjectAnimator anim6 = ObjectAnimator.ofFloat(view, "scaleY",
                1.2f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.play(anim3).after(anim1);
        animSet.play(anim4).after(anim1);
        animSet.play(anim5).after(anim4);
        animSet.play(anim6).after(anim4);
        animSet.setDuration(200);
        animSet.start();
    }

    /**
     * 弹起控件,然后垂直下坠到消失,再出现在原来位置
     */
    public static void playDropBall(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationY",
                0, -view.getHeight());
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "translationY",
                -view.getHeight(), 500);
        final ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "translationY",
                500, 0);
        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(anim1);
        animSet.play(anim2).after(anim1);
        anim1.setDuration(500);
        anim2.setDuration(1000);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                anim3.setDuration(0);
                anim3.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 控件向上弹起逐渐消失
     */
    public static void playUpon(final View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationY",
                view.getHeight(), 0);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "alpha",
                1.0f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.setDuration(500);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 控件向下逐渐消失
     */
    public static void playDown(final View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationY",
                view.getHeight(), view.getHeight() * 2);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "alpha",
                1.0f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.setDuration(500);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 控件从最小逐渐恢复到原状
     */
    public static void playButtonBackAnimation(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",
                0f, 1f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",
                0f, 1f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha",
                0.2f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.play(anim3).with(anim2);
        animSet.setDuration(500);
        animSet.start();
    }

    /**
     * 控件从原状逐渐消失
     */
    public static void playButtonDismissAnimation(final View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",
                1f, 0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",
                1f, 0f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha",
                1f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.play(anim3).with(anim2);
        animSet.setDuration(500);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

//    /**
//     * 控件向上位移一段高度.然后掉落最后消失
//     */
//    public static void playUpDismissAnimation(View view) {
//        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",
//                0f, 1f);
//        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",
//                0f, 1f);
//        ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha",
//                0.2f, 1f);
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(anim1).with(anim2);
//        animSet.play(anim3).with(anim2);
//        animSet.setDuration(500);
//        animSet.start();
//    }


    public void playTest01(final View view1, final View view2) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view1,
                "RotationY", 0f, 90f);
        objectAnimator.setDuration(500);
        objectAnimator.start();
        final ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(
                view2, "RotationY", -90f, 0f);
        objectAnimator2.setDuration(500);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                objectAnimator2.start();
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
            }
        });
    }

    public static void playTest2(View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        alpha.setDuration(2000);//设置动画时间
        alpha.setInterpolator(new DecelerateInterpolator());//设置动画插入器，减速
        alpha.setRepeatCount(-1);//设置动画重复次数，这里-1代表无限
        alpha.setRepeatMode(Animation.REVERSE);//设置动画循环模式。
        alpha.start();//启动动画。
    }

    public static void playTest3(View view) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);

        animatorSet.setDuration(2000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();
    }

    public static void playTest4(View view) {
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(view, "translationX",
                view.getPivotX(), 300);
        translationUp.setInterpolator(new DecelerateInterpolator());
        translationUp.setDuration(1500);
        translationUp.start();
    }

    public static void playTest5(View view,
                                 View view1,
                                 View view2,
                                         View view3) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotationX", 0f, 360f);
        anim.setDuration(3000);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view1, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view1, "scaleY", 0f, 1f);
        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(view2, "scaleX", 0f, 1f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(view2, "scaleY", 0f, 1f);
        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        ObjectAnimator scaleX3 = ObjectAnimator.ofFloat(view3, "scaleX", 0f, 1f);
        ObjectAnimator scaleY3 = ObjectAnimator.ofFloat(view3, "scaleY", 0f, 1f);
        scaleX.setDuration(1000);
        scaleY.setDuration(1000);

//        set.play(anim).with(scaleX); //两个动画一起执行
        set.playTogether(anim,scaleX,scaleY);
        set.play(scaleX2).after(scaleX);
        set.play(scaleY2).after(scaleX);
        set.play(scaleX3).after(scaleY2);
        set.play(scaleY3).after(scaleY2);
        set.start();
    }
}
