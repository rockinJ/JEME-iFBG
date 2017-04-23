package com.jemetech.jeme_ifbg.android;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Lei on 11/29/2016.
 */

public class DrawingView extends View {

    private Drawer drawer = new Drawer() {
        @Override
        public void draw(Canvas canvas) {

        }
    };

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawer.draw(canvas);
    }

    public static interface Drawer {
        void draw(Canvas canvas);
    }
}
