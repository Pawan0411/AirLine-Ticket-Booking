package pp.airlineticketbooking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import pp.Adapter;
import pp.model.CircularQueue;
import pp.model.Passenger;

public class Individual_Airline extends AppCompatActivity {

    @BindView(R.id.heading)
    TextView heading;
    @BindView(R.id.join_queue)
    Button join_queue;
    @BindView(R.id.fly)
    Button increament_time;
    @BindView(R.id.security)
    Button security;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ArrayList<Passenger> passengers_list = new ArrayList<>();
    private Adapter adapter;

    int time = 0;
    int nLines = 4; // Total no of check in Line
    int maxLineLength = 8; // Total no of customers allow0ed in the line
    int sLines_male = 2; // Total number of Security booth male line
    int sLines_female = 3; // Total number of Security booth female line
    int processPerMin = 2; // Item Processed/Min
    // int incLength = 1; // Incremenet time always by 1
    int maxItems = 10; // Max item that can be bought by the customer
    int boarding_pass = 1; // 1 min for boarding pass
    int nElemns = 0;
    String gender = "";
    int j = 0;
    int index = 0;

    private boolean status;
    //Initialize
    Passenger[] passenger = new Passenger[200];
    ArrayList<Passenger> passengers = new ArrayList<>();
    CircularQueue[] lines = new CircularQueue[nLines];
    CircularQueue[] sec_lines_male = new CircularQueue[sLines_male];
    CircularQueue[] sec_lines_female = new CircularQueue[sLines_female];

    private ProgressDialog progressDialog;
    private DatabaseReference linesdatabase;
    private String airline_name;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_airline);

        ButterKnife.bind(this);
        Intent intent = getIntent();
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Fetching data..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        airline_name = intent.getStringExtra("Airline_name");

        heading.setText("Welcome to " + airline_name + " Airlines!");

        adapter = new Adapter(passengers_list, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Passengers");
        linesdatabase = database.getReference("CheckLines");
        DatabaseReference securityChecks = database.getReference("SecurityChecksLines");

        //Initailize the lines
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new CircularQueue(maxLineLength);
        }

        for (int i = 0; i < sLines_male; i++) {
            sec_lines_male[i] = new CircularQueue(maxLineLength);
        }

        for (int i = 0; i < sLines_female; i++) {
            sec_lines_female[i] = new CircularQueue(maxLineLength);
        }
            if (airline_name != null) {
                index = 0;
                linesdatabase.child(airline_name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Toast.makeText(Individual_Airline.this, String.valueOf(index), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        if (dataSnapshot.getChildrenCount() == 0){
                            // Toast.makeText(Individual_Airline.this, "No data present", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        passengers_list.clear();

                        for (DataSnapshot items : dataSnapshot.getChildren()) {
                            index = 0;
                            for (DataSnapshot pasengerSnapshot : items.getChildren()) {
                                Passenger pass = pasengerSnapshot.getValue(Passenger.class);
                                Individual_Airline.this.lines[index].insertf(pass);
                                Toast.makeText(Individual_Airline.this, String.valueOf(index) + String.valueOf(lines[index].size()), Toast.LENGTH_SHORT).show();
                                passengers_list.add(pass);
                            }
                        index++;
                        }
                        index++;
                        if (index >= dataSnapshot.getChildrenCount()) {
                            index = (int) (index - dataSnapshot.getChildrenCount());
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Individual_Airline.this, "Data not present", Toast.LENGTH_SHORT).show();
                        // progressDialog.dismiss();
                    }
                });
            }

        for (int i = 0; i  <sec_lines_male.length; i++){
            int index = i;
               securityChecks.child("Male").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if (dataSnapshot.getChildrenCount() == 0){
                         //  Toast.makeText(Individual_Airline.this, "No data present", Toast.LENGTH_SHORT).show();
                             return;
                       }

                       for (DataSnapshot items : dataSnapshot.getChildren()){
                           Passenger passenger = items.getValue(Passenger.class);
                           sec_lines_male[index].insertf(passenger);
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
        }

        for (int i = 0; i < sec_lines_female.length; i++){
            int index = i;
            securityChecks.child("Female").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0){
                     //   Toast.makeText(Individual_Airline.this, "No data present", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DataSnapshot items : dataSnapshot.getChildren()){
                        Passenger passenger = items.getValue(Passenger.class);
                        sec_lines_female[index].insertf(passenger);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (airline_name != null) {
            databaseReference.child(airline_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    if (dataSnapshot.getChildrenCount() == 0){
                      //  Toast.makeText(Individual_Airline.this, "No data present", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DataSnapshot items : dataSnapshot.getChildren()){
                        Passenger pass = items.getValue(Passenger.class);
                        //Toast.makeText(Individual_Airline.this, String.valueOf(pass.getLuggage()), Toast.LENGTH_SHORT).show();
                        passenger[nElemns] = pass;
                        nElemns++;
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //when join_queue is selected
        join_queue.setOnClickListener(view -> {
//                time += 1;
            // Shortest Line
            int shortestLine = 0;
            int minSize = lines[0].size();
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].size() < minSize) {
                    minSize = lines[i].size();
                    shortestLine = i;
                }
            }

            // Add the Customer to the Shortest Line
            int items = new Random().nextInt(maxItems) + 1;
            if (Math.random() > 0.5) {
                gender = "M";
            } else {
                gender = "F";
            }
            passenger[nElemns] = new Passenger(items, gender);
            lines[shortestLine].insert(passenger[nElemns], airline_name, shortestLine);
            nElemns++;
        });

        //remove 2luggages at 1min of passage
        increament_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time += 1;
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].isEmpty()) { // skip empty queues
                        continue;
                    }
                    status = lines[i].decFront(processPerMin, passenger[i], i, airline_name); // decrement item count
                    if (status) {
                        //Security Check line
                        String gender = passenger[i].getGender();
                        int shortestLine = 0;
                        if (gender.equals("M")) {
                            int minSize = sec_lines_male[0].size();
                            for (int j = 0; j < sec_lines_male.length; j++) {
                                if (sec_lines_male[j].size() < minSize) {
                                    minSize = sec_lines_male[j].size();
                                    shortestLine = j;
                                }
                            }
                            sec_lines_male[shortestLine].insert_sec(passenger[i], shortestLine);
                        } else if (gender.equals("F")){
                            int minSize = sec_lines_female[0].size();
                            for (int j = 0; j < sec_lines_female.length; j++) {
                                if (sec_lines_female[j].size() < minSize) {
                                    minSize = sec_lines_female[j].size();
                                    shortestLine = j;
                                }
                            }
                            sec_lines_male[shortestLine].insert_sec(passenger[i], shortestLine);
                        }
                    }
                }
            }
        });

        security.setOnClickListener(view -> {
            Intent intent1 = new Intent(Individual_Airline.this, Security.class);
            startActivity(intent1);
        });
    }

}
