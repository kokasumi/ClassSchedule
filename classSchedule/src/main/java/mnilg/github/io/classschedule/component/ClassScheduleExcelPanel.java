package mnilg.github.io.classschedule.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import mnilg.github.io.classschedule.R;
import mnilg.github.io.classschedule.utils.Utils;

/**
 * 参考https://github.com/zhouchaoyuan/excelPanel的实现逻辑，实现课程表组件，该组件与原组件的不同之处在于课程长度是不固定的，也就是说Normal Cell的高度可变，且课程数组是按照每天
 * 课程的排列来给的，为了实现方便，需要将纵向Y轴与横向X轴对调
 */
public class ClassScheduleExcelPanel extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    public static final int TAG_KEY = R.id.lib_excel_panel_tag_key;
    public static final int DEFAULT_LENGTH = 56;
    public static final int LOADING_VIEW_WIDTH = 30;

    private int leftCellWidth;
    private int topCellHeight;
    private int normalCellWidth;
    private int amountAxisX = 0;
    private int amountAxisY = 0;
    private int dividerHeight;

    protected View dividerLine;
    protected RecyclerView mRecyclerView;
    protected RecyclerView topRecyclerView;
    protected RecyclerView leftRecyclerView;
    protected BaseExcelPanelAdapter excelPanelAdapter;
    private static Map<Integer, Integer> leftIndexHeight;
    private static Map<Integer,Map<Integer,Integer>> normalIndexHeightMap;

    public ClassScheduleExcelPanel(Context context) {
        this(context, null);
    }

    public ClassScheduleExcelPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ClassScheduleExcelPanel,
                0, 0);
        try {
            leftCellWidth = (int) a.getDimension(R.styleable.ClassScheduleExcelPanel_left_cell_width, Utils.dp2px(DEFAULT_LENGTH, getContext()));
            topCellHeight = (int) a.getDimension(R.styleable.ClassScheduleExcelPanel_top_cell_height, Utils.dp2px(DEFAULT_LENGTH, getContext()));
            normalCellWidth = (int) a.getDimension(R.styleable.ClassScheduleExcelPanel_normal_cell_width, Utils.dp2px(DEFAULT_LENGTH, getContext()));
        } finally {
            a.recycle();
        }
        leftIndexHeight = new TreeMap<>();
        normalIndexHeightMap = new TreeMap<>();
        initWidget();
    }

    private void initWidget() {

        //content's RecyclerView
        mRecyclerView = createMajorContent();
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LayoutParams mlp = (LayoutParams) mRecyclerView.getLayoutParams();
        mlp.leftMargin = leftCellWidth;
        mlp.topMargin = topCellHeight;
        mRecyclerView.setLayoutParams(mlp);

        //top RecyclerView
        topRecyclerView = createTopHeader();
        addView(topRecyclerView, new LayoutParams(LayoutParams.WRAP_CONTENT, topCellHeight));
        LayoutParams tlp = (LayoutParams) topRecyclerView.getLayoutParams();
        tlp.leftMargin = leftCellWidth;
        topRecyclerView.setLayoutParams(tlp);

        //left RecyclerView
        leftRecyclerView = createLeftHeader();
        addView(leftRecyclerView, new LayoutParams(leftCellWidth, LayoutParams.WRAP_CONTENT));
        LayoutParams llp = (LayoutParams) leftRecyclerView.getLayoutParams();
        llp.topMargin = topCellHeight;
        leftRecyclerView.setLayoutParams(llp);

        dividerLine = createDividerToLeftHeader();
        addView(dividerLine, new ViewGroup.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutParams lineLp = (LayoutParams) dividerLine.getLayoutParams();
        lineLp.leftMargin = leftCellWidth;
        dividerLine.setLayoutParams(lineLp);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onGlobalLayout() {
        if (dividerHeight == getMeasuredHeight() && getMeasuredHeight() != 0) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        LayoutParams lineLp1 = (LayoutParams) dividerLine.getLayoutParams();
        dividerHeight = lineLp1.height = getMeasuredHeight();
        dividerLine.setLayoutParams(lineLp1);
    }

    protected RecyclerView createTopHeader() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getTopLayoutManager());
        recyclerView.addOnScrollListener(contentScrollListener);
        return recyclerView;
    }

    protected RecyclerView createLeftHeader() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getLeftLayoutManager());
        recyclerView.addOnScrollListener(leftScrollListener);
        return recyclerView;
    }

    protected RecyclerView createMajorContent() {
        RecyclerView recyclerView = new ExcelMajorRecyclerView(getContext());
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.addOnScrollListener(contentScrollListener);
        return recyclerView;
    }

    protected View createDividerToLeftHeader() {
        View view = new View(getContext());
        view.setVisibility(GONE);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_line));
        return view;
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        if (null == mRecyclerView || null == mRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return layoutManager;
        }
        return mRecyclerView.getLayoutManager();
    }

    private RecyclerView.LayoutManager getTopLayoutManager() {
        if (null == topRecyclerView || null == topRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return layoutManager;
        }
        return topRecyclerView.getLayoutManager();
    }

    private RecyclerView.LayoutManager getLeftLayoutManager() {
        if (null == leftRecyclerView || null == leftRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            return layoutManager;
        }
        return leftRecyclerView.getLayoutManager();
    }

    /**
     * horizontal listener
     */
    private RecyclerView.OnScrollListener contentScrollListener
            = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            amountAxisX += dx;
            fastScrollTo(amountAxisX, mRecyclerView);
            fastScrollTo(amountAxisX, topRecyclerView);
            if (dx == 0 && dy == 0) {
                return;
            }
        }
    };

    /**
     * vertical listener
     */
    private RecyclerView.OnScrollListener leftScrollListener
            = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //if (dy == 0) {return;} can't do this if use reset(amountAxisY==0),excelPanel will dislocation
            amountAxisY += dy;
            for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                if (mRecyclerView.getChildAt(i) instanceof RecyclerView) {
                    RecyclerView recyclerView1 = (RecyclerView) mRecyclerView.getChildAt(i);
                    fastScrollVerticalNormal(amountAxisY, recyclerView1,i);
                }
            }
            fastScrollVerticalLeft();
            if (excelPanelAdapter != null) {
                excelPanelAdapter.setAmountAxisY(amountAxisY);
            }
        }
    };

    void fastScrollVerticalLeft() {
        fastScrollVertical(amountAxisY, leftRecyclerView,leftIndexHeight);
    }

    /**
     * Normal Cell垂直方向上快速滑动
     * @param amountAxis
     * @param recyclerView
     * @param positionX
     */
    static void fastScrollVerticalNormal(int amountAxis,RecyclerView recyclerView,int positionX) {
        fastScrollVertical(amountAxis,recyclerView,normalIndexHeightMap.get(positionX));
    }

    /**
     * Cell垂直方向上快速滑动
     * @param amountAxis
     * @param recyclerView
     * @param indexHeight
     */
    private static void fastScrollVertical(int amountAxis,RecyclerView recyclerView,Map<Integer,Integer> indexHeight) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (indexHeight == null) {
            indexHeight = new TreeMap<>();
            //call this method the OnScrollListener's onScrolled will be called，but dx and dy always be zero.
            linearLayoutManager.scrollToPositionWithOffset(0, -amountAxis);
        } else {
            int total = 0, count = 0;
            Iterator<Integer> iterator = indexHeight.keySet().iterator();
            while (null != iterator && iterator.hasNext()) {
                int height = indexHeight.get(iterator.next());
                if (total + height >= amountAxis) {
                    break;
                }
                total += height;
                count++;
            }
            linearLayoutManager.scrollToPositionWithOffset(count, -(amountAxis - total));
        }
    }

    private void fastScrollTo(int amountAxis, RecyclerView recyclerView) {
        int position = 0, width = normalCellWidth;
        position += amountAxis / width;
        amountAxis %= width;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //call this method the OnScrollListener's onScrolled will be called，but dx and dy always be zero.
        linearLayoutManager.scrollToPositionWithOffset(position, -amountAxis);
    }

    public void setAdapter(BaseExcelPanelAdapter excelPanelAdapter) {
        if (excelPanelAdapter != null) {
            this.excelPanelAdapter = excelPanelAdapter;
            this.excelPanelAdapter.setLeftCellWidth(leftCellWidth);
            this.excelPanelAdapter.setTopCellHeight(topCellHeight);
            this.excelPanelAdapter.setOnScrollListener(leftScrollListener);
            this.excelPanelAdapter.setExcelPanel(this);
            distributeAdapter();
        }
    }

    private void distributeAdapter() {
        if (leftRecyclerView != null) {
            leftRecyclerView.setAdapter(excelPanelAdapter.getLeftRecyclerViewAdapter());
        }
        if (topRecyclerView != null) {
            topRecyclerView.setAdapter(excelPanelAdapter.getTopRecyclerViewAdapter());
        }
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(excelPanelAdapter.getmRecyclerViewAdapter());
        }
    }

    /**
     * @param dx horizontal distance to scroll
     */
    void scrollBy(int dx) {
        contentScrollListener.onScrolled(mRecyclerView, dx, 0);
    }

    public boolean canChildScrollUp() {
        return amountAxisY > 0;
    }

    public void reset() {
        if (leftIndexHeight == null) {
            leftIndexHeight = new TreeMap<>();
        }
        leftIndexHeight.clear();
        if(normalIndexHeightMap == null){
            normalIndexHeightMap = new TreeMap<>();
        }
        normalIndexHeightMap.clear();
        amountAxisY = 0;
        amountAxisX = 0;
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public int findFirstVisibleItemPosition() {
        int position = -1;
        if (mRecyclerView.getLayoutManager() != null && excelPanelAdapter != null) {
            LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            return firstVisibleItem;
        }
        return position;
    }

    /**
     * use to adjust the height and width of the left header cell
     * @param holder
     * @param position
     */
    public void onAfterBindLeft(RecyclerView.ViewHolder holder,int position) {
        onAfterBind(holder,position,leftIndexHeight);
    }

    /**
     * use to adjust the height and width of the normal cell
     * @param holder
     * @param positionX
     * @param positionY
     */
    public void onAfterBindNormal(RecyclerView.ViewHolder holder,int positionX,int positionY) {
        if(normalIndexHeightMap == null)
            normalIndexHeightMap = new TreeMap<>();
        Map<Integer,Integer> normalIndexHeight = normalIndexHeightMap.get(positionX);
        if(normalIndexHeight == null) {
            normalIndexHeight = new TreeMap<>();
            normalIndexHeightMap.put(positionX,normalIndexHeight);
        }
        onAfterBind(holder,positionY,normalIndexHeight);
    }

    /**
     * use to adjust the height and width of the cell
     * @param holder
     * @param position
     * @param indexHeight
     */
    private void onAfterBind(RecyclerView.ViewHolder holder,int position,Map<Integer,Integer> indexHeight) {
        if (holder != null && holder.itemView != null) {
            if (indexHeight == null) {
                indexHeight = new TreeMap<>();
            }
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            indexHeight.put(position, layoutParams.height);
        }
    }
}
