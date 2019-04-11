package com.karigor.mightyblog.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.karigor.mightyblog.Models.commentsData;
import com.karigor.mightyblog.R;

import java.util.Calendar;
import java.util.Locale;

public class postDetailsActivity extends AppCompatActivity {

    ImageView postdetailImage,postdetailCommentUserImage,postdetailAuthorImage;
    TextView  postdetailPostTitle,postdetailPostDesc,postdetailDateAuthor;
    Button postdetailBtnAddcomment;
    EditText postdetailTxtComment;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference dataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        //set the toolbar Invisible
        Window w= getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        //initialize views
        postdetailImage= findViewById(R.id.post_details_image);
        postdetailAuthorImage= findViewById(R.id.post_details_authorImage);
        postdetailCommentUserImage= findViewById(R.id.post_details_commentUserPhoto);
        postdetailPostTitle=findViewById(R.id.post_details_title);
        postdetailPostDesc=findViewById(R.id.post_details_desc);
        postdetailDateAuthor=findViewById(R.id.post_details_date_author);
        postdetailBtnAddcomment=findViewById(R.id.post_details_btn_add_comment);
        postdetailTxtComment=findViewById(R.id.post_details_commentText);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


        //get Data From Intent
        String title= getIntent().getExtras().getString("title");
        String description= getIntent().getExtras().getString("desc");
        String authorId= getIntent().getExtras().getString("author");
        String postImage= getIntent().getExtras().getString("postImage");
        String userImage= getIntent().getExtras().getString("userImage");
        final String postkey= getIntent().getExtras().getString("postKey");
        long  postDate= getIntent().getExtras().getLong("postDate");

        //glide images
        Glide.with(this).load(postImage).into(postdetailImage);
        Glide.with(this).load(userImage).into(postdetailAuthorImage);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(postdetailCommentUserImage);

        //setTexts
        postdetailPostTitle.setText(title);
        postdetailPostDesc.setText(description);

        String date=timeStampToTime(postDate);
        postdetailDateAuthor.setText(date);


        //Firebse database comment upload
        database= FirebaseDatabase.getInstance();


        //onclickListener
        postdetailBtnAddcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the values necessary to add a comment
                String commentText=postdetailTxtComment.getText().toString();
                String userId=currentUser.getUid();
                String userImage=currentUser.getPhotoUrl().toString();
                String userName=currentUser.getDisplayName();
                dataref=database.getReference("comments").child(postkey);
                commentsData comment=new commentsData(commentText,userId,userImage,userName);

                dataref.push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            postdetailTxtComment.getText().clear();
                            message("comment added");
                        }
                        else{
                            message(task.getException().getMessage());
                        }

                    }
                });

            }
        });

    }

    private void message(String s) {

        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    private String timeStampToTime(long time)
    {
        Calendar calendar= Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date= DateFormat.format("dd-MM-yyyy  HH:mm:ss",calendar).toString();

        return date;
    }
}
