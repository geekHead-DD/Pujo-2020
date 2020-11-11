package com.applex.utsav.registration;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;
import com.applex.utsav.MainActivity;
import com.applex.utsav.NewPostHome;
import com.applex.utsav.R;
import com.applex.utsav.models.AccessToken;
import com.applex.utsav.models.BaseUserModel;
import com.applex.utsav.models.NotifCount;
import com.applex.utsav.preferences.IntroPref;
import com.applex.utsav.utility.BasicUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static java.lang.Boolean.FALSE;

public class LoginActivityPhone extends AppCompatActivity {

    private EditText mPhoneNo,mCode;
    private Dialog myDialogue;
    private String verificationId;

    private IntroPref introPref;

    private ImageView back;

    private FirebaseUser fireuser;

    private AccessToken accessToken;
    private String tokenStr;

    private LottieAnimationView progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        introPref = new IntroPref(LoginActivityPhone.this);
        String lang= introPref.getLanguage();
        Locale locale= new Locale(lang);
        Locale.setDefault(locale);
        Configuration config= new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        /////////////////DAY OR NIGHT MODE///////////////////
        if(introPref.getTheme() == 1) {
            FirebaseFirestore.getInstance().document("Mode/night_mode")
                    .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    if(task.getResult().getBoolean("night_mode")) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        } else if(introPref.getTheme() == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if(introPref.getTheme() == 3) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        /////////////////DAY OR NIGHT MODE///////////////////
        setContentView(R.layout.activity_login_phone);

        if(introPref.getTheme() == 1) {
            FirebaseFirestore.getInstance().document("Users/"+ FirebaseAuth.getInstance().getUid())
                    .addSnapshotListener(LoginActivityPhone.this, (value, error) -> {
                        if(value != null) {
                            if(value.getBoolean("listener") != null && value.getBoolean("listener")) {
                                FirebaseFirestore.getInstance().document("Mode/night_mode")
                                        .get().addOnCompleteListener(task -> {
                                    if(task.isSuccessful()) {
                                        if(task.getResult().getBoolean("night_mode")) {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                        } else {
                                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                        }
                                    } else {
                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    }
                                    new Handler().postDelayed(() -> {
                                        FirebaseFirestore.getInstance().document("Users/"+ FirebaseAuth.getInstance().getUid()).update("listener", false);
                                        startActivity(new Intent(LoginActivityPhone.this, LoginActivityPhone.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }, 200);
                                });
                            }
                        }
                    });
        }

        mCode = findViewById(R.id.code);
        mPhoneNo = findViewById(R.id.phone);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivityPhone.super.onBackPressed();
            }
        });

        myDialogue = new Dialog(LoginActivityPhone.this);

        myDialogue.setContentView(R.layout.dialog_otp_progress);
        myDialogue.setCanceledOnTouchOutside(FALSE);
        myDialogue.findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = myDialogue.findViewById(R.id.otp_dialog);
                if(et.getText().toString().length()==6){
                    verifyCode(et.getText().toString());
                }
                else
                    Toast.makeText(getApplicationContext(),"Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });
        Objects.requireNonNull(myDialogue.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mCode.getText().toString();

                String number = mPhoneNo.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    mPhoneNo.setError("Valid number is required");
                    mPhoneNo.requestFocus();
                    return;
                }
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if(number.matches(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+91",""))){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("phone",mPhoneNo.getText().toString().trim());
                        startActivity(intent);
                    }
                    else {
                        FirebaseAuth.getInstance().signOut();
                        String phoneNumber = code + number;
                        myDialogue.show();
                        sendVerificationCode(phoneNumber);
                    }
                }
                else {
                    String phoneNumber = code + number;
                    myDialogue.show();
                    sendVerificationCode(phoneNumber);
                }

            }
        });
    }

    /////////////////SEND NO FOR VERIFICATION/////////////
    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                (Activity) TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                EditText et = myDialogue.findViewById(R.id.otp_dialog);
                et.setText(code);
//                if(et.getText().toString().length()==6){
//                    verifyCode(et.getText().toString());
//                }
//                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            myDialogue.dismiss();
        }
    };
    /////////////////SEND NO FOR VERIFICATION/////////////

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance().document("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()) {
                                                IntroPref introPref= new IntroPref(LoginActivityPhone.this);
                                                BaseUserModel userModel = documentSnapshot.toObject(BaseUserModel.class);

                                                accessToken = new AccessToken();
                                                NotifCount notifCount= new NotifCount();
                                                notifCount.setNotifCount(0);

                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                            @Override
                                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                                if(instanceIdResult == null) {
                                                                    Log.w("TAG", "getInstanceId failed");
                                                                    BasicUtility.showToast(getApplicationContext(),"Error creating token");
                                                                    return;
                                                                }
                                                                else {

                                                                    progress.setVisibility(GONE);
                                                                    // Get new Instance ID token
                                                                    tokenStr = instanceIdResult.getToken();
                                                                    accessToken.setRegToken(tokenStr);

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("Users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/AccessToken/")
                                                                            .document("Token").set(accessToken);

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("Users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/notifCount/")
                                                                            .document("notifCount").set(notifCount);

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                            .update("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                                    introPref.setType(userModel.getType());
                                                                    introPref.setUserdp(userModel.getDp());
                                                                    introPref.setFullName(userModel.getName());
                                                                    introPref.setGender(userModel.getGender());
                                                                    Intent i1 = new Intent(LoginActivityPhone.this, MainActivity.class);
                                                                    startActivity(i1);
                                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                                    finish();

                                                                }
                                                            }
                                                        });
                                            }
                                            else {
                                                Intent i = new Intent(LoginActivityPhone.this, RegChoice.class);
                                                i.putExtra("value", "phone");
                                                i.putExtra("email", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                                startActivity(i);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                finish();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            BasicUtility.showToast(getApplicationContext(), "Something went wrong...");
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            myDialogue.dismiss();
                        }
                    }

                });
    }

}