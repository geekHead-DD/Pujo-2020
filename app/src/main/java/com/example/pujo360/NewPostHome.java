package com.example.pujo360;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pujo360.LinkPreview.ApplexLinkPreview;
import com.example.pujo360.LinkPreview.ViewListener;
import com.example.pujo360.adapters.TagAdapter;
import com.example.pujo360.models.HomePostModel;
import com.example.pujo360.models.TagModel;
import com.example.pujo360.preferences.IntroPref;
import com.example.pujo360.util.BottomTagsDialog;
import com.example.pujo360.util.InternetConnection;
import com.example.pujo360.util.StoreTemp;
import com.example.pujo360.util.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class NewPostHome extends AppCompatActivity implements BottomTagsDialog.BottomSheetListener {

    private ArrayList<TagModel> selected_tags;

    private TextView postusername;
    private Button post_anon, post;
    private ImageView cam, gallery, cross, user_image;
    private EditText postcontent,edtagtxt;
    private ImageView info, postimage;
    private Dialog dialog;
    private Button customTag, moreTags;
//    private Spinner postspinner;

    private ApplexLinkPreview LinkPreview;
    private IntroPref introPref;

    private String textdata="", colorValue;
    private RecyclerView tags_selectedRecycler;
    private TagAdapter tagAdapter2;
    private ImageCompressor imageCompressor;
//    private Uri mHighQualityImageUri = null;

    private Uri filePath, finalUri;
    private StorageReference storageReferenece;

    private StorageReference reference;
    private Uri downloadUri;
    private String generatedFilePath, ts, USERNAME, PROFILEPIC;

    private FirebaseAuth mAuth;
    private FirebaseUser fireuser;

    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 2000;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    String[] cameraPermission;
    String[] storagePermission;
    private byte[] pic;
    private ProgressDialog progressDialog;

    private ImageButton close_image, edit_image;
    private RelativeLayout container_image;

    private HomePostModel homePostModel, editPostModel;
    private DocumentReference docRef;


    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        postusername = findViewById(R.id.post_username);
        user_image = findViewById(R.id.user_image99);
        info = findViewById(R.id.info99);
        cross = findViewById(R.id.cross99);
        cam= findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        postcontent = findViewById(R.id.post_content);
        postimage = findViewById(R.id.post_image);
        post_anon= findViewById(R.id.post_anonymous);
        post = findViewById(R.id.post);
//        postspinner=findViewById(R.id.post_spinner);
        close_image = findViewById(R.id.close_image);
        edit_image = findViewById(R.id.edit_image);
        container_image = findViewById(R.id.image_container);
        LinkPreview = findViewById(R.id.LinkPreView);

        customTag = findViewById(R.id.CustomTag);
        moreTags = findViewById(R.id.MoreTags);

        mAuth = FirebaseAuth.getInstance();
        fireuser = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReferenece = storage.getReference();

        tags_selectedRecycler = findViewById(R.id.tags_selectedList) ;
        selected_tags = new ArrayList<>();
        buildRecyclerView_selectedtags();

        ///////////////////LOADING CURRENT USER DP AND UNAME//////////////////////

        introPref = new IntroPref(NewPostHome.this);
        USERNAME = introPref.getFullName();
        postusername.setText(USERNAME);

        PROFILEPIC = introPref.getUserdp();
        if(PROFILEPIC!= null){
//
//            if(PROFILEPIC.matches("0")){
//                user_image.setImageResource(R.drawable.default_dp_1);
//            }
//            else if(PROFILEPIC.matches("1")){
//                user_image.setImageResource(R.drawable.default_dp_2);
//            }
//            else if(PROFILEPIC.matches("2")){
//                user_image.setImageResource(R.drawable.default_dp_3);
//            }
//            else if(PROFILEPIC.matches("3")){
//                user_image.setImageResource(R.drawable.default_dp_4);
//            }
//            else if(PROFILEPIC.matches("4")){
//                user_image.setImageResource(R.drawable.default_dp_5);
//            }
//            else if(PROFILEPIC.matches("5")){
//                user_image.setImageResource(R.drawable.default_dp_6);
//            }
//            else if(PROFILEPIC.matches("6")){
//                user_image.setImageResource(R.drawable.default_dp_7);
//            }
//            else if(PROFILEPIC.matches("7")){
//                user_image.setImageResource(R.drawable.default_dp_8);
//            }
//            else if(PROFILEPIC.matches("8")){
//                user_image.setImageResource(R.drawable.default_dp_9);
//            }
//            else if(PROFILEPIC.matches("9")){
//                user_image.setImageResource(R.drawable.default_dp_10);
//            }
//            else{
                Picasso.get().load(PROFILEPIC).into(user_image);
//            }

        }
        ///////////////////LOADING CURRENT USER DP AND UNAME//////////////////////
        editPostModel= new HomePostModel();

        ///////////////SHARED CONTENT///////////////
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(type == null && intent.getStringExtra("target")!=null){
            List<String> postingIn = new ArrayList<>();

            if(intent.getStringExtra("target").matches("3")){
                postingIn.add("Your Campus");
                postingIn.add("Global");
            }
            else if(intent.getStringExtra("target").matches("2")){
                postingIn.add("Global");
                postingIn.add("Your Campus");
            }

            else if(intent.getStringExtra("target").matches("11")){ //Challenge
                postingIn.add("Global");
//                postingIn.add("Your Campus");
                post_anon.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
            }

            else if(intent.getStringExtra("target").matches("4")){ //Community
                postingIn.add(intent.getStringExtra("comName"));
//                postingIn.add("Your Campus");
//                postingIn.add("Global");
                post_anon.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
            }

            if(intent.getStringExtra("target").matches("100")){// EDIT POST
                post_anon.setVisibility(View.GONE);
                if(intent.getStringExtra("usN")!=null){
                    editPostModel.setUsN(intent.getStringExtra("usN"));
                    postusername.setText(editPostModel.getUsN());

//                    if(editPostModel.getUsN().matches("Anonymous")){
//                        user_image.setImageResource(R.drawable.ic_anonymous_icon);
//                        post.setVisibility(View.GONE);
//                        post_anon.setVisibility(View.VISIBLE);
//                    }
                }

                if(intent.getStringExtra("dp")!=null)
                    editPostModel.setDp(intent.getStringExtra("dp"));

                if(intent.getStringExtra("uid")!=null)
                    editPostModel.setUid(intent.getStringExtra("uid"));

                if(intent.getStringExtra("bool")!=null){
                    if(intent.getStringExtra("bool").matches("0")||intent.getStringExtra("bool").matches("2")){
                        postingIn.add("Global");
                    }
                    else if(intent.getStringExtra("bool").matches("3")){
                        postingIn.add("Your Campus");
                    }
                }

                if(intent.getStringExtra("challengeID")!=null){
                    postingIn.add("Global");
                    post_anon.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    editPostModel.setChallengeID(intent.getStringExtra("challengeID"));
                }

                if(intent.getSerializableExtra("reportL")!=null)
                    editPostModel.setReportL((ArrayList<String>) intent.getSerializableExtra("reportL"));

                if(intent.getStringExtra("docID")!=null)
                    editPostModel.setDocID(intent.getStringExtra("docID"));

                if(intent.getStringExtra("likeCheck")!=null)
                    editPostModel.setLikeCheck(Integer.parseInt(intent.getStringExtra("likeCheck")));

                if(intent.getSerializableExtra("likeL")!=null)
                    editPostModel.setLikeL((ArrayList<String>) intent.getSerializableExtra("likeL"));

                if(intent.getStringExtra("cmtNo")!=null)
                    editPostModel.setCmtNo(Long.parseLong(intent.getStringExtra("cmtNo")));

                if((StoreTemp.getInstance().getTagTemp())!=null){
                    editPostModel.setTagL(StoreTemp.getInstance().getTagTemp());
                    tags_selectedRecycler.setVisibility(View.VISIBLE);
                    selected_tags= editPostModel.getTagL();

                    tagAdapter2.notifyDataSetChanged();
                    buildRecyclerView_selectedtags();
                }


                if(intent.getStringExtra("txt")!=null){
                    editPostModel.setTxt(intent.getStringExtra("txt"));
                    postcontent.setText(editPostModel.getTxt());
                }


                if(intent.getStringExtra("img")!=null){
                    editPostModel.setImg(intent.getStringExtra("img"));
                    container_image.setVisibility(View.VISIBLE);
                    postimage.setVisibility(View.VISIBLE);
                    // postimage.setImageURI(Uri.parse(editPostModel.getImg()));
                    Picasso.get().load(editPostModel.getImg()).into(postimage);

//                    finalUri = Uri.parse(editPostModel.getImg());
                    // postimage.setImageURI(finalUri);

//                    Bitmap bitmap = null;

                    Picasso.get().load(editPostModel.getImg()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap2, Picasso.LoadedFrom from) {
                            Bitmap bitmap = bitmap2;
                            ByteArrayOutputStream baos =new ByteArrayOutputStream();
                            // bitmap= BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                            pic = baos.toByteArray();

                            if(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, String.valueOf(System.currentTimeMillis()), null)!=null){
                                filePath = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, String.valueOf(System.currentTimeMillis()), null));
                            }


                        }
                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Toast.makeText(NewPostHome.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }

                    });

//                    try {
//                        final BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inJustDecodeBounds = true;
//                        options.inSampleSize = 2;
//                        options.inJustDecodeBounds = false;
//                        options.inTempStorage = new byte[16 * 1024];
//                        InputStream input = this.getContentResolver().openInputStream(Uri.parse(editPostModel.getImg()));
//                        bitmap = BitmapFactory.decodeStream(input, null, options);
//                        ByteArrayOutputStream baos =new ByteArrayOutputStream();
//                       // bitmap= BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
//                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
//                        pic = baos.toByteArray();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Utility.showToast(getApplicationContext(),"dgdb");
//                    }

//                    Bitmap bitmap = null;
//                    try {
//                        final BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inJustDecodeBounds = true;
//                        options.inSampleSize = 2;
//                        options.inJustDecodeBounds = false;
//                        options.inTempStorage = new byte[16 * 1024];
//
//                        InputStream input = this.getContentResolver().openInputStream(Uri.parse(editPostModel.getImg()));
//                        bitmap = BitmapFactory.decodeStream(input, null, options);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    ByteArrayOutputStream baos =new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
//                    pic = baos.toByteArray();
                }

                if(intent.getStringExtra("comID")!=null){
                    postingIn.add(intent.getStringExtra("comName"));
                    post_anon.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    editPostModel.setComID(intent.getStringExtra("comID"));
                }


                if(intent.getStringExtra("comName")!=null)
                    editPostModel.setComName(intent.getStringExtra("comName"));

                if(intent.getStringExtra("ts")!=null)
                    editPostModel.setTs(Long.parseLong(intent.getStringExtra("ts")));

                if(intent.getStringExtra("newTs")!=null)
                    editPostModel.setNewTs(Long.parseLong(intent.getStringExtra("newTs")));

            }

            ArrayAdapter<String> arrayAdapter;
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, postingIn);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            postspinner.setAdapter(arrayAdapter);
        }

        ///////////////////SHARED CONTENT////////////////////
        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    postcontent.setText(sharedText);
                    if(postcontent.getUrls().length>0){
                        URLSpan urlSnapItem = postcontent.getUrls()[0];
                        String url = urlSnapItem.getURL();
                        if(url!= null && url.contains("http")){
                            LinkPreview.setLink(url ,new ViewListener() {
                                @Override
                                public void onSuccess(boolean status) {
                                }
                                @Override
                                public void onError(Exception e) {
                                }
                            });
                        }
                    }
                }
            }
            else if (type.startsWith("image/")) {
                filePath = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                finalUri = filePath;
                container_image.setVisibility(View.VISIBLE);
                postimage.setVisibility(View.VISIBLE);
                postimage.setImageURI(finalUri);

                Bitmap bitmap = null;
                try {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = 2;
                    options.inJustDecodeBounds = false;
                    options.inTempStorage = new byte[16 * 1024];

                    InputStream input = this.getContentResolver().openInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(input, null, options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos =new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                pic = baos.toByteArray();

            }

        }
        //////////////////SHARED CONTENT///////////////////

        ///////////////////////IMAGE HANDLING////////////////////////
        gallery.setOnClickListener(v -> {
            if (!checkStoragePermission()) {
                requestStoragePermission();
            }
            else {
                pickGallery();
            }
        });

        cam.setOnClickListener(v -> {
            if (!checkCameraPermission()) {
                requestCameraPermission();
            }
            else {
                pickCamera();
            }
        });

        edit_image.setOnClickListener(v -> {
            CropImage.activity(filePath)
                    .setActivityTitle("Crop Image")
                    .setAllowRotation(TRUE)
                    .setAllowCounterRotation(TRUE)
                    .setAllowFlipping(TRUE)
                    .setAutoZoomEnabled(TRUE)
                    .setMultiTouchEnabled(FALSE)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(NewPostHome.this);
        });

        close_image.setOnClickListener(v -> {
            filePath = null;
            finalUri = null;
            pic = null;
            container_image.setVisibility(View.GONE);
        });
        ///////////////////////IMAGE HANDLING////////////////////////

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        ///////////////////////POST////////////////////////
        post.setOnClickListener(v -> {
            if(InternetConnection.checkConnection(getApplicationContext())){
                String text_content = postcontent.getText().toString();

                if(text_content.trim().isEmpty() && pic==null){
                    Utility.showToast(getApplicationContext(),"Post has got nothing...");
                }
                else{
                    if(intent.getStringExtra("target")!=null && intent.getStringExtra("target").matches("100")){
                        progressDialog = new ProgressDialog(NewPostHome.this);
                        progressDialog.setTitle("Saving changes");
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();

//                        if (postspinner.getSelectedItem().toString().matches("Global")){
//                            FragmentGlobal.changed = 1;
//                            ProfileActivity.change = 1;
//                            docRef = firebaseFirestore.collection("Home").document("Global")
//                                    .collection("Feeds").document(editPostModel.getDocID());
//                        }
//
//                        else{
//                            FragmentCampus.changed = 1;
//                            CommunityActivity.changed = 1;
//                            docRef = firebaseFirestore.collection("Home").document(CAMPUSNAME)
//                                    .collection("Feeds").document(editPostModel.getDocID());
//                        }

                        docRef = firebaseFirestore.collection("Feeds").document(editPostModel.getDocID());

                        ts = Long.toString(editPostModel.getTs());

                        if(selected_tags!= null && selected_tags.size()>0 ) {
                            editPostModel.setTagL(selected_tags);
                        }
                        if(text_content!= null && !text_content.isEmpty()  ) {
                            editPostModel.setTxt(text_content.trim());
                        }

                        if(pic!= null){
                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                            if (postspinner.getSelectedItem().toString().matches("Global")){
//                                reference = storageReferenece.child("Home/").child("Global/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else if(postspinner.getSelectedItem().toString().matches("Your Campus")){
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else {
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
                            reference = storageReferenece.child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                            Toast.makeText(getApplicationContext(), ""+pic.length/1024, Toast.LENGTH_LONG).show();

                            reference.putBytes(pic)
                                    .addOnSuccessListener(taskSnapshot ->
                                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                downloadUri = uri;
                                                generatedFilePath = downloadUri.toString();

                                                editPostModel.setImg(generatedFilePath);
                                                docRef.set(editPostModel).addOnCompleteListener(task -> {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        if(isTaskRoot()){
                                                            startActivity(new Intent(NewPostHome.this, MainActivity.class));
                                                        }
                                                        else if(intent.getStringExtra("FromViewMoreHome")!=null){
                                                            Intent i= new Intent(NewPostHome.this, ViewMoreHome.class);
                                                            i.putExtra("username", editPostModel.getUsN());
                                                            i.putExtra("userdp", editPostModel.getDp());
                                                            i.putExtra("docID", editPostModel.getDocID());
                                                            StoreTemp.getInstance().setTagTemp(editPostModel.getTagL());
                                                            //            StoreTemp.getInstance().setLikeList(currentItem.getLikeL());

                                                            i.putExtra("comName", editPostModel.getComName());
                                                            i.putExtra("comID", editPostModel.getComID());

                                                            i.putExtra("likeL", editPostModel.getLikeL());
                                                            i.putExtra("postPic", editPostModel.getImg());
                                                            i.putExtra("postText", editPostModel.getTxt());
                                                            i.putExtra("bool", "3");
                                                            i.putExtra("commentNo", Long.toString(editPostModel.getCmtNo()));

                                                            i.putExtra("uid", editPostModel.getUid());
                                                            i.putExtra("timestamp", Long.toString(editPostModel.getTs()));
                                                            i.putExtra("newTs", Long.toString(editPostModel.getNewTs()));
                                                            startActivity(i);
                                                            finish();

                                                        }
                                                        else {
                                                            NewPostHome.super.onBackPressed();
                                                        }
                                                    }else{
                                                        Utility.showToast(getApplicationContext(),"Something went wrong...");

                                                    }
                                                });

                                            }))

                                    .addOnFailureListener(e -> {
                                        Utility.showToast(getApplicationContext(), "Something went wrong");
                                        if(progressDialog!= null)
                                            progressDialog.dismiss();
                                    });

                        }

                        else {
                            editPostModel.setImg(null);
                            docRef.set(editPostModel).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();

                                    if(isTaskRoot()){
                                        startActivity(new Intent(NewPostHome.this, MainActivity.class));
                                    }
                                    else if(intent.getStringExtra("FromViewMoreHome")!=null){
                                        Intent i= new Intent(NewPostHome.this, ViewMoreHome.class);
                                        i.putExtra("username", editPostModel.getUsN());
                                        i.putExtra("userdp", editPostModel.getDp());
                                        i.putExtra("docID", editPostModel.getDocID());
                                        StoreTemp.getInstance().setTagTemp(editPostModel.getTagL());
                                        //            StoreTemp.getInstance().setLikeList(currentItem.getLikeL());

                                        i.putExtra("comName", editPostModel.getComName());
                                        i.putExtra("comID", editPostModel.getComID());

                                        i.putExtra("likeL", editPostModel.getLikeL());
                                        i.putExtra("postPic", editPostModel.getImg());
                                        i.putExtra("postText", editPostModel.getTxt());
                                        i.putExtra("bool", "3");
                                        i.putExtra("commentNo", Long.toString(editPostModel.getCmtNo()));

                                        i.putExtra("uid", editPostModel.getUid());
                                        i.putExtra("timestamp", Long.toString(editPostModel.getTs()));
                                        i.putExtra("newTs", Long.toString(editPostModel.getNewTs()));
                                        startActivity(i);
                                        finish();

                                    }
                                    else {
                                        NewPostHome.super.onBackPressed();
                                    }

                                }else{
                                    Utility.showToast(getApplicationContext(),"Something went wrong.");

                                }
                            });
                        }

                    }
                    else {
                        Long tsLong = System.currentTimeMillis();
                        ts = tsLong.toString();
                        progressDialog = new ProgressDialog(NewPostHome.this);
                        progressDialog.setTitle("Uploading");
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        //////////////CAMPUS or CAMPUS COMMUNITIES//////////////
//                    if(getIntent().getStringExtra("target").matches("3")||getIntent().getStringExtra("target").matches("2")
//                            ||getIntent().getStringExtra("target").matches("4") ){

//                        if (postspinner.getSelectedItem().toString().matches("Global")){
//                            FragmentGlobal.changed = 1;
//                            ProfileActivity.change = 1;
//                            docRef = firebaseFirestore.collection("Home").document("Global")
//                                    .collection("Feeds").document();
//                        }
//
//                        else{
//                            FragmentCampus.changed = 1;
//                            CommunityActivity.changed = 1;
//                            docRef = firebaseFirestore.collection("Home").document(CAMPUSNAME)
//                                    .collection("Feeds").document();
//                        }
                        docRef = firebaseFirestore.collection("Feeds").document();

                        homePostModel = new HomePostModel();
                        homePostModel.setUsN(introPref.getFullName());

                        homePostModel.setDp(introPref.getUserdp());
                        if(getIntent().getStringExtra("target")!= null){
                            if(getIntent().getStringExtra("target").matches("11")){
                                homePostModel.setChallengeID(getIntent().getStringExtra("challengeID"));
                            }
                            if(getIntent().getStringExtra("target").matches("4")){
                                homePostModel.setComID(getIntent().getStringExtra("comID"));
                                homePostModel.setComName(getIntent().getStringExtra("comName"));
                            }
                        }

                        homePostModel.setTs(tsLong);
                        homePostModel.setNewTs(tsLong);

                        homePostModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        if(selected_tags!= null && selected_tags.size()>0 ) {
                            homePostModel.setTagL(selected_tags);
                        }
                        if(text_content!= null && !text_content.isEmpty()  ) {
                            homePostModel.setTxt(text_content.trim());
                        }

                        if(pic!= null){
                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                            if (postspinner.getSelectedItem().toString().matches("Global")){
//                                reference = storageReferenece.child("Home/").child("Global/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else if(postspinner.getSelectedItem().toString().matches("Your Campus")){
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else {
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
                            reference = storageReferenece.child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");

                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                            Toast.makeText(getApplicationContext(), ""+pic.length/1024, Toast.LENGTH_LONG).show();

                            reference.putBytes(pic)
                                    .addOnSuccessListener(taskSnapshot ->
                                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                downloadUri = uri;
                                                generatedFilePath = downloadUri.toString();

                                                homePostModel.setImg(generatedFilePath);
                                                docRef.set(homePostModel).addOnCompleteListener(task -> {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        if(isTaskRoot()){
                                                            startActivity(new Intent(NewPostHome.this, MainActivity.class));
                                                        }
                                                        else {
                                                            NewPostHome.super.onBackPressed();
                                                        }
                                                    }else{
                                                        Utility.showToast(getApplicationContext(),"Something went wrong...");

                                                    }
                                                });

                                            }))

                                    .addOnFailureListener(e -> {
                                        Utility.showToast(getApplicationContext(), "Something went wrong");
                                        if(progressDialog!= null)
                                            progressDialog.dismiss();
                                    });

                        }
                        else {
                            docRef.set(homePostModel).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();

                                    if(isTaskRoot()){
                                        startActivity(new Intent(NewPostHome.this, MainActivity.class));
                                    }
                                    else {
                                        NewPostHome.super.onBackPressed();
                                    }
                                }else{
                                    Utility.showToast(getApplicationContext(),"Something went wrong.");

                                }
                            });
                        }
                    }

                }
            }
            else {
                Utility.showToast(getApplicationContext(), "Network unavailable...");
            }

        });

//        post_anon.setOnClickListener(v -> {
//            postusername.setText(R.string.anonymous);
//            user_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_anonymous_icon));
//
//            if(InternetConnection.checkConnection(getApplicationContext())) {
//                String text_content = postcontent.getText().toString();
//
//                if (text_content.isEmpty() && pic == null) {
//                    Utility.showToast(getApplicationContext(), "Post has got nothing...");
//                } else {
//                    if(intent.getStringExtra("target")!=null && intent.getStringExtra("target").matches("100")){
//                        progressDialog = new ProgressDialog(NewPostHome.this);
//                        progressDialog.setTitle("Saving changes");
//                        progressDialog.setMessage("Please wait...");
//                        progressDialog.show();
//
//
//                        if (postspinner.getSelectedItem().toString().matches("Global")){
//                            FragmentGlobal.changed = 1;
//                            ProfileActivity.change = 1;
//                            docRef = firebaseFirestore.collection("Home").document("Global")
//                                    .collection("Feeds").document(editPostModel.getDocID());
//                        }
//
//                        else{
//                            FragmentCampus.changed = 1;
//                            CommunityActivity.changed = 1;
//                            docRef = firebaseFirestore.collection("Home").document(CAMPUSNAME)
//                                    .collection("Feeds").document(editPostModel.getDocID());
//                        }
//
//                        ts = Long.toString(editPostModel.getTs());
//
//                        if(selected_tags!= null && selected_tags.size()>0 ) {
//                            editPostModel.setTagL(selected_tags);
//                        }
//                        if(text_content!= null && !text_content.isEmpty()  ) {
//                            editPostModel.setTxt(text_content.trim());
//                        }
//
//                        if(pic!= null){
//                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                            if (postspinner.getSelectedItem().toString().matches("Global")){
//                                reference = storageReferenece.child("Home/").child("Global/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else if(postspinner.getSelectedItem().toString().matches("Your Campus")){
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else {
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
////                            Toast.makeText(getApplicationContext(), ""+pic.length/1024, Toast.LENGTH_LONG).show();
//
//                            reference.putBytes(pic)
//                                    .addOnSuccessListener(taskSnapshot ->
//                                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
//                                                downloadUri = uri;
//                                                generatedFilePath = downloadUri.toString();
//
//                                                editPostModel.setImg(generatedFilePath);
//                                                docRef.set(editPostModel).addOnCompleteListener(task -> {
//                                                    if(task.isSuccessful()){
//                                                        progressDialog.dismiss();
//                                                        if(isTaskRoot()){
//                                                            startActivity(new Intent(NewPostHome.this, MainActivity.class));
//                                                        }
//                                                        else {
//                                                            NewPostHome.super.onBackPressed();
//                                                        }
//                                                    }else{
//                                                        Utility.showToast(getApplicationContext(),"Something went wrong...");
//
//                                                    }
//                                                });
//
//                                            }))
//
//                                    .addOnFailureListener(e -> {
//                                        Utility.showToast(getApplicationContext(), "Something went wrong");
//                                        if(progressDialog!= null)
//                                            progressDialog.dismiss();
//                                    });
//
//                        }
//
//                        else {
//                            editPostModel.setImg(null);
//                            docRef.set(editPostModel).addOnCompleteListener(task -> {
//                                if(task.isSuccessful()){
//                                    progressDialog.dismiss();
//
//                                    if(isTaskRoot()){
//                                        startActivity(new Intent(NewPostHome.this, MainActivity.class));
//                                    }
//                                    else {
//                                        NewPostHome.super.onBackPressed();
//                                    }
//                                }else{
//                                    Utility.showToast(getApplicationContext(),"Something went wrong.");
//
//                                }
//                            });
//                        }
//
//                    }
//
//                    else {
//                        Long tsLong = System.currentTimeMillis();
//                        ts = tsLong.toString();
//                        progressDialog = new ProgressDialog(NewPostHome.this);
//                        progressDialog.setTitle("Uploading");
//                        progressDialog.setMessage("Please wait...");
//                        progressDialog.show();
//                        //////////////CAMPUS or CAMPUS COMMUNITIES//////////////
////                    if (getIntent().getStringExtra("target").matches("3")||getIntent().getStringExtra("target").matches("2")) {
//
//                        if (postspinner.getSelectedItem().toString().matches("Global")){
//                            FragmentGlobal.changed = 1;
//                            ProfileActivity.change = 1;
//                            docRef = firebaseFirestore.collection("Home").document("Global")
//                                    .collection("Feeds").document();
//                        }
//                        else {
//                            FragmentCampus.changed = 1;
//                            docRef = firebaseFirestore.collection("Home").document(CAMPUSNAME)
//                                    .collection("Feeds").document();
//                        }
//
//                        homePostModel = new HomePostModel();
//                        homePostModel.setUsN("Anonymous");
//
//                        homePostModel.setNewTs(tsLong);
//                        homePostModel.setTs(tsLong);
//                        homePostModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                        if(!postspinner.getSelectedItem().toString().matches("Your Campus")){
//                            homePostModel.setComID(getIntent().getStringExtra("comID"));
//                            homePostModel.setComName(getIntent().getStringExtra("comName"));
//                        }
//                        if (selected_tags != null && selected_tags.size() > 0) {
//                            homePostModel.setTagL(selected_tags);
//                        }
//                        if (!text_content.isEmpty()) {
//                            homePostModel.setTxt(text_content.trim());
//                        }
//                        if (pic != null) {
//                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                            if (postspinner.getSelectedItem().toString().matches("Global")){
//                                reference = storageReferenece.child("Home/").child("Global/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else if(postspinner.getSelectedItem().toString().matches("Your Campus")){
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            else {
//                                reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                            }
//                            /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
////                            Toast.makeText(getApplicationContext(), ""+pic.length/1024, Toast.LENGTH_LONG).show();
//                            reference.putBytes(pic)
//                                    .addOnSuccessListener(taskSnapshot ->
//                                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
//                                                downloadUri = uri;
//                                                generatedFilePath = downloadUri.toString();
//
//                                                homePostModel.setImg(generatedFilePath);
//                                                docRef.set(homePostModel).addOnCompleteListener(task -> {
//                                                    if(task.isSuccessful()){
//                                                        progressDialog.dismiss();
//                                                        if(isTaskRoot()){
//                                                            startActivity(new Intent(NewPostHome.this, MainActivity.class));
//                                                        }
//                                                        else {
//                                                            NewPostHome.super.onBackPressed();
//                                                        }
//
//                                                    }else{
//                                                        Utility.showToast(getApplicationContext(),"Something went wrong...");
//
//                                                    }
//                                                });
//
//                                            }))
//
//                                    .addOnFailureListener(e -> {
//                                        Utility.showToast(getApplicationContext(), "Something went wrong");
//                                        if(progressDialog!= null)
//                                            progressDialog.dismiss();
//                                    });
//                        } else {
//                            docRef.set(homePostModel).addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    progressDialog.dismiss();
//                                    if(isTaskRoot()){
//                                        startActivity(new Intent(NewPostHome.this, MainActivity.class));
//                                    }
//                                    else {
//                                        NewPostHome.super.onBackPressed();
//                                    }
//
//                                } else {
//                                    Utility.showToast(getApplicationContext(), "Something went wrong.");
//
//                                }
//                            });
//                        }
//                    }
//
//                }
//            }
//            else{
//                Utility.showToast(getApplicationContext(), "Network unavailable...");
//            }
//
//        });
        ///////////////////////POST////////////////////////

        customTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        moreTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomTagsDialog bottomTagsDialog = new BottomTagsDialog();
                bottomTagsDialog.show(getSupportFragmentManager(),"BottomSheet");
            }
        });


//        info.setOnClickListener(v -> {
//            dialog = new Dialog(NewPostHome.this);
//            dialog.setContentView(R.layout.dialog_info_post);
//            dialog.show();
//        });

        cross.setOnClickListener(v -> {
            String text_content = postcontent.getText().toString();

            if(text_content.isEmpty() && pic==null){
                if(isTaskRoot()){
                    startActivity(new Intent(NewPostHome.this, MainActivity.class));
                    finish();
                }else {
                    super.onBackPressed();
                }
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewPostHome.this);
                builder.setTitle("Are you sure?")
                        .setMessage("Changes will be discarded...")
                        .setPositiveButton("Sure", (dialog, which) -> {
                            if(isTaskRoot()){
                                startActivity(new Intent(NewPostHome.this, MainActivity.class));
                                finish();
                            }else {
                                super.onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            }

        });

    }



    ////////////TAGS////////////////
    private void openDialog() {
        AlertDialog.Builder dialog= new AlertDialog.Builder(NewPostHome.this);
        LayoutInflater inflater= LayoutInflater.from(NewPostHome.this);
        View view=inflater.inflate(R.layout.dialog_tag_spinner,null);
        edtagtxt =view.findViewById(R.id.addtag);
        dialog.setView(view)
                .setTitle("Add Tag")
                .setNegativeButton("Cancel", (dialog12, which) ->
                        dialog12.dismiss())
                .setPositiveButton("Done", (dialog1, which) -> {
                    textdata = edtagtxt.getText().toString().trim();
                    if(textdata.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Empty tag", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        edtagtxt.setText("");

                        ArrayList<String> TagColorArray = new ArrayList<>();
                        TagColorArray.add("#f4b4ff");
                        TagColorArray.add("#aaf1ff");
                        TagColorArray.add("#ffdfad");
                        TagColorArray.add("#bcffa2");
                        TagColorArray.add("#cecbff");
                        TagColorArray.add("#cfffef");
                        TagColorArray.add("#ffc0bd");
                        TagColorArray.add("#faff9c");
                        TagColorArray.add("#7efdff");
                        TagColorArray.add("#ffe87b");

                        int pos= (int) (Math.random()* 10);
                        colorValue= TagColorArray.get(pos);

                        TagModel mytag = new TagModel();
                        mytag.setName_tag(textdata);
                        mytag.setColor_hex(colorValue);
                        selected_tags.add(mytag);

                        tagAdapter2.notifyDataSetChanged();
                        tags_selectedRecycler.setVisibility(View.VISIBLE);
                        Toast.makeText(NewPostHome.this,"New Tag Added", Toast.LENGTH_SHORT).show();
                    }


                })
                .show();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void buildRecyclerView_selectedtags(){
        tags_selectedRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        tags_selectedRecycler.setLayoutManager(linearLayoutManager);
        tags_selectedRecycler.setItemAnimator(new DefaultItemAnimator());
      //  selected_tags = new ArrayList<>();

        tagAdapter2 = new TagAdapter(selected_tags, getApplicationContext());
        tags_selectedRecycler.setAdapter(tagAdapter2);

        tagAdapter2.onClickListener((position, tag, color) -> {
            Toast.makeText(getApplicationContext(), "Long Press to remove tag", Toast.LENGTH_SHORT).show();
        });
        tagAdapter2.onLongClickListener((position, tag_name, tag_color) ->{
            TagModel tagModel = new TagModel();
            tagModel.setName_tag(tag_name);
            tagModel.setColor_hex(tag_color);

            selected_tags.remove(position);
            tagAdapter2.notifyItemRemoved(position);
//                    models.add(tagModel);
//                    models.sort((o1, o2) -> o1.getName_tag().compareTo(o2.getName_tag()));
//                    Collections.sort(models);
            if(selected_tags.size()==0)
                tags_selectedRecycler.setVisibility(View.GONE);

            //   tagAdapter.notifyDataSetChanged();
        });

//        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                selected_tags.remove(viewHolder.getAdapterPosition());
//                tagAdapter2.notifyItemRemoved(viewHolder.getAdapterPosition());
//            }
//        });
//        helper.attachToRecyclerView(tags_selectedRecycler);

    }

    @Override
    public void onTagClicked(TagModel tagModel) {
        tags_selectedRecycler.setVisibility(View.VISIBLE);
        selected_tags.add(tagModel);
        tagAdapter2.notifyDataSetChanged();
    }

    ////////////TAGS////////////////

    ///////////////////////HANDLE CAMERA AND GALLERY//////////////////////////
    private void pickGallery(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data!=null){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                try {
                    filePath = data.getData();
                    finalUri = filePath;
                    if(filePath!=null) {

//                        postimage.setVisibility(View.VISIBLE);
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        options.inSampleSize = 2;
                        options.inJustDecodeBounds = false;
                        options.inTempStorage = new byte[16 * 1024];

                        InputStream input = this.getContentResolver().openInputStream(filePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                        pic = baos.toByteArray();

                        imageCompressor = new ImageCompressor(pic);
                        imageCompressor.execute();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){

                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                pic = baos.toByteArray();
//                postimage.setImageBitmap(bitmap);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                filePath = Uri.parse(path);
                finalUri = filePath;
                imageCompressor = new ImageCompressor(pic);
                imageCompressor.execute();

//                postimage.setVisibility(View.VISIBLE);
//                container_image.setVisibility(View.VISIBLE);

            }

            ////////////////////////CROP//////////////////////
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                finalUri = resultUri;
//                postimage.setVisibility(View.VISIBLE);
//                postimage.setImageURI(finalUri);
//                container_image.setVisibility(View.VISIBLE);

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), finalUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos =new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                pic = baos.toByteArray();
                imageCompressor = new ImageCompressor(pic);
                imageCompressor.execute();

            }
            else {//CROP ERROR
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
            ////////////////////////CROP//////////////////////

        }

    }
    ///////////////////////HANDLE CAMERA AND GALLERY///////////////////////////

    //////////////////////PREMISSIONS//////////////////////////
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(NewPostHome.this, storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(NewPostHome.this, cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this,"permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){

                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this,"permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //////////////////////PREMISSIONS//////////////////////////


    class ImageCompressor extends AsyncTask<Void, Void, byte[]> {

        private final float maxHeight = 1080.0f;
        private final float maxWidth = 720.0f;
        private byte[] pic2;

        public ImageCompressor(byte[] pic) {
            this.pic2 = pic;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public byte[] doInBackground(Void... strings) {
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(pic2, 0, pic2.length, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            float imgRatio = (float) actualWidth / (float) actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeByteArray(pic2, 0, pic2.length, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 4.0f;
            float middleY = actualHeight / 4.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 4, middleY - bmp.getHeight() / 4, new Paint(Paint.FILTER_BITMAP_FLAG));

//            if(bmp!=null)
//            {
//                bmp.recycle();
//            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            byte[] by = out.toByteArray();
//            bmp.recycle();
//            scaledBitmap.recycle();
            return by;
        }

        @Override
        protected void onPostExecute(byte[] picCompressed) {
            if(picCompressed!= null) {
                pic = picCompressed;
//                Toast.makeText(getApplicationContext(), ""+ pic.length/1024,Toast.LENGTH_LONG).show();
                Bitmap bitmap = BitmapFactory.decodeByteArray(picCompressed, 0 ,picCompressed.length);
                postimage.setImageBitmap(bitmap);
                container_image.setVisibility(View.VISIBLE);
                postimage.setVisibility(View.VISIBLE);
//                /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                if (postspinner.getSelectedItem().toString().matches("Global")){
//                    reference = storageReferenece.child("Home/").child("Global/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                }
//                else if(postspinner.getSelectedItem().toString().matches("Your Campus")){
//                    reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                }
//                else {
//                    reference = storageReferenece.child("Home/").child(CAMPUSNAME+"/").child("Feeds/").child(fireuser.getUid() +"_"+ ts + "post_img");
//                }
//                /////////////SELECT GLOBAL/YOUR CAMPUS/////////////
//                reference.putBytes(pic)
//                        .addOnSuccessListener(taskSnapshot ->
//                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
//                                    downloadUri = uri;
////                                    generatedFilePath = downloadUri.toString();
//
//                                    homePostModel.setImg(generatedFilePath);
//                                    docRef.set(homePostModel).addOnCompleteListener(task -> {
//                                        if(task.isSuccessful()){
//                                            progressDialog.dismiss();
//                                            NewPostHome.super.onBackPressed();
//
//                                        }else{
//                                            Utility.showToast(getApplicationContext(),"Something went wrong...");
//
//                                        }
//                                    });
//
//                                }))
//
//                        .addOnFailureListener(e -> {
//                            Utility.showToast(getApplicationContext(), "Something went wrong");
//                            if(progressDialog!= null)
//                                 progressDialog.dismiss();
//
//                        });           Image i

            }
        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 4;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }

            return inSampleSize;
        }

    }


    @Override
    public void onBackPressed() {
        String text_content = postcontent.getText().toString();

        if(text_content.isEmpty() && pic==null){
            if(isTaskRoot()){
                startActivity(new Intent(NewPostHome.this, MainActivity.class));
                finish();
            }else {
                super.onBackPressed();
            }
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(NewPostHome.this);
            builder.setTitle("Are you sure?")
                    .setMessage("Changes will be discarded...")
                    .setPositiveButton("Sure", (dialog, which) -> {
                        if(isTaskRoot()){
                            startActivity(new Intent(NewPostHome.this, MainActivity.class));
                            finish();
                        }else {
                            super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(imageCompressor != null) {
            imageCompressor.cancel(true);
        }
    }
}