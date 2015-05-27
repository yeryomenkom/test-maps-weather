package yeryomenkom.ua.weather;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import retrofit.RestAdapter;

public class DataFragment extends Fragment {
    static final String endpointWeather = "http://api.openweathermap.org";
    static final String rootForPict = "http://api.openweathermap.org/img/w/";
    static final String extForPict = ".png";

    private WeatherSimpleAPI api;
    private WeatherResponse weatherResponse;
    private OnDataFragmentEventListener listener;
    private Worker worker;

    private boolean isLastRequestFinishedWithError = true;

    public void setListener(@Nullable OnDataFragmentEventListener listener) {
        this.listener = listener;
    }

    public void setListenerAndGetLastResult(@NonNull OnDataFragmentEventListener listener) {
        this.listener = listener;
        if(isLastRequestFinishedWithError) errorWhileGettingInfo();
        else infoAvailable();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpointWeather).build();
        api = restAdapter.create(WeatherSimpleAPI.class);
    }

    void downloadWeatherInfo(String cityName){
        if(cityName.isEmpty()) {
            Toast.makeText(getActivity(),"You have no typed anything!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(worker != null) worker.cancel(false);
        worker = new Worker();
        worker.execute(cityName);
    }

    void infoAvailable(){
        if(listener != null) listener.infoAvailable(weatherResponse);
        worker=null;
    }

    void errorWhileGettingInfo(){
        if(listener != null) listener.errorWhileGettingInfo();
        worker=null;
    }

    class Worker extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            weatherResponse = api.getWeather(params[0]);
            return weatherResponse.weather != null;
        }

        @Override
        protected void onPostExecute(Boolean isOk) {
            if(isCancelled()) return;
            isLastRequestFinishedWithError = !isOk;
            if(isOk) {
                infoAvailable();
            } else {
                errorWhileGettingInfo();
            }
        }
    }

    interface OnDataFragmentEventListener {
        void infoAvailable(WeatherResponse weatherResponse);
        void errorWhileGettingInfo();
    }
}
