package comq.example.raymond.mahdshopping;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

import comq.Model.Model;
import comq.Model.OrderInfoModel;

public class DisplayFrames extends AppCompatActivity {
    private Toolbar toolbar;

    private TextView description, price, size;
    private EditText editTextName, editTextEmail, editTextAddress, editTextPhone;
    private ImageView img_frame;
    private Button btnOrder;

    private String frameId;

    private String address, name, email, phone;
    private String framePrice, frameSize, frameDescription, image;

    private ProgressDialog orderProgress;






    private FirebaseAuth mAuth;
    private DatabaseReference frames, orders, database;
    String uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_frames);

        orderProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        frames = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("frames");
        database = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("users");

        orders = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("orders");

        uId = mAuth.getUid();





        price = findViewById(R.id.price);
        size = findViewById(R.id.size);
        description = findViewById(R.id.description);
        btnOrder = findViewById(R.id.btn_oder);
        img_frame = findViewById(R.id.frame);

        toolbar = findViewById(R.id.display_frames_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Frame Details");


        //get room id from Intent
        if (getIntent() != null) {
            frameId = getIntent().getStringExtra("frameId");
            //Toast.makeText(this, ""+frameId, Toast.LENGTH_SHORT).show();
            if (!frameId.isEmpty()) {
                getFrameDetail(frameId);
            }

        }

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //placeOrder();
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        //create alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayFrames.this);
        alertDialog.setTitle("Your Information");
        alertDialog.setMessage("Confirm or edit your information");


        LayoutInflater inflater = this.getLayoutInflater();
        View customer_info_layout = inflater.inflate(R.layout.display_customer_info, null);


        editTextName = customer_info_layout.findViewById(R.id.edtName);
        editTextAddress = customer_info_layout.findViewById(R.id.edtAddress);
        editTextPhone = customer_info_layout.findViewById(R.id.edtPhone);

        editTextEmail = customer_info_layout.findViewById(R.id.edtEmail);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child(uId).child("name").getValue(String.class);
                email = dataSnapshot.child(uId).child("email").getValue(String.class);
                address = dataSnapshot.child(uId).child("address").getValue(String.class);
                phone = dataSnapshot.child(uId).child("phone").getValue(String.class);

                editTextName.setText(name);
                editTextEmail.setText(email);
                editTextAddress.setText(address);
                editTextPhone.setText(phone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        frames.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                framePrice = dataSnapshot.child(frameId).child("price").getValue(String.class);
                frameDescription = dataSnapshot.child(frameId).child("name").getValue(String.class);
                frameSize = dataSnapshot.child(frameId).child("size").getValue(String.class);
                image = dataSnapshot.child(frameId).child("image").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        alertDialog.setView(customer_info_layout);



        //set button
        alertDialog.setPositiveButton("Confirm & Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                orderProgress.setMessage("Placing order....");
                OrderInfoModel newOrder = new OrderInfoModel();



                long orderDate = new Date().getTime();

                newOrder.setCustomerAddress(address);
                newOrder.setCustomerEmail(email);
                newOrder.setCustomerId(uId);
                newOrder.setCustomerName(name);
                newOrder.setCustomerPhone(phone);
                newOrder.setFrameSize(frameSize);
                newOrder.setFramePrice(framePrice);
                newOrder.setFrameImage(image);
                newOrder.setFrameName(frameDescription);
                newOrder.setStatus("Not Approved!");
                newOrder.setOrderDate(orderDate);


                orders.push().setValue(newOrder);
                orderProgress.dismiss();
                Toast.makeText(DisplayFrames.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DisplayFrames.this, UsersHome.class));

                dialogInterface.dismiss();


            }
        });
        alertDialog.setNegativeButton("Edit & Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });



        alertDialog.show();
    }

    private void placeOrder() {
    }

    private void getFrameDetail(String frameId) {
        frames.child(frameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Model model = dataSnapshot.getValue(Model.class);


                    price.setText("Price: "+ model.getPrice());
                    size.setText("Size: " + model.getSize());
                    description.setText("Description: " + model.getName());

                    Picasso.get().load(model.getImage()).into(img_frame);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
