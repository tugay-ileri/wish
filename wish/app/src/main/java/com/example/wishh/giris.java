package com.example.wishh;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthAPIService;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class giris extends AppCompatActivity {
    EditText mail, sifre;
    TextView rlt_kaydol;
    RelativeLayout rlt_giris, rlt_huawei_giris;
    FirebaseAuth giris_yetkisi;
    ProgressDialog pdgiris;
    AuthHuaweiId huaweiAccount;
    HuaweiIdAuthService service;
    AccountAuthParams authParamss;
    AccountAuthService servicee;
    HuaweiIdAuthParams authParams;
    FirebaseUser kullanici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giris);

        pdgiris = new ProgressDialog(giris.this);
        pdgiris.setMessage("giriş yapılıyor");
        pdgiris.show();
        tannımla();
        control();
        gecis();
        huawei_giris();
        hizli_giris(authParamss,servicee);




    }

    //xml den gelen verileri değişkenlere atama.
    public void tannımla() {
        mail = findViewById(R.id.g_mail);
        sifre = findViewById(R.id.g_sifre);

        rlt_giris = findViewById(R.id.g_giris);
        rlt_kaydol = findViewById(R.id.g_kaydol);
        rlt_huawei_giris = findViewById(R.id.huawei_giris);

        giris_yetkisi = FirebaseAuth.getInstance();

         authParamss = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
         servicee = AccountAuthManager.getService(giris.this, authParamss);

        authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setMobileNumber()
                .setId()
                .createParams();
        service = HuaweiIdAuthManager.getService(giris.this, authParams);
    }

    //gelen veriyi konrol eder
    public void control() {
        rlt_giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdgiris.show();
                String maill = mail.getText().toString();
                String sifree = sifre.getText().toString();
                if (maill.isEmpty() || sifree.isEmpty()){
                    pdgiris.dismiss();
                    Toast.makeText(giris.this, "lütfen boş alan bırakmayın", Toast.LENGTH_LONG).show();
                }
                else
                {
                    giris_yap(maill,sifree);
                }
            }
        });
    }
    // kaydola tıklandığında kaydol.java activity geçiş sağlar.

    public void gecis() {
        rlt_kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), kaydol.class));
            }
        });
    }

    public void giris_yap(String mail, String sifre) {
        giris_yetkisi.signInWithEmailAndPassword(mail, sifre)
                .addOnCompleteListener(giris.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference yol_giris = FirebaseDatabase.getInstance().getReference().child("kullaniciler").child(giris_yetkisi.getCurrentUser().getUid());

                            yol_giris.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Intent intent = new Intent(giris.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    pdgiris.dismiss();
                                    Toast.makeText(giris.this, "giriş yapıldı", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    pdgiris.dismiss();

                                }
                            });
                        }
                        else
                        {
                            pdgiris.dismiss();
                            Toast.makeText(giris.this, "lütfen bilgileriniz kontrol edip tekrar deneyin", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void huawei_giris() {
        rlt_huawei_giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdgiris.show();
               huawei_veri_al(authParams , service );

            }
        });
    }


    public void huawei_veri_al(HuaweiIdAuthParams authprm,HuaweiIdAuthService servicee) {
        HuaweiIdAuthService service = servicee ;
        HuaweiIdAuthParams authParams = authprm;
        startActivityForResult(service.getSignInIntent(), 1907);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1907) {
            com.huawei.hmf.tasks.Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);

            if (authHuaweiIdTask.isSuccessful()) {
                huaweiAccount = authHuaweiIdTask.getResult();

                if (huaweiAccount.getEmail().isEmpty()) {
                    pdgiris.dismiss();
                    Toast.makeText(this, "bizim için adın ve mailin önemli lütfen huawei kimliğinde bunları doğrula", Toast.LENGTH_LONG).show();
                } else {
                    String sifre = huaweiAccount.getUid() ;
                    giris_yap(huaweiAccount.getEmail(),huaweiAccount.getUid());

                }

            } else {
                // The sign-in failed. No processing is required. Logs are recorded to facilitate fault locating.
                Log.e("TAG", "sign in failed : " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());


            }
        }

    }


    public void hizli_giris(AccountAuthParams athprm, AccountAuthService servie) {
        AccountAuthParams authParams = athprm;
        AccountAuthService service = servie;
        com.huawei.hmf.tasks.Task<AuthAccount> task = service.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                Log.i("TAG", "displayName:" + authAccount.getDisplayName());
                startActivity(new Intent(giris.this,MainActivity.class));
                pdgiris.dismiss();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException)e;
                    Log.i("TAG", "sign failed status:" + apiException.getStatusCode());
                    pdgiris.dismiss();
                }
            }
        });

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        kullanici =  FirebaseAuth.getInstance().getCurrentUser();
        if (kullanici != null)
        {
            startActivity(new Intent(giris.this,MainActivity.class));
        }
        pdgiris.dismiss();

    }

}
