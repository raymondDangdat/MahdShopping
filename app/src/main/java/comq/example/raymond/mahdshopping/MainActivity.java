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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnSignIn;
    private TextView txtRegisterActivity;

    private FirebaseAuth mAuth;

    //progress dialog

    private ProgressDialog signInProgress;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        signInProgress = new ProgressDialog(this);

        //initialize our toolBar
        toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Login Here");



        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);
        btnSignIn = findViewById(R.id.btnLogin);
        txtRegisterActivity = findViewById(R.id.txtYetToRegister);



        //set onclick listener on the textview to take user to registration activity
        txtRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        signInProgress.setTitle("Sign In");
        signInProgress.setMessage("Signing in...");
        final String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Email and Password needed for sign in", Toast.LENGTH_SHORT).show();
        }else {
            signInProgress.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                if (email.equals("mahdshop@gmail.com")){
                                    //logout
                                    Intent signIn = new Intent(MainActivity.this, AdminHome.class);
                                    signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(signIn);
                                }else {
                                    //logout
                                    Intent signIn = new Intent(MainActivity.this, UsersHome.class);
                                    signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(signIn);
                                }
                            }
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signInProgress.dismiss();
                    Toast.makeText(MainActivity.this, "Error! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
