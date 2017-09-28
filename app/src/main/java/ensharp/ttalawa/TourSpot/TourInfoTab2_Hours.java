package ensharp.ttalawa.TourSpot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ensharp.ttalawa.R;

public class TourInfoTab2_Hours extends Fragment {

    public String spotName;
    private LinearLayout inflatedLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        spotName = TourInfoActivity.spotName;
        if (spotName.equals(getString(R.string.spot_namsangol))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_namsangol, container, false);
        } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_namsanpark, container, false);
        } else if (spotName.equals(getString(R.string.spot_nseoul))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_nseoultower, container, false);
        } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_gyeongbok, container, false);
        } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_jongmyo, container, false);
        } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_samziegil, container, false);
        } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_changdeok, container, false);
        } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_changkyeong, container, false);
        } else if (spotName.equals(getString(R.string.spot_63square))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_63square, container, false);
        } else if (spotName.equals(getString(R.string.spot_mbc))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_mbcpark, container, false);
        } else if (spotName.equals(getString(R.string.spot_skypark))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.hours_skypark, container, false);
        } else {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.spot_tab_null, container, false);
        }

        setContentWork();
        return inflatedLayout;
    }

    public void setContentWork() {

    }

}