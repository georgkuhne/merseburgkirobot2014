package de.home.zeppelin_fernsteuerung;

import java.text.DecimalFormat;

import de.home.zeppelin_fernsteuerung.adapter.TabsPagerAdapter;
import de.home.zeppelin_fernsteuerung.widgets.verticalseekbar.VerticalSeekBar;
import de.home.zeppelin_fernsteuerung.widgets.verticalseekbar.VerticalSeekBar.OnSeekBarChangeListener;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements TabListener{
	
	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private VerticalSeekBar seekbar1, seekbar2;
    private Button b_reset, b_fix;
    // Tab titles
    private String[] tabs = { "Karte", "Bild", "Statistik" };
 
	
	DecimalFormat df = new DecimalFormat("0.00");

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		// Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        seekbar1 = (VerticalSeekBar) findViewById(R.id.ProgressBar01);
        seekbar2 = (VerticalSeekBar) findViewById(R.id.ProgressBar02);
        b_reset = (Button) findViewById(R.id.button_reset);
        b_fix = (Button) findViewById(R.id.buttonfix);
        
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);       
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
         
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
         
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
        seekbar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(VerticalSeekBar seekBar,
					int progress, boolean fromUser) {
				int max1 = seekbar1.getMax();
		        System.out.println("max wert seekbar1: " + max1);
		        int akt_wertsb1 = seekbar1.getProgress();
		        System.out.println("aktueller wert sb1: " + akt_wertsb1);
				
			}

			@Override
			public void onStartTrackingTouch(VerticalSeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(VerticalSeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
        });
        
        seekbar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(VerticalSeekBar seekBar,
					int progress, boolean fromUser) {
				int max2 = seekbar2.getMax();
		        System.out.println("max wert seekbar2: " + max2);
		        int akt_wertsb2 = seekbar2.getProgress();
		        System.out.println("aktueller wert sb2: " + akt_wertsb2);
				
			}

			@Override
			public void onStartTrackingTouch(VerticalSeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(VerticalSeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
        });
	
        b_reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				seekbar1.setProgress(127);
				seekbar2.setProgress(127);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.4
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	

}
