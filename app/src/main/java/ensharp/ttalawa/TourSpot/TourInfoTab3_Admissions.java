package ensharp.ttalawa.TourSpot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ensharp.ttalawa.R;

public class TourInfoTab3_Admissions extends Fragment {

    public String spotName;
    private LinearLayout inflatedLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        spotName = TourInfoActivity.spotName;
        if (spotName.equals(getString(R.string.spot_namsangol))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_namsangol, container, false);
        } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_namsanpark, container, false);
        } else if (spotName.equals(getString(R.string.spot_nseoul))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_nseoultower, container, false);
        } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_gyeongbok, container, false);
        } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_jongmyo, container, false);
        } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_changdeok, container, false);
        } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_changkyeong, container, false);
        } else if (spotName.equals(getString(R.string.spot_63square))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_63square, container, false);
        } else if (spotName.equals(getString(R.string.spot_mbc))) {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.adm_mbcpark, container, false);
        } else {
            inflatedLayout = (LinearLayout) inflater.inflate(R.layout.spot_tab_null, container, false);
        }

        setContentWork();
        return inflatedLayout;
    }


    public void setContentWork() {

    }

}