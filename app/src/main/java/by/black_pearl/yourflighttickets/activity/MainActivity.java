package by.black_pearl.yourflighttickets.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import by.black_pearl.yourflighttickets.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String DATE_FROM = "datefrom";
    public static final String DATE_TO = "dateto";
    private TextView mDateFromTv;
    private TextView mDateToTv;
    private TextView mFlightFromTv;
    private TextView mFlightToTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDateFromTv = (TextView) findViewById(R.id.tv_fly_data_from);
        mDateToTv = (TextView) findViewById(R.id.tv_fly_date_to);
        mFlightFromTv = (TextView) findViewById(R.id.tv_flying_from);
        mFlightToTv = (TextView) findViewById(R.id.tv_flying_to);
        SaveHelper saver = (SaveHelper) getLastCustomNonConfigurationInstance();
        if (saver != null) {
            mFlightFromTv.setText(saver.from);
            mFlightToTv.setText(saver.to);
            mDateFromTv.setText(saver.dateFrom);
            mDateToTv.setText(saver.dateTo);
        }
        mDateFromTv.setOnClickListener(this);
        mDateToTv.setOnClickListener(this);
        mFlightFromTv.setOnClickListener(this);
        mFlightToTv.setOnClickListener(this);
        findViewById(R.id.btn_find).setOnClickListener(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new SaveHelper(
                mFlightFromTv.getText().toString(),
                mFlightToTv.getText().toString(),
                mDateFromTv.getText().toString(),
                mDateToTv.getText().toString()
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_fly_data_from:
                showDialog(Chalenge.DateFrom);
                break;
            case R.id.tv_fly_date_to:
                showDialog(Chalenge.DateTo);
                break;
            case R.id.tv_flying_from:
                showDialog(Chalenge.FlyFrom);
                break;
            case R.id.tv_flying_to:
                showDialog(Chalenge.FlyTo);
                break;
            case R.id.btn_find:
                if (!isValidInput()) {
                    Snackbar.make(v, R.string.incorrect_values, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                SimpleDateFormat printedSdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                SimpleDateFormat requestSdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String dateFrom = mDateFromTv.getText().toString();
                String dateTo = mDateToTv.getText().toString();
                try {
                    Date dateTmp = printedSdf.parse(dateFrom);
                    dateFrom = requestSdf.format(dateTmp);
                    dateTmp = printedSdf.parse(dateTo);
                    dateTo = requestSdf.format(dateTmp);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
                intent.putExtra(DATE_FROM, dateFrom);
                intent.putExtra(DATE_TO, dateTo);
                intent.putExtra(FROM, mFlightFromTv.getText().toString());
                intent.putExtra(TO, mFlightToTv.getText().toString());
                startActivity(intent);
                break;
        }
    }

    /**
     * Check for valid inputs.
     * @return true is valid.
     */
    private boolean isValidInput() {
        return !(!isValidDate(mDateFromTv.getText().toString()) || !isValidDate(mDateToTv.getText().toString()) ||
                !isValidDateOrder(mDateFromTv.getText().toString(), mDateToTv.getText().toString()) ||
                !isValidWay(mFlightFromTv.getText().toString(), mFlightToTv.getText().toString()));
    }

    /** Check for selected date after today.
     * @param inputDate input date
     * @return true is valid.
     */
    private boolean isValidDate(String inputDate) {
        if (inputDate.equals("")) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        int dayToday = calendar.get(Calendar.DAY_OF_MONTH);
        int monthToday = calendar.get(Calendar.MONTH) + 1;
        int yearToday = calendar.get(Calendar.YEAR);
        String[] date = inputDate.split("\\.");
        return !(Integer.valueOf(date[2]) < yearToday || Integer.valueOf(date[1]) < monthToday ||
                Integer.valueOf(date[0]) < dayToday && Integer.valueOf(date[1]) <= monthToday);
    }

    /**
     * Check for "date after date"
     * @param inputDate1 first date
     * @param inputDate2 second date
     * @return true is valid.
     */
    private boolean isValidDateOrder(String inputDate1, String inputDate2) {
        String[] input1 = inputDate1.split("\\.");
        String[] input2 = inputDate2.split("\\.");
        return !(Integer.valueOf(input2[2]) < Integer.valueOf(input1[2]) || Integer.valueOf(input2[1]) < Integer.valueOf(input1[1]) ||
                Integer.valueOf(input2[0]) < Integer.valueOf(input1[0]) && Integer.valueOf(input2[1]).equals(Integer.valueOf(input1[1])));
    }

    /**
     * Check for valid way.
     * @param way1 way from
     * @param way2 way to
     * @return true if way1 and way2 are valid.
     */
    private boolean isValidWay(String way1, String way2) {
        return !(way1.equals("") || way2.equals("")) && !way1.equals(way2);
    }

    /**
     * Shown dialog with action.
     * @param chalenge Enum of button.
     */
    private void showDialog(final Chalenge chalenge) {
        if (chalenge == Chalenge.DateFrom) {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    getDialogCalback(chalenge),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        }
        else if (chalenge == Chalenge.DateTo) {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    getDialogCalback(chalenge),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH) + 1
            ).show();
        }
        else {
            final AlertDialog dialog;
            ListViewCompat lvc = new ListViewCompat(this);
            ArrayList<String> aeroports = new ArrayList<>();
            aeroports.add(getString(R.string.airport_mow));
            aeroports.add(getString(R.string.airport_sip));
            ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            aa.addAll(aeroports);
            lvc.setAdapter(aa);
            dialog = new AlertDialog.Builder(this)
                    .setView(lvc)
                    .setMessage(R.string.choose_airport)
                    .create();
            dialog.show();
            AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (view instanceof TextView) {
                        String aer = ((TextView) view).getText().toString();
                        if (chalenge == Chalenge.FlyFrom) {
                            mFlightFromTv.setText(aer);
                        }
                        else if (chalenge == Chalenge.FlyTo) {
                            mFlightToTv.setText(aer);
                        }
                    }
                    dialog.cancel();
                }
            };
            lvc.setOnItemClickListener(itemListener);
        }
    }

    private DatePickerDialog.OnDateSetListener getDialogCalback(final Chalenge chalenge) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat printSdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                if (chalenge == Chalenge.DateFrom) {
                    mDateFromTv.setText(printSdf.format(calendar.getTime()));
                }
                else {
                    mDateToTv.setText(printSdf.format(calendar.getTime()));
                }
            }
        };
    }

    /**
     * Enum of button.
     */
    private enum Chalenge {DateFrom, DateTo, FlyFrom, FlyTo}

    /**
     * Class-helper that help to save data after screen rotation.
     */
    private class SaveHelper {
        private String from;
        private String to;
        private String dateFrom;
        private String dateTo;

        private SaveHelper(String from, String to, String dateFrom, String dateTo) {
            this.from = from;
            this.to = to;
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
        }
    }
}
