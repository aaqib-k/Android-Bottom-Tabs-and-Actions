package com.github.aaqib786.bottomnavigationaction;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

public class NavActionBarLayout extends FrameLayout {

    static final float NAVBAR_PORTRAIT_FACTOR = 0.16f;
    static final float NAVBAR_LANDSCAPE_FACTOR = 0.09f;

    FrameLayout containerView;
    NavActionBar navActionBar;

    int width, height;

    public NavActionBarLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public NavActionBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NavActionBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        containerView = new FrameLayout(getContext(), attrs);
        containerView.setId(Utils.generateViewId());

        navActionBar = new NavActionBar(getContext(), attrs, containerView.getId());
        navActionBar.setId(Utils.generateViewId());

        addView(containerView);
        addView(navActionBar);
    }

    private void calculateValuesOnMeasure() {
        int navBarHeight;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            navBarHeight = (int) (NAVBAR_PORTRAIT_FACTOR * width);
        } else {
            navBarHeight = (int) (NAVBAR_LANDSCAPE_FACTOR * width);
        }

        LayoutParams layoutParams = new LayoutParams(
                width,
                height - navBarHeight,
                Gravity.TOP);
        containerView.setLayoutParams(layoutParams);

        layoutParams = new LayoutParams(
                width,
                height,
                Gravity.BOTTOM);
        navActionBar.setLayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        if (width <= 0)
            width = Utils.getScreenWidth(getContext());
        if (height <= 0)
            height = Utils.getScreenWidth(getContext());

        calculateValuesOnMeasure();
        setMeasuredDimension(width, height);
    }


    public void addTab(int tabNameResID, int resourceID) throws IllegalArgumentException {
        addTab(getContext().getString(tabNameResID), resourceID);
    }

    public void addTab(String tabName, int resourceID) throws IllegalArgumentException {
        navActionBar.addTab(tabName, resourceID);
    }

    public void addAction(int actionNameResID, int actionDescriptionResID, int resourceID) throws IllegalStateException {
        addAction(getContext().getString(actionNameResID),
                getContext().getString(actionDescriptionResID),
                resourceID);
    }

    public void addAction(String actionName, String actionDescription, int resourceID) throws IllegalStateException {
        navActionBar.addAction(actionName, actionDescription, resourceID);
    }

    public void setSelectionCallback(SelectionCallback selectionCallback) {
        navActionBar.setSelectionCallback(selectionCallback);
    }

    public void setSelectedTab(int index) throws IllegalArgumentException {
        navActionBar.setSelectedTab(index);
    }

    public int getSelectedTabIndex() {
        return navActionBar.getSelectedTabIndex();
    }

    public void setActionPosition(ActionPosition actionPosition) {
        navActionBar.setActionPosition(actionPosition);
    }

    public int getContainerId() {
        return containerView.getId();
    }


    private static class SavedState extends BaseSavedState {
        int selectedTab;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedTab = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selectedTab);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.selectedTab = navActionBar.getSelectedTabIndex();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        navActionBar.setSelectedTab(ss.selectedTab);
    }

}
