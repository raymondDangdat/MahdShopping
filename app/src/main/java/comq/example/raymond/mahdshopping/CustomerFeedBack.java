package comq.example.raymond.mahdshopping;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.Common.Common;
import comq.Interface.ItemClickListener;
import comq.Model.FeedbackModel;
import comq.Utils.MemoUtils;

public class CustomerFeedBack extends AppCompatActivity {
    private Toolbar toolbar;

    String uId;

    private DatabaseReference feedbackDb, users;


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<FeedbackModel, CustomerFeedbackViewHolder> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_feed_back);

        toolbar = findViewById(R.id.customerCareToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Customers' Feedback");


        //init
        recyclerView = findViewById(R.id.recycler_feedbacks);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);





        feedbackDb = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("feedbacks");

        users = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("users");


        loadFeedbacks();


    }

    private void loadFeedbacks() {
        FirebaseRecyclerOptions<FeedbackModel> options = new FirebaseRecyclerOptions.Builder<FeedbackModel>()
                .setQuery(feedbackDb, FeedbackModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<FeedbackModel, CustomerFeedbackViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerFeedbackViewHolder holder, int position, @NonNull FeedbackModel model) {
                holder.txtFeedback.setText(model.getFeedback());
                holder.txtDate.setText("Date: " + MemoUtils.dateFromLong(model.getDateSent()));
                holder.txtCustomerName.setText(model.getCustomerName());

                uId = model.getuId();
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent reply = new Intent(CustomerFeedBack.this, ReplyCustomerFeedBack.class);
                        reply.putExtra("uId",uId);
                        reply.putExtra("complaintId", adapter.getRef(position).getKey());
                        startActivity(reply);
                    }
                });

            }

            @NonNull
            @Override
            public CustomerFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customer_feedback_admin, viewGroup, false);
                CustomerFeedbackViewHolder viewHolder = new CustomerFeedbackViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public class CustomerFeedbackViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnCreateContextMenuListener{
        public TextView txtFeedback, txtDate;
        public TextView txtCustomerName;


        private ItemClickListener itemClickListener;


        public CustomerFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFeedback = itemView.findViewById(R.id.feedback);
            txtDate = itemView.findViewById(R.id.date);
            txtCustomerName = itemView.findViewById(R.id.customer_name);



            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Cancel Order?");
            menu.add(0,0, getAdapterPosition(), Common.CANCEL);

        }
    }
}
