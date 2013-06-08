package org.androidcare.android.widget;

import org.androidcare.android.R;

import android.content.Context;
import android.content.res.TypedArray;
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
    
    ImageButton draggable = null;
    TextView txtText = null;
    Thread thread = null;
    static final float animationSpeed = 2.5f;
    static final long animationInterval = 10;
    Handler handler = new Handler();
    View.OnClickListener onClickListener;
    boolean vibrate = false;
    
    //attributes
    String a_text = "";
    
    int lastLeft = 0;
    
    OnTouchListener d_onTouchListener = new OnTouchListener() {
        
        float origin;
        int left;
        
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            boolean result = false;
            float x = ev.getRawX();
            int action = ev.getAction();
            
            switch(action){
            case MotionEvent.ACTION_DOWN:
                origin = x;
                left = v.getLeft();
                result = true;
                stopOriginAnimation();
                vibrate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startOriginAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                int newPosition = (int)(x - origin);
                move(left + newPosition);
                break;
            default:
                // nothing to be done
            }
            
            return result;
        }
    };
    
    public void move(int left){
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
    
    private void vibrate() {
        if(vibrate){
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
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
        draggable.setImageResource(R.drawable.logo);
        
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
                handler.post(m_runnableOnTimer);
            }
                
        }
    };
    
    Runnable m_runnableOnTimer = new Runnable() {

        public void run() {
            onReturn();
        }
        
    };
    
    void onReturn() {
        int left = draggable.getLeft();
        
        int displacement = (int)(animationSpeed * animationInterval);
        move(left - displacement);
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
        vibrate = b;
    }
}
