package com.karigor.mightyblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.karigor.mightyblog.Activities.postDetailsActivity;
import com.karigor.mightyblog.Models.BlogPostData;
import com.karigor.mightyblog.R;

import java.util.List;

public class postAdapter extends RecyclerView.Adapter<postAdapter.MyViewHolder> {

    Context mContext;
    List<BlogPostData>mdata;

    public postAdapter(Context mContext, List<BlogPostData> mdata) {
        this.mContext = mContext;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {

        View row= LayoutInflater.from(mContext).inflate(R.layout.row_post_item,viewGroup,false);

        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position ) {


        //populating the views in row_post_item layout
        holder.blogTitle.setText(mdata.get(position).getTitle());
        Glide.with(mContext).load(mdata.get(position).getPicture()).into(holder.postImage);
        Glide.with(mContext).load(mdata.get(position).getUserPhoto()).into(holder.postProfileImage);

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }


    //This is the View Holder class
    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView blogTitle;
        ImageView postImage;
        ImageView postProfileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            blogTitle= itemView.findViewById(R.id.row_post_title);
            postImage= itemView.findViewById(R.id.row_post_image);
            postProfileImage= itemView.findViewById(R.id.row_user_image);

            //if we click on the item in homepage
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToPostDetails= new Intent(mContext, postDetailsActivity.class);
                    int position=getAdapterPosition();//gets the adapter position

                    goToPostDetails.putExtra("title",mdata.get(position).getTitle());
                    goToPostDetails.putExtra("desc",mdata.get(position).getDescription());
                    goToPostDetails.putExtra("author",mdata.get(position).getUserId());
                    goToPostDetails.putExtra("postImage",mdata.get(position).getPicture());
                    goToPostDetails.putExtra("userImage",mdata.get(position).getUserPhoto());
                    goToPostDetails.putExtra("postKey",mdata.get(position).getPostKey());

                    long timestamp=(long) mdata.get(position).getTimestamp();
                    goToPostDetails.putExtra("postDate",timestamp);
                    mContext.startActivity(goToPostDetails);

                }
            });
        }
    }
}
