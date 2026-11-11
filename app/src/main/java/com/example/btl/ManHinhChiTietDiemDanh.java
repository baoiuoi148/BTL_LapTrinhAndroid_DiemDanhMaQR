package com.example.btl;

import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DuLieuDiemDanh.LopHoc lop = DuLieuDiemDanh.timLop(
                getIntent().getStringExtra(DuLieuDiemDanh.EXTRA_MA_LOP));
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
        findViewById(R.id.imageViewQuayLai).setOnClickListener(view -> finish());
        datChu(R.id.textViewTenMonHoc, lop.ten);
        datChu(R.id.textViewHocKy, DuLieuDiemDanh.HOC_KY);
        datChu(R.id.textViewMaLop, "Mã lớp: " + lop.ma);
        datChu(R.id.textViewGiangVien, "GV: " + lop.giangVien);

        List<DuLieuDiemDanh.BuoiHoc> buoiHoc = DuLieuDiemDanh.layBuoiHoc(this, lop);
        int coMat = 0;
        for (DuLieuDiemDanh.BuoiHoc buoi : buoiHoc) {
            if (buoi.coMat) {
                coMat++;
            }
        }
        int tongSoBuoi = buoiHoc.size();
        int phanTram = tongSoBuoi == 0 ? 0 : Math.round(100f * coMat / tongSoBuoi);
        datChu(R.id.textViewSoBuoiDaHoc, "Đã diễn ra " + tongSoBuoi + " buổi học");
        datChu(R.id.textViewSoBuoiCoMat, coMat + " buổi");
        datChu(R.id.textViewSoBuoiVang, (tongSoBuoi - coMat) + " buổi");
        datChu(R.id.textViewPhanTram, phanTram + "%");
        ((ProgressBar) findViewById(R.id.progressBarThamGia)).setProgress(phanTram);

        RecyclerView danhSach = findViewById(R.id.recyclerViewBuoiHoc);
        danhSach.setAdapter(new DanhSachDiemDanhAdapter<>(
                R.layout.item_buoi_hoc, buoiHoc, (view, buoi, viTri) -> {
            ((TextView) view.findViewById(R.id.textViewTenBuoi)).setText("Buổi " + (viTri + 1));
            ((TextView) view.findViewById(R.id.textViewNgay)).setText(buoi.ngay);
            ((TextView) view.findViewById(R.id.textViewGio)).setText(buoi.gio);
            TextView trangThai = view.findViewById(R.id.textViewTrangThai);
            trangThai.setText(buoi.coMat ? "Có mặt" : "Vắng mặt");
            trangThai.setBackgroundResource(buoi.coMat ? R.drawable.bg_tag_present : R.drawable.bg_tag_absent);
            trangThai.setTextColor(ContextCompat.getColor(this,
                    buoi.coMat ? R.color.tag_present_text : R.color.tag_absent_text));
        }));
    }

    private void datChu(int id, String noiDung) {
        ((TextView) findViewById(id)).setText(noiDung);
    }
}
