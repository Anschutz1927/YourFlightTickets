package by.black_pearl.yourflighttickets.activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import by.black_pearl.yourflighttickets.Flight;
import by.black_pearl.yourflighttickets.FlightFragment;
import by.black_pearl.yourflighttickets.R;
import by.black_pearl.yourflighttickets.adapter.ResultVpAdapter;
import by.black_pearl.yourflighttickets.task.FlightsLoader;

public class SearchActivity extends AppCompatActivity implements FlightFragment.TransferInterface, ViewPager.OnPageChangeListener {

    private ProgressBar mLoaderPb;
    private ResultVpAdapter mResultVpAdapter;
    private FlightsLoader mLoaderTask;
    private ViewPager mPagerVp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.tickets_search));
        mLoaderPb = (ProgressBar) findViewById(R.id.pb_load_logo);
        SaverHelper saver = (SaverHelper) getLastCustomNonConfigurationInstance();
        if (saver != null) {
            mLoaderTask = saver.loaderTask;
            mLoaderTask.updateCallback(getLoaderCallback());
            if (mLoaderTask.getStatus() == AsyncTask.Status.RUNNING) {
                mLoaderPb.setVisibility(View.VISIBLE);
                mResultVpAdapter = new ResultVpAdapter(getSupportFragmentManager());
            }
            else {
                mLoaderPb.setVisibility(View.INVISIBLE);
                mResultVpAdapter = saver.resultVpAdapter;
            }
            onPageSelected(saver.pageSelectedVp);
        }
        else {
            mLoaderPb.setVisibility(View.VISIBLE);
            mResultVpAdapter = new ResultVpAdapter(getSupportFragmentManager());
            mLoaderTask = new FlightsLoader(getLoaderCallback());
            startLoader();
        }
        mPagerVp = (ViewPager) findViewById(R.id.vp_resulter);
        mPagerVp.setAdapter(mResultVpAdapter);
        mPagerVp.addOnPageChangeListener(this);
        mPagerVp.setClipToPadding(false);
        mPagerVp.setPageMargin(12);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (mLoaderTask.getStatus() == AsyncTask.Status.RUNNING || mLoaderTask.getStatus() == AsyncTask.Status.FINISHED) {
            return new SaverHelper(mResultVpAdapter, mLoaderTask, mPagerVp.getCurrentItem());
        }
        return null;
    }

    private FlightsLoader.Callback getLoaderCallback() {
        return new FlightsLoader.Callback() {
            @Override
            public void onComplete(ArrayList<ArrayList<Flight>> flights) {
                mLoaderPb.setVisibility(View.INVISIBLE);
                mResultVpAdapter.updateData(flights);
                if (flights != null && flights.size() != 0 && flights.get(0) != null && flights.get(0).get(0).depDate != null) {
                    setTitle(flights.get(0).get(0).depDate);
                }
                else {
                    setTitle(getString(R.string.no_valid_tickets));
                }
            }
        };
    }

    /**
     * Starting to download information by tickets.
     */
    private void startLoader() {
        String dateFrom = getIntent().getExtras().getString(MainActivity.DATE_FROM);
        String dateTo = getIntent().getExtras().getString(MainActivity.DATE_TO);
        String from = getIntent().getExtras().getString(MainActivity.FROM);
        String to = getIntent().getExtras().getString(MainActivity.TO);

        ArrayList<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date fDate;
        Date sDate;
        try {
            fDate = sdf.parse(dateFrom);
            sDate = sdf.parse(dateTo);
            calendar.setTime(fDate);
            while (calendar.getTime().before(sDate)) {
                String strDate = sdf.format(calendar.getTime());
                dateList.add(strDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            dateList.add(sdf.format(sDate));
        } catch (ParseException e) {
            finish();
            e.printStackTrace();
        }
        FlightsLoader.FlightParams params = new FlightsLoader.FlightParams(from, to, dateList);
        mLoaderTask.execute(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean enableOptionsMenu = mLoaderTask.getStatus() == AsyncTask.Status.FINISHED &&
                mResultVpAdapter.getPositionListData(0) != null &&
                mResultVpAdapter.getPositionListData(0).size() != 0;
        menu.setGroupEnabled(R.id.sort_group, enableOptionsMenu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ArrayList<Flight> getData(int position) {
        return mResultVpAdapter.getPositionListData(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mLoaderTask.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        String txt = mResultVpAdapter.getPositionListData(position) != null &&
                mResultVpAdapter.getPositionListData(position).size() != 0 ?
                mResultVpAdapter.getPositionListData(position).get(0).depDate :
                getString(R.string.no_tickets);
        setTitle(txt);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * Class-helper that help to save data after screen rotation.
     */
    private class SaverHelper {

        private final ResultVpAdapter resultVpAdapter;
        private final FlightsLoader loaderTask;
        private final int pageSelectedVp;

        SaverHelper(ResultVpAdapter resultVpAdapter, FlightsLoader loaderTask, int viewPagePageSelected) {
            this.resultVpAdapter = resultVpAdapter;
            this.loaderTask = loaderTask;
            this.pageSelectedVp = viewPagePageSelected;
        }
    }
}
