package com.mobile.fe_bankproject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TopUpPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 2;

    public TopUpPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        // Return different fragments based on position
        switch (position) {
            case 0:
                return new PhoneCardFragment(); // Fragment for "Thẻ điện thoại"
            case 1:
                return new Data4GFragment(); // Fragment for "Data 4G"
            default:
                return new PhoneCardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
} 