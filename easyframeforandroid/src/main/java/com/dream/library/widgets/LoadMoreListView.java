package com.dream.library.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dream.library.R;
import com.dream.library.widgets.progress.CircularProgressBar;


public class LoadMoreListView extends ListView implements OnScrollListener, View.OnClickListener {

    private OnScrollListener mOnScrollListener;
    private LayoutInflater mInflater;

    private RelativeLayout      mFooterView;
    private TextView            mTextLoadMore;
    private CircularProgressBar mProgressBarLoadMore;

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mIsLoadingMore = false;
    private boolean mCanLoadMore = true;
    private int mCurrentScrollState;

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooterView = (RelativeLayout) mInflater.inflate(R.layout.common_load_more_footer, this, false);
        mTextLoadMore = (TextView) mFooterView.findViewById(R.id.common_load_more_footer_msg);
        mProgressBarLoadMore = (CircularProgressBar) mFooterView.findViewById(R.id.common_load_more_footer_progress);

        addFooterView(mFooterView);
        mFooterView.setOnClickListener(this);
        super.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (mOnLoadMoreListener != null) {

            if (visibleItemCount == totalItemCount) {
                mProgressBarLoadMore.setVisibility(View.GONE);
//                mTextLoadMore.setVisibility(View.GONE);
                return;
            }

            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

            if (!mIsLoadingMore && loadMore && mCurrentScrollState != SCROLL_STATE_IDLE) {
                if (!mCanLoadMore) {
                    mTextLoadMore.setVisibility(View.VISIBLE);
                    return;
                }
                mProgressBarLoadMore.setVisibility(View.VISIBLE);
                mTextLoadMore.setVisibility(View.GONE);
                mIsLoadingMore = true;
                onLoadMore();
            }

        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }

    }


    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        mCanLoadMore = canLoadMore;
        if (canLoadMore) {
            mTextLoadMore.setVisibility(View.GONE);
        } else {
            mTextLoadMore.setVisibility(View.VISIBLE);
        }
    }

    private void onLoadMore() {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
        mProgressBarLoadMore.setVisibility(View.GONE);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}