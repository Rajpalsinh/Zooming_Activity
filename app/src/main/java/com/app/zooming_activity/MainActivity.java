package com.app.zooming_activity;

import android.animation.Animator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class MainActivity extends FragmentActivity {

    private Animator CurrentmAnimator;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View thumb1View = findViewById(R.id.button_1);
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(thumb1View, R.drawable.venue1);
            }
        });

        thumb1View.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                zoomImageFromThumb(thumb1View, R.drawable.venue1);

                return true;
            }


        });

        final View imagebutton1 = findViewById(R.id.button_2);
        imagebutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(imagebutton1, R.drawable.venue2);
            }
        });
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        if (CurrentmAnimator != null) {
            CurrentmAnimator.cancel();
        }
        final ImageView ImageViewexpanded = (ImageView) findViewById(R.id.expanded_image);
        ImageViewexpanded.setImageResource(imageResId);

        final Rect Boundsstart = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        thumbView.getGlobalVisibleRect(Boundsstart);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        Boundsstart.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) Boundsstart.width() / Boundsstart.height()) {
            startScale = (float) Boundsstart.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - Boundsstart.width()) / 2;
            Boundsstart.left -= deltaWidth;
            Boundsstart.right += deltaWidth;
        } else {
            startScale = (float) Boundsstart.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - Boundsstart.height()) / 2;
            Boundsstart.top -= deltaHeight;
            Boundsstart.bottom += deltaHeight;
        }
        thumbView.setAlpha(0f);
        ImageViewexpanded.setVisibility(View.VISIBLE);
        ImageViewexpanded.setPivotX(0f);
        ImageViewexpanded.setPivotY(0f);
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat( ImageViewexpanded, View.X, Boundsstart.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat( ImageViewexpanded, View.Y, Boundsstart.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat( ImageViewexpanded, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat( ImageViewexpanded, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                CurrentmAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                CurrentmAnimator = null;
            }
        });
        set.start();
        CurrentmAnimator = set;
        final float startScaleFinal = startScale;
        ImageViewexpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurrentmAnimator != null) {
                    CurrentmAnimator.cancel();
                }
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat( ImageViewexpanded, View.X, Boundsstart.left))
                        .with(ObjectAnimator.ofFloat( ImageViewexpanded, View.Y, Boundsstart.top))
                        .with(ObjectAnimator
                                .ofFloat( ImageViewexpanded, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat( ImageViewexpanded, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override

                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        ImageViewexpanded.setVisibility(View.GONE);
                        CurrentmAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        ImageViewexpanded.setVisibility(View.GONE);
                        CurrentmAnimator= null;
                    }
                });
                set.start();
                CurrentmAnimator = set;
            }
        });
    }
}
