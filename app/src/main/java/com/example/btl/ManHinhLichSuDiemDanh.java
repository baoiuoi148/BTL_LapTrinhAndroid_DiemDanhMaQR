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

public class ManHinhLichSuDiemDanh extends AppCompatActivity {
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
        findViewById(R.id.imageViewDongLichSu).setOnClickListener(view -> finish());
        TextView hocKy = findViewById(R.id.textViewHocKy);
        hocKy.setText(DuLieuDiemDanh.HOC_KY);
        findViewById(R.id.imageViewMuiTenHocKy).setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(this, view);
            menu.getMenu().add(DuLieuDiemDanh.HOC_KY).setCheckable(true).setChecked(true);
            menu.show();
        });

        RecyclerView danhSach = findViewById(R.id.recyclerViewLopHoc);
        danhSach.setAdapter(new DanhSachDiemDanhAdapter<>(
                R.layout.item_lop_hoc, DuLieuDiemDanh.DANH_SACH_LOP, (view, lop, viTri) -> {
            ((TextView) view.findViewById(R.id.textViewKhungGio)).setText(lop.tiet);
            ((TextView) view.findViewById(R.id.textViewMaLop)).setText(
                    "Mã lớp: " + lop.ma + " - " + lop.ten);
            ((TextView) view.findViewById(R.id.textViewGiangVien)).setText("GV: " + lop.giangVien);
            ((TextView) view.findViewById(R.id.textViewLichHoc)).setText(lop.lich);
            view.setOnClickListener(row -> {
                Intent intent = new Intent(this, ManHinhChiTietDiemDanh.class);
                intent.putExtra(DuLieuDiemDanh.EXTRA_MA_LOP, lop.ma);
                startActivity(intent);
            });
        }));
    }
}
