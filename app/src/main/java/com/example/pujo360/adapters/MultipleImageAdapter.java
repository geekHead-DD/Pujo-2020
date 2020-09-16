package com.example.pujo360.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pujo360.R;

import java.util.ArrayList;

public class MultipleImageAdapter extends RecyclerView.Adapter<MultipleImageAdapter.ProgrammingViewHolder>
{
    private ArrayList<byte[]> mList;
    private ArrayList<String> mListUrl;

    Context mcontext;

    private OnClickListener mListener;
    private OnLongClickListener Listener;

    public interface OnClickListener {
        void onClickListener(int position);
    }

    public void onClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public interface OnLongClickListener {
        void onLongClickListener(int position);
    }

    public void onLongClickListener(OnLongClickListener onLongClickListener) {
        Listener= onLongClickListener;
    }

    public MultipleImageAdapter() {
    }

    public MultipleImageAdapter(ArrayList<byte[]> list, Context context) {
        this.mList = list;
        this.mcontext=context;
    }



    @NonNull
    @Override
    public ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_multiple_image,viewGroup, false);
        return new ProgrammingViewHolder(v , mListener, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProgrammingViewHolder programmingViewHolder, int i) {
        byte[] currentItem = mList.get(i);

        if (currentItem != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem, 0 ,currentItem.length);
            programmingViewHolder.image.setImageBitmap(bitmap);
        }





    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ProgrammingViewHolder extends RecyclerView.ViewHolder{

        //        TextView name;
//        CardView card;
        ImageView image;
        ImageButton unselect;

        private ProgrammingViewHolder(@NonNull View itemView, OnClickListener listener, OnLongClickListener onLongClickListener) {
            super(itemView);
//            name = itemView.findViewById(R.id.display_name_student);
//            card = itemView.findViewById(R.id.Card);
            image = itemView.findViewById(R.id.image);
            unselect = itemView.findViewById(R.id.unselect);


            unselect.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION ){
                        listener.onClickListener(position);
                    }
                }
            });
//
//            card.setOnLongClickListener(v -> {
//                if(onLongClickListener != null){
//                    int position = getAdapterPosition();
//                    if(position != RecyclerView.NO_POSITION ){
//                        onLongClickListener.onLongClickListener(position);
//                    }
//                }
//                return true;
//            });


        }
    }
}
