package com.example.fproje;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button btn1,btn2;
    private EditText emaill,passwordd;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btn2=findViewById(R.id.btn2);
        btn1=findViewById(R.id.btn1);
        emaill=findViewById(R.id.email);
        passwordd=findViewById(R.id.password);
        progressBar=findViewById(R.id.progressbar);
        mAuth=FirebaseAuth.getInstance();

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email=String.valueOf(emaill.getText());
                password=String.valueOf(passwordd.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Email girişi yapınız", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Password girişi yapınız", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task < AuthResult > task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Giriş başarılı", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, drawermenu.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Hatalı" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                        startActivity(intent);
                    }
                });
                };
            });
        };
    }

