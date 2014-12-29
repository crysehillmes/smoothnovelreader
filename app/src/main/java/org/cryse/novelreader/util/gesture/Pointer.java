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

public class Pointer {

    // This Pointer's id.
    private int mId;

    // The time (in milliseconds) when this Pointer went down.
    private long mDownTime;

    // The x coordinate (in pixels) where this Pointer went down.
    private float mDownX;

    // The y coordinate (in pixels) where this Pointer went down.
    private float mDownY;

    // The time (in milliseconds) when this Pointer went up.
    private long mUpTime;

    // The x coordinate (in pixels) where this Pointer went up.
    private float mUpX;

    // The y coordinate (in pixels) where this Pointer went up.
    private float mUpY;

    // Limits for identifying taps and swipes.
    private float mUpXUpperLimit;
    private float mUpXLowerLimit;
    private float mUpYUpperLimit;
    private float mUpYLowerLimit;

    public Pointer(int pId,
                   long pDownTime,
                   float pDownX, float pDownY,
                   float pMovementLimitPx) {

        mId = pId;
        mDownTime = pDownTime;
        mDownX = pDownX;
        mDownY = pDownY;

        mUpXUpperLimit = mDownX + pMovementLimitPx;
        mUpXLowerLimit = mDownX - pMovementLimitPx;
        mUpYUpperLimit = mDownY + pMovementLimitPx;
        mUpYLowerLimit = mDownY - pMovementLimitPx;
    }

    public void setUpTime(long pUpTime) {
        mUpTime = pUpTime;
    }

    public void setUpX(float pUpX) {
        mUpX = pUpX;
    }

    public void setUpY(float pUpY) {
        mUpY = pUpY;
    }

    public int getId() {
        return mId;
    }

    public float getDownX() {
        return mDownX;
    }

    public float getDownY() {
        return mDownY;
    }

    public float getUpX() {
        return mUpX;
    }

    public float getUpY() {
        return mUpY;
    }

    public boolean existedWithinTimeLimit(int pTimeLimit) {
        return mUpTime - mDownTime <= pTimeLimit;
    }

    public boolean tapped() {
        return mUpX < mUpXUpperLimit &&
                mUpX > mUpXLowerLimit &&
                mUpY < mUpYUpperLimit &&
                mUpY > mUpYLowerLimit;
    }

    public boolean swipedUp() {
        return mUpX < mUpXUpperLimit &&
                mUpX > mUpXLowerLimit &&
                mUpY <= mUpYLowerLimit;
    }

    public boolean swipedDown() {
        return mUpX < mUpXUpperLimit &&
                mUpX > mUpXLowerLimit &&
                mUpY >= mUpYUpperLimit;
    }

    public boolean swipedLeft() {
        return mUpX <= mUpXLowerLimit &&
                mUpY < mUpYUpperLimit &&
                mUpY > mUpYLowerLimit;
    }

    public boolean swipedRight() {
        return mUpX >= mUpXUpperLimit &&
                mUpY < mUpYUpperLimit &&
                mUpY > mUpYLowerLimit;
    }

    private double distanceFormula(double pXI, double pYI,
                                   double pXII, double pYII) {

        return Math.sqrt(Math.pow(pXI - pXII, 2) + Math.pow(pYI - pYII, 2));
    }

    public boolean pinchedIn(Pointer pPointer, float pMovementLimitPx) {
        return distanceFormula(mDownX, mDownY, pPointer.getDownX(), pPointer.getDownY()) + pMovementLimitPx <=
                distanceFormula(mUpX, mUpY, pPointer.getUpX(), pPointer.getUpY());
    }

    public boolean pinchedOut(Pointer pPointer, float pMovementLimitPx) {
        return distanceFormula(mDownX, mDownY, pPointer.getDownX(), pPointer.getDownY()) - pMovementLimitPx >=
                distanceFormula(mUpX, mUpY, pPointer.getUpX(), pPointer.getUpY());
    }
}