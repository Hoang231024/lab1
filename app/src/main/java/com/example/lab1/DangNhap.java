package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DangNhap extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        firebaseAuth = FirebaseAuth.getInstance();
        EditText edtEmail = findViewById(R.id.edemail);
        EditText edtPass = findViewById(R.id.edipass);
        Button btnDangKy = findViewById(R.id.btndn);
        TextView btn_dang_nhapotp = findViewById(R.id.btn_lay_otp);
        String emailnhap = getIntent().getStringExtra("EMAIL");
        String passnhap = getIntent().getStringExtra("Pass");
        edtEmail.setText(emailnhap);
        edtPass.setText(passnhap);
        btn_dang_nhapotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DangNhap.this,lay_OTP.class));
            }
        });
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(DangNhap.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(DangNhap.this, "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
                }  else if (pass.length() <= 5) {
                    Toast.makeText(DangNhap.this, "Mật khẩu phải có ít nhất 5 ký tự", Toast.LENGTH_SHORT).show();
                } else if (!pass.matches("^[A-Z].*")) {
                    Toast.makeText(DangNhap.this, "Mật khẩu phải viết hoa ký tự đầu", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(DangNhap.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Toast.makeText(DangNhap.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DangNhap.this, MainActivity.class));
                            } else {
                                Log.w("loi ne", task.getException());
                                Toast.makeText(DangNhap.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });

    }
}