package com.karigor.mightyblog.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karigor.mightyblog.R;

public class loginActivity extends AppCompatActivity{

    private EditText loginEmail;
    private EditText loginPass;
    private Button   btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private TextView txtGetregistered;
    private TextView txtNoaccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializations
        loginEmail=findViewById(R.id.loginEmail);
        loginPass=findViewById(R.id.loginPass);
        btnLogin=findViewById(R.id.btn_login);
        loginProgress=findViewById(R.id.loginProgress);
        mAuth= FirebaseAuth.getInstance();
        txtGetregistered=findViewById(R.id.txtGetRegistered);
        txtNoaccount=findViewById(R.id.textView);

        loginProgress.setVisibility(View.INVISIBLE);



        //onclick Listeners

        //register user
        txtGetregistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoReg();
            }
        });



        //login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=loginEmail.getText().toString();
                String password=loginPass.getText().toString();

                if(email.isEmpty() || password.isEmpty())
                {
                    showMessage("Please Fill all the fields!");
                }else{
                    btnLogin.setVisibility(View.INVISIBLE);
                    txtGetregistered.setVisibility(View.INVISIBLE);
                    txtNoaccount.setVisibility(View.INVISIBLE);
                    signInUser(email,password);
                }
            }
        });
    }


    //Take user to Reg Page
    private void gotoReg() {

        Intent homepageIntent= new Intent(this,RegisterActivity.class);
        startActivity(homepageIntent);

    }


    //signs in user
    private void signInUser(String email, String password) {
        loginProgress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    showMessage("LoggedIn!");
                    goHome();

                }else
                {
                    showMessage(task.getException().getMessage());
                }
                loginProgress.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                txtGetregistered.setVisibility(View.VISIBLE);
                txtNoaccount.setVisibility(View.VISIBLE);

            }
        });

    }


    //take User to Home Page
    private void goHome() {
        Intent homepageIntent= new Intent(this,HomeNav.class);
        startActivity(homepageIntent);
        finish();
    }


    //show message
    private void showMessage(String message){
        Toast.makeText(loginActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null)
        {
            goHome();
        }
    }
}
