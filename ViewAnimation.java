package com.example.trackent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class ViewAnimation{
    public static Boolean rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        return rotate;
    }

    public static void showIn(final View v, View view) {
        v.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        v.setAlpha(0f);
        view.setAlpha(0f);
        v.setTranslationY(v.getHeight());
        view.setTranslationY(v.getHeight());
        v.animate()
                .setDuration(200)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
        view.animate()
                .setDuration(200)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    public static void showOut(final View v, View view) {
        v.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        v.setAlpha(1f);
        view.setAlpha(1f);
        v.setTranslationY(0);
        view.setTranslationY(0);
        v.animate()
                .setDuration(200)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(0f)
                .start();
        view.animate()
                .setDuration(200)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(0f)
                .start();
    }

    public static void init(final View v, View view) {
        // hides the mini FAB buttons when the app started.
        v.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        v.setTranslationY(v.getHeight());
        view.setTranslationY(v.getHeight());
        v.setAlpha(0f);
        view.setAlpha(0f);
    }
}
