package mnilg.github.io.classschedule;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mnilg.github.io.classschedule.bean.Course;
import mnilg.github.io.classschedule.bean.CourseTime;
import mnilg.github.io.classschedule.bean.CourseWeek;
import mnilg.github.io.classschedule.component.BaseExcelPanelAdapter;

public class CourseAdapter extends BaseExcelPanelAdapter<CourseWeek,CourseTime,Course> {
    private Context context;
    private View.OnClickListener blockListener;

    public CourseAdapter(Context context, View.OnClickListener blockListener) {
        super(context);
        this.context = context;
        this.blockListener = blockListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_status_normal_cell, parent, false);
        CourseHolder cellHolder = new CourseHolder(layout);
        return cellHolder;
    }

    @Override
    public void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition) {
        Course cell = getMajorItem(verticalPosition, horizontalPosition);
        if (null == holder || !(holder instanceof CourseHolder) || cell == null) {
            return;
        }
        CourseHolder viewHolder = (CourseHolder) holder;
        viewHolder.itemView.setTag(cell);
        viewHolder.itemView.setOnClickListener(blockListener);
        ViewGroup.LayoutParams params = viewHolder.itemView.getLayoutParams();
        viewHolder.tvCourseName.setText(cell.getCourseName());
        params.height = getCellHeight(cell);
        viewHolder.itemView.setLayoutParams(params);
        if (cell.getStatus() == 0) {
            viewHolder.tvCourseName.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else if (cell.getStatus() == 1) {
            viewHolder.tvCourseName.setBackgroundColor(ContextCompat.getColor(context, R.color.left));
        } else if (cell.getStatus() == 2) {
            viewHolder.tvCourseName.setBackgroundColor(ContextCompat.getColor(context, R.color.staying));
        } else {
            viewHolder.tvCourseName.setBackgroundColor(ContextCompat.getColor(context, R.color.booking));
        }
    }

    private int getCellHeight(Course course) {
        return (int) (context.getResources().getDimension(R.dimen.room_status_cell_length) * (course.getClassTime() / 60.0));
    }

    @Override
    public RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_status_top_header, parent, false);
        TopHolder topHolder = new TopHolder(layout);
        return topHolder;
    }

    @Override
    public void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseWeek rowTitle = getTopItem(position);
        if (null == holder || !(holder instanceof TopHolder) || rowTitle == null) {
            return;
        }
        TopHolder viewHolder = (TopHolder) holder;
        viewHolder.roomWeek.setText(rowTitle.getWeek());
    }

    static class TopHolder extends RecyclerView.ViewHolder {
        public final TextView roomWeek;

        public TopHolder(View itemView) {
            super(itemView);
            roomWeek = (TextView) itemView.findViewById(R.id.week_label);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_status_left_header_item, parent, false);
        LeftHolder leftHolder = new LeftHolder(layout);
        return leftHolder;
    }

    @Override
    public void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseTime colTitle = getLeftItem(position);
        if (null == holder || !(holder instanceof LeftHolder) || colTitle == null) {
            return;
        }
        LeftHolder viewHolder = (LeftHolder) holder;
        viewHolder.roomNumberLabel.setText(colTitle.getTime());
    }

    static class LeftHolder extends RecyclerView.ViewHolder {

        public final TextView roomNumberLabel;

        public LeftHolder(View itemView) {
            super(itemView);
            roomNumberLabel = (TextView) itemView.findViewById(R.id.room_number_label);
        }
    }

    @Override
    public View onCreateTopLeftView() {
        return LayoutInflater.from(context).inflate(R.layout.course_status_normal_cell, null);
    }

    private class CourseHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tvCourseName;

        public CourseHolder(View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
        }
    }
}
