package pp.airlineticketbooking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import pp.Adapter;
import pp.model.CircularQueue;
import pp.model.Passenger;

public class Security extends AppCompatActivity {

    @BindView(R.id.fly)
    Button increament_time;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    int sLines_male = 2; // Total number of Security booth male line
    int sLines_female = 3; // Total number of Security booth female line
    int processPerMin = 2; // Item Processed/Min
    int maxLineLength = 8; // Total no of customers allow0ed in the line


    CircularQueue[] sec_lines_male = new CircularQueue[sLines_male];
    CircularQueue[] sec_lines_female = new CircularQueue[sLines_female];

    ArrayList<Passenger> passengers_list = new ArrayList<>();
    private Adapter adapter;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference securityChecks = database.getReference("SecurityChecksLines");

        adapter = new Adapter(passengers_list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < sLines_male; i++) {
            sec_lines_male[i] = new CircularQueue(maxLineLength);
        }

        for (int i = 0; i < sLines_female; i++) {
            sec_lines_female[i] = new CircularQueue(maxLineLength);
        }


        for (int i = 0; i < 4; i++) {
            int index = i;
            securityChecks
                    .child("Female")
                    .child("line-" + i)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            passengers_list.clear();

                            if (dataSnapshot.getChildrenCount() == 0) {
                                Log.e("error", "No data present");
                                progressDialog.dismiss();
                                return;
                            }

                            for (DataSnapshot items : dataSnapshot.getChildren()) {
                                Passenger pass = items.getValue(Passenger.class);

                                passengers_list.add(pass);
                                sec_lines_female[index].insertf(pass);
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            securityChecks
                    .child("Male")
                    .child("line-" + i)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0) {
                                Log.e("error", "No data present");
                                progressDialog.dismiss();
                                return;
                            }

                            for (DataSnapshot items : dataSnapshot.getChildren()) {
                                Passenger pass = items.getValue(Passenger.class);

                                passengers_list.add(pass);
                                sec_lines_male[index].insertf(pass);
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        increament_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < sec_lines_male.length; i++) {
                    if (sec_lines_male[i].isEmpty()) { // skip empty queues
                        continue;
                    }

                    sec_lines_male[i].sdecFront_m(processPerMin, i); // decrement item count

                }

                for (int i = 0; i < sec_lines_female.length; i++) {
                    if (sec_lines_female[i].isEmpty()) { // skip empty queues
                        continue;
                    }

                    sec_lines_female[i].sdecFront_f(processPerMin, i); // decrement item count

                }
            }
        });
    }
}
