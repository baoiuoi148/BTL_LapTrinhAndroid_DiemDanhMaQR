package com.example.btl;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhTrangChuGiaoVien extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout mframeLayoutDiemDanhLop;
    private TextView mtvLecturerName, mtvLecturerId;
    private String maGV;

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
        napThongTinGiangVien();
        addAction();
        XacNhanDangXuat.attach(this);
    }

    private void napThongTinGiangVien() {
        maGV = ThanhDieuHuong.maNguoiDung(this);
        if (maGV == null || maGV.trim().isEmpty() || maGV.startsWith("24A")) {
            maGV = "GV202401";
        }
        try (DatabaseHelper db = new DatabaseHelper(this);
             Cursor c = db.layHoSoGiangVien(maGV)) {
            if (c.moveToFirst()) {
                String hoTen = c.getString(0);
                if (mtvLecturerName != null) {
                    mtvLecturerName.setText(hoTen);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mtvLecturerId != null) {
            mtvLecturerId.setText(maGV);
        }
    }

    private void addAction() {
        if (mframeLayoutDiemDanhLop != null) {
            mframeLayoutDiemDanhLop.setOnClickListener(this);
        }
    }

    private void initView() {
        mframeLayoutDiemDanhLop = findViewById(R.id.frameLayoutDiemDanhLop);
        mtvLecturerName = findViewById(R.id.tvLecturerName);
        mtvLecturerId = findViewById(R.id.tvLecturerId);
    }

    @Override
    public void onClick(View v) {
        if (v == mframeLayoutDiemDanhLop) {
            Intent intent = new Intent(ManHinhTrangChuGiaoVien.this, ManHinhLichGiangDay.class);
            intent.putExtra(ThanhDieuHuong.EXTRA_MA_NGUOI_DUNG, maGV);
            startActivity(intent);
        }
    }
}