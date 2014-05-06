package de.home.zeppelin_fernsteuerung.adapter;


import de.home.zeppelin_fernsteuerung.BildFragment;
import de.home.zeppelin_fernsteuerung.StatistikFragment;
import de.home.zeppelin_fernsteuerung.MapFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
        	return new MapFragment();
        case 1:
            // Games fragment activity
            return new BildFragment();
		case 2:
            // Movies fragment activity
            return new StatistikFragment();
        	
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
