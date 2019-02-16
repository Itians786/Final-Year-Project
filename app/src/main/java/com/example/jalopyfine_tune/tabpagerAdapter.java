package com.example.jalopyfine_tune;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class tabpagerAdapter extends FragmentStatePagerAdapter {

    Integer tabnumber=3;
    public tabpagerAdapter(FragmentManager fm) {
        super(fm);
    }




    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                Bike tab1 = new Bike();
                return tab1;

            case 1:
                Car tab2 = new Car();
                return tab2;

            case 2:
                Other tab3 = new Other();
                return tab3;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {

        return tabnumber;
    }


}
