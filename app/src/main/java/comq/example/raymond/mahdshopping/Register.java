package comq.example.raymond.mahdshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private Button btnSignUp;
    private EditText edtName, edtAddress, editEmail,edtPhone, edtPassword, edtCPassword;
    private TextView txtSignInActivity;

    private Toolbar toolbar;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    //progress dialog
    private ProgressDialog regProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //initialize our toolBar
        toolbar = findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Register Here");



        //find views
        btnSignUp = findViewById(R.id.btnSignUp);
        edtName = findViewById(R.id.editTextName);
        edtAddress = findViewById(R.id.editTextAddress);
        editEmail = findViewById(R.id.editTextEmail);
        edtPhone = findViewById(R.id.editTextPhone);
        edtCPassword = findViewById(R.id.editTextCPassword);
        edtPassword = findViewById(R.id.editTextPassword);
        txtSignInActivity = findViewById(R.id.txtHaveAccount);

        //initialize auth
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("MahdShopping").child("users");



        //initialize progress dialog
        regProgress = new ProgressDialog(this);

        //setOnclickListener
        txtSignInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, MainActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        regProgress.setTitle("Account Creation");
        regProgress.setMessage("Signing up ...");
        //get the text
        final String name = edtName.getText().toString().trim();
        final String address = edtAddress.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String cPassword = edtCPassword.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)
                || TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Fill all fields please", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(cPassword)){
            Toast.makeText(this, "Your password must match your confirm password", Toast.LENGTH_SHORT).show();
        }else {
            regProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String uId = mAuth.getCurrentUser().getUid();
                        database.child(uId).child("name").setValue(name);
                        database.child(uId).child("email").setValue(email);
                        database.child(uId).child("uId").setValue(uId);
                        database.child(uId).child("phone").setValue(phone);
                        database.child(uId).child("address").setValue(address).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        regProgress.dismiss();
                                        Toast.makeText(Register.this, "Registration Completed", Toast.LENGTH_SHORT).show();
                                        //logout
                                        Intent signIn = new Intent(Register.this, MainActivity.class);
                                        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(signIn);

                                    }
                                }
                        ).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                regProgress.dismiss();
                                Toast.makeText(Register.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    regProgress.dismiss();
                    Toast.makeText(Register.this, "Error! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
