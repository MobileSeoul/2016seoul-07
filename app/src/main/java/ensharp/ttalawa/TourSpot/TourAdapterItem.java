package ensharp.ttalawa.TourSpot;

/**
 * Created by Moon on 2015-03-02.
 */
public abstract class TourAdapterItem {
    public static final int TYPE_COURSE = 1;
    public static final int TYPE_SPOT = 2;

    private String course;

    public TourAdapterItem(String course) {
        this.course = course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    public String getCourseToString() {
        return getCourse();
    }

    public abstract int getType();

}
