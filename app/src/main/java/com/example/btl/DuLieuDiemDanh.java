package com.example.btl;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

final class DuLieuDiemDanh {
    static final String EXTRA_MA_LOP = "maLop";
    // SQLite hiện chỉ có giờ bắt đầu theo lớp.
    private static final long THOI_GIAN_DIEM_DANH_MS = 15 * 60_000L;

    private DuLieuDiemDanh() {}

    static List<LopHoc> layLop(DatabaseHelper db, String maSV) {
        List<LopHoc> lopHoc = new ArrayList<>();
        if (maSV == null) {
            return lopHoc;
        }
        try (Cursor c = db.layLichHocSinhVien(maSV)) {
            while (c.moveToNext()) {
                lopHoc.add(new LopHoc(c));
            }
        }
        return lopHoc;
    }

    static LopHoc timLop(DatabaseHelper db, String maSV, String maLop) {
        for (LopHoc lop : layLop(db, maSV)) {
            if (lop.ma.equals(maLop)) {
                return lop;
            }
        }
        return null;
    }

    static List<BuoiHoc> layBuoiHoc(DatabaseHelper db, String maSV, LopHoc lop) {
        List<BuoiHoc> buoiHoc = new ArrayList<>();
        try (Cursor c = db.layChiTietDiemDanhCuaSV(lop.ma, maSV)) {
            while (c.moveToNext()) {
                buoiHoc.add(new BuoiHoc(c, lop.gioHoc));
            }
        }
        buoiHoc.sort(Comparator.comparingInt(buoi -> buoi.soThuTu));

        // Tự động xác định buổi đang học (mặc định là buổi 6, hoặc tự trượt sang buổi tiếp theo nếu đã điểm danh)
        int buoiDangHoc = 6;
        for (BuoiHoc b : buoiHoc) {
            if (b.soThuTu >= buoiDangHoc && b.coMat) {
                buoiDangHoc = b.soThuTu + 1;
            }
        }
        for (BuoiHoc b : buoiHoc) {
            b.setBuoiDangHoc(buoiDangHoc);
        }

        return buoiHoc;
    }

    static String dinhDangGio(long thoiGian, String mau) {
        return new SimpleDateFormat(mau, Locale.getDefault()).format(new Date(thoiGian));
    }

    private static String docChu(Cursor c, String cot) {
        String giaTri = c.getString(c.getColumnIndexOrThrow(cot));
        return giaTri == null ? "" : giaTri;
    }

    static final class LopHoc {
        final String ma;
        final String ten;
        final String giangVien;
        final String hocKy;
        final String tiet;
        final String lich;
        final String gioHoc;

        LopHoc(Cursor c) {
            ma = docChu(c, "ma_lop");
            ten = docChu(c, "ten_mon");
            giangVien = docChu(c, "ten_giang_vien");
            hocKy = docChu(c, "hoc_ky");
            tiet = docChu(c, "tiet_hoc");
            lich = android.text.TextUtils.join(" · ", java.util.Arrays.asList(
                    docChu(c, "thu_hoc"), "Tiết " + tiet, docChu(c, "phong_hoc")));
            gioHoc = docChu(c, "gio_bat_dau");
        }
    }

    enum TrangThai {
        CO_MAT("Có mặt"),
        VANG("Vắng mặt"),
        CHUA_DEN("Chưa đến buổi học"),
        CHUA_DIEM_DANH("Chưa điểm danh");

        final String nhan;

        TrangThai(String nhan) {
            this.nhan = nhan;
        }
    }

    static final class BuoiHoc {
        final int soThuTu;
        final String ngay;
        final String gio;
        final boolean coMat;
        final long batDau;
        private int buoiDangHoc = 6;

        BuoiHoc(Cursor c, String gio) {
            soThuTu = c.getInt(c.getColumnIndexOrThrow("so_thu_tu"));
            ngay = docChu(c, "ngay_hoc");
            this.gio = gio;
            coMat = c.getInt(c.getColumnIndexOrThrow("co_mat")) == 1;
            batDau = docGioBatDau(ngay, gio);
        }

        void setBuoiDangHoc(int buoi) {
            this.buoiDangHoc = buoi;
        }

        boolean daChot(long hienTai) {
            if (coMat) {
                return true;
            }
            return soThuTu < buoiDangHoc;
        }

        TrangThai trangThai(long hienTai) {
            if (coMat) {
                return TrangThai.CO_MAT;
            }
            if (soThuTu > buoiDangHoc) {
                return TrangThai.CHUA_DEN;
            }
            if (soThuTu == buoiDangHoc) {
                return TrangThai.CHUA_DIEM_DANH;
            }
            return TrangThai.VANG;
        }

        private static long docGioBatDau(String ngay, String gio) {
            SimpleDateFormat dinhDang = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ROOT);
            dinhDang.setLenient(false);
            try {
                return dinhDang.parse(ngay + " " + gio).getTime();
            } catch (ParseException e) {
                return Long.MAX_VALUE;
            }
        }
    }
}
