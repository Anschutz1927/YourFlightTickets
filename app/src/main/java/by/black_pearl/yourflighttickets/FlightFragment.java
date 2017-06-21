package by.black_pearl.yourflighttickets;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import by.black_pearl.yourflighttickets.adapter.FlightRvAdapter;


public class FlightFragment extends Fragment {
    private static final String POSITION = "position";

    private int mPosition = 0;
    private TransferInterface mInterface;
    private FlightRvAdapter mAdapter;

    public FlightFragment() {
    }

    public static FlightFragment newInstance(int position) {
        FlightFragment fragment = new FlightFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof TransferInterface) {
            this.mInterface = (TransferInterface) getActivity();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(POSITION);
        }
        this.mAdapter = new FlightRvAdapter();
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayList<Flight> data = getData();
        mAdapter.updateData(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDetach() {
        mInterface = null;
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mAdapter.updateData(mInterface.getData(mPosition));
        switch (item.getItemId()) {
            case R.id.show_all:
                return true;
            case R.id.fater_direct_flight:
                mAdapter.sortByFastDirect();
                return true;
            case R.id.low_price_direct_flight:
                mAdapter.sortByLowPriceDirect();
                return true;
            case R.id.faster_transfer_flight:
                mAdapter.sortByFastTransfer();
                return true;
            case R.id.low_price_transfer_flight:
                mAdapter.sortByLowPriceTransfer();
                return true;
            case R.id.best_ticket:
                mAdapter.sortByBestTicket();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Flight> getData() {
        if (mInterface != null) {
            return mInterface.getData(mPosition);
        }
        return null;
    }

    public interface TransferInterface {
        ArrayList<Flight> getData(int position);
    }
}
