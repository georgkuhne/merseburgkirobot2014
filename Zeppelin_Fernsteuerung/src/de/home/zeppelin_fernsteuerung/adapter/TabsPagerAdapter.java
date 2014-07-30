package de.home.zeppelin_fernsteuerung.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import de.home.zeppelin_fernsteuerung.BildFragment;
import de.home.zeppelin_fernsteuerung.MainActivity;
import de.home.zeppelin_fernsteuerung.MapFragment;
import de.home.zeppelin_fernsteuerung.StatistikFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	MainActivity mainActivity;
	private MapFragment map;
	private BildFragment bild;

	public TabsPagerAdapter(FragmentManager fm, MainActivity mainActivity) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			map = new MapFragment(mainActivity);
			return map;
		case 1:

			bild = new BildFragment(mainActivity);
			return bild;
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

	public void init() {
		bild.init();

	}

}
