// Copyright 2014 Miras Absar
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.cryse.novelreader.util.gesture;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

public abstract class SimpleGestureDetector implements OnTouchListener {

    // The amount of time (in milliseconds) a gesture has to be performed.
    private static final int TIME_LIMIT = 300;

    // The amount of distance (in density-independent pixels) a Pointer has to move to trigger a gesture.
    private static final int MOVEMENT_LIMIT_DP = 48;

    // The gesture id for an invalid gesture.
    public static final int INVALID_GESTURE = -1;

    // Gesture ids for one-finger gestures.
    public static final int TAP = 0;
    public static final int SWIPE_UP = 1;
    public static final int SWIPE_DOWN = 2;
    public static final int SWIPE_LEFT = 3;
    public static final int SWIPE_RIGHT = 4;

    // Gesture ids for two-finger gestures.
    public static final int TWO_FINGER_TAP = 5;
    public static final int TWO_FINGER_SWIPE_UP = 6;
    public static final int TWO_FINGER_SWIPE_DOWN = 7;
    public static final int TWO_FINGER_SWIPE_LEFT = 8;
    public static final int TWO_FINGER_SWIPE_RIGHT = 9;
    public static final int TWO_FINGER_PINCH_IN = 10;
    public static final int TWO_FINGER_PINCH_OUT = 11;

    // The amount of distance (in pixels) a Pointer has to move, to trigger a gesture.
    private float mMovementLimitPx;

    // A list of Pointers involved in a gesture.
    private ArrayList<Pointer> mPointers;

    public SimpleGestureDetector(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        float mDisplayDensity = mDisplayMetrics.density;

        mMovementLimitPx = MOVEMENT_LIMIT_DP * mDisplayDensity;
    }

    public SimpleGestureDetector(float displayDensity) {
        mMovementLimitPx = MOVEMENT_LIMIT_DP * displayDensity;
    }

    private int getGestureId() {
        int mTotalPointerCount = mPointers.size();

        if (mTotalPointerCount == 1) {
            Pointer mPointer = mPointers.get(0);

            if (mPointer.existedWithinTimeLimit(TIME_LIMIT)) {
                if (mPointer.tapped() && mPointer.existedWithinTimeLimit(100)) {
                    return TAP;
                } else if (mPointer.swipedUp()) {
                    return SWIPE_UP;
                } else if (mPointer.swipedDown()) {
                    return SWIPE_DOWN;
                } else if (mPointer.swipedLeft()) {
                    return SWIPE_LEFT;
                } else if (mPointer.swipedRight()) {
                    return SWIPE_RIGHT;
                } else {
                    return INVALID_GESTURE;
                }
            } else {
                return INVALID_GESTURE;
            }
        } else if (mTotalPointerCount == 2) {
            Pointer mPointerI = mPointers.get(0);
            Pointer mPointerII = mPointers.get(1);

            if (mPointerI.existedWithinTimeLimit(TIME_LIMIT) &&
                    mPointerII.existedWithinTimeLimit(TIME_LIMIT)) {

                if (mPointerI.tapped() &&
                        mPointerII.tapped()) {

                    return TWO_FINGER_TAP;
                } else if (mPointerI.swipedUp() &&
                        mPointerII.swipedUp()) {

                    return TWO_FINGER_SWIPE_UP;
                } else if (mPointerI.swipedDown() &&
                        mPointerII.swipedDown()) {

                    return TWO_FINGER_SWIPE_DOWN;
                } else if (mPointerI.swipedLeft() &&
                        mPointerII.swipedLeft()) {

                    return TWO_FINGER_SWIPE_LEFT;
                } else if (mPointerI.swipedRight() &&
                        mPointerII.swipedRight()) {

                    return TWO_FINGER_SWIPE_RIGHT;
                } else if (mPointerI.pinchedIn(mPointerII, mMovementLimitPx)) {
                    return TWO_FINGER_PINCH_IN;
                } else if (mPointerI.pinchedOut(mPointerII, mMovementLimitPx)) {
                    return TWO_FINGER_PINCH_OUT;
                } else {
                    return INVALID_GESTURE;
                }
            } else {
                return INVALID_GESTURE;
            }
        } else {
            return INVALID_GESTURE;
        }
    }

    public abstract boolean onGesture(View pView, int pGestureId);

    public abstract boolean onTouchResult(View view, MotionEvent motionEvent);


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int mActionIndex = motionEvent.getActionIndex();

        int mPointerId = motionEvent.getPointerId(mActionIndex);
        long mEventTime = motionEvent.getEventTime();
        float mX = motionEvent.getX(mActionIndex);
        float mY = motionEvent.getY(mActionIndex);

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPointers = new ArrayList<Pointer>();

                mPointers.add(new Pointer(mPointerId,
                        mEventTime,
                        mX, mY,
                        mMovementLimitPx));
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mPointers.add(new Pointer(mPointerId,
                        mEventTime,
                        mX, mY,
                        mMovementLimitPx));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                for (int pIndex = mPointers.size() - 1 ; pIndex >= 0; pIndex--) {
                    if (mPointers.get(pIndex).getId() == mPointerId) {
                        mPointers.get(pIndex).setUpTime(mEventTime);
                        mPointers.get(pIndex).setUpX(mX);
                        mPointers.get(pIndex).setUpY(mY);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                for (int pIndex = mPointers.size() - 1 ; pIndex >= 0; pIndex--) {
                    if (mPointers.get(pIndex).getId() == mPointerId) {
                        mPointers.get(pIndex).setUpTime(mEventTime);
                        mPointers.get(pIndex).setUpX(mX);
                        mPointers.get(pIndex).setUpY(mY);
                        break;
                    }
                }

                return onGesture(view, getGestureId());
        }
        return onTouchResult(view, motionEvent);
    }
}