package com.xiyoumusic.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class PinglunExpandableListView extends ExpandableListView {
    public PinglunExpandableListView(Context context) {
        super(context);
    }

    public PinglunExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinglunExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
