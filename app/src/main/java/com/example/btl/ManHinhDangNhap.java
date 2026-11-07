package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhDangNhap extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dangnhap);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View từ layout activity_login.xml
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(ManHinhDangNhap.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Phân quyền tài khoản
            String userLower = username.toLowerCase();
            if (userLower.contains("GV") || userLower.contains("giangvien")) {
                Toast.makeText(ManHinhDangNhap.this, "Đăng nhập tài khoản Giảng viên thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ManHinhDangNhap.this, ManHinhTrangChuGiaoVien.class);
                startActivity(intent);
            } else {
                Toast.makeText(ManHinhDangNhap.this, "Đăng nhập tài khoản Sinh viên thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ManHinhDangNhap.this, ManHinhTrangChuSinhVien.class);
                startActivity(intent);
            }
        });

        // Chuyển sang màn hình Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ManHinhDangNhap.this, ManHinhQuenMatKhau.class);
            startActivity(intent);
        });
    }
}