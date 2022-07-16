package sinhala.novels.ebooks.Class;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import sinhala.novels.ebooks.Fragments.HomeFragment;
import sinhala.novels.ebooks.Fragments.SettingsFragment;

public class OrdersPageAdapter extends FragmentStateAdapter {

    public OrdersPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            default:
                return new SettingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
