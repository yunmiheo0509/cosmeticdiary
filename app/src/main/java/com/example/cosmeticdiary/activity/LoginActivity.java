package com.example.cosmeticdiary.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cosmeticdiary.DialogCheckIdPw;
import com.example.cosmeticdiary.MySharedPreferences;
import com.example.cosmeticdiary.R;
import com.example.cosmeticdiary.model.LoginModel;
import com.example.cosmeticdiary.retrofit.RetrofitHelper;
import com.example.cosmeticdiary.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private DialogCheckIdPw dialogCheckIdPw;
    private Button btnlogin;
    private Button btnregist;
    private TextView btnfindIdPw;
    private EditText et_id;
    private EditText et_password;

    RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnlogin = findViewById(R.id.btn_login);
        btnregist = findViewById(R.id.btn_resgist);
        btnfindIdPw = findViewById(R.id.tv_findID);

        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_pw);

        // SharedPreferences 안에 값이 저장되어 있지 않을 때 -> Login
        if(MySharedPreferences.getUserId(this).isEmpty()
                || MySharedPreferences.getUserPass(this).isEmpty()) {
            Login();
        }
        else { // SharedPreferences 안에 값이 저장되어 있을 때 -> MainActivity로 이동
            Toast.makeText(this, MySharedPreferences.getUserId(this) + "님 자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnfindIdPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindIdPwActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Login() {
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofitService = RetrofitHelper.getRetrofit().create(RetrofitService.class);

                Call<LoginModel> call = retrofitService.getLoginCheck(et_id.getText().toString(),
                        et_password.getText().toString());

                call.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        if (response.isSuccessful()) {
                            Log.d("연결 성공", response.message());
                            LoginModel loginModel = response.body();
                            Log.v("code", loginModel.getCode());
                            Log.v("success", loginModel.getSuccess());
                            if (loginModel.getCode().equals("200")) {
                                MySharedPreferences.setUserId(LoginActivity.this, et_id.getText().toString());
                                MySharedPreferences.setUserPass(LoginActivity.this, et_password.getText().toString());

                                Toast.makeText(LoginActivity.this, "로그인 되었습니다.".toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

//                                appData.setPREF_LOGIN_ID(loginModel.getUserID());
//                                appData.setPREF_LOGIN("y");
                            } else {
//                                Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 확인해주세요"
//                                        , Toast.LENGTH_SHORT).show();
                                dialogCheckIdPw = new DialogCheckIdPw(LoginActivity.this, dialogListener);
                                dialogCheckIdPw.show();
                                et_password.setText("");
                                Log.d("ssss", response.message());
                            }

                        } else if (response.code() == 404) {
                            Toast.makeText(LoginActivity.this, "인터넷 연결을 확인해주세요"
                                    , Toast.LENGTH_SHORT).show();
                            Log.d("ssss", response.message());

                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        Log.d("ssss", t.getMessage());
                    }
                });
            }
        });
    }

    //다이얼로그창
    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogCheckIdPw.dismiss();
        }
    };
}