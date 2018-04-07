package mnilg.github.io.classschedule.component;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;

import mnilg.github.io.classschedule.utils.Utils;

/**
 * Created by zhouchaoyuan on 2016/12/11.
 */

public abstract class BaseExcelPanelAdapter<T, L, M> implements OnExcelPanelListener {

    public static final int LOADING_VIEW_WIDTH = 30;

    private int leftCellWidth;
    private int topCellHeight;
    protected int amountAxisY = 0;

    private Context mContext;
    private RecyclerViewAdapter topRecyclerViewAdapter;
    private RecyclerViewAdapter leftRecyclerViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private View leftTopView;
    private ClassScheduleExcelPanel excelPanel;
    protected RecyclerView.OnScrollListener onScrollListener;
    protected List<T> topData;
    protected List<L> leftData;
    protected List<List<M>> majorData;

    public BaseExcelPanelAdapter(Context context) {
        mContext = context;
        initRecyclerViewAdapter();
    }

    private void initRecyclerViewAdapter() {
        topRecyclerViewAdapter = new TopRecyclerViewAdapter(mContext, topData, this);
        leftRecyclerViewAdapter = new LeftRecyclerViewAdapter(mContext, leftData, this);
        mRecyclerViewAdapter = new MajorRecyclerViewAdapter(mContext, majorData, this);
    }

    public void setTopData(List<T> topData) {
        this.topData = topData;
        topRecyclerViewAdapter.setData(topData);
    }

    public void setLeftData(List<L> leftData) {
        this.leftData = leftData;
        leftRecyclerViewAdapter.setData(leftData);
    }

    public void setMajorData(List<List<M>> majorData) {
        this.majorData = majorData;
        mRecyclerViewAdapter.setData(majorData);
    }

    public void setAllData(List<L> leftData, List<T> topData, List<List<M>> majorData) {
        setLeftData(leftData);
        setTopData(topData);
        setMajorData(majorData);
        excelPanel.scrollBy(0);
        excelPanel.fastScrollVerticalLeft();
        if (!Utils.isEmpty(leftData) && !Utils.isEmpty(topData) && excelPanel != null
                && !Utils.isEmpty(majorData) && leftTopView == null) {
            leftTopView = onCreateTopLeftView();
            excelPanel.addView(leftTopView, new FrameLayout.LayoutParams(leftCellWidth, topCellHeight));
        } else if (leftTopView != null) {
            if (Utils.isEmpty(leftData)) {
                leftTopView.setVisibility(View.GONE);
            } else {
                leftTopView.setVisibility(View.VISIBLE);
            }
        }
    }

    public RecyclerViewAdapter getmRecyclerViewAdapter() {
        return mRecyclerViewAdapter;
    }

    public RecyclerViewAdapter getLeftRecyclerViewAdapter() {
        return leftRecyclerViewAdapter;
    }

    public RecyclerViewAdapter getTopRecyclerViewAdapter() {
        return topRecyclerViewAdapter;
    }

    public void setLeftCellWidth(int leftCellWidth) {
        this.leftCellWidth = leftCellWidth;
    }

    public void setTopCellHeight(int topCellHeight) {
        this.topCellHeight = topCellHeight;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof MajorRecyclerViewAdapter) {
            ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).setOnScrollListener(onScrollListener);
        }
    }

    public T getTopItem(int position) {
        if (Utils.isEmpty(topData) || position < 0 || position >= topData.size()) {
            return null;
        }
        return topData.get(position);
    }

    public L getLeftItem(int position) {
        if (Utils.isEmpty(leftData) || position < 0 || position >= leftData.size()) {
            return null;
        }
        return leftData.get(position);
    }

    public M getMajorItem(int x, int y) {
        if (Utils.isEmpty(majorData) || x < 0 || x >= majorData.size() || Utils
                .isEmpty(majorData.get(x)) || y < 0 || y >= majorData.get(x).size()) {
            return null;
        }
        return majorData.get(x).get(y);
    }

    public void setAmountAxisY(int amountAxisY) {
        this.amountAxisY = amountAxisY;
        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof MajorRecyclerViewAdapter) {
            ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).setAmountAxisY(amountAxisY);
        }
    }

    public void setExcelPanel(ClassScheduleExcelPanel excelPanel) {
        this.excelPanel = excelPanel;
    }

    protected View createTopStaticView() {
        View topStaticView = new View(mContext);
        int loadingWidth = Utils.dp2px(LOADING_VIEW_WIDTH, mContext);
        topStaticView.setLayoutParams(new ViewGroup.LayoutParams(loadingWidth, topCellHeight));

        return topStaticView;
    }

    protected View createMajorLoadingView() {
        int loadingWidth = Utils.dp2px(LOADING_VIEW_WIDTH, mContext);
        LinearLayout loadingView = new LinearLayout(mContext);
        loadingView.setOrientation(LinearLayout.VERTICAL);
        loadingView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(loadingWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        loadingView.setLayoutParams(lpp);

        ProgressBar progressBar = new ProgressBar(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                Utils.dp2px(16, mContext), Utils.dp2px(16, mContext)));
        progressBar.setLayoutParams(lp);

        loadingView.addView(progressBar, lp);

        return loadingView;
    }

    @Override
    public int getCellItemViewType(int verticalPosition, int horizontalPosition) {
        return RecyclerViewAdapter.TYPE_NORMAL;
    }

    @Override
    public int getLeftItemViewType(int position) {
        return RecyclerViewAdapter.TYPE_NORMAL;
    }

    @Override
    public int getTopItemViewType(int position) {
        return RecyclerViewAdapter.TYPE_NORMAL;
    }

    public final void notifyDataSetChanged() {
        topRecyclerViewAdapter.notifyDataSetChanged();
        leftRecyclerViewAdapter.notifyDataSetChanged();
        ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).customNotifyDataSetChanged();
    }

    @Override
    public void onAfterBindLeft(RecyclerView.ViewHolder holder, int position) {
        if (excelPanel != null) {
            excelPanel.onAfterBindLeft(holder, position);
        }
    }

    @Override
    public void onAfterBindNormal(RecyclerView.ViewHolder holder, int positionX, int positionY) {
        if (excelPanel != null) {
            excelPanel.onAfterBindNormal(holder, positionX,positionY);
        }
    }
}
