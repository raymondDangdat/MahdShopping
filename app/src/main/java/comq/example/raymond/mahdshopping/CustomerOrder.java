package comq.example.raymond.mahdshopping;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.Common.Common;
import comq.Interface.ItemClickListener;
import comq.Model.GetCustomerOrder;
import comq.Model.OrderInfoModel;
import comq.Utils.MemoUtils;

public class CustomerOrder extends AppCompatActivity {
    private Toolbar toolbar;

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference orders;
    private String uId;

    private FirebaseRecyclerAdapter<GetCustomerOrder, CustomerOrdersViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        toolbar = findViewById(R.id.customer_orders_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your order history");



        mAuth = FirebaseAuth.getInstance();

        uId = mAuth.getUid();

        //init
        recyclerView = findViewById(R.id.recycler_customer_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        orders = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("orders");

        loadOrders(uId);



    }

    private void loadOrders(String uId) {
        FirebaseRecyclerOptions<GetCustomerOrder> options = new FirebaseRecyclerOptions.Builder<GetCustomerOrder>()
                .setQuery(orders.orderByChild("customerId").equalTo(uId), GetCustomerOrder.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<GetCustomerOrder, CustomerOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerOrdersViewHolder holder, int position, @NonNull GetCustomerOrder model) {
                holder.txtFrameName.setText("Frame Name: " + model.getFrameName());
                holder.txtPrice.setText("Price: " + model.getFramePrice());
                holder.txtStatus.setText("Status: " + model.getStatus());
                holder.txtSize.setText("Size: " + model.getFrameSize());
                holder.txtDateOrdered.setText("Date Ordered: " + MemoUtils.dateFromLong(model.getOrderDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public CustomerOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customer_order_layout, viewGroup, false);
                CustomerOrdersViewHolder viewHolder = new CustomerOrdersViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public class CustomerOrdersViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnCreateContextMenuListener{
        public TextView txtPrice, txtDateOrdered, txtFrameName, txtSize, txtStatus;

        public ImageView img_frame;

        private ItemClickListener itemClickListener;


        public CustomerOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDateOrdered = itemView.findViewById(R.id.date_ordered);
            txtPrice = itemView.findViewById(R.id.frame_price);
            txtFrameName = itemView.findViewById(R.id.frame_name);
            txtSize = itemView.findViewById(R.id.size);
            txtStatus = itemView.findViewById(R.id.status);



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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.CANCEL)){
            updateOrderStatus(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void updateOrderStatus(String key, GetCustomerOrder item) {
        String status = item.getStatus();
        if (status.equals("Not Approved!")){
            item.setStatus("Canceled");
            orders.child(key).setValue(item);
        }else {
            Toast.makeText(this, "Sorry your order has been approved already, please contact customer care", Toast.LENGTH_SHORT).show();
        }

    }

}
