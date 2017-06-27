package com.example.ben.final_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * UnderlinedBoldTextView class - in order to set automatically an underlined
 * and bold android.widget.TextView
 */
@SuppressLint("AppCompatCustomView")
public class UnderlinedBoldTextView extends TextView {
    public UnderlinedBoldTextView(Context context) {
        super(context);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
    }

    public UnderlinedBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
    }

    public UnderlinedBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
    }
}
