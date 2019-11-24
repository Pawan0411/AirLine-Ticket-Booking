package pp.airlineticketbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

//    @BindView(R.id.heading)
//    TextView heading;
    @BindView(R.id.indian_airline)
    Button indian_airline;
    @BindView(R.id.air_india)
    Button air_india;
    @BindView(R.id.indigo)
    Button indigo;
    @BindView(R.id.vistara)
    Button vistara;

    private String APP_OPENED_FIRST_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //when Indian Airline is selected
        indian_airline.setOnClickListener(view -> {
            Intent airline1 = new Intent(MainActivity.this, Individual_Airline.class);
            airline1.putExtra("Airline_name", indian_airline.getText().toString());
            startActivity(airline1);
        });

        //when Air India is selected
        air_india.setOnClickListener(view -> {
            Intent airline2 = new Intent(MainActivity.this, Individual_Airline.class);
            airline2.putExtra("Airline_name", air_india.getText().toString());
            startActivity(airline2);
        });

        //when Indigo is selected
        indigo.setOnClickListener(view -> {
            Intent airline3 = new Intent(MainActivity.this, Individual_Airline.class);
            airline3.putExtra("Airline_name", indigo.getText().toString());
            startActivity(airline3);
        });

        //when Vistara is selected
        vistara.setOnClickListener(view -> {
            Intent airline4 = new Intent(MainActivity.this, Individual_Airline.class);
            airline4.putExtra("Airline_name", vistara.getText().toString());
            startActivity(airline4);
        });

    }
}
