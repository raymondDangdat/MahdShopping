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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.Common.Common;
import comq.Interface.ItemClickListener;
import comq.Model.OrderInfoModel;
import comq.Utils.MemoUtils;

public class AdminViewOrders extends AppCompatActivity {
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference orders;


    private FirebaseRecyclerAdapter<OrderInfoModel, OrderViewHolder>adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);


        orders = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("orders");


        //init
        recyclerView = findViewById(R.id.recycler_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        toolbar = findViewById(R.id.orders_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Orders");


        loadOrders();

    }

    private void loadOrders() {
        FirebaseRecyclerOptions<OrderInfoModel> options = new FirebaseRecyclerOptions.Builder<OrderInfoModel>()
                .setQuery(orders, OrderInfoModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<OrderInfoModel, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull OrderInfoModel model) {
                holder.txtCustomerName.setText("Customer's Name: " + model.getCustomerName());
                holder.txtFrameName.setText("Frame Name:" + model.getFrameName());
                holder.txtStatus.setText("Status: " + model.getStatus());
                holder.txtSize.setText("Frame Size: " + model.getFrameSize());
                holder.txtPrice.setText("Frame Price: " + model.getFramePrice());
                holder.txtDateOrdered.setText("Date Ordered: " + MemoUtils.dateFromLong(model.getOrderDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_layout, viewGroup, false);
                OrderViewHolder viewHolder = new OrderViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnCreateContextMenuListener{
        public TextView txtFrameName, txtCustomerName, txtDateOrdered, txtStatus, txtSize, txtPrice;

        private ItemClickListener itemClickListener;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFrameName = itemView.findViewById(R.id.frame_name);
            txtCustomerName = itemView.findViewById(R.id.customer_name);
            txtDateOrdered = itemView.findViewById(R.id.date_ordered);
            txtPrice = itemView.findViewById(R.id.price);
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
        public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            contextMenu.setHeaderTitle("Select an action");
            contextMenu.add(0,0, getAdapterPosition(), Common.APPROVE);
            contextMenu.add(0,1, getAdapterPosition(), Common.DELETE);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
            deleteFrame(adapter.getRef(item.getOrder()).getKey());
        }else if (item.getTitle().equals(Common.APPROVE)){
            updateOrderStatus(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void updateOrderStatus(String key, OrderInfoModel item) {
        item.setStatus("Approved!");
        String size = item.getFrameSize();

        orders.child(key).setValue(item);

    }

    private void deleteFrame(String key) {
        orders.child(key).removeValue();
    }
}
