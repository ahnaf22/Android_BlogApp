package com.karigor.mightyblog.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karigor.mightyblog.Fragments.homeFragment;
import com.karigor.mightyblog.Fragments.profileFragment;
import com.karigor.mightyblog.Fragments.settingsFragment;
import com.karigor.mightyblog.Models.BlogPostData;
import com.karigor.mightyblog.R;

import org.xmlpull.v1.XmlPullParser;

public class HomeNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog addPostPopup;
    ImageView popupUserImage,popupAddpostImage,popupaddPostbtn;
    EditText popupPostTitle,popupPostDesc;
    ProgressBar popupPostProgress;
    private static final int REQUEST_CODE = 2;
    private Uri selectedImageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");


        //initializations
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer,new homeFragment()).commit();


        initPopup();
        setupPopopImageClick();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageUri=null;
                popupAddpostImage.setImageURI(selectedImageUri);
                popupPostProgress.setVisibility(View.INVISIBLE);
                popupaddPostbtn.setVisibility(View.VISIBLE);
                popupPostTitle.getText().clear();
                popupPostDesc.getText().clear();
                popupPostTitle.requestFocus();
                addPostPopup.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
    }


    //sets up what happens when popup image is clicked
    private void setupPopopImageClick() {

        popupAddpostImage.setOnClickListener(new View.OnClickListener() {
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


    //Initialize popup window
    private void initPopup(){
        addPostPopup=new Dialog(this);
        addPostPopup.setContentView(R.layout.addpost_popup);
        addPostPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addPostPopup.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        addPostPopup.getWindow().getAttributes().gravity= Gravity.TOP;


        //initialize Items
        popupUserImage=addPostPopup.findViewById(R.id.popup_userImage);
        popupAddpostImage=addPostPopup.findViewById(R.id.popup_added_image);
        popupaddPostbtn=addPostPopup.findViewById(R.id.popup_addpost_Image);
        popupPostProgress=addPostPopup.findViewById(R.id.popup_addPost_progressBar);
        popupPostTitle=addPostPopup.findViewById(R.id.popup_blogTitle);
        popupPostDesc=addPostPopup.findViewById(R.id.popup_blogDesc);

        Glide.with(this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        //set onlcick listeners
        popupaddPostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPostProgress.setVisibility(View.VISIBLE);
                popupaddPostbtn.setVisibility(View.INVISIBLE);

                final String blogTitle=popupPostTitle.getText().toString();
                final String blogDescription= popupPostDesc.getText().toString();

                if(!blogTitle.isEmpty() && !blogDescription.isEmpty() && selectedImageUri!=null)
                {
                    //Start with uploading the Image
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                    final StorageReference imageFilepath= storageReference.child("blogpost_Images").child(selectedImageUri.getLastPathSegment());

                    imageFilepath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageDownloadUrl=uri.toString();

                                    BlogPostData post= new BlogPostData(
                                            blogTitle,
                                            blogDescription,
                                            currentUser.getUid(),
                                            imageDownloadUrl,
                                            currentUser.getPhotoUrl().toString());
                                    //save Post to Database
                                    addPostToFirebaseDatabase(post);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    popupPostProgress.setVisibility(View.INVISIBLE);
                                    popupaddPostbtn.setVisibility(View.VISIBLE);
                                    message(e.getMessage());
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            popupPostProgress.setVisibility(View.INVISIBLE);
                            popupaddPostbtn.setVisibility(View.VISIBLE);
                            message(e.getMessage());

                        }
                    });

                }
                else
                {
                    popupPostProgress.setVisibility(View.INVISIBLE);
                    popupaddPostbtn.setVisibility(View.VISIBLE);
                    message("Please fill out all the fields!");
                }


            }
        });


    }



    //Add post to database
    private void addPostToFirebaseDatabase(BlogPostData post) {

        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference dbReference= database.getReference("posts").push();

        //get unique Id of a post
        String Key= dbReference.getKey();
        post.setPostKey(Key);

        //save data to database
        dbReference.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    message("Added post Successfully!");
                    popupPostProgress.setVisibility(View.INVISIBLE);
                    popupaddPostbtn.setVisibility(View.VISIBLE);
                    addPostPopup.dismiss();
                }
                else
                {
                    popupPostProgress.setVisibility(View.INVISIBLE);
                    popupaddPostbtn.setVisibility(View.VISIBLE);
                    message(task.getException().getMessage());
                }

            }
        });



    }


    //message toast method
    private void message(String message) {
        Toast.makeText(HomeNav.this,message, Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.home_nav, menu);
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

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer,new homeFragment()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer,new profileFragment()).commit();
        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer,new settingsFragment()).commit();

        } else if (id == R.id.nav_logOut) {
            gotoLoginPage();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void gotoLoginPage() {
        mAuth.signOut();
        Intent gotoLogin= new Intent(this,loginActivity.class);
        startActivity(gotoLogin);
        finish();

    }


    public void updateNavHeader(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);

        TextView navUsername= headerView.findViewById(R.id.nav_username);
        TextView navMail= headerView.findViewById(R.id.nav_userMail);
        ImageView userImage=headerView.findViewById(R.id.nav_userPhoto);

        navUsername.setText(currentUser.getDisplayName());
        navMail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(userImage);

    }


    //Take user permissions for Higher android versions
    private void getPermissionToRead() {

        //  check If the permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Pleae give the permission to Upload your photo", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            popupAddpostImage.setImageURI(selectedImageUri);

        }else
        {
            message("No Image Selected!");
        }
    }



}
