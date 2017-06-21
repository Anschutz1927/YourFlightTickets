package by.black_pearl.yourflighttickets.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import by.black_pearl.yourflighttickets.Flight;
import by.black_pearl.yourflighttickets.FlightFragment;


public class ResultVpAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ArrayList<Flight>> mFlights = new ArrayList<>();

    public ResultVpAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FlightFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        if (mFlights == null) {
            return 0;
        }
        else {
            return mFlights.size();
        }
    }

    public void updateData(ArrayList<ArrayList<Flight>> flights) {
        this.mFlights = flights;
        notifyDataSetChanged();
    }

    /**
     * Call to get list of flights in some day by position.
     * @param posistion position
     * @return list of data
     */
    public ArrayList<Flight> getPositionListData(int posistion) {
        if (mFlights != null && mFlights.size() != 0) {
            return mFlights.get(posistion);
        }
        return null;
    }
}
