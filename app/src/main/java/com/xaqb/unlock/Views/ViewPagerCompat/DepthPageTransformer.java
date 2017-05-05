package com.xaqb.unlock.Views.ViewPagerCompat;


import android.support.v4.view.ViewPager;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class DepthPageTransformer implements ViewPager.PageTransformer {
//	private static final float MIN_SCALE = 0.8f;
//
//	private static float MIN_ALPHA = 0.5f;
//	private String TAG = "abc";

	public void transformPage(View view, float position) {
//		int pageWidth = view.getWidth();
//		int pageHeight = view.getHeight();

		// if (position < -1) { // [-Infinity,-1)
		// // This page is way off-screen to the left.
		// ViewHelper.setAlpha(view,0);
		// } else if (position <= 1) { // [-1,1]
		// // Modify the default slide transition to
		// // shrink the page as well
		// float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
		// float vertMargin = pageHeight * (1 - scaleFactor) / 2;
		// float horzMargin = pageWidth * (1 - scaleFactor) / 2;
		// if (position < 0) {
		// ViewHelper.setTranslationX(view,horzMargin - vertMargin / 2);
		// } else {
		// ViewHelper.setTranslationX(view,-horzMargin + vertMargin / 2);
		// }
		// // Scale the page down (between MIN_SCALE and 1)
		// ViewHelper.setScaleX(view,scaleFactor);
		// ViewHelper.setScaleY(view,scaleFactor);
		// // Fade the page relative to its size.
		// ViewHelper.setAlpha(view,MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 -
		// MIN_SCALE) * (1 - MIN_ALPHA));
		// } else { // (1,+Infinity]
		// // This page is way off-screen to the right.
		// ViewHelper.setAlpha(view,0);
		// }

		// if (position < -1) {
		// // ViewHelper.setAlpha(view, 0);
		// } else if (position == -1) {
		// // ViewHelper.setScaleX(view, 1.1f);
		// // ViewHelper.setAlpha(view, 0.6f);
		// } else if (position < 0) {
		// // ViewHelper.setTranslationX(view, pageWidth * position);
		// ViewHelper.setAlpha(view, 0.6f + (1 - 0.6f) * (1 + position));
		// ViewHelper.setScaleX(view, MIN_SCALE + (1.1f - MIN_SCALE) * (1.1f -
		// position));
		// ViewHelper.setScaleY(view, 0.9f + (0.8f - 0.9f) * (0.8f + position));
		//
		// } else if (position == 0) {
		// // ViewHelper.setAlpha(view, 1);
		// ViewHelper.setTranslationX(view, 0);
		// // view.
		// ViewHelper.setScaleX(view, 0.8f);
		// ViewHelper.setScaleY(view, 0.9f);
		// } else if (position < 1) {
		// // ViewHelper.setAlpha(view, 1 - position);
		// ViewHelper.setAlpha(view, 0.6f + (1 - 0.6f) * (1 - position));
		// // ViewHelper.setTranslationX(view, pageWidth * -position);
		// // ViewHelper.setTranslationX(view, pageWidth * position);
		// // float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - position);
		// ViewHelper.setScaleX(view, MIN_SCALE + (1.1f - MIN_SCALE) * (1.1f +
		// position));
		// ViewHelper.setScaleY(view, 0.9f + (0.8f - 0.9f) * (0.8f + position));
		// // ViewHelper.setScaleX(view, scaleFactor);
		// // ViewHelper.setScaleY(view, scaleFactor);
		//
		// } else if (position == 1) {
		// // ViewHelper.setScaleX(view, 1.1f);
		// // ViewHelper.setAlpha(view, 0.6f);
		// // ViewHelper.setScaleY(view, 0.9f);
		// } else {
		// // ViewHelper.setAlpha(view, 0);
		// }

		//////////////////////////////////////////////////////////
		if (position < -1) {

		} else if (position < 0) {
			ViewHelper.setAlpha(view, 0.5f + (1 - 0.5f) * (1 + position));
			//ViewHelper.setScaleX(view, �?终要变成的比�? + (位置�?0时的比例 - �?终要变成的比�?) * (1f + position));
			ViewHelper.setScaleX(view, 1.1f + (0.8f - 1.1f) * (1f + position));
			ViewHelper.setScaleY(view, 0.8f + (0.9f - 0.8f) * (1 + position));
		} else if (position == 0) {
			ViewHelper.setAlpha(view, 1);
			ViewHelper.setTranslationX(view, 0);
			// view.
			ViewHelper.setScaleX(view, 0.8f);
			ViewHelper.setScaleY(view, 0.9f);
		} else if (position <= 1) {
			ViewHelper.setAlpha(view, 0.5f + (1 - 0.5f) * (1 - position));
			//ViewHelper.setScaleX(view, �?终要变成的比�? + (位置�?0时的比例 - �?终要变成的比�?) * (1f - position));
			ViewHelper.setScaleX(view, 1.1f + (0.8f - 1.1f) * (1 - position));
			ViewHelper.setScaleY(view, 0.8f + (0.9f - 0.8f) * (1 - position));
			// ViewHelper.setAlpha(view, 0.6f + (1 - 0.6f) * (1 - position));
			// ViewHelper.setScaleX(view, MIN_SCALE + (1.1f - MIN_SCALE) * (1.1f
			// + position));
			// ViewHelper.setScaleY(view, 0.9f + (0.8f - 0.9f) * (0.8f +
			// position));
		} else {
			ViewHelper.setAlpha(view, 0.5f - (1 - 0.5f) * (position - 1));
			ViewHelper.setScaleX(view, 1.1f - (0.8f - 1.1f) * (position - 1));
			ViewHelper.setScaleY(view, 0.8f - (0.9f - 0.8f) * (position - 1));
		}

		//////////////////////////////////////////////////////////

	}
}
