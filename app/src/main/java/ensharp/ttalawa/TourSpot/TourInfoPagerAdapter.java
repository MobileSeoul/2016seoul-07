package ensharp.ttalawa.TourSpot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TourInfoPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TourInfoPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TourInfoTab1_Intro tab1 = new TourInfoTab1_Intro();
                return tab1;
            case 1:
                TourInfoTab2_Hours tab2 = new TourInfoTab2_Hours();
                return tab2;
            case 2:
                TourInfoTab3_Admissions tab3 = new TourInfoTab3_Admissions();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}