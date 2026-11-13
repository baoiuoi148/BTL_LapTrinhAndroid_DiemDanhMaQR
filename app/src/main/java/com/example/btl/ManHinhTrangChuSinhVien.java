package com.example.btl;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhTrangChuSinhVien extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu_sinhvien);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_student_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ThanhDieuHuong.gan(this);
        XacNhanDangXuat.attach(this);
        findViewById(R.id.linearLayoutMoLichSu).setOnClickListener(view ->
                startActivity(ThanhDieuHuong.chuyenMan(this, ManHinhLichSuDiemDanh.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String maSV = ThanhDieuHuong.maNguoiDung(this);
        if (maSV == null) {
            finish();
            return;
        }

        try (DatabaseHelper db = new DatabaseHelper(this);
             Cursor hoSo = db.layHoSoSinhVien(maSV)) {
            if (!hoSo.moveToFirst()) {
                finish();
                return;
            }
            String lopNienChe = hoSo.getString(1);
            ((TextView) findViewById(R.id.tvStudentName)).setText(hoSo.getString(0));
            ((TextView) findViewById(R.id.tvStudentId)).setText(
                    TextUtils.isEmpty(lopNienChe) ? maSV : maSV + " - " + lopNienChe);
            hienLichHomNay(db, maSV);
        }
    }

    private void hienLichHomNay(DatabaseHelper db, String maSV) {
        LinearLayout danhSach = findViewById(R.id.linearLayoutLichHomNay);
        danhSach.removeAllViews();
        String ngay = DuLieuDiemDanh.dinhDangGio(System.currentTimeMillis(), "dd/MM/yyyy");
        try (Cursor lich = db.layLichHocHomNay(maSV, ngay)) {
            while (lich.moveToNext()) {
                View theLop = getLayoutInflater().inflate(R.layout.item_lich_hoc_hom_nay, danhSach, false);
                ((TextView) theLop.findViewById(R.id.tvTenMonHomNay)).setText(lich.getString(0));
                ((TextView) theLop.findViewById(R.id.tvTietHocHomNay)).setText(lich.getString(1));
                java.util.List<String> thongTin = new java.util.ArrayList<>();
                for (int cot = 2; cot <= 4; cot++) {
                    String giaTri = lich.getString(cot);
                    if (!TextUtils.isEmpty(giaTri)) {
                        thongTin.add(cot == 4 ? "GV: " + giaTri : giaTri);
                    }
                }
                ((TextView) theLop.findViewById(R.id.tvThongTinLopHomNay))
                        .setText(TextUtils.join(" · ", thongTin));
                danhSach.addView(theLop);
            }
            int soLop = danhSach.getChildCount();
            ((TextView) findViewById(R.id.tvTieuDeLichHomNay))
                    .setText("Lịch hôm nay (" + soLop + " lớp)");
            findViewById(R.id.tvLichHomNayTrong).setVisibility(soLop == 0 ? View.VISIBLE : View.GONE);
        }
    }
}
