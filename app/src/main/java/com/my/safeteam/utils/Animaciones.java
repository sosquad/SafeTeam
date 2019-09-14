package com.my.safeteam.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class Animaciones {
    public AnimationSet slideFadeAnimation(View view, int delay, int fromX, int toX, int fromY, int toY, float fromAlpha, float toAlpha) {
        TranslateAnimation translationAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translationAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(true);
        animationSet.setDuration(delay);
        return animationSet;
    }
}
