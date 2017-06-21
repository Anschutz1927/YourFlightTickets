package by.black_pearl.yourflighttickets.manager;


import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;

import by.black_pearl.yourflighttickets.Data;
import by.black_pearl.yourflighttickets.Flight;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RetrofitManager {

    public static final String API_KEY = Data.API_KEY;
    public static final String HOST_PATH = Data.HOST_PATH;
    public static final String TICKET_URL = Data.TICKET_URL;
    public static final String USERNAME = Data.USERNAME;
    public static final String PASSWORD = Data.PASSWORD;

    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(HOST_PATH)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = newInstance();
    private static OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

    private static Retrofit newInstance() {
        return retrofitBuilder.build();
    }

    /**
     * Get retrofit object without login and password.
     * @param serviceClass server interface api
     * @param <S> server interface class
     * @return Retrofit object
     */
    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    /**
     * Get retrofit object with login and password.
     * @param serviceClass server interface api
     * @param username username
     * @param password password
     * @param <S> server interface class
     * @return Retrofit object
     */
    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createService(serviceClass, authToken);
        }
        return createService(serviceClass, null, null);
    }

    /**
     * Get retrofit object with access token.
     * @param serviceClass server interface api
     * @param authToken token
     * @param <S> server interface class
     * @return Retrofit object
     */
    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if (!httpBuilder.interceptors().contains(interceptor)) {
                httpBuilder.addInterceptor(interceptor);
                retrofitBuilder.client(httpBuilder.build());
                retrofit = retrofitBuilder.build();
            }
        }
        return retrofit.create(serviceClass);
    }

    /**
     * Server API
     */
    public interface FlightApi {

        /**
         * Get json body of flight request
         * @param from flight way from
         * @param to flight way to
         * @param date flight date
         * @return JSon body
         */
        @GET(TICKET_URL + "{from}" + "{to}" + "{date}" + "?apikey=" + API_KEY)
        Call<ResponseBody> getFlightsBody(@Path("from") String from, @Path("to") String to, @Path("date") String date);

        /**
         * Get json object of flight request
         * @param from flight way from
         * @param to flight way to
         * @param date flight date
         * @return JSon based object
         */
        @GET(TICKET_URL + "{from}" + "{to}" + "{date}" + "?apikey=" + API_KEY)
        Call<ArrayList<Flight>> getFlights(@Path("from") String from, @Path("to") String to, @Path("date") String date);
    }

    /**
     * Interceptor of token
     */
    private static class AuthenticationInterceptor implements Interceptor {

        private String authToken;

        AuthenticationInterceptor(String authToken) {
            this.authToken = authToken;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", authToken);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }
}
