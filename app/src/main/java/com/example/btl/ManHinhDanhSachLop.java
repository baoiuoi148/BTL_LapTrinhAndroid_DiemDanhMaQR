package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhDanhSachLop extends AppCompatActivity implements View.OnClickListener {
    private ImageView mimageViewHienMaQR;
    private ImageView mimageViewUserHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_danh_sach_lop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        addAction();
    }

    private void addAction() {
        mimageViewHienMaQR.setOnClickListener(this);
        if (mimageViewUserHeader != null) {
            mimageViewUserHeader.setOnClickListener(this);
        }
    }

    private void initView() {
        mimageViewHienMaQR = findViewById(R.id.imageViewHienMaQR);
        mimageViewUserHeader = findViewById(R.id.imageViewUserHeader);
    }

    private void xacNhanDangXuat() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(ManHinhDanhSachLop.this, ManHinhDangNhap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v == mimageViewHienMaQR) {
            Intent intent = new Intent(ManHinhDanhSachLop.this, ManHinhHienMaQR.class);
            startActivity(intent);
        } else if (v == mimageViewUserHeader) {
            xacNhanDangXuat();
        }
    }
}
