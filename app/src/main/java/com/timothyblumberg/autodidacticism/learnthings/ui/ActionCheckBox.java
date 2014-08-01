package com.timothyblumberg.autodidacticism.learnthings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;

/**
 * Created by Tim on 8/1/14.
 */
public class ActionCheckBox extends CheckBox {
    public ActionCheckBox(Context context) {
        super(context);
    }

    public ActionCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }
}
