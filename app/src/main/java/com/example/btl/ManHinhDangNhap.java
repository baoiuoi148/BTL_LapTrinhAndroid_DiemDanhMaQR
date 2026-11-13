package com.example.btl;

import android.content.Intent;
import android.database.Cursor;
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
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dangnhap);

        dbHelper = new DatabaseHelper(this);

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

            // Bước 1: Kiểm tra rỗng
            boolean hasError = false;
            if (username.isEmpty()) {
                etUsername.setError("Vui lòng nhập tên đăng nhập");
                hasError = true;
            }
            if (password.isEmpty()) {
                etPassword.setError("Vui lòng nhập mật khẩu");
                hasError = true;
            }
            if (hasError) {
                return;
            }

            // Bước 2: Kiểm tra định dạng tên đăng nhập (Mã tài khoản)
            String regexSinhVien = "^(19|20|21|22|23|24)A1001D\\d{4}$";
            String regexGiangVien = "^GV\\d+$";

            boolean isSinhVien = username.matches(regexSinhVien);
            boolean isGiangVien = username.matches(regexGiangVien);

            if (!isSinhVien && !isGiangVien) {
                etUsername.setError("Tài khoản không đúng định dạng");
                Toast.makeText(ManHinhDangNhap.this, "Tài khoản không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bước 3: Truy vấn SQLite kiểm tra đăng nhập
            Cursor cursor = dbHelper.kiemTraDangNhap(username, password);

            if (cursor != null && cursor.moveToFirst()) {
                int indexVaiTro = cursor.getColumnIndex("vai_tro");
                int indexMaNguoiDung = cursor.getColumnIndex("ma_nguoi_dung");

                String vaiTro = indexVaiTro != -1 ? cursor.getString(indexVaiTro) : "";
                String maNguoiDung = indexMaNguoiDung != -1 ? cursor.getString(indexMaNguoiDung) : username;

                Toast.makeText(ManHinhDangNhap.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                if ("GIANG_VIEN".equalsIgnoreCase(vaiTro) || isGiangVien) {
                    Intent intent = new Intent(ManHinhDangNhap.this, ManHinhTrangChuGiaoVien.class);
                    intent.putExtra("ma_nguoi_dung", maNguoiDung);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ManHinhDangNhap.this, ManHinhTrangChuSinhVien.class);
                    intent.putExtra("ma_nguoi_dung", maNguoiDung);
                    startActivity(intent);
                }

                cursor.close();
                finish();
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                Toast.makeText(ManHinhDangNhap.this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
            }
        });

        // Chuyển sang màn hình Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ManHinhDangNhap.this, ManHinhQuenMatKhau.class);
            startActivity(intent);
        });
    }
}