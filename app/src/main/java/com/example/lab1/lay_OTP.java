package com.example.lab1;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class lay_OTP extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private EditText otpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lay_otp);
        mAuth = FirebaseAuth.getInstance();
        otpEditText = findViewById(R.id.editTextOTP);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                otpEditText.setText(phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(lay_OTP.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Xử lý khi OTP được gửi thành công
                mVerificationId = verificationId;
                Toast.makeText(lay_OTP.this, "OTP đã được gửi", Toast.LENGTH_SHORT).show();
            }
        };
        Button btnGetOTP = findViewById(R.id.btnGetOTP);
        Button btnLoginOTP = findViewById(R.id.btnLoginOTP);

        btnGetOTP.setOnClickListener(view -> {
            EditText phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
            String phoneNumber = phoneNumberEditText.getText().toString();
            getOTP(phoneNumber);
        });

        btnLoginOTP.setOnClickListener(view -> {
            String otp = otpEditText.getText().toString();
            verifyOTP(otp);
        });

    }


    private static final int AUTH_TIMEOUT_SECONDS = 60;

    private void getOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phoneNumber,
                AUTH_TIMEOUT_SECONDS,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }


    private void verifyOTP(String code) {
        if (mVerificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(this, "Vui lòng nhận OTP trước khi xác thực", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(lay_OTP.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(lay_OTP.this, MainActivity.class));
                            finish(); // Kết thúc hoạt động hiện tại sau khi đăng nhập thành công
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(lay_OTP.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(lay_OTP.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

}