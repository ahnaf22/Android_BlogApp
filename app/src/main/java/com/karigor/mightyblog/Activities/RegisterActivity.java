package com.karigor.mightyblog.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karigor.mightyblog.R;

public class RegisterActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 2;
    private ImageView userImage;
    private Uri selectedImageUri;

    private EditText textUsername;
    private EditText textEmail;
    private EditText textPassword;
    private EditText textConfirmPassword;
    private ProgressBar regProgress;
    private Button regBtn;


    private FirebaseAuth mauth;
    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);




        //Initializations
        userImage = findViewById(R.id.user_photo);
        textUsername = findViewById(R.id.regName);
        textEmail = findViewById(R.id.regMail);
        textPassword = findViewById(R.id.regPassword);
        textConfirmPassword = findViewById(R.id.regPassword2);
        regProgress = findViewById(R.id.reg_progressbar);
        regBtn = findViewById(R.id.regBtn);
        mauth = FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();

        regProgress.setVisibility(View.INVISIBLE);


        //onclick Listeners
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                regProgress.setVisibility(View.VISIBLE);

                //get the usernames and other data in string
                String username = textUsername.getText().toString();
                String email = textEmail.getText().toString();
                String password = textPassword.getText().toString();
                String confirmPassword = textConfirmPassword.getText().toString();


                if (!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !email.isEmpty() &&selectedImageUri!=null) {
                    if (!password.equals(confirmPassword)) {
                        showMessage("Passwords Didnt Match");
                        regBtn.setVisibility(View.VISIBLE);
                    } else {

                        createUserAccount(username, email, password);

                    }

                } else {
                    showMessage("Please enter all the info");
                    regBtn.setVisibility(View.VISIBLE);
                    regProgress.setVisibility(View.INVISIBLE);
                }


            }
        });


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    getPermissionToRead();
                } else {
                    openGallery();
                }
            }
        });


    }






    //function to show messages
    private void showMessage(String message) {

        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

    }


    // function to create user account
    private void createUserAccount(final String username, String email, String password) {

        mauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showMessage("Account Created!");

                    //update the userInfo
                    updateUserInfo(username,selectedImageUri,mauth.getCurrentUser());


                }else{
                    showMessage(task.getException().getMessage());
                    regBtn.setVisibility(View.VISIBLE);
                    regProgress.setVisibility(View.INVISIBLE);
                }
            }
        });


    }


    // Update the  username and other info in database
    private void updateUserInfo(final String username, Uri selectedImageUri, final FirebaseUser currentUser) {

        final StorageReference imagepath= mStorage.child("user_photos").child(selectedImageUri.getLastPathSegment());
        imagepath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //get the download profile Image URI
                imagepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate= new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    showMessage("Registered Successfully");
                                    gotoBlog();
                                }else
                                {
                                    showMessage(task.getException().getMessage());
                                }
                            }
                        });
                    }
                });
            }
        });

    }



    //goes to other activity
    private void gotoBlog() {
        Intent homepageIntent= new Intent(RegisterActivity.this,HomeNav.class);
        startActivity(homepageIntent);
        finish();
    }


    //Take user permissions for Higher android versions
    private void getPermissionToRead() {

        //  check If the permission is granted or not
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Pleae give the permission to Upload your photo", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

        } else {
            openGallery();
        }


    }




    //open the  Image gallery to set User Image
    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "Choose your photo"), REQUEST_CODE);
        //uses the request code to identify which intent is fetching data from files
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //checks which request is fetched and if there is  any data in the response from the intent

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            selectedImageUri = data.getData();
            userImage.setImageURI(selectedImageUri);
        }else{
            showMessage("No Image Selected!");
        }
    }
}
