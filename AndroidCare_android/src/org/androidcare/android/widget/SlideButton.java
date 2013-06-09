package org.androidcare.android.widget;

import java.net.URI;

import org.androidcare.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class SlideButton extends FrameLayout{
    
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    
    ImageButton draggable = null;
    TextView txtText = null;
    Thread thread = null;
    static final long animationTime = 750; // in milliseconds
    static final long animationInterval = 25;
    Handler handler = new Handler();
    View.OnClickListener onClickListener;
    
    //attributes
    String a_text = "";
    boolean a_vibrate = false;
    int a_vibration_length = 100;
    int a_orientation = SlideButton.HORIZONTAL;
    Drawable a_draggable_src;
    
    int lastLeft = 0;
    int lastTop = 0;
    
    OnTouchListener d_onTouchListener = new OnTouchListener() {
        
        float origin_x, origin_y;
        int left;
        int top;
        
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            boolean result = false;
            float x = ev.getRawX();
            float y = ev.getRawY();
            int action = ev.getAction();
            
            switch(action){
            case MotionEvent.ACTION_DOWN:
                origin_x = x;
                origin_y = y;
                left = v.getLeft();
                top = v.getTop();
                result = true;
                stopOriginAnimation();
                vibrate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startOriginAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                switch(a_orientation){
                case SlideButton.HORIZONTAL:
                    int newX = (int)(x - origin_x);
                    moveX(left + newX);
                    break;
                case SlideButton.VERTICAL:
                    int newY = (int)(y - origin_y);
                    moveY(top + newY);
                    break;
                default:
                    // nothing to be done
                }
                break;
            default:
                // nothing to be done
            }
            
            return result;
        }
    };
    
    public void moveX(int left){
        int width = draggable.getWidth();
        int height = draggable.getHeight();
        int maxLeft = getWidth() - width;
        
        if(left < 0){
            left = 0;
            stopOriginAnimation();
        }else if(left >= maxLeft){
            left = maxLeft;
            if(onClickListener != null && left != lastLeft){
                vibrate();
                onClickListener.onClick(this);
            }
        }
        
        int right = left + width;
        int top = draggable.getTop();
        int bottom = top + height;
        lastLeft = left;
        draggable.layout(left, top, right, bottom);
    }
    
    public void moveY(int top){
        int width = draggable.getWidth();
        int height = draggable.getHeight();
        int maxTop = getHeight() - height;
        
        if(top < 0){
            top = 0;
            stopOriginAnimation();
        }else if(top >= maxTop){
            top = maxTop;
            if(onClickListener != null && top != lastTop){
                vibrate();
                onClickListener.onClick(this);
            }
        }
        
        int left = draggable.getLeft();
        int right = left + width;
        int bottom = top + height;
        lastTop = top;
        draggable.layout(left, top, right, bottom);
    }
    
    private void vibrate() {
        if(a_vibrate){
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(a_vibration_length);
            vibrator.cancel();
        }
    }

    public interface OnClickListener {
        void onClick(SlideButton widget);
    }

    public SlideButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        parseAttributes(attrs);
        
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.slider_button, this);
        
        draggable = (ImageButton)findViewById(R.id.sb_draggable);
        draggable.setOnTouchListener(d_onTouchListener);
        
        if(a_draggable_src == null){
            draggable.setImageResource(android.R.drawable.sym_def_app_icon);
        }else{
            draggable.setImageDrawable(a_draggable_src);
        }
        
        
        txtText = (TextView)findViewById(R.id.sb_text);
        txtText.setText(a_text);
    }
    
    private void parseAttributes(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SlideButton);
       
        final int num = array.getIndexCount();
        for(int i = 0; i < num; i++){
            int attr = array.getIndex(i);
            switch(attr){
            case R.styleable.SlideButton_text:
                a_text = array.getString(attr);
                break;
            case R.styleable.SlideButton_vibrate:
                a_vibrate = array.getBoolean(attr, false);
                break;
            case R.styleable.SlideButton_vibration_length:
                a_vibration_length = array.getInteger(attr, 100);
                break;
            case R.styleable.SlideButton_orientation:
                a_orientation = array.getInteger(attr, SlideButton.HORIZONTAL);
                break;
            case R.styleable.SlideButton_draggable_src:
                a_draggable_src = array.getDrawable(attr);
                break;
            default:
                // nothing to be done
            }
        }
        array.recycle();
    }

    Runnable originAnimation = new Runnable() {

        public void run() {
            while(true) {
                try {
                    Thread.sleep(animationInterval);
                } catch (InterruptedException e) {
                    return;
                }
                handler.post(returnAnimation);
            }
                
        }
    };
    
    Runnable returnAnimation = new Runnable() {

        public void run() {
            onReturn();
        }
        
    };
    
    void onReturn() {
        int animationSteps = (int)(animationTime / animationInterval);
        switch(a_orientation){
        case SlideButton.HORIZONTAL:
            int left = draggable.getLeft();
            int widgetWidth = getWidth();
            int displacementX = widgetWidth / animationSteps;
            moveX(left - displacementX);
            break;
        case SlideButton.VERTICAL:
            int top = draggable.getTop();
            int widgetHeight = getHeight();
            
            int displacementY = widgetHeight / animationSteps;
            moveY(top - displacementY);
            break;
        default: 
            // nothing to be done
        }
    }
    
    public void startOriginAnimation(){
        stopOriginAnimation();
        thread = new Thread(originAnimation);
        thread.start();
    }
    
    public void stopOriginAnimation() {
        if(thread == null) {
                return;
        }
        thread.interrupt();
        thread = null;
    }
    
    @Override
    public void setOnClickListener(View.OnClickListener listener){
        onClickListener = listener;
    }

    public void setVibration(boolean b) {
        a_vibrate = b;
    }
}
