package com.github.aaqib786.bottomnavigationaction;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aaqib.bottomnavigationaction.R;

class NavActionBar extends View {

    private static final int DEFAULT_TAB_BACKGROUND_COLOR = Color.parseColor("#3F51B5");
    private static final int DEFAULT_TAB_TEXT_COLOR = Color.parseColor("#ffffff");
    private static final int DEFAULT_ACTION_BACKGROUND_COLOR = Color.parseColor("#FF5722");
    private static final int DEFAULT_ACTION_BACKGROUND_COLOR_SELECTED = Color.parseColor("#d84315");
    private static final int DEFAULT_ACTION_TEXT_COLOR_PRIMARY = Color.parseColor("#ffffff");
    private static final int DEFAULT_ACTION_TEXT_COLOR_SECONDARY = Color.parseColor("#ffffff");
    private static final int DEFAULT_TINT_COLOR = Color.parseColor("#80000000");
    private static final int MAX_FPS = 60;
    private static final float ANIMATION_FACTOR = 1.2f;
    private static final int BAR_BORDER_THICKNESS = Utils.dpToPx(1);

    private int tabBackgroundColor = DEFAULT_TAB_BACKGROUND_COLOR;
    private int tabTextColor = DEFAULT_TAB_TEXT_COLOR;
    private int actionBackgroundColor = DEFAULT_ACTION_BACKGROUND_COLOR;
    private int actionBackgroundColorSelected = DEFAULT_ACTION_BACKGROUND_COLOR_SELECTED;
    private int actionTextColorPrimary = DEFAULT_ACTION_TEXT_COLOR_PRIMARY;
    private int actionTextColorSecondary = DEFAULT_ACTION_TEXT_COLOR_SECONDARY;

    private ArrayMap<Integer, Item> tabItems = new ArrayMap<>();
    private ArrayMap<Integer, Item> actionItems = new ArrayMap<>();

    private Paint mPaintTabBackground;
    private TextPaint mPaintTabText;
    private TextPaint mPaintTabTextSelected;
    private Paint mPaintActionBackground;
    private Paint mPaintActionBackgroundSelected;
    private TextPaint mPaintActionTextPrimary;
    private TextPaint mPaintActionTextSecondary;
    private Paint mPaintTint;

    private int containerResID;
    private int width, widthHalf, height, barHeight, topHeight;
    private int tabPadding, tabIconSize, tabIconSizeHalf, tabTextY;
    private int tabIconSizeSelected, tabIconSizeHalfSelected;
    private float actionFromCenterX, actionFromCenterY;
    private float actionCurrRadius, actionToRadius;
    private int actionIconSize, actionIconSizeHalf;
    private float actionNameY, actionDescY;
    private float actionShadowOffsetX, actionShadowOffsetY;
    private float actionRadius;

    private SelectionCallback mCallback;
    private int selectedTab = 0;
    private int selectedAction = -1;
    private ActionPosition actionPosition = ActionPosition.MIDDLE;

    private int tabItemsSizeMinusOne;
    private int actionItemsSizeMinusOne;
    private long lastTime;
    private long deltaTime = MAX_FPS;
    private ActionsState actionsState = ActionsState.IDLE_START;
    private float animationStep;
    private boolean firstTime = true;

    private enum ActionsState {
        IDLE_START, INCOMING, OUTGOING, IDLE_END
    }

    public void addTab(int tabNameResID, int resourceID) throws IllegalArgumentException {
        addTab(getContext().getString(tabNameResID), resourceID);
    }

    public void addTab(String tabName, int resourceID) throws IllegalArgumentException {
        if (TextUtils.isEmpty(tabName))
            throw new IllegalArgumentException("Tab name cannot be empty");
        tabItems.put(tabItems.size(), new Item(tabItems.size(), tabName, resourceID));
    }

    public void addAction(int actionNameResID, int actionDescriptionResID, int resourceID) throws IllegalStateException {
        addAction(getContext().getString(actionNameResID),
                getContext().getString(actionDescriptionResID),
                resourceID);
    }

    public void addAction(String actionName, String actionDescription, int resourceID) throws IllegalStateException {
        if (actionItems.size() < 4) {
            actionItems.put(actionItems.size(),
                    new Item(actionItems.size(),
                            actionName,
                            actionDescription,
                            resourceID));
        } else {
            throw new IllegalStateException("Maximum 4 actions have been reached. Cannot add more than that.");
        }
    }

    public void setSelectionCallback(SelectionCallback selectionCallback) {
        this.mCallback = selectionCallback;
    }

    public void setSelectedTab(int index) throws IllegalArgumentException {
        if (index >= tabItems.size())
            throw new IllegalArgumentException("index is " + index + " but tab items size is " + tabItems.size());
        selectedTab = index;
        invalidate();
    }

    public void setActionPosition(ActionPosition actionPosition) {
        this.actionPosition = actionPosition;
    }

    public int getSelectedTabIndex() {
        return selectedTab;
    }

    public NavActionBar(Context context, int containerResID) {
        super(context);
        init(context, null, containerResID);
    }

    public NavActionBar(Context context, @Nullable AttributeSet attrs, int containerResID) {
        super(context, attrs);
        init(context, attrs, containerResID);
    }

    private void init(Context context, AttributeSet attrs, int containerResID) {
        this.containerResID = containerResID;
        initAttributes(context, attrs);
        initObjects();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.NavActionBarLayout,
                    0, 0);
            try {
                if (a.hasValue(R.styleable.NavActionBarLayout_tabBackgroundColor)) {
                    tabBackgroundColor = a.getColor(R.styleable.NavActionBarLayout_tabBackgroundColor,
                            DEFAULT_TAB_BACKGROUND_COLOR);
                }
                if (a.hasValue(R.styleable.NavActionBarLayout_tabTextColor)) {
                    tabTextColor = a.getColor(R.styleable.NavActionBarLayout_tabTextColor,
                            DEFAULT_TAB_TEXT_COLOR);
                }
                if (a.hasValue(R.styleable.NavActionBarLayout_actionBackgroundColor)) {
                    actionBackgroundColor = a.getColor(R.styleable.NavActionBarLayout_actionBackgroundColor,
                            DEFAULT_ACTION_BACKGROUND_COLOR);
                }
                if (a.hasValue(R.styleable.NavActionBarLayout_actionBackgroundColorSelected)) {
                    actionBackgroundColorSelected = a.getColor(R.styleable.NavActionBarLayout_actionBackgroundColorSelected,
                            DEFAULT_ACTION_BACKGROUND_COLOR_SELECTED);
                }
                if (a.hasValue(R.styleable.NavActionBarLayout_actionTextColorPrimary)) {
                    actionTextColorPrimary = a.getColor(R.styleable.NavActionBarLayout_actionTextColorPrimary,
                            DEFAULT_ACTION_TEXT_COLOR_PRIMARY);
                }
                if (a.hasValue(R.styleable.NavActionBarLayout_actionTextColorSecondary)) {
                    actionTextColorSecondary = a.getColor(R.styleable.NavActionBarLayout_actionTextColorSecondary,
                            DEFAULT_ACTION_TEXT_COLOR_SECONDARY);
                }
                if (a.hasValue(R.styleable.NavActionBarLayout_tabHeight)) {
                    barHeight = a.getDimensionPixelSize(R.styleable.NavActionBarLayout_tabHeight, 0);
                }
                if (a.hasValue(R.styleable.NavActionBarLayout_actionRadius)) {
                    actionRadius = a.getDimensionPixelSize(R.styleable.NavActionBarLayout_actionRadius, 0);
                }
            } finally {
                a.recycle();
            }
        }
    }

    private void initObjects() {
        mPaintTabBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTabBackground.setStyle(Paint.Style.FILL);
        mPaintTabBackground.setColor(tabBackgroundColor);

        mPaintTabText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaintTabText.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintTabText.setTextAlign(Paint.Align.CENTER);
        mPaintTabText.setLinearText(true);
        mPaintTabText.setColor(tabTextColor);
        mPaintTabText.setAlpha(127);

        mPaintTabTextSelected = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaintTabTextSelected.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintTabTextSelected.setTextAlign(Paint.Align.CENTER);
        mPaintTabTextSelected.setLinearText(true);
        mPaintTabTextSelected.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintTabTextSelected.setColor(tabTextColor);

        mPaintActionBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintActionBackground.setStyle(Paint.Style.FILL);
        mPaintActionBackground.setColor(actionBackgroundColor);

        mPaintActionBackgroundSelected = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintActionBackgroundSelected.setStyle(Paint.Style.FILL);
        mPaintActionBackgroundSelected.setColor(actionBackgroundColorSelected);

        mPaintActionTextPrimary = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaintActionTextPrimary.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintActionTextPrimary.setTextAlign(Paint.Align.CENTER);
        mPaintActionTextPrimary.setLinearText(true);
        mPaintActionTextPrimary.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintActionTextPrimary.setColor(actionTextColorPrimary);

        mPaintActionTextSecondary = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaintActionTextSecondary.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintActionTextSecondary.setTextAlign(Paint.Align.CENTER);
        mPaintActionTextSecondary.setLinearText(true);
        mPaintActionTextSecondary.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        mPaintActionTextSecondary.setColor(actionTextColorSecondary);

        mPaintTint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTint.setStyle(Paint.Style.FILL);
        mPaintTint.setColor(DEFAULT_TINT_COLOR);
    }

    private void calculateValuesOnMeasure() {
        tabItemsSizeMinusOne = tabItems.size() - 1;
        actionItemsSizeMinusOne = actionItems.size() - 1;
        float tabAdjustment = 0.3f * barHeight;
        if (actionRadius <= 0) {
            actionRadius = 0.6f * barHeight;
        }
        actionIconSize = (int) (1f * actionRadius);
        actionIconSizeHalf = actionIconSize / 2;
        actionShadowOffsetX = 0.1f * actionIconSizeHalf;
        actionShadowOffsetY = 0.2f * actionIconSizeHalf;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            actionNameY = height * 0.3f;
            actionDescY = height * 0.35f;
        } else {
            actionNameY = height * 0.15f;
            actionDescY = height * 0.20f;
        }

        if (tabItemsSizeMinusOne >= 0) {
            int tabWidth = width / tabItems.size();
            boolean adjustmentNeeded = actionItemsSizeMinusOne >= 0 && tabWidth < (3 * (actionRadius * 2));
            int adjustTab1 = -1;
            int adjustTab2 = -1;
            if (adjustmentNeeded) {
                tabWidth = (width - (int) (tabAdjustment * 2)) / tabItems.size();
                if (actionPosition == ActionPosition.START) {
                    adjustTab1 = 0;
                    adjustTab2 = 1;
                } else if (actionPosition == ActionPosition.MIDDLE) {
                    int adjustNearSeparator = (tabItemsSizeMinusOne + 1) / 2;
                    adjustTab1 = adjustNearSeparator - 1;
                    adjustTab2 = adjustNearSeparator;
                } else {
                    adjustTab1 = tabItemsSizeMinusOne - 1;
                    adjustTab2 = tabItemsSizeMinusOne;
                }
            }
            int minTabSize = Math.min(tabWidth, barHeight);
            tabPadding = (int) (0.15f * minTabSize);
            tabIconSize = (int) (0.4f * minTabSize);
            tabIconSizeHalf = tabIconSize / 2;
            tabIconSizeSelected = (int) (0.45f * minTabSize);
            tabIconSizeHalfSelected = tabIconSizeSelected / 2;
            mPaintTabText.setTextSize(0.2f * minTabSize);
            mPaintTabTextSelected.setTextSize(mPaintTabText.getTextSize() * 1.1f);
            tabTextY = (int) (height - (0.16f * barHeight));

            int currentX = 0;
            int endX;
            float nameWidth = tabWidth - (tabPadding * 2);
            for (int i = 0; i <= tabItemsSizeMinusOne; i++) {
                Item tabItem = tabItems.get(i);
                endX = currentX + tabWidth;
                if (adjustmentNeeded) {
                    if (i == adjustTab1) {
                        tabItem.adjustmentRight = tabAdjustment;
                        endX += tabAdjustment;
                    } else if (i == adjustTab2) {
                        tabItem.adjustmentLeft = tabAdjustment;
                        endX += tabAdjustment;
                    }
                }
                tabItem.bounds.set(currentX, topHeight, endX, topHeight + barHeight);
                tabItem.widthHalf = tabWidth / 2;
                tabItem.name = (String) TextUtils.ellipsize(tabItem.name,
                        mPaintTabText,
                        nameWidth,
                        TextUtils.TruncateAt.END);
                if (i != tabItemsSizeMinusOne) {
                    currentX = endX;
                }
            }
        }

        if (actionItemsSizeMinusOne >= 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mPaintActionTextPrimary.setTextSize(0.08f * width);
                mPaintActionTextSecondary.setTextSize(0.04f * width);
            } else {
                mPaintActionTextPrimary.setTextSize(0.04f * width);
                mPaintActionTextSecondary.setTextSize(0.02f * width);
            }

            Item mainActionItem = actionItems.get(0);
            if (actionPosition == ActionPosition.START) {
                if (tabItemsSizeMinusOne >= 0) {
                    mainActionItem.centerX = tabItems.get(0).bounds.right;
                } else {
                    mainActionItem.centerX = width * 0.25f;
                }
            } else if (actionPosition == ActionPosition.MIDDLE) {
                if (tabItemsSizeMinusOne >= 0) {
                    int middle = (tabItemsSizeMinusOne + 1) / 2;
                    mainActionItem.centerX = tabItems.get(middle).bounds.left;
                } else {
                    mainActionItem.centerX = width * 0.5f;
                }
            } else {
                if (tabItemsSizeMinusOne >= 0) {
                    mainActionItem.centerX = tabItems.get(tabItemsSizeMinusOne).bounds.left;
                } else {
                    mainActionItem.centerX = width * 0.75f;
                }
            }
            mainActionItem.centerY = topHeight;
            mainActionItem.widthHalf = actionRadius;
            mainActionItem.bounds.set(mainActionItem.centerX - mainActionItem.widthHalf,
                    mainActionItem.centerY - mainActionItem.widthHalf,
                    mainActionItem.centerX + mainActionItem.widthHalf,
                    mainActionItem.centerY + mainActionItem.widthHalf);

            actionFromCenterX = mainActionItem.centerX;
            actionFromCenterY = mainActionItem.centerY;
            actionCurrRadius = 0;
            actionToRadius = 4 * actionRadius;
            animationStep = actionToRadius / 200;
            if (animationStep == 0) {
                animationStep = 1;
            }
            double[] angles = new double[3];
            int widthHalf = width / 2;
            int centerAreaHalf = (int) ((0.3f * width) / 2f);
            int centerStart = widthHalf - centerAreaHalf;
            int centerEnd = widthHalf + centerAreaHalf;
            if (actionItemsSizeMinusOne == 1) {
                angles[0] = 1.5708; // 90 deg
            } else if (actionItemsSizeMinusOne == 2) {
                if (mainActionItem.centerX < centerStart) {
                    angles[0] = 1.39626; // 80 deg
                    angles[1] = 0.523599; // 30 deg
                } else if (mainActionItem.centerX > centerEnd) {
                    angles[0] = 2.61799; // 150 deg
                    angles[1] = 1.74533; // 100 deg
                } else {
                    angles[0] = 2.0944; // 120 deg
                    angles[1] = 1.0472; // 60 deg
                }
            } else if (actionItemsSizeMinusOne == 3) {
                if (mainActionItem.centerX < centerStart) {
                    angles[0] = 1.74533; // 100 deg
                    angles[1] = 1.0472; // 60 deg
                    angles[2] = 0.349066; // 20 deg
                } else if (mainActionItem.centerX > centerEnd) {
                    angles[0] = 2.79253; // 160 deg
                    angles[1] = 2.0944; // 120 deg
                    angles[2] = 1.39626; // 80 deg
                } else {
                    angles[0] = 2.35619; // 135 deg
                    angles[1] = 1.5708; // 90 deg
                    angles[2] = 0.785398; // 45 deg
                }
            }

            float nameDescWidth = 0.8f * width;
            for (int i = 1; i <= actionItemsSizeMinusOne; i++) {
                Item actionItem = actionItems.get(i);
                actionItem.centerX = mainActionItem.centerX;
                actionItem.centerY = mainActionItem.centerY;
                actionItem.widthHalf = mainActionItem.widthHalf;
                actionItem.bounds.set(actionItem.centerX - actionItem.widthHalf,
                        actionItem.centerY - actionItem.widthHalf,
                        actionItem.centerX + actionItem.widthHalf,
                        actionItem.centerY + actionItem.widthHalf);
                actionItem.angleRadians = angles[i - 1];
                actionItem.name = (String) TextUtils.ellipsize(actionItem.name,
                        mPaintActionTextPrimary,
                        nameDescWidth,
                        TextUtils.TruncateAt.END);
                actionItem.description = (String) TextUtils.ellipsize(actionItem.description,
                        mPaintActionTextSecondary,
                        nameDescWidth,
                        TextUtils.TruncateAt.END);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTabsAndSeparators(canvas);
        drawActionAndSubActions(canvas);
        calculateDeltaTime();
        updateValues();
        if (firstTime && tabItemsSizeMinusOne >= 0) {
            firstTime = false;
            if (mCallback != null && selectedTab >= 0) {
                mCallback.onTabSelected(tabItems.get(selectedTab), containerResID);
            }
        }
    }

    private void updateValues() {
        if (actionItemsSizeMinusOne > 0) {
            if (actionsState == ActionsState.INCOMING || actionsState == ActionsState.OUTGOING) {
                if (actionsState == ActionsState.INCOMING) {
                    actionCurrRadius -= ANIMATION_FACTOR * animationStep * deltaTime;
                    if (actionCurrRadius <= 0) {
                        actionCurrRadius = 0;
                        actionsState = ActionsState.IDLE_START;
                    }
                } else {
                    actionCurrRadius += ANIMATION_FACTOR * animationStep * deltaTime;
                    if (actionCurrRadius >= actionToRadius) {
                        actionCurrRadius = actionToRadius;
                        actionsState = ActionsState.IDLE_END;
                    }
                }
                for (int i = 1; i <= actionItemsSizeMinusOne; i++) {
                    updateActionItem(actionItems.get(i));
                }
                invalidate();
            }
        }
    }

    private void updateActionItem(Item item) {
        item.centerX = (float) (actionFromCenterX + actionCurrRadius * Math.cos(item.angleRadians));
        item.centerY = (float) (actionFromCenterY - actionCurrRadius * Math.sin(item.angleRadians));
        item.bounds.set(item.centerX - item.widthHalf,
                item.centerY - item.widthHalf,
                item.centerX + item.widthHalf,
                item.centerY + item.widthHalf);
    }

    private void calculateDeltaTime() {
        long currTime = System.currentTimeMillis();
        deltaTime = currTime - lastTime;
        if (deltaTime > MAX_FPS)
            deltaTime = MAX_FPS;
        lastTime = currTime;
    }

    private void drawTabsAndSeparators(Canvas canvas) {
        if (tabItemsSizeMinusOne >= 0) {
            canvas.drawRect(0,
                    topHeight - BAR_BORDER_THICKNESS,
                    width,
                    topHeight,
                    mPaintTint);
            canvas.drawRect(0,
                    topHeight,
                    width,
                    height,
                    mPaintTabBackground);
        }
        for (int i = 0; i <= tabItemsSizeMinusOne; i++) {
            drawTab(canvas, tabItems.get(i));
        }
    }

    private void drawTab(Canvas canvas, Item item) {
        int centerX;
        if (item.adjustmentLeft > 0) {
            centerX = (int) (item.bounds.left + item.adjustmentLeft + ((item.bounds.right - (item.bounds.left + item.adjustmentLeft)) / 2));
        } else if (item.adjustmentRight > 0) {
            centerX = (int) (item.bounds.left + ((item.bounds.right - item.adjustmentRight - item.bounds.left) / 2));
        } else {
            centerX = (int) (item.bounds.left + ((item.bounds.right - item.bounds.left) / 2));
        }
        Drawable drawable = Utils.getDrawable(getContext(), item.resID);
        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();
        float scale = drawableHeight / drawableWidth;
        int iconSize = selectedTab == item.index ? tabIconSizeSelected : tabIconSize;
        int iconSizeHalf = selectedTab == item.index ? tabIconSizeHalfSelected : tabIconSizeHalf;
        if (scale == 1.0f) {
            // Width == Height
            drawable.setBounds(centerX - iconSizeHalf,
                    topHeight + tabPadding,
                    centerX + iconSizeHalf,
                    topHeight + tabPadding + iconSize);
        } else if (scale > 1.0f) {
            // Height > Width
            drawableWidth = (float) iconSize / scale;
            int drawableWidthHalf = (int) (drawableWidth / 2);
            drawable.setBounds(centerX - drawableWidthHalf,
                    topHeight + tabPadding,
                    centerX + drawableWidthHalf,
                    topHeight + tabPadding + iconSize);
        } else {
            // Width > Height
            int iconCenterY = topHeight + tabPadding + iconSizeHalf;
            drawableHeight = (float) iconSize * scale;
            int drawableHeightHalf = (int) (drawableHeight / 2);
            drawable.setBounds(centerX - iconSizeHalf,
                    iconCenterY - drawableHeightHalf,
                    centerX + iconSizeHalf,
                    iconCenterY + drawableHeightHalf);
        }
        drawable.setAlpha(selectedTab == item.index ? 255 : 127);
        drawable.draw(canvas);
        canvas.drawText(item.name,
                centerX,
                tabTextY,
                selectedTab == item.index ? mPaintTabTextSelected : mPaintTabText);
    }

    private void drawActionAndSubActions(Canvas canvas) {
        if (actionsState != ActionsState.IDLE_START) {
            canvas.drawRect(0, 0, width, height, mPaintTint);
            for (int i = actionItemsSizeMinusOne; i >= 0; i--) {
                drawAction(canvas, actionItems.get(i));
            }
        } else if (actionItemsSizeMinusOne >= 0) {
            drawAction(canvas, actionItems.get(0));
        }
    }

    private void drawAction(Canvas canvas, Item item) {
        canvas.drawCircle(item.centerX + actionShadowOffsetX,
                item.centerY + actionShadowOffsetY,
                item.widthHalf,
                mPaintTint);
        canvas.drawCircle(item.centerX,
                item.centerY,
                item.widthHalf,
                selectedAction == item.index ? mPaintActionBackgroundSelected : mPaintActionBackground);
        Drawable drawable = Utils.getDrawable(getContext(), item.resID);
        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();
        float scale = drawableHeight / drawableWidth;
        if (scale == 1.0f) {
            // Width == Height
            drawable.setBounds((int) item.centerX - actionIconSizeHalf,
                    (int) item.centerY - actionIconSizeHalf,
                    (int) item.centerX + actionIconSizeHalf,
                    (int) item.centerY + actionIconSizeHalf);
        } else if (scale > 1.0f) {
            // Height > Width
            drawableWidth = (float) actionIconSize / scale;
            int drawableWidthHalf = (int) (drawableWidth / 2);
            drawable.setBounds((int) item.centerX - drawableWidthHalf,
                    (int) item.centerY - actionIconSizeHalf,
                    (int) item.centerX + drawableWidthHalf,
                    (int) item.centerY + actionIconSizeHalf);
        } else {
            // Width > Height
            drawableHeight = (float) actionIconSize * scale;
            int drawableHeightHalf = (int) (drawableHeight / 2);
            drawable.setBounds((int) item.centerX - actionIconSizeHalf,
                    (int) item.centerY - drawableHeightHalf,
                    (int) item.centerX + actionIconSizeHalf,
                    (int) item.centerY + drawableHeightHalf);
        }
        drawable.setAlpha((item.index == 0 || selectedAction == item.index) ? 255 : 127);
        drawable.draw(canvas);
        if (selectedAction == item.index) {
            if (!TextUtils.isEmpty(item.name))
                canvas.drawText(item.name,
                        widthHalf,
                        actionNameY,
                        mPaintActionTextPrimary);
            if (!TextUtils.isEmpty(item.description))
                canvas.drawText(item.description,
                        widthHalf,
                        actionDescY,
                        mPaintActionTextSecondary);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
        widthHalf = width / 2;

        if (barHeight <= 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                barHeight = (int) (NavActionBarLayout.NAVBAR_PORTRAIT_FACTOR * width);
            } else {
                barHeight = (int) (NavActionBarLayout.NAVBAR_LANDSCAPE_FACTOR * width);
            }
        }

        topHeight = height - barHeight;
        calculateValuesOnMeasure();
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onDownAndMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                boolean invalidate = checkAndSelect(x, y);
                if (actionsState != ActionsState.IDLE_START && actionsState != ActionsState.INCOMING) {
                    actionsState = ActionsState.INCOMING;
                    selectedAction = -1;
                    invalidate = true;
                }
                if (invalidate)
                    invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                onDownAndMove(x, y);
                break;
        }
        return true;
    }

    private boolean checkAndSelect(float x, float y) {
        if (x >= 0 && x <= width) {
            if (y > topHeight && y <= height) {
                // Tab or Action
                boolean inActionBounds = false;
                if (actionItemsSizeMinusOne >= 0) {
                    Item item = actionItems.get(0);
                    inActionBounds = item.bounds.contains(x, y);
                    if (actionItemsSizeMinusOne == 0 && inActionBounds) {
                        selectedAction = -1;
                        if (mCallback != null) {
                            mCallback.onActionSelected(item, containerResID);
                        }
                        return true;
                    }
                }
                if (!inActionBounds) {
                    for (int i = 0; i <= tabItemsSizeMinusOne; i++) {
                        Item item = tabItems.get(i);
                        if (item.bounds.contains(x, y)) {
                            selectedTab = item.index;
                            if (mCallback != null) {
                                mCallback.onTabSelected(item, containerResID);
                            }
                            return true;
                        }
                    }
                }
            } else if (y <= topHeight && y >= 0) {
                // Action
                int i = actionItemsSizeMinusOne > 0 ? 1 : 0;
                for (; i <= actionItemsSizeMinusOne; i++) {
                    Item item = actionItems.get(i);
                    if (item.bounds.contains(x, y)) {
                        selectedAction = -1;
                        if (mCallback != null) {
                            mCallback.onActionSelected(item, containerResID);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void onDownAndMove(float x, float y) {
        if (actionItemsSizeMinusOne > 0) {
            int i = actionsState == ActionsState.IDLE_START ? 0 : 1;
            for (; i <= actionItemsSizeMinusOne; i++) {
                Item item = actionItems.get(i);
                if (item.bounds.contains(x, y)) {
                    if (i == 0) {
                        actionsState = ActionsState.OUTGOING;
                        invalidate();
                    } else if (actionsState == ActionsState.IDLE_END && selectedAction != item.index) {
                        selectedAction = item.index;
                        invalidate();
                    }
                    return;
                }
            }
            if (selectedAction != -1) {
                selectedAction = -1;
                invalidate();
            }
        }
    }

}
