package com.example.btl;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

final class DuLieuDiemDanh {
    static final String HOC_KY = "HK1 - 2026-2027";
    static final String EXTRA_MA_LOP = "maLop";
    static final List<LopHoc> DANH_SACH_LOP = Arrays.asList(
            new LopHoc("K24-7E1019.22-I.2627-1.1_LT", "Lập trình cho thiết bị di động",
                    "Mai Thị Thúy Hà", "1-4", "Thứ 2 · Tiết 1-4 · Phòng 24", "07:30"),
            new LopHoc("K24-7E1005.22-I.2627-4.4_LT", "Toán rời rạc",
                    "Nguyễn Thùy Linh", "5-8", "Thứ 2 · Tiết 5-8 · Phòng 44", "13:00"),
            new LopHoc("K24-7E1021.22-I.2627-2.2_LT", "Quản trị mạng",
                    "Vũ Xuân Hạnh", "1-4", "Thứ 3 · Tiết 1-4 · Phòng 33-34", "07:30"),
            new LopHoc("K24-7E1018.22-I.2627-5.5_LT", "Lập trình Web",
                    "Thái Thanh Tùng", "5-8", "Thứ 6 · Tiết 5-8 · Phòng 41", "13:00"));

    private DuLieuDiemDanh() {}

    static LopHoc timLop(String maLop) {
        for (LopHoc lop : DANH_SACH_LOP) {
            if (lop.ma.equals(maLop)) {
                return lop;
            }
        }
        return null;
    }

    static long ghiNhan(Context context, LopHoc lop, String ngay, long thoiGian) {
        SharedPreferences boNho = boNho(context, lop);
        long gioDaGhiNhan = boNho.getLong(ngay, 0);
        if (gioDaGhiNhan != 0) {
            return gioDaGhiNhan;
        }
        boNho.edit().putLong(ngay, thoiGian).apply();
        return thoiGian;
    }

    static List<BuoiHoc> layBuoiHoc(Context context, LopHoc lop) {
        List<BuoiHoc> buoiHoc = new ArrayList<>();
        SharedPreferences boNho = boNho(context, lop);
        String[] ngayMau = {"10/09/2026", "17/09/2026", "24/09/2026",
                "01/10/2026", "08/10/2026", "15/10/2026"};
        boolean[] coMatMau = {true, false, true, true, false, false};
        for (int i = 0; i < ngayMau.length; i++) {
            String ngay = ngayMau[i];
            buoiHoc.add(new BuoiHoc(ngay, lop.gioHoc, coMatMau[i] || boNho.contains(ngay)));
        }
        for (String ngay : boNho.getAll().keySet()) {
            if (!Arrays.asList(ngayMau).contains(ngay)) {
                buoiHoc.add(new BuoiHoc(ngay, dinhDangGio(boNho.getLong(ngay, 0), "HH:mm"), true));
            }
        }
        buoiHoc.sort((a, b) -> khoaNgay(a.ngay).compareTo(khoaNgay(b.ngay)));
        return buoiHoc;
    }

    private static String khoaNgay(String ngay) {
        return ngay.substring(6) + ngay.substring(3, 5) + ngay.substring(0, 2);
    }

    static String dinhDangGio(long thoiGian, String mau) {
        return new SimpleDateFormat(mau, Locale.getDefault()).format(new Date(thoiGian));
    }

    private static SharedPreferences boNho(Context context, LopHoc lop) {
        return context.getSharedPreferences("diem_danh_" + lop.ma, Context.MODE_PRIVATE);
    }

    static final class LopHoc {
        final String ma;
        final String ten;
        final String giangVien;
        final String tiet;
        final String lich;
        final String gioHoc;

        LopHoc(String ma, String ten, String giangVien, String tiet, String lich, String gioHoc) {
            this.ma = ma;
            this.ten = ten;
            this.giangVien = giangVien;
            this.tiet = tiet;
            this.lich = lich;
            this.gioHoc = gioHoc;
        }
    }

    static final class BuoiHoc {
        final String ngay;
        final String gio;
        final boolean coMat;

        BuoiHoc(String ngay, String gio, boolean coMat) {
            this.ngay = ngay;
            this.gio = gio;
            this.coMat = coMat;
        }
    }
}
