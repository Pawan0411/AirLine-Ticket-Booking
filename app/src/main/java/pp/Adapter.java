package pp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pp.airlineticketbooking.R;
import pp.model.Passenger;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Passenger> passengers;
    private Context mContext;

    public Adapter (List<Passenger> passengers, Context mContext){
        this.passengers = passengers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lines, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            Passenger passenger = passengers.get(position);
            holder.gender.setText("Gender : "  +passenger.getGender());
            holder.luggage.setText("Luggage : " + passenger.getLuggage());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.gender)
        TextView gender;
        @BindView(R.id.luggage)
        TextView luggage;
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.parent_layout)
        ConstraintLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
