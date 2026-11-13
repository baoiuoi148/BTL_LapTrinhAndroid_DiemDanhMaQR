package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ManHinhLichSuDiemDanh extends AppCompatActivity {
    private final Map<String, List<DuLieuDiemDanh.LopHoc>> lopTheoHocKy = new TreeMap<>();
    private String hocKyDaChon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_lich_su_diem_danh);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        ThanhDieuHuong.gan(this);
        XacNhanDangXuat.attach(this);
        findViewById(R.id.imageViewDongLichSu).setOnClickListener(view -> finish());

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            for (DuLieuDiemDanh.LopHoc lop : DuLieuDiemDanh.layLop(db, ThanhDieuHuong.maNguoiDung(this))) {
                lopTheoHocKy.computeIfAbsent(lop.hocKy, key -> new ArrayList<>()).add(lop);
            }
        }

        if (lopTheoHocKy.isEmpty()) {
            ((TextView) findViewById(R.id.textViewHocKy)).setText("Chưa có lớp đã đăng ký");
            findViewById(R.id.imageViewMuiTenHocKy).setVisibility(android.view.View.GONE);
            findViewById(R.id.textViewDanhSachLop).setVisibility(android.view.View.GONE);
            return;
        }

        String hocKyDaLuu = savedInstanceState == null ? "" : savedInstanceState.getString("hocKy", "");
        hienThiHocKy(lopTheoHocKy.containsKey(hocKyDaLuu) ? hocKyDaLuu : lopTheoHocKy.keySet().iterator().next());
        findViewById(R.id.imageViewMuiTenHocKy).setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(this, view);
            for (String hocKy : lopTheoHocKy.keySet()) {
                menu.getMenu().add(hocKy).setCheckable(true).setChecked(hocKy.equals(hocKyDaChon));
            }
            menu.setOnMenuItemClickListener(item -> {
                hienThiHocKy(item.getTitle().toString());
                return true;
            });
            menu.show();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("hocKy", hocKyDaChon);
    }

    private void hienThiHocKy(String hocKy) {
        hocKyDaChon = hocKy;
        ((TextView) findViewById(R.id.textViewHocKy)).setText(hocKy);
        RecyclerView danhSach = findViewById(R.id.recyclerViewLopHoc);
        danhSach.setAdapter(new DanhSachDiemDanhAdapter<>(
                R.layout.item_lop_hoc, lopTheoHocKy.get(hocKy), (view, lop, viTri) -> {
            ((TextView) view.findViewById(R.id.textViewKhungGio)).setText(lop.tiet);
            ((TextView) view.findViewById(R.id.textViewMaLop)).setText(
                    "Mã lớp: " + lop.ma + " - " + lop.ten);
            ((TextView) view.findViewById(R.id.textViewGiangVien)).setText("GV: " + lop.giangVien);
            ((TextView) view.findViewById(R.id.textViewLichHoc)).setText(lop.lich);
            view.setOnClickListener(row -> {
                Intent intent = ThanhDieuHuong.chuyenMan(this, ManHinhChiTietDiemDanh.class);
                intent.putExtra(DuLieuDiemDanh.EXTRA_MA_LOP, lop.ma);
                startActivity(intent);
            });
        }));
    }
}
