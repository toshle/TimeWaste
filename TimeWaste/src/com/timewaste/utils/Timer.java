package com.timewaste.utils;

import org.andengine.engine.handler.IUpdateHandler;
 
 
public class Timer implements IUpdateHandler {
        // ===========================================================
        // Constants          
        // ===========================================================
       
        // ===========================================================          
        // Fields        
        // ===========================================================
        private ITimerCallback mCallback;
        private float mInterval;
       
        private float mSecondsElapsed;
        // ===========================================================          
        // Constructors          
        // ===========================================================
        public Timer(final float pInterval, final ITimerCallback pCallback) {
                this.mInterval = pInterval;
                this.mCallback = pCallback;
        }
        // ===========================================================          
        // Getter & Setter          
        // ===========================================================
        public void setInterval(final float pInterval) {
                this.mInterval = pInterval;
        }
        // ===========================================================          
        // Methods for/from SuperClass/Interfaces          
        // ===========================================================  
        @Override
        public void onUpdate(float pSecondsElapsed) {
                this.mSecondsElapsed += pSecondsElapsed;
                if(this.mSecondsElapsed >= this.mInterval) {
                        this.mSecondsElapsed -= this.mInterval;
                        this.mCallback.onTick();
                }
        }
        @Override
        public void reset() {
                this.mSecondsElapsed = 0;
               
        }
 
        public interface ITimerCallback {
                public void onTick();
        }
}