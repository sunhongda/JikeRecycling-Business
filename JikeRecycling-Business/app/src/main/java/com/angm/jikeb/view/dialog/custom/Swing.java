package com.angm.jikeb.view.dialog.custom;

import android.view.View;

import com.angm.jikeb.view.dialog.base.BaseAnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class Swing extends BaseAnimatorSet {
	public Swing() {
		duration = 1000;
	}

	@Override
	public void setAnimation(View view) {
		animatorSet.playTogether(//
				ObjectAnimator.ofFloat(view, "alpha", 1, 1, 1, 1, 1, 1, 1, 1),//
				ObjectAnimator.ofFloat(view, "rotation", 0, 10, -10, 6, -6, 3, -3, 0));
	}
}
