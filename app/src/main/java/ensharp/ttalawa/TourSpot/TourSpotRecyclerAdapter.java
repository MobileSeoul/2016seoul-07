package ensharp.ttalawa.TourSpot;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ensharp.ttalawa.R;


public class TourSpotRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<TourAdapterItem> itemList;

    private OnItemClickListener listener;


    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layoutCourse;
        public TextView courseTitleView;
        public TextView courseSubTextView;

        public CourseViewHolder(View v) {
            super(v);
            courseTitleView = (TextView) v.findViewById(R.id.courseTitleView);
            layoutCourse=(LinearLayout)v.findViewById(R.id.layout_course);
            courseSubTextView =(TextView)v.findViewById(R.id.courseEngText);
        }
    }
    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView spotItemView;
        public TextView tourCheckText;
        private OnViewHolderClickListener listener;

        public DataViewHolder(View v, OnViewHolderClickListener listener) {
            super(v);
            spotItemView = (TextView) v.findViewById(R.id.spotItemView);
            tourCheckText=(TextView)v.findViewById(R.id.tourCheck);
            v.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if(listener != null)
                listener.onViewHolderClick(getPosition());
        }

        private interface OnViewHolderClickListener {
            void onViewHolderClick(int position);
        }
    }

    public TourSpotRecyclerAdapter(ArrayList<TourData> dataset) {
        itemList = initItemList(dataset);
    }

    private ArrayList<TourAdapterItem> initItemList(ArrayList<TourData> dataset) {
        ArrayList<TourAdapterItem> result = new ArrayList<>();

        String courseType="";

        for(TourData data:dataset) {
            if(courseType!=data.getCourse())
            {
                result.add(new TourCourseItem(data.getCourse()));
                courseType=data.getCourse();
            }
            result.add(data);
        }
        return result;
    }


    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TourAdapterItem.TYPE_COURSE)
            return new CourseViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_course, parent, false));
        else
            return new DataViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_spot, parent, false),
                    new DataViewHolder.OnViewHolderClickListener() {
                        @Override
                        public void onViewHolderClick(int position) {
                            if(listener != null)
                                listener.onItemClick(position);
                        }
                    }
            );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //코스 이름
        if(holder instanceof CourseViewHolder) {
            CourseViewHolder tHolder = (CourseViewHolder) holder;
            if((itemList.get(position)).getCourseToString()=="도심") {
                tHolder.courseTitleView.setText(R.string.course_dosim_main);
                tHolder.layoutCourse.setBackgroundResource(R.drawable.data_course_dosim);
                tHolder.courseTitleView.setTextColor(Color.parseColor("#9c4876"));
                tHolder.courseSubTextView.setText(R.string.course_dosim_sub);
                tHolder.courseSubTextView.setTextColor(Color.parseColor("#be729c"));
            }
            else if((itemList.get(position)).getCourseToString()=="고궁") {
                tHolder.courseTitleView.setText(R.string.course_gogung_main);
                tHolder.layoutCourse.setBackgroundResource(R.drawable.data_course_gogung);
                tHolder.courseTitleView.setTextColor(Color.parseColor("#1189a2"));
                tHolder.courseSubTextView.setText(R.string.course_gogung_sub);
                tHolder.courseSubTextView.setTextColor(Color.parseColor("#44bbd3"));
            }
            else if((itemList.get(position)).getCourseToString()=="동대문&대학로") {
                tHolder.courseTitleView.setText(R.string.course_dongdae_main);
                tHolder.layoutCourse.setBackgroundResource(R.drawable.data_course_dong_dae);
                tHolder.courseTitleView.setTextColor(Color.parseColor("#486d9f"));
                tHolder.courseSubTextView.setText(R.string.course_dongdae_sub);
                tHolder.courseSubTextView.setTextColor(Color.parseColor("#7a9fd0"));
            }
            else if((itemList.get(position)).getCourseToString()=="여의도") {
                tHolder.courseTitleView.setText(R.string.course_yeouido_main);
                tHolder.layoutCourse.setBackgroundResource(R.drawable.data_course_yeouido);
                tHolder.courseTitleView.setTextColor(Color.parseColor("#875295"));
                tHolder.courseSubTextView.setText(R.string.course_yeouido_sub);
                tHolder.courseSubTextView.setTextColor(Color.parseColor("#ab7cb7"));
            }
            else if((itemList.get(position)).getCourseToString()=="상암") {
                tHolder.courseTitleView.setText(R.string.course_sangam_main);
                tHolder.layoutCourse.setBackgroundResource(R.drawable.data_course_sangam);
                tHolder.courseTitleView.setTextColor(Color.parseColor("#b36b24"));
                tHolder.courseSubTextView.setText(R.string.course_sangam_sub);
                tHolder.courseSubTextView.setTextColor(Color.parseColor("#e49d56"));
            }
        }

        //관광명소 이름 및 인증 여부
        else if (holder instanceof DataViewHolder) {
            DataViewHolder dHolder = (DataViewHolder) holder;
            //관광명소 이름
            dHolder.spotItemView.setText(
                    ((TourData) itemList.get(position))
                            .getSpotName());
            //인증여부
            //Log.i("onbindviewholderBef",position+"번 "+dHolder.tourCheckText.getText());
            if(((TourData) itemList.get(position)).getCheckTour()=="방문완료"){
                Log.w("setText","삽입 완료");
//                dHolder.tourCheckText.setTextColor(Color.parseColor("#df5539"));
                dHolder.tourCheckText.setTextColor(Color.WHITE);
                dHolder.tourCheckText.setBackgroundResource(R.drawable.recycler_complete_btn);
            }else{
                dHolder.tourCheckText.setTextColor(Color.parseColor("#C2CBDB"));
                dHolder.tourCheckText.setBackgroundResource(R.drawable.recycler_btn_bg);
            }
            //Log.i("onbindviewholderAft",position+"번 "+dHolder.tourCheckText.getText());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public TourData getItem(int position) {
        return (TourData)itemList.get(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}