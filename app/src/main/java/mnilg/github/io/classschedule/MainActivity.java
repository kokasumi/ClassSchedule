package mnilg.github.io.classschedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mnilg.github.io.classschedule.bean.Course;
import mnilg.github.io.classschedule.bean.CourseTime;
import mnilg.github.io.classschedule.bean.CourseWeek;
import mnilg.github.io.classschedule.component.ClassScheduleExcelPanel;

/**
 * Created time:2018/4/7
 * Created by:ligang
 * Description:
 */

public class MainActivity extends AppCompatActivity {
    private ClassScheduleExcelPanel excelPanel;
    private String[] courseNameStrs = {"数学","英语","物理",""};
    private int[] courseTime = {45,15,30,30,20,40,120,15,45,75,25,15,305};
    private int[] courseTime2 = {60,70,80,45,60,35,55,70,70,25,15,45,150};

    private CourseAdapter mAdapter;
    private List<CourseWeek> courseWeekList;
    private List<CourseTime> courseTimeList;
    private List<List<Course>> courseList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        excelPanel = findViewById(R.id.class_schedule_panel);
        mAdapter = new CourseAdapter(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        excelPanel.setAdapter(mAdapter);
        initData();
    }

    private void initData() {
        courseWeekList = new ArrayList<>();
        courseTimeList = new ArrayList<>();
        courseList = new ArrayList<>();
        for(int i = 0;i < 7;i++) {
            CourseWeek courseWeek = new CourseWeek();
            courseWeek.setWeek("周" + (i + 1));
            courseWeekList.add(courseWeek);
            List<Course> courseList1 = new ArrayList<>();
            for(int j = 0;j < 13;j++) {
                Course course = new Course();
                course.setCourseName(courseNameStrs[j % 4]);
                if(i % 2 == 0) {
                    course.setClassTime(courseTime[j]);
                }else {
                    course.setClassTime(courseTime2[j]);
                }
                course.setStatus(i % 4);
                courseList1.add(course);
            }
            courseList.add(courseList1);
        }
        for(int i = 0;i < 13;i++) {
            CourseTime courseTime = new CourseTime();
            courseTime.setTime((9+ i) + ":00");
            courseTimeList.add(courseTime);
        }
        mAdapter.setAllData(courseTimeList,courseWeekList,courseList);
    }
}
