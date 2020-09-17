package com.example.pujo360.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pujo360.R;
import com.example.pujo360.adapters.CommentAdapter;
import com.example.pujo360.models.CommentModel;
import com.example.pujo360.preferences.IntroPref;
import com.example.pujo360.util.InternetConnection;
import com.example.pujo360.util.Utility;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class BottomCommentsDialog extends BottomSheetDialogFragment {

    private RecyclerView commentRecycler;
    private CommentAdapter commentAdapter;
    private ArrayList<CommentModel> models;
    private ProgressBar progressBar, progressComment;
    private String docID;
    private DocumentSnapshot lastVisible;
    private int checkGetMore = -1;
    private EditText newComment;
    private ImageView send;
    private DocumentReference docRef;
    private CollectionReference commentRef;

    public BottomCommentsDialog(String docID) {
        this.docID = docID;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.bottomsheetcomments, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        commentRecycler =v.findViewById(R.id.flamed_recycler);
        progressBar = v.findViewById(R.id.progress5);
        ImageView dismiss = v.findViewById(R.id.dismissflame);
        NestedScrollView nestedScrollView = v.findViewById(R.id.scroll_view);
        nestedScrollView.setNestedScrollingEnabled(true);

        ImageView commentimg = v.findViewById(R.id.user_image_comment);
        newComment = v.findViewById(R.id.new_comment);
        send = v.findViewById(R.id.send_comment);
        progressComment = v.findViewById(R.id.commentProgress);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecycler.setLayoutManager(layoutManager);
        commentRecycler.setItemAnimator(new DefaultItemAnimator());
        commentRecycler.setNestedScrollingEnabled(true);
        commentRecycler.setHasFixedSize(false);

        progressBar.setVisibility(View.VISIBLE);

        commentRef = FirebaseFirestore.getInstance().collection("Reels/" + docID + "/commentL/");
        docRef = FirebaseFirestore.getInstance().document("Reels/" + docID + "/");

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)(vv, scrollX, scrollY, oldScrollX, oldScrollY) ->{
            if(vv.getChildAt(vv.getChildCount() - 1) != null) {
                if((scrollY >= (vv.getChildAt(vv.getChildCount() - 1).getMeasuredHeight() - vv.getMeasuredHeight() )) &&
                        scrollY > oldScrollY) {
                    if(checkGetMore != -1){
                        if(progressBar.getVisibility() == View.GONE) {
                            progressBar.setVisibility(View.VISIBLE);
                            fetchMore_flames();//Load more data
                        }
                    }
                }
            }
        });

        buildRecyclerView_flames();

        Picasso.get().load(new IntroPref(requireActivity()).getUserdp()).fit().centerCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(commentimg, new Callback() {
                    @Override
                    public void onSuccess() { }

                    @Override
                    public void onError(Exception e) {
                        commentimg.setImageResource(R.drawable.ic_account_circle_black_24dp);
                    }
                });

        commentimg.setOnClickListener(v2 -> {
            newComment.requestFocus();
            newComment.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(newComment, InputMethodManager.SHOW_IMPLICIT);
            ///////////ENABLE KEYBOARD//////////
        });

        send.setOnClickListener(v2 -> {
            if(InternetConnection.checkConnection(requireActivity())){
                if(newComment.getText().toString().isEmpty()){
                    Utility.showToast(requireActivity(), "Thoughts need to be typed...");
                }
                else {
                    send.setVisibility(View.GONE);
                    progressComment.setVisibility(View.VISIBLE);
                    String comment = newComment.getText().toString().trim();
                    long tsLong = System.currentTimeMillis();
                    CommentModel commentModel = new CommentModel();

                    commentModel.setComment(comment);
                    commentModel.setType(new IntroPref(requireActivity()).getType());
                    commentModel.setUid(FirebaseAuth.getInstance().getUid());
                    commentModel.setPostUid(commentModel.getUid());
                    commentModel.setUserdp(new IntroPref(requireActivity()).getUserdp());
                    commentModel.setUsername(new IntroPref(requireActivity()).getFullName());
                    commentModel.setTs(0L); ///Pending state
                    commentModel.setPostID(commentModel.getDocID());

                    newComment.setText("");
                    models.add(0,commentModel);
                    commentAdapter.notifyItemInserted(0);

                    ///////////////////BATCH WRITE///////////////////
                    WriteBatch batch = FirebaseFirestore.getInstance().batch();

                    DocumentReference cmtDoc = commentRef.document(Long.toString(tsLong));
                    commentModel.setTs(tsLong);
                    commentModel.setDocID(Long.toString(tsLong));

                    batch.set(cmtDoc, commentModel);
                    batch.update(docRef, "cmtNo", FieldValue.increment(1));

                    batch.commit().addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            send.setVisibility(View.VISIBLE);
                            progressComment.setVisibility(View.GONE);
                            commentRecycler.setAdapter(commentAdapter);
                            commentRecycler.setVisibility(View.VISIBLE);
                        }
                        else {
                            commentModel.setTs(0L); ///Pending state
                            models.remove(commentModel);
                            commentModel.setTs(-1L);
                            models.add(0, commentModel);
                            commentAdapter.notifyDataSetChanged();
                            send.setVisibility(View.VISIBLE);
                            progressComment.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        }

                    });
                    ///////////////////BATCH WRITE///////////////////
                }
            }
            else {
                Utility.showToast(requireActivity(), "Network unavailable...");
            }
        });

        dismiss.setOnClickListener(v1 -> BottomCommentsDialog.super.onDestroyView());
        return v;
    }

    private void buildRecyclerView_flames(){
        progressBar.setVisibility(View.VISIBLE);
        models = new ArrayList<>();

        commentRef.limit(10).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                    CommentModel commentModel = document.toObject(CommentModel.class);
                    Objects.requireNonNull(commentModel).setDocID(document.getId());
                    models.add(commentModel);
                }
                if (models.size() > 0) {
                    commentAdapter = new CommentAdapter(getActivity(), models, 2);
                    commentRecycler.setAdapter(commentAdapter);

                    if(task.getResult().size() > 0)
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                    if(models.size() < 10) {
                        checkGetMore = -1;
                    } else {
                        checkGetMore = 0;
                    }
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void fetchMore_flames(){
        progressBar.setVisibility(View.VISIBLE);

        commentRef.limit(10).startAfter(lastVisible).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                ArrayList<CommentModel> commentModels = new ArrayList<>();
                for(DocumentSnapshot document: Objects.requireNonNull(task.getResult())) {
                    CommentModel commentModel = document.toObject(CommentModel.class);
                    Objects.requireNonNull(commentModel).setDocID(document.getId());
                    commentModels.add(commentModel);
                }
                if(commentModels.size() > 0) {
                    int lastSize = models.size();
                    models.addAll(commentModels);
                    commentAdapter.notifyItemRangeInserted(lastSize, commentModels.size());
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                }
            }
            progressBar.setVisibility(View.GONE);
            if(models.size() < 10){
                checkGetMore = -1;
            }
            else {
                checkGetMore = 0;
            }
        });
    }
}