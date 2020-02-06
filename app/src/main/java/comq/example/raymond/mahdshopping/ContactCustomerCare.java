package comq.example.raymond.mahdshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import comq.Common.Common;
import comq.Interface.ItemClickListener;
import comq.Model.FeedbackModel;
import comq.Utils.MemoUtils;

public class ContactCustomerCare extends AppCompatActivity {
    private Toolbar toolbar;

    private EditText editTextComplain;
    private DatabaseReference feedbackDb, users;

    private ProgressDialog progressDialog;

    private String uId;
    private FirebaseAuth auth;
    private FeedbackModel newFeedback;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<FeedbackModel, CustomerFeedbackViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_customer_care);

        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.customerCareToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Contact Customer Care");

        editTextComplain = findViewById(R.id.edit_text_complain);


        //init
        recyclerView = findViewById(R.id.recycler_feedbacks);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        auth = FirebaseAuth.getInstance();
        feedbackDb = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("feedbacks");

        users = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("users");


        uId = auth.getUid();


        loadFeedbacks(uId);




    }

    private void loadFeedbacks(String uId) {
        FirebaseRecyclerOptions<FeedbackModel>options = new FirebaseRecyclerOptions.Builder<FeedbackModel>()
                .setQuery(feedbackDb.orderByChild("uId").equalTo(uId), FeedbackModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<FeedbackModel, CustomerFeedbackViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerFeedbackViewHolder holder, int position, @NonNull FeedbackModel model) {
                holder.txtFeedback.setText(model.getFeedback());
                holder.txtDate.setText("Date: " + MemoUtils.dateFromLong(model.getDateSent()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }

            @NonNull
            @Override
            public CustomerFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customer_feedback, viewGroup, false);
                CustomerFeedbackViewHolder viewHolder = new CustomerFeedbackViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_memo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_memo:
                //saveMemo();
                contactCustomerCare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void contactCustomerCare() {
        final String feedback = editTextComplain.getText().toString().trim();

        if (TextUtils.isEmpty(feedback)){
            Toast.makeText(this, "You can't send an empty feedback", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setMessage("Sending your feedback...");
            progressDialog.show();

            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                  String name = dataSnapshot.child(uId).child("name").getValue(String.class);
                    long dateSent = new Date().getTime();

                  newFeedback = new FeedbackModel();
                  newFeedback.setCustomerName(name);
                  newFeedback.setFeedback(feedback);
                  newFeedback.setDateSent(dateSent);
                  newFeedback.setuId(uId);

                  feedbackDb.push().setValue(newFeedback);
                  progressDialog.dismiss();

                    Toast.makeText(ContactCustomerCare.this, "Feedback sent successfully! ", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(ContactCustomerCare.this, ContactCustomerCare.class));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    public class CustomerFeedbackViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        public TextView txtFeedback, txtDate;


        private ItemClickListener itemClickListener;


        public CustomerFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFeedback = itemView.findViewById(R.id.feedback);
            txtDate = itemView.findViewById(R.id.date);



            //itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);

        }

//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Cancel Order?");
//            menu.add(0,0, getAdapterPosition(), Common.CANCEL);
//
//        }
    }
}
