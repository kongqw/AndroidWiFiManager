package kong.qingwei.kqwwifimanagerdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kqw on 2016/8/2.
 * KqwRecyclerView
 * 支持点击监听的RecyclerView
 */
public class KqwRecyclerView extends RecyclerView implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetector;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public KqwRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mGestureDetector = new GestureDetectorCompat(getContext(), new ItemTouchHelperGestureListener());
        addOnItemTouchListener(this);
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder vh = getChildViewHolder(child);
                // 回调
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(vh);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder vh = getChildViewHolder(child);
                // 回调
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(vh);
                }
            }
        }
    }


    public interface OnItemClickListener {
        void onItemClick(ViewHolder v);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(ViewHolder v);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        mOnItemLongClickListener = listener;
    }
}
