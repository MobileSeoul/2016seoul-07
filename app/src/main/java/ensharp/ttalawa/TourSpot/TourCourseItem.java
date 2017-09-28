package ensharp.ttalawa.TourSpot;

/**
 * Created by Moon on 2015-03-03.
 */
public class TourCourseItem extends TourAdapterItem {

    public TourCourseItem(String course) {
        super(course);
    }

    @Override
    public int getType() {
        return TYPE_COURSE;
    }
}
