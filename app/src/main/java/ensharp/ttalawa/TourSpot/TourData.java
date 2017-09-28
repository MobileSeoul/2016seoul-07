package ensharp.ttalawa.TourSpot;

/**
 * Created by Moon on 2015-03-02.
 */
public class TourData extends TourAdapterItem {
    private String spotName;
    private String checkTour;

    public TourData(String spotName, String course,String checkTour) {
        super(course);
        this.spotName = spotName;
        this.checkTour=checkTour;
    }

    @Override
    public int getType() {
        return TYPE_SPOT;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getCheckTour(){return checkTour;}
}
