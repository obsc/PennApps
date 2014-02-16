/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aircanvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    
    private boolean down;
    
    private float downx;
    private float downy;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setEGLConfigChooser(8,8,8,8,16,0);
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        
        setZOrderOnTop(true);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setWillNotDraw(false);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
	protected void onDraw(Canvas canvas){
    	
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();
        
        float maxx = getWidth();
        float maxy = getHeight();
        
        float threshx = maxx / 8;
        float threshy = maxy / 8;
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	down = true;
            	downx = x;
            	downy = y;
            	break;
            case MotionEvent.ACTION_UP:
            	down = false;
            	mRenderer.action_up();
            	requestRender();
            	break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                break;
        }
        
        if (down) {
            if (downx > threshx && downx < maxx - threshx && downy > threshy & downy < maxy + threshy) {
//            	mRenderer.drawing(x,y);
            	
            }
            else {
            if (downx < threshx) {
            	mRenderer.left();
            }
            if (downx > maxx - threshx) {
            	mRenderer.right();
            }
            if (downy < threshy) {
            	mRenderer.back();
            }
            if (downy > maxy - threshy) {
            	mRenderer.forward();
            }
            
            requestRender();
            }
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

}
