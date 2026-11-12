package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhLichGiangDay extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mlinearLayoutTrangChu, mlinearLayoutLopGiangDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_lich_giang_day);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        addAction();
        XacNhanDangXuat.attach(this);
    }

    private void addAction() {
        mlinearLayoutTrangChu.setOnClickListener(this);
        mlinearLayoutLopGiangDay.setOnClickListener(this);
    }

    private void initView() {
        mlinearLayoutTrangChu = findViewById(R.id.linearLayoutTrangChu);
        mlinearLayoutLopGiangDay = findViewById(R.id.linearLayoutLopGiangDay);
    }

    @Override
    public void onClick(View v) {
        if (v == mlinearLayoutTrangChu) {
            Intent intent = new Intent(ManHinhLichGiangDay.this, ManHinhTrangChuGiaoVien.class);
            finish();
        }
        if (v == mlinearLayoutLopGiangDay) {
            Intent intent = new Intent(ManHinhLichGiangDay.this, ManHinhDanhSachLop.class);
            startActivity(intent);
        }
    }
}