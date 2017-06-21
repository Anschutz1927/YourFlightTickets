package by.black_pearl.yourflighttickets.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import by.black_pearl.yourflighttickets.Flight;
import by.black_pearl.yourflighttickets.manager.RetrofitManager;
import retrofit2.Response;


public class FlightsLoader extends AsyncTask<FlightsLoader.FlightParams, String, ArrayList<ArrayList<Flight>>> {

    private Callback mCallback;

    public FlightsLoader(Callback callback) {
        this.mCallback = callback;
    }

    public void updateCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected ArrayList<ArrayList<Flight>> doInBackground(FlightsLoader.FlightParams... params) {
        String from = params[0].from;
        String to = params[0].to;
        ArrayList<String> dateList = params[0].dateList;
        ArrayList<ArrayList<Flight>> flights = new ArrayList<>();
        RetrofitManager.FlightApi api = RetrofitManager
                .createService(RetrofitManager.FlightApi.class, RetrofitManager.USERNAME, RetrofitManager.PASSWORD);
        try {
            for (String date : dateList) {
                Response<ArrayList<Flight>> response = api.getFlights(from, to, date).execute();
                if (response.isSuccessful() && response.code() == 200) {
                    flights.add(response.body());
                }
                else {
                    Log.i(
                            getClass().getSimpleName(),
                            response.errorBody().string() == null ? "no message..." : response.errorBody().string()
                    );
                }
                Log.i(getClass().getSimpleName(), response.code() + response.message());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flights;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<Flight>> flights) {
        super.onPostExecute(flights);
        mCallback.onComplete(flights);
    }

    public interface Callback {
        void onComplete(ArrayList<ArrayList<Flight>> flights);
    }

    public static class FlightParams {
        private String from;
        private String to;
        private ArrayList<String> dateList;

        public FlightParams(String from, String to, ArrayList<String> dateList) {
            this.from = from;
            this.to = to;
            this.dateList = dateList;
        }
    }
}
