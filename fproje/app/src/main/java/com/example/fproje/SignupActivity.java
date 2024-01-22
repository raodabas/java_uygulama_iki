package com.example.fproje;

import android.content.Intent;
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

public class SignupActivity extends AppCompatActivity {
    private Button btn1, btn2;
    private EditText firstname,lastname,emaill,passwordd;

    private ProgressBar progressBar;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        btn2=findViewById(R.id.btn2);
        btn1=findViewById(R.id.btn1);
        firstname=findViewById(R.id.fname);
        lastname=findViewById(R.id.lname);
        emaill=findViewById(R.id.email);
        passwordd=findViewById(R.id.password);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressbar);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View v){
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email=String.valueOf(emaill.getText());
                password=String.valueOf(passwordd.getText());

                if (TextUtils.isEmpty(email)) {

                    Toast.makeText(SignupActivity.this,"Email girişi yapınız",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {

                    Toast.makeText(SignupActivity.this,"Email girişi yapınız",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this,"Hesap oluşturuldu",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignupActivity.this,"Hesap oluşturalamadı"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
