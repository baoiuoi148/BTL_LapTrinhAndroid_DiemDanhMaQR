package com.example.btl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DiemDanhQR.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Tài khoản
        db.execSQL("CREATE TABLE TaiKhoan (" +
                "ma_nguoi_dung TEXT PRIMARY KEY, " +
                "mat_khau TEXT NOT NULL, " +
                "ho_ten TEXT NOT NULL, " +
                "email TEXT, " +
                "vai_tro TEXT NOT NULL, " +
                "lop_nien_che TEXT)");

        // 2. Tạo bảng Lớp học phần
        db.execSQL("CREATE TABLE LopHoc (" +
                "ma_lop TEXT PRIMARY KEY, " +
                "ten_mon TEXT NOT NULL, " +
                "hoc_ky TEXT NOT NULL, " +
                "thu_hoc TEXT, " +
                "tiet_hoc TEXT, " +
                "phong_hoc TEXT, " +
                "gio_bat_dau TEXT, " +
                "ma_gv TEXT, " +
                "FOREIGN KEY (ma_gv) REFERENCES TaiKhoan(ma_nguoi_dung))");

        // 3. Tạo bảng Sinh viên - Lớp học
        db.execSQL("CREATE TABLE SinhVien_LopHoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ma_sv TEXT NOT NULL, " +
                "ma_lop TEXT NOT NULL, " +
                "FOREIGN KEY (ma_sv) REFERENCES TaiKhoan(ma_nguoi_dung), " +
                "FOREIGN KEY (ma_lop) REFERENCES LopHoc(ma_lop))");

        // 4. Tạo bảng Buổi học
        db.execSQL("CREATE TABLE BuoiHoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ma_lop TEXT NOT NULL, " +
                "ngay_hoc TEXT NOT NULL, " +
                "so_thu_tu INTEGER, " +
                "FOREIGN KEY (ma_lop) REFERENCES LopHoc(ma_lop))");

        // 5. Tạo bảng Điểm danh
        db.execSQL("CREATE TABLE DiemDanh (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "buoi_hoc_id INTEGER NOT NULL, " +
                "ma_sv TEXT NOT NULL, " +
                "trang_thai INTEGER DEFAULT 0, " +
                "thoi_gian_quet INTEGER, " +
                "FOREIGN KEY (buoi_hoc_id) REFERENCES BuoiHoc(id), " +
                "FOREIGN KEY (ma_sv) REFERENCES TaiKhoan(ma_nguoi_dung), " +
                "UNIQUE(buoi_hoc_id, ma_sv))");

        // Thêm dữ liệu mẫu ban đầu để chạy thử app
        khoiTaoDuLieuMau(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DiemDanh");
        db.execSQL("DROP TABLE IF EXISTS BuoiHoc");
        db.execSQL("DROP TABLE IF EXISTS SinhVien_LopHoc");
        db.execSQL("DROP TABLE IF EXISTS LopHoc");
        db.execSQL("DROP TABLE IF EXISTS TaiKhoan");
        onCreate(db);
    }

    // ================= DỮ LIỆU MẪU BAN ĐẦU =================
    private void khoiTaoDuLieuMau(SQLiteDatabase db) {
        // Tài khoản sinh viên & giảng viên
        db.execSQL("INSERT INTO TaiKhoan VALUES ('24A1001D0308', '123456', 'Trần Hồng Ngọc', 'sv308@fithou.edu.vn', 'SINH_VIEN', '2410A01')");
        db.execSQL("INSERT INTO TaiKhoan VALUES ('GV202401', '123456', 'Mai Thị Thúy Hà', 'hamtt@fithou.edu.vn', 'GIANG_VIEN', NULL)");
        db.execSQL("INSERT INTO TaiKhoan VALUES ('GV202402', '123456', 'Nguyễn Thùy Linh', 'linhnt@fithou.edu.vn', 'GIANG_VIEN', NULL)");

        // Lớp học phần
        db.execSQL("INSERT INTO LopHoc VALUES ('K24-7E1019.22-I.2627-1.1_LT', 'Lập trình cho thiết bị di động', 'HK1 - 2026-2027', 'Thứ 2', '1-4', 'Phòng 24', '07:30', 'GV202401')");
        db.execSQL("INSERT INTO LopHoc VALUES ('K24-7E1005.22-I.2627-4.4_LT', 'Toán rời rạc', 'HK1 - 2026-2027', 'Thứ 2', '5-8', 'Phòng 44', '13:00', 'GV202402')");

        // Đăng ký lớp cho sinh viên
        db.execSQL("INSERT INTO SinhVien_LopHoc (ma_sv, ma_lop) VALUES ('24A1001D0308', 'K24-7E1019.22-I.2627-1.1_LT')");
        db.execSQL("INSERT INTO SinhVien_LopHoc (ma_sv, ma_lop) VALUES ('24A1001D0308', 'K24-7E1005.22-I.2627-4.4_LT')");

        // Các buổi học của môn Di động
        String maLopDiDong = "K24-7E1019.22-I.2627-1.1_LT";
        String[] ngayMau = {"10/09/2026", "17/09/2026", "24/09/2026", "01/10/2026", "08/10/2026", "15/10/2026"};
        int[] trangThaiMau = {1, 0, 1, 1, 0, 0}; // 1: có mặt, 0: vắng

        for (int i = 0; i < ngayMau.length; i++) {
            ContentValues cvBuoi = new ContentValues();
            cvBuoi.put("ma_lop", maLopDiDong);
            cvBuoi.put("ngay_hoc", ngayMau[i]);
            cvBuoi.put("so_thu_tu", i + 1);
            long buoiId = db.insert("BuoiHoc", null, cvBuoi);

            // Ghi nhận điểm danh mẫu cho SV 24A1001D0308
            ContentValues cvDiemDanh = new ContentValues();
            cvDiemDanh.put("buoi_hoc_id", buoiId);
            cvDiemDanh.put("ma_sv", "24A1001D0308");
            cvDiemDanh.put("trang_thai", trangThaiMau[i]);
            cvDiemDanh.put("thoi_gian_quet", trangThaiMau[i] == 1 ? System.currentTimeMillis() : null);
            db.insert("DiemDanh", null, cvDiemDanh);
        }
    }

    // ================= CÁC HÀM TRUY VẤN CHÍNH =================

    // 1. Kiểm tra đăng nhập
    public Cursor kiemTraDangNhap(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM TaiKhoan WHERE ma_nguoi_dung = ? AND mat_khau = ?",
                new String[]{username, password});
    }

    // 2. Lấy thời khóa biểu / Lịch học của Sinh viên
    public Cursor layLichHocSinhVien(String maSV) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT l.*, t.ho_ten AS ten_giang_vien " +
                "FROM LopHoc l " +
                "JOIN SinhVien_LopHoc s ON l.ma_lop = s.ma_lop " +
                "JOIN TaiKhoan t ON l.ma_gv = t.ma_nguoi_dung " +
                "WHERE s.ma_sv = ?";
        return db.rawQuery(sql, new String[]{maSV});
    }

    // 3. Lấy lịch dạy của Giảng viên
    public Cursor layLichDayGiangVien(String maGV) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT l.*, COUNT(s.ma_sv) AS si_so " +
                "FROM LopHoc l " +
                "LEFT JOIN SinhVien_LopHoc s ON l.ma_lop = s.ma_lop " +
                "WHERE l.ma_gv = ? " +
                "GROUP BY l.ma_lop";
        return db.rawQuery(sql, new String[]{maGV});
    }

    // 4. Kiểm tra sinh viên có thuộc lớp hay không
    public boolean kiemTraSinhVienThuocLop(String maSV, String maLop) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM SinhVien_LopHoc WHERE ma_sv = ? AND ma_lop = ?",
                new String[]{maSV, maLop});
        boolean thuocLop = c.getCount() > 0;
        c.close();
        return thuocLop;
    }

    // 5. Ghi nhận điểm danh khi quét mã QR thành công
    public boolean ghiNhanDiemDanh(String maSV, String maLop, String ngayHoc, long thoiGianQuet) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Tìm hoặc tạo BuoiHoc cho ngày hôm đó nếu chưa có
        long buoiHocId = -1;
        Cursor cBuoi = db.rawQuery("SELECT id FROM BuoiHoc WHERE ma_lop = ? AND ngay_hoc = ?",
                new String[]{maLop, ngayHoc});
        if (cBuoi.moveToFirst()) {
            buoiHocId = cBuoi.getLong(0);
        } else {
            ContentValues cvBuoi = new ContentValues();
            cvBuoi.put("ma_lop", maLop);
            cvBuoi.put("ngay_hoc", ngayHoc);
            buoiHocId = db.insert("BuoiHoc", null, cvBuoi);
        }
        cBuoi.close();

        // Ghi hoặc cập nhật điểm danh
        ContentValues cv = new ContentValues();
        cv.put("buoi_hoc_id", buoiHocId);
        cv.put("ma_sv", maSV);
        cv.put("trang_thai", 1); // 1 = Có mặt
        cv.put("thoi_gian_quet", thoiGianQuet);

        long result = db.insertWithOnConflict("DiemDanh", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    // 6. Lấy lịch sử điểm danh của 1 lớp cho Sinh viên (Dùng cho ManHinhChiTietDiemDanh)
    public Cursor layChiTietDiemDanhCuaSV(String maLop, String maSV) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT b.id, b.ngay_hoc, b.so_thu_tu, " +
                "COALESCE(d.trang_thai, 0) AS co_mat, d.thoi_gian_quet " +
                "FROM BuoiHoc b " +
                "LEFT JOIN DiemDanh d ON b.id = d.buoi_hoc_id AND d.ma_sv = ? " +
                "WHERE b.ma_lop = ? " +
                "ORDER BY b.id ASC";
        return db.rawQuery(sql, new String[]{maSV, maLop});
    }
}