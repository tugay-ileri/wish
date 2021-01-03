package com.example.wishh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.io.Serializable;
import java.util.HashMap;

public class kaydol extends AppCompatActivity {
    EditText k_mail, k_ad, k_soyad, k_sifre;
    RadioButton radioButton;
    RadioGroup k_radioGroup;
    RelativeLayout k_kaydol, k_huawei_kaydol;
    AuthHuaweiId huaweiAccount;
    HuaweiIdAuthService service;
    AccountAuthParams authParamss;
    AccountAuthService servicee;
    HuaweiIdAuthParams authParams;

    FirebaseAuth yetki;
    DatabaseReference yol;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaydol);
        tanımla();
        kaydol_control();
        k_huawei_kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                huawei_veri_al(authParams , service );
            }
        });

    }

    public void tanımla() {
        yetki = FirebaseAuth.getInstance();
        k_radioGroup = findViewById(R.id.k_radiogroup);
        k_mail = findViewById(R.id.mail);
        k_ad = findViewById(R.id.ad);
        k_soyad = findViewById(R.id.soyad);
        k_sifre = findViewById(R.id.sifre);
        k_kaydol = findViewById(R.id.k_kaydol);
        k_huawei_kaydol = findViewById(R.id.k_huawei);
        authParamss = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
        servicee = AccountAuthManager.getService(kaydol.this, authParamss);

        authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setMobileNumber()
                .setId()
                .createParams();
        service = HuaweiIdAuthManager.getService(kaydol.this, authParams);
    }

    // kaydolurken gerekli  bilgileri kontrol et kullanıcı bilgileri doğru girdiyse kaydet.
    public void kaydol_control() {
        k_kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(kaydol.this);
                pd.setMessage("lütfen bekleyin...");
                pd.show();

                radiogrp();

                String mail = k_mail.getText().toString();
                String ad = k_ad.getText().toString();
                String soyad = k_soyad.getText().toString();
                String sifre = k_sifre.getText().toString();
                String radio = radioButton.getText().toString();

                if ((radio.isEmpty() || mail.isEmpty() || ad.isEmpty() || soyad.isEmpty() || sifre.isEmpty())) {
                    pd.dismiss();
                    Toast.makeText(kaydol.this, "lütfen boş alaln bırakmayın", Toast.LENGTH_LONG).show();


                } else if (sifre.length() < 6) {

                    Toast.makeText(kaydol.this, "şifreniz 6 karakterten uzun olmalı!", Toast.LENGTH_LONG).show();
                } else {
                    kaydet(mail,ad,soyad,radio,sifre);
                }
            }
        });

    }

    public void radiogrp() {
        int radioid = k_radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);
    }

    //kulanıcı kaydetme kodları
    public void kaydet(String mail, String ad, String soyad, String cinsiyet, String sifre) {
        yetki.createUserWithEmailAndPassword(mail, sifre)
                .addOnCompleteListener( kaydol.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NotNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseKullanici = yetki.getCurrentUser();

                            String kullaniciid = firebaseKullanici.getUid();

                            yol = FirebaseDatabase.getInstance().getReference().child("kullaniciler").child(kullaniciid);

                            HashMap<String,Object> hashMap = new HashMap<>();

                            hashMap.put("id",kullaniciid);
                            hashMap.put("mail",mail);
                            hashMap.put("ad",ad.toLowerCase());
                            hashMap.put("soyad",soyad.toLowerCase());
                            hashMap.put("cinsiyet",cinsiyet);
                            hashMap.put("sifre",sifre);
                            yol.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    pd.dismiss();

                                    Intent intent = new Intent(kaydol.this,giris.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                }
                            });
                        }
                        else
                            {
                                pd.dismiss();
                                Toast.makeText(kaydol.this, "kulandığınız mail veya şifre sisteme tanımlanamıyor", Toast.LENGTH_LONG).show();

                            }
                    }
                });
    }
    public void huawei_veri_al(HuaweiIdAuthParams authprm, HuaweiIdAuthService servicee) {
        HuaweiIdAuthService service = servicee ;
        HuaweiIdAuthParams authParams = authprm;
        startActivityForResult(service.getSignInIntent(), 1881);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1881) {
            com.huawei.hmf.tasks.Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);

            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();


                    kaydet(huaweiAccount.getEmail(),huaweiAccount.getDisplayName(),huaweiAccount.getFamilyName(), String.valueOf(huaweiAccount.getGender()),huaweiAccount.getUid());

            } else {
                // The sign-in failed. No processing is required. Logs are recorded to facilitate fault locating.
                Log.e("TAG", "sign in failed : " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());


            }
        }

    }
}