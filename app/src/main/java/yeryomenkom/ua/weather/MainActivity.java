package yeryomenkom.ua.weather;

import android.app.FragmentManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener,
        DataFragment.OnDataFragmentEventListener {

    EditText searchEditText;
    TextView weatherText;
    ImageView weatherIcon;
    View fragmentView;

    DataFragment dataFragment;
    GoogleMap map;

    ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(null);
        getSupportActionBar().setIcon(null);

        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        FragmentManager fm = getFragmentManager();

        weatherIcon = (ImageView) findViewById(R.id.weather_pict);
        weatherText = (TextView) findViewById(R.id.weather_text);

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        fragmentView = mapFragment.getView();
        fragmentView.setVisibility(View.INVISIBLE);
        map = mapFragment.getMap();

        dataFragment = (DataFragment) fm.findFragmentByTag("data");

        if(dataFragment==null) {
            dataFragment = new DataFragment();
            fm.beginTransaction().add(dataFragment,"data").commit();
        }

        dataFragment.setListenerAndGetLastResult(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        View v = menu.findItem(R.id.search_item).getActionView();
        searchEditText = (EditText) v.findViewById(R.id.txt_search);
        searchEditText.setOnEditorActionListener(this);
        searchEditText.setHint("Type City name...");

        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Toast.makeText(this,"Check your network state!",Toast.LENGTH_SHORT).show();
                errorWhileGettingInfo();
                return false;
            }
            dataFragment.downloadWeatherInfo(searchEditText.getText().toString());
        }

        return false;
    }

    @Override
    public void infoAvailable(WeatherResponse response) {
        weatherText.setText(response.toNiceString());
        Picasso.with(getApplicationContext()).load(DataFragment.rootForPict
                +response.weather[0].icon
                + DataFragment.extForPict).into(weatherIcon);
        LatLng latLng = new LatLng(response.coord.lat,response.coord.lon);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
        fragmentView.setVisibility(View.VISIBLE);
        weatherIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorWhileGettingInfo() {
        weatherText.setText("have not result");
        fragmentView.setVisibility(View.INVISIBLE);
        weatherIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        dataFragment.setListener(null);
        super.onDestroy();
    }
}
