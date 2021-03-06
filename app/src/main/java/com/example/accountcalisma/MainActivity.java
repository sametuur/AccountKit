package com.example.accountcalisma;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class MainActivity extends AppCompatActivity {
    TextView txt,email;
    Button button;
    AuthHuaweiId huaweiAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        huaweiAccount = null;
        button = findViewById(R.id.button);
        txt = findViewById(R.id.txt);
        email=findViewById(R.id.email);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().setIdToken().createParams();
                HuaweiIdAuthService service = HuaweiIdAuthManager.getService(MainActivity.this, authParams);
                if(huaweiAccount == null)
                {
                    startActivityForResult(service.getSignInIntent(), 1123);
                }
                else
                {
                    Task<Void> signOutTask = service.signOut();
                    signOutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            // Processing after the sign-out.
                            Log.i("TAG", "signOut complete");
                            button.setText("Giriş Yap");
                            txt.setText("Giriş Yap");
                            email.setVisibility(View.INVISIBLE);
                            huaweiAccount = null;
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result to obtain an ID token from AuthHuaweiId.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1123)
        {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful())
            {
                // The sign-in is successful, and the user's HUAWEI ID information and ID token are obtained.
                huaweiAccount = authHuaweiIdTask.getResult();
                txt.setText(huaweiAccount.getDisplayName());
                button.setText("Çıkış Yap");
                email.setVisibility(View.VISIBLE);
                email.setText(huaweiAccount.getEmail());


            } else {
                // The sign-in failed. No processing is required. Logs are recorded to facilitate fault locating.
                Log.e("TAG", "sign in failed : " +((ApiException)authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }
}