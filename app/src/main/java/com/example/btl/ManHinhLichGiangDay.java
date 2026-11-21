package com.example.btl;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhLichGiangDay extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mlinearLayoutTrangChu, containerDanhSachLopDay;
    private TextView mtextViewSoLuongLop;
    private String maGV;

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

        maGV = ThanhDieuHuong.maNguoiDung(this);
        if (maGV == null || maGV.trim().isEmpty() || maGV.startsWith("24A")) {
            maGV = "GV202401";
        }

        initView();
        addAction();
        napLichGiangDayTuSQLite();
        XacNhanDangXuat.attach(this);
    }

    private void addAction() {
        if (mlinearLayoutTrangChu != null) {
            mlinearLayoutTrangChu.setOnClickListener(this);
        }
    }

    private void initView() {
        mlinearLayoutTrangChu = findViewById(R.id.linearLayoutTrangChu);
        containerDanhSachLopDay = findViewById(R.id.containerDanhSachLopDay);
        mtextViewSoLuongLop = findViewById(R.id.textView4);
    }

    private void napLichGiangDayTuSQLite() {
        if (containerDanhSachLopDay == null) return;
        containerDanhSachLopDay.removeAllViews();

        try (DatabaseHelper db = new DatabaseHelper(this);
             Cursor c = db.layLichDayGiangVien(maGV)) {
            int soLuong = c.getCount();
            if (mtextViewSoLuongLop != null) {
                mtextViewSoLuongLop.setText("Số lượng lớp được phân công: " + soLuong);
            }

            while (c.moveToNext()) {
                String maLop = c.getString(0);
                String tenMon = c.getString(1);
                String thuHoc = c.getString(3);
                String tietHoc = c.getString(4);
                String phongHoc = c.getString(5);
                int siSo = c.getInt(8);

                // Khối Card Lớp Học
                LinearLayout card = new LinearLayout(this);
                LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cardLp.setMargins(0, dp(16), 0, 0);
                card.setLayoutParams(cardLp);
                card.setBackground(ContextCompat.getDrawable(this, R.drawable.chunhatbotron));
                card.setOrientation(LinearLayout.HORIZONTAL);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(dp(12), dp(12), dp(12), dp(12));

                // Box tiết học
                TextView tvTiet = new TextView(this);
                LinearLayout.LayoutParams tietLp = new LinearLayout.LayoutParams(dp(54), dp(54));
                tietLp.setMarginEnd(dp(12));
                tvTiet.setLayoutParams(tietLp);
                tvTiet.setBackground(ContextCompat.getDrawable(this, R.drawable.sotietbotron));
                tvTiet.setGravity(Gravity.CENTER);
                tvTiet.setText(tietHoc != null ? tietHoc : "1-4");
                tvTiet.setTextColor(Color.parseColor("#32B588"));
                tvTiet.setTextSize(15);
                tvTiet.setTypeface(Typeface.DEFAULT_BOLD);
                card.addView(tvTiet);

                // Khối thông tin lớp
                LinearLayout infoLayout = new LinearLayout(this);
                LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                infoLayout.setLayoutParams(infoLp);
                infoLayout.setOrientation(LinearLayout.VERTICAL);

                TextView tvTenLop = new TextView(this);
                tvTenLop.setText("Mã lớp: " + maLop + " - " + tenMon);
                tvTenLop.setTextColor(Color.BLACK);
                tvTenLop.setTextSize(14);
                tvTenLop.setTypeface(Typeface.DEFAULT_BOLD);
                infoLayout.addView(tvTenLop);

                TextView tvSiSo = new TextView(this);
                LinearLayout.LayoutParams siSoLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                siSoLp.setMargins(0, dp(3), 0, 0);
                tvSiSo.setLayoutParams(siSoLp);
                tvSiSo.setText("Sĩ số lớp: " + (siSo > 0 ? siSo : 45));
                tvSiSo.setTextColor(Color.parseColor("#555555"));
                tvSiSo.setTextSize(13);
                infoLayout.addView(tvSiSo);

                TextView tvLichHoc = new TextView(this);
                LinearLayout.LayoutParams lichLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lichLp.setMargins(0, dp(3), 0, 0);
                tvLichHoc.setLayoutParams(lichLp);
                tvLichHoc.setText((thuHoc != null ? thuHoc : "Thứ 2") + " Tiết " + (tietHoc != null ? tietHoc : "1-4") + " " + (phongHoc != null ? phongHoc : "Phòng 24"));
                tvLichHoc.setTextColor(Color.parseColor("#555555"));
                tvLichHoc.setTextSize(13);
                infoLayout.addView(tvLichHoc);

                card.addView(infoLayout);

                card.setClickable(true);
                card.setFocusable(true);
                card.setOnClickListener(v -> {
                    Intent intent = new Intent(ManHinhLichGiangDay.this, ManHinhDanhSachLop.class);
                    intent.putExtra("EXTRA_MA_LOP", maLop);
                    intent.putExtra(ThanhDieuHuong.EXTRA_MA_NGUOI_DUNG, maGV);
                    startActivity(intent);
                });

                containerDanhSachLopDay.addView(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int dp(int value) {
        return (int) (getResources().getDisplayMetrics().density * value);
    }

    @Override
    public void onClick(View v) {
        if (v == mlinearLayoutTrangChu) {
            Intent intent = new Intent(ManHinhLichGiangDay.this, ManHinhTrangChuGiaoVien.class);
            intent.putExtra(ThanhDieuHuong.EXTRA_MA_NGUOI_DUNG, maGV);
            startActivity(intent);
            finish();
        }
    }
}
