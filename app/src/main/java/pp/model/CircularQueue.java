package pp.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CircularQueue {
    private int max, rear, front;
    private int[] a;
    private int size;
    private boolean track = true;

    private int temp;
    private String p_gender;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference databaseReference = database.getReference("Passengers");
    private DatabaseReference linesdatabase = database.getReference("CheckLines");
    private DatabaseReference securityChecks = database.getReference("SecurityChecksLines");

    public CircularQueue(int m) {
        max = m;
        size = 0;
        a = new int[m];
        rear = front = -1;
    }

    public void insert(Passenger val, String airline_name, int shortestLine) {
        if (isFull()) {
            Log.e("tag", "CUSTOMER CANNOT BE ADDED AS QUEUE IS FULL");
        } else {
            String key;
            if (isEmpty()) {
                front = rear = 0;
                a[rear] = val.getLuggage();
                key = databaseReference.push().getKey();
                if (key != null) {
                    if (airline_name != null) {
                        val.setKey(key);
                        databaseReference.child(airline_name).child(key).setValue(val);
                        linesdatabase.child(airline_name).child("line-" + shortestLine).child(key).setValue(val);
                    }
                }
            } else {
                rear = (rear + 1) % max;
                a[rear] = val.getLuggage();
                key = databaseReference.push().getKey();
                if (key != null) {
                    if (airline_name != null) {
                        val.setKey(key);
                        databaseReference.child(airline_name).child(key).setValue(val);
                        linesdatabase.child(airline_name).child("line-" + shortestLine).child(key).setValue(val);
                    }
                }
            }
            size++;
        }
    }

    public void insertf(Passenger passenger) {
        if (isFull()) {
            Log.e("tag", "CUSTOMER CANNOT BE ADDED AS QUEUE IS FULL");
        } else {
            if (isEmpty()) {
                front = rear = 0;
                a[rear] = passenger.getLuggage();
            } else {
                rear = (rear + 1) % max;
                a[rear] = passenger.getLuggage();
            }
            size++;
        }
    }

    public void insert_sec(Passenger val, int sline) {
        if (isFull()) {
            Log.e("tag", "CUSTOMER CANNOT BE ADDED AS QUEUE IS FULL");
        } else {
            if (isEmpty()) {
                front = rear = 0;
                a[rear] = val.getLuggage();
                if (val.getGender().equals("M"))
                    securityChecks.child("Male").child("line-" + sline).child(val.getKey()).setValue(val);
                else
                    securityChecks.child("Female").child("line-" + sline).child(val.getKey()).setValue(val);
            } else {
                rear = (rear + 1) % max;
                a[rear] = val.getLuggage();
                if (val.getGender().equals("M"))
                    securityChecks.child("Male").child("line-" + sline).child(val.getKey()).setValue(val);
                else
                    securityChecks.child("Female").child("line-" + sline).child(val.getKey()).setValue(val);
            }
            size++;
        }
    }

    public int size() {
        return size;
    }

    public boolean decFront(int rmv, Passenger gender, int index, String airline_name) {
        if (track) {
            track = false;
            temp = a[front];
            p_gender = gender.getGender();
        }
        a[front] -= rmv;
        linesdatabase
                .child(airline_name)
                .child("line-" + index)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot items : dataSnapshot.getChildren()) {
                            Passenger passenger = items.getValue(Passenger.class);
                            if (passenger != null) {
                                Log.e("data", String.valueOf(passenger.getLuggage()));
                                //ecreament time by 2 luggages
                                int luggage = passenger.getLuggage() - 2;
                                if (luggage <= 0) {
                                    linesdatabase.child(airline_name).child("line-" + index).child(passenger.getKey()).removeValue();
                                } else {
                                    linesdatabase.child(airline_name)
                                            .child("line-" + index)
                                            .child(passenger.getKey())
                                            .child("luggage").setValue(luggage);
                                }

                            }
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (a[front] <= 0) {
            track = true;
            delete();
            return true;
        } else {
            return false;
        }
    }

    public void sdecFront_m(int rmv, int index){
        a[front] -= rmv;
        securityChecks
                .child("Male")
                .child("line-" + index)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot items : dataSnapshot.getChildren()) {
                            Passenger passenger = items.getValue(Passenger.class);
                            if (passenger != null) {
                                Log.e("data", String.valueOf(passenger.getLuggage()));
                                //ecreament time by 2 luggages
                                int luggage = passenger.getLuggage() - 2;
                                if (luggage <= 0) {
                                    securityChecks.child("Male").child("line-" + index).child(passenger.getKey()).removeValue();
                                } else {
                                    securityChecks
                                            .child("Male")
                                            .child("line-" + index)
                                            .child(passenger.getKey())
                                            .child("luggage").setValue(luggage);
                                }

                            }
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (a[front] <= 0) {
            delete();
        }
    }

    public void sdecFront_f(int rmv, int index){
        a[front] -= rmv;
        securityChecks
                .child("Female")
                .child("line-" + index)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot items : dataSnapshot.getChildren()) {
                            Passenger passenger = items.getValue(Passenger.class);
                            if (passenger != null) {
                                Log.e("data", String.valueOf(passenger.getLuggage()));
                                //ecreament time by 2 luggages
                                int luggage = passenger.getLuggage() - 2;
                                if (luggage <= 0) {
                                    securityChecks.child("Female").child("line-" + index).child(passenger.getKey()).removeValue();
                                } else {
                                    securityChecks
                                            .child("Female")
                                            .child("line-" + index)
                                            .child(passenger.getKey())
                                            .child("luggage").setValue(luggage);
                                }

                            }
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (a[front] <= 0) {
            delete();
        }
    }

    public void display(int index) {
        if (!isEmpty()) {
            int i;
            for (i = front; i != rear; i = (i + 1) % max) {
                Log.e("loop" + index, a[i] + "");
            }
            Log.e("loop" + index, a[i] + "");
        }
        Log.e("empty", "true");
    }

    private boolean isFull() {
        return front == (rear + 1) % max;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void delete() {
        if (isEmpty()) {
            System.out.println("Underflow");
        } else {
            int temp = a[front];
            if (rear == front) {
                rear = -1;
                front = -1;
            } else {
                front = (front + 1) % max;
            }
            size--;
        }
    }
}

