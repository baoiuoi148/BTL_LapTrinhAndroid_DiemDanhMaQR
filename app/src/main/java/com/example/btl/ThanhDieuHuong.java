package com.example.btl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

final class ThanhDieuHuong {
    static final String EXTRA_MA_NGUOI_DUNG = "ma_nguoi_dung";

    private ThanhDieuHuong() {}

    static String maNguoiDung(Activity manHinh) {
        return manHinh.getIntent().getStringExtra(EXTRA_MA_NGUOI_DUNG);
    }

    static Intent chuyenMan(Activity manHinh, Class<? extends Activity> dich) {
        return new Intent(manHinh, dich).putExtra(EXTRA_MA_NGUOI_DUNG, maNguoiDung(manHinh));
    }

    static void gan(Activity manHinh) {
        gan(manHinh, () -> {
            Intent intent = chuyenMan(manHinh, ManHinhDiemDanh.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            manHinh.startActivity(intent);
        });
    }

    static void gan(Activity manHinh, Runnable quetQr) {
        View trangChu = manHinh.findViewById(R.id.linearLayoutNavTrangChu);
        if (trangChu != null) {
            trangChu.setOnClickListener(view -> veTrangChu(manHinh));
        }
        View nutQr = manHinh.findViewById(R.id.linearLayoutNavQr);
        if (nutQr != null) {
            nutQr.setOnClickListener(view -> quetQr.run());
        }
        View iconQrTrangChu = manHinh.findViewById(R.id.imageViewQuetQR);
        if (iconQrTrangChu != null) {
            iconQrTrangChu.setOnClickListener(view -> quetQr.run());
        }
    }

    static void veTrangChu(Activity manHinh) {
        if (manHinh instanceof ManHinhTrangChuSinhVien) {
            return;
        }
        Intent intent = chuyenMan(manHinh, ManHinhTrangChuSinhVien.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        manHinh.startActivity(intent);
    }
}
