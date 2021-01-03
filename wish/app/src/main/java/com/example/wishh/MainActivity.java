    package com.example.wishh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wishh.cframe.baslangic;
import com.example.wishh.cframe.profil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationView;
    Fragment secili_fragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tanımla();

            }
            public void tanımla()
            {
                navigationView = findViewById(R.id.navigation);
                navigationView.setOnNavigationItemSelectedListener(navigationViewItemselected);
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new baslangic()).commit();
            }
            private BottomNavigationView.OnNavigationItemSelectedListener navigationViewItemselected =
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                            switch (item.getItemId())
                            {
                                case R.id.navigation_baslangic:
                                    secili_fragment = new baslangic();
                                    break;
                                case R.id.navigation_arama:
                                    secili_fragment = null;
                                    startActivity(new Intent(MainActivity.this,anasayfa.class));
                                    break;
                                case R.id.navigation_profil:
                                    SharedPreferences.Editor editor = getSharedPreferences("prefs",MODE_PRIVATE).edit();
                                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    editor.apply();
                                    secili_fragment =new profil();
                                    break;

                            }

                            if (secili_fragment != null)
                            {
                                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,secili_fragment).commit();
                            }



                            return true;
                        }
                    };

        }