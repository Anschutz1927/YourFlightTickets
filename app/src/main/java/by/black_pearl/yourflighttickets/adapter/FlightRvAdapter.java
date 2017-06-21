package by.black_pearl.yourflighttickets.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import by.black_pearl.yourflighttickets.Flight;
import by.black_pearl.yourflighttickets.R;


public class FlightRvAdapter extends RecyclerView.Adapter<FlightRvAdapter.ViewHolder> {

    private ArrayList<Flight> mFlights;

    public FlightRvAdapter() {
    }

    public void updateData(ArrayList<Flight> flights) {
        this.mFlights = flights;
        notifyDataSetChanged();
    }

    public void sortByFastDirect() {
        ArrayList<Flight> list = new ArrayList<>();
        for (int i = 0; i < mFlights.size(); i++) {
            if (mFlights.get(i).airlineCode2 != null && mFlights.get(i).airlineName2 != null) {
                continue;
            }
            if (list.size() == 0) {
                list.add(mFlights.get(i));
                continue;
            }
            for (int j = 0; j <= list.size(); j++) {
                if (j == list.size()) {
                    list.add(mFlights.get(i));
                    break;
                }
                else if (Integer.valueOf(mFlights.get(i).duration) < Integer.valueOf(list.get(j).duration)) {
                    list.add(j, mFlights.get(i));
                    break;
                }
            }
        }
        mFlights = list;
        notifyDataSetChanged();
    }

    public void sortByLowPriceDirect() {
        ArrayList<Flight> list = new ArrayList<>();
        for (int i = 0; i < mFlights.size(); i++) {
            if (mFlights.get(i).airlineCode2 != null && mFlights.get(i).airlineName2 != null) {
                continue;
            }
            if (list.size() == 0) {
                list.add(mFlights.get(i));
                continue;
            }
            for (int j = 0; j <= list.size(); j++) {
                if (j == list.size()) {
                    list.add(mFlights.get(i));
                    break;
                }
                else if (Integer.valueOf(mFlights.get(i).price) < Integer.valueOf(list.get(j).price)) {
                    list.add(j, mFlights.get(i));
                    break;
                }
            }
        }
        mFlights = list;
        notifyDataSetChanged();
    }

    public void sortByFastTransfer() {
        ArrayList<Flight> list = new ArrayList<>();
        for (int i = 0; i < mFlights.size(); i++) {
            if (mFlights.get(i).airlineCode2 == null && mFlights.get(i).airlineName2 == null) {
                continue;
            }
            if (list.size() == 0) {
                list.add(mFlights.get(i));
                continue;
            }
            for (int j = 0; j <= list.size(); j++) {
                if (j == list.size()) {
                    list.add(mFlights.get(i));
                    break;
                }
                else if (Integer.valueOf(mFlights.get(i).duration) < Integer.valueOf(list.get(j).duration)) {
                    list.add(j, mFlights.get(i));
                    break;
                }
            }
        }
        mFlights = list;
        notifyDataSetChanged();
    }

    public void sortByLowPriceTransfer() {
        ArrayList<Flight> list = new ArrayList<>();
        for (int i = 0; i < mFlights.size(); i++) {
            if (mFlights.get(i).airlineCode2 == null && mFlights.get(i).airlineName2 == null) {
                continue;
            }
            if (list.size() == 0) {
                list.add(mFlights.get(i));
                continue;
            }
            for (int j = 0; j <= list.size(); j++) {
                if (j == list.size()) {
                    list.add(mFlights.get(i));
                    break;
                }
                else if (Integer.valueOf(mFlights.get(i).price) < Integer.valueOf(list.get(j).price)) {
                    list.add(j, mFlights.get(i));
                    break;
                }
            }
        }
        mFlights = list;
        notifyDataSetChanged();
    }

    public void sortByBestTicket() {
        int COST = 500, TIME = 500, N = 5;
        for (int i = 0; i < mFlights.size(); i++) {
            float weight;
            weight = Integer.valueOf(mFlights.get(i).price) / COST + Integer.valueOf(mFlights.get(i).duration) / TIME;
            if (mFlights.get(i).airlineName2 != null) {
                weight += 1 / N;
            }
            mFlights.get(i).weight = weight;
        }
        ArrayList<Flight> list = new ArrayList<>();
        for (Flight flight : mFlights) {
            if (list.size() == 0) {
                list.add(flight);
                continue;
            }
            for (int i = 0; i <= list.size(); i++) {
                if (i == list.size()) {
                    list.add(flight);
                    break;
                }
                else if (flight.weight <= list.get(i).weight) {
                    list.add(i, flight);
                    break;
                }
            }
        }
        mFlights = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_flight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Flight flight = mFlights.get(position);
        holder.mBuyBtn.setText(flight.price + "\n" + flight.priceCurrency);
        holder.mDepTimeTv.setText(flight.depTime);
        holder.mDepDateTv.setText(flight.depDate);
        holder.mDepAirportTv.setText(flight.airportNameFrom + " (" + flight.iataFrom + ")");
        holder.mArrTimeTv.setText(flight.arrTime);
        holder.mArrDateTv.setText(flight.arrDate);
        holder.mArrAirportTv.setText(flight.airportNameTo + " (" + flight.iataTo + ")");
        if (flight.airlineName2 != null && flight.airlineCode2 != null) {
            holder.mSellStopsTv.setText("1");
        }
        else {
            holder.mSellStopsTv.setText("0");
        }
        holder.mFlightTimeTv.setText(flight.duration + " (" + flight.durationStr + ")");
        holder.mCodeTv.setText(flight.code);
    }

    @Override
    public int getItemCount() {
        if (mFlights == null) {
            return 0;
        }
        else {
            return mFlights.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final Button mBuyBtn;
        private final TextView mDepTimeTv;
        private final TextView mDepDateTv;
        private final TextView mDepAirportTv;
        private final TextView mArrTimeTv;
        private final TextView mArrDateTv;
        private final TextView mArrAirportTv;
        private final TextView mSellStopsTv;
        private final TextView mFlightTimeTv;
        private final TextView mCodeTv;

        ViewHolder(View view) {
            super(view);
            mBuyBtn = (Button) view.findViewById(R.id.btn_buy);
            mDepTimeTv = (TextView) view.findViewById(R.id.tv_dep_time);
            mDepDateTv = (TextView) view.findViewById(R.id.tv_dep_date);
            mDepAirportTv = (TextView) view.findViewById(R.id.tv_dep_airport);
            mArrTimeTv = (TextView) view.findViewById(R.id.tv_arr_time);
            mArrDateTv = (TextView) view.findViewById(R.id.tv_arr_date);
            mArrAirportTv = (TextView) view.findViewById(R.id.tv_arr_airport);
            mSellStopsTv = (TextView) view.findViewById(R.id.tv_stops);
            mFlightTimeTv = (TextView) view.findViewById(R.id.tv_flight_time);
            mCodeTv = (TextView) view.findViewById(R.id.tv_code);
        }
    }
}
