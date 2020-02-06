package comq.example.raymond.mahdshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import comq.Model.Model;
import comq.example.raymond.mahdshopping.R;

public class AddFrameActivity extends AppCompatActivity {
    private MaterialEditText editTextPrice, editTextSize, editTextFrameName;
    private ImageButton imageButtonFrame;
    private Button btnAddFrame;

    private Toolbar toolbar;

    private DatabaseReference frames;
    private StorageReference mStorageImage;

    private Model newFrame;

    private ProgressDialog addFrameDialog;




    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_frame);


        addFrameDialog = new ProgressDialog(this);


        //initialize our toolBar
        toolbar = findViewById(R.id.addFrameToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Frame");


        frames = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("frames");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("frames");




        editTextPrice = findViewById(R.id.edtPrice);
        editTextSize = findViewById(R.id.edtSize);
        editTextFrameName = findViewById(R.id.edtName);
        imageButtonFrame = findViewById(R.id.imgFrame);
        btnAddFrame = findViewById(R.id.btnAddFrame);



        imageButtonFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        btnAddFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFrame();
            }
        });
    }

    private void addFrame() {
        addFrameDialog.setTitle("Add Frame");
        addFrameDialog.setMessage("Adding frame...");
        final String frameName = editTextFrameName.getText().toString().trim();
        final String price = editTextPrice.getText().toString().trim();
        final String size = editTextSize.getText().toString().trim();

        if (TextUtils.isEmpty(frameName) || TextUtils.isEmpty(price) || TextUtils.isEmpty(size)){
            Toast.makeText(this, "Please include frame name, price and size", Toast.LENGTH_SHORT).show();
        }else {
            addFrameDialog.show();
            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                    newFrame = new Model();

                    newFrame.setImage(downloadUrl);
                    newFrame.setName(frameName);
                    newFrame.setPrice(price);
                    newFrame.setSize(size);

                    if (newFrame != null){
                        frames.push().setValue(newFrame);
                        addFrameDialog.dismiss();
                        Toast.makeText(AddFrameActivity.this, "New Frame Added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddFrameActivity.this, AdminHome.class));
                    }else {
                        addFrameDialog.dismiss();
                        Toast.makeText(AddFrameActivity.this, "Sorry something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddFrameActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
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
                imageButtonFrame.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
