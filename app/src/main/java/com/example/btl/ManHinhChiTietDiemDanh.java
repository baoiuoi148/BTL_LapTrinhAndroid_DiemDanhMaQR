package com.example.btl;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManHinhChiTietDiemDanh extends AppCompatActivity {
    private DuLieuDiemDanh.LopHoc lop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            lop = DuLieuDiemDanh.timLop(db, ThanhDieuHuong.maNguoiDung(this),
                    getIntent().getStringExtra(DuLieuDiemDanh.EXTRA_MA_LOP));
        }
        if (lop == null) {
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_chi_tiet_diem_danh);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        ThanhDieuHuong.gan(this);
        XacNhanDangXuat.attach(this);

        findViewById(R.id.imageViewQuayLai).setOnClickListener(view -> finish());
        datChu(R.id.textViewTenMonHoc, lop.ten);
        datChu(R.id.textViewHocKy, lop.hocKy);
        datChu(R.id.textViewMaLop, "Mã lớp: " + lop.ma);
        datChu(R.id.textViewGiangVien, "GV: " + lop.giangVien);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lop == null) {
            return;
        }

        List<DuLieuDiemDanh.BuoiHoc> buoiHoc;
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            buoiHoc = DuLieuDiemDanh.layBuoiHoc(db, ThanhDieuHuong.maNguoiDung(this), lop);
        }
        long hienTai = System.currentTimeMillis();
        int daChot = 0;
        int coMat = 0;
        for (DuLieuDiemDanh.BuoiHoc buoi : buoiHoc) {
            if (buoi.daChot(hienTai)) {
                daChot++;
                if (buoi.coMat) {
                    coMat++;
                }
            }
        }
        int phanTram = daChot == 0 ? 0 : Math.round(100f * coMat / daChot);
        datChu(R.id.textViewSoBuoiDaHoc, "Đã chốt điểm danh " + daChot + " buổi");
        datChu(R.id.textViewSoBuoiCoMat, coMat + " buổi");
        datChu(R.id.textViewSoBuoiVang, (daChot - coMat) + " buổi");
        datChu(R.id.textViewPhanTram, daChot == 0 ? "—" : phanTram + "%");
        boolean canCanhBao = daChot > 0 && 100L * coMat < 80L * daChot;
        findViewById(R.id.textViewCanhBaoDiemDanh)
                .setVisibility(canCanhBao ? View.VISIBLE : View.GONE);
        datChu(R.id.textViewDanhSachBuoiHoc,
                buoiHoc.isEmpty() ? "Chưa có buổi học" : "Danh sách buổi học");
        ((ProgressBar) findViewById(R.id.progressBarThamGia)).setProgress(phanTram);

        RecyclerView danhSach = findViewById(R.id.recyclerViewBuoiHoc);
        danhSach.setAdapter(new DanhSachDiemDanhAdapter<>(
                R.layout.item_buoi_hoc, buoiHoc, (view, buoi, viTri) -> {
            int soThuTu = buoi.soThuTu > 0 ? buoi.soThuTu : viTri + 1;
            ((TextView) view.findViewById(R.id.textViewTenBuoi)).setText("Buổi " + soThuTu);
            ((TextView) view.findViewById(R.id.textViewNgay)).setText(buoi.ngay);
            ((TextView) view.findViewById(R.id.textViewGio)).setText(buoi.gio);
            TextView nhanTrangThai = view.findViewById(R.id.textViewTrangThai);
            DuLieuDiemDanh.TrangThai trangThai = buoi.trangThai(hienTai);
            nhanTrangThai.setText(trangThai.nhan);

            int nen = R.drawable.bg_the_vien_xam;
            int mauChu = R.color.text_secondary;
            switch (trangThai) {
                case CO_MAT:
                    nen = R.drawable.bg_tag_present;
                    mauChu = R.color.tag_present_text;
                    break;
                case VANG:
                    nen = R.drawable.bg_tag_absent;
                    mauChu = R.color.tag_absent_text;
                    break;
                default:
                    break;
            }
            nhanTrangThai.setBackgroundResource(nen);
            nhanTrangThai.setTextColor(ContextCompat.getColor(this, mauChu));
        }));
    }

    private void datChu(int id, String noiDung) {
        ((TextView) findViewById(id)).setText(noiDung);
    }
}
