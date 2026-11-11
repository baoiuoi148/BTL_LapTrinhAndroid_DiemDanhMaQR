package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhTrangChuGiaoVien extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout mframeLayoutDiemDanhLop;
    private ImageView mimageViewUserHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu_giaovien);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_lecturer_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        addAction();
    }

    private void addAction() {
        mframeLayoutDiemDanhLop.setOnClickListener(this);
        if (mimageViewUserHeader != null) {
            mimageViewUserHeader.setOnClickListener(this);
        }
    }

    private void initView() {
        mframeLayoutDiemDanhLop = findViewById(R.id.frameLayoutDiemDanhLop);
        mimageViewUserHeader = findViewById(R.id.imageViewUserHeader);
    }

    private void xacNhanDangXuat() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(ManHinhTrangChuGiaoVien.this, ManHinhDangNhap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v == mframeLayoutDiemDanhLop) {
            Intent intent = new Intent(ManHinhTrangChuGiaoVien.this, ManHinhLichGiangDay.class);
            startActivity(intent);
        } else if (v == mimageViewUserHeader) {
            xacNhanDangXuat();
        }
    }
}
