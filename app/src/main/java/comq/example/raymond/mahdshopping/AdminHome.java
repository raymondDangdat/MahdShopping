package comq.example.raymond.mahdshopping;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import comq.Common.Common;
import comq.Interface.ItemClickListener;
import comq.Model.Model;

public class AdminHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    private MaterialEditText editTextName, editTextFrameSize, editTextPrice;
    private ImageButton btnUpload;

    private FloatingActionButton fab;

    private Model model;

    private DatabaseReference frames;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseRecyclerAdapter<Model, FrameViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        btnUpload = findViewById(R.id.imgFrame);


        //init
        recyclerView = findViewById(R.id.recycler_frames);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        frames = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("frames");

        fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showAddFrameDialog();
                startActivity(new Intent(AdminHome.this, AddFrameActivity.class));
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //load frames
        loadFrames();

    }

    @Override
    protected void onStart() {
        loadFrames();
        super.onStart();
    }

    private void loadFrames() {
        FirebaseRecyclerOptions<Model>options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(frames, Model.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Model, FrameViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FrameViewHolder holder, int position, @NonNull Model model) {

                holder.txtSize.setText(model.getSize());
                holder.txtPrice.setText(model.getPrice());
                holder.txtName.setText(model.getName());

                Picasso.get().load(model.getImage()).into(holder.img_frame);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });



            }

            @NonNull
            @Override
            public FrameViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frame_layout, viewGroup, false);
                FrameViewHolder viewHolder = new FrameViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void showAddFrameDialog() {
        //create alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminHome.this);
        alertDialog.setTitle("Add new Frame");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_frame_laayout = inflater.inflate(R.layout.add_frame, null);


        editTextName = add_frame_laayout.findViewById(R.id.edtName);
        editTextFrameSize = add_frame_laayout.findViewById(R.id.edtSize);
        editTextPrice = add_frame_laayout.findViewById(R.id.edtPrice);

        btnUpload = add_frame_laayout.findViewById(R.id.imgFrame);




        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });


        alertDialog.setView(add_frame_laayout);



        //set button
        alertDialog.setPositiveButton("Add Frame", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //we just create a new category
                if (model != null){
                    frames.push().setValue(model);
                    Toast.makeText(AdminHome.this, "Frame Added Successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AdminHome.this, "Frame detail is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //to set it to square
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                btnUpload.setImageURI(mImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage() {
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_frame) {
            startActivity(new Intent(AdminHome.this, AddFrameActivity.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(AdminHome.this, AdminViewOrders.class));

        } else if (id == R.id.nav_customer) {
            startActivity(new Intent(AdminHome.this, CustomerFeedBack.class));
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.) {

        } else if (id == R.id.nav_sign_out) {

            mAuth.signOut();
            //logout
            Intent logout= new Intent(AdminHome.this, MainActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class FrameViewHolder extends RecyclerView.ViewHolder implements
    View.OnClickListener, View.OnCreateContextMenuListener{
        public TextView txtPrice, txtName, txtSize;

        public ImageView img_frame;

        private ItemClickListener itemClickListener;


        public FrameViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.price);
            txtSize = itemView.findViewById(R.id.size);

            img_frame = itemView.findViewById(R.id.img_frame);


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
            contextMenu.add(0,0, getAdapterPosition(), Common.UPDATE);
            contextMenu.add(0,1, getAdapterPosition(), Common.DELETE);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
            deleteFrame(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFrame(String key) {
        frames.child(key).removeValue();
    }
}
