package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

        // Ánh xạ View từ layout activity_dangnhap.xml
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Xử lý ẩn / hiện mật khẩu bằng icon con mắt matbat.xml & mattat.xml
        final boolean[] isPasswordVisible = {false};
        if (ivTogglePassword != null) {
            ivTogglePassword.setOnClickListener(v -> {
                if (isPasswordVisible[0]) {
                    // Ẩn mật khẩu (chỉ thấy chấm/mã số) -> Hiện icon matbat
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.matbat);
                    isPasswordVisible[0] = false;
                } else {
                    // Hiện mật khẩu -> Hiện icon mattat
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.mattat);
                    isPasswordVisible[0] = true;
                }
                etPassword.setSelection(etPassword.getText().length());
            });
        }

        // Nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(ManHinhDangNhap.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Phân quyền tài khoản (không phân biệt chữ hoa/thường)
            String userLower = username.toLowerCase();
            if (userLower.contains("gv") || userLower.contains("giangvien") || userLower.contains("giaovien")) {
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