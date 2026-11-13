package com.example.btl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DiemDanhQR.db";
    private static final int DATABASE_VERSION = 12;
    private static final String MA_SV_QUAN_TRI_MANG_MAU = "24A1001D0102";
    private static final String MA_LOP_QUAN_TRI_MANG_MAU = "K24-7E1021.22-I.2627-2.2_LT";
    private static final String MA_LOP_DI_DONG_MAU = "K24-7E1019.22-I.2627-1.1_LT";
    private static final String[] NGAY_DI_DONG_CU = {
            "10/09/2026", "17/09/2026", "24/09/2026", "01/10/2026",
            "08/10/2026", "15/10/2026", "13/12/2026"
    };

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
        if (oldVersion < 3) {
            themGiangVienMau(db);
        }
        if (oldVersion < 4) {
            themLopHocMau(db);
        }
        if (oldVersion < 5) {
            themBuoiHocMau(db);
        }
        if (oldVersion < 6) {
            String maSinhVien = MA_SV_QUAN_TRI_MANG_MAU;
            String maLop = MA_LOP_QUAN_TRI_MANG_MAU;
            themSinhVienMau(db, maSinhVien, "123456", "Hoàng Trung Đức",
                    "sv0102@fithou.edu.vn", "2410A01", maLop);
            db.delete("SinhVien_LopHoc", "ma_sv = ? AND ma_lop <> ?",
                    new String[]{maSinhVien, maLop});
        }
        if (oldVersion < 7) {
            themDiemDanhQuanTriMangMau(db);
        }
        if (oldVersion < 8) {
            themSinhVienMau(db, "24A1001D0048", "123", "Đinh Trọng Quốc Bảo",
                    "24a1001d0048@students.hou.edu.vn", "2410A01",
                    MA_LOP_DI_DONG_MAU, "K24-7E1005.22-I.2627-4.4_LT");
            themDiemDanhBaoMau(db);
        }
        if (oldVersion < 9) {
            themDiemDanhBaoMau(db);
        }
        if (oldVersion < 10) {
            themSinhVienMau(db, MA_SV_QUAN_TRI_MANG_MAU, "123456", "Hoàng Trung Đức",
                    "sv0102@fithou.edu.vn", "2410A01", MA_LOP_QUAN_TRI_MANG_MAU, MA_LOP_DI_DONG_MAU);
            themDiemDanhDucDiDongMau(db);
        }
        if (oldVersion < 11) {
            // Dọn dẹp tài khoản gõ nhầm 5 số 00048, chỉ giữ đúng 3 sinh viên cho lớp Di động
            db.execSQL("PRAGMA foreign_keys = OFF;");
            db.delete("DiemDanh", "ma_sv = ?", new String[]{"24A1001D00048"});
            db.delete("SinhVien_LopHoc", "ma_sv = ?", new String[]{"24A1001D00048"});
            db.delete("TaiKhoan", "ma_nguoi_dung = ?", new String[]{"24A1001D00048"});
            db.delete("SinhVien_LopHoc", "ma_lop = ? AND ma_sv NOT IN (?, ?, ?)",
                    new String[]{MA_LOP_DI_DONG_MAU, "24A1001D0048", "24A1001D0308", MA_SV_QUAN_TRI_MANG_MAU});
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
        if (oldVersion < 12) {
            themLopMau(db, "K24-7E1019.22-I.2627-3.1_LT", "Lập trình cho thiết bị di động",
                    "Thứ 6", "1-4", "Phòng 24", "07:30", "GV202401");
            themLopMau(db, "K24-7E1017.22-I.2627-1.1_LT", "Công nghệ phần mềm",
                    "Thứ 3", "5-8", "Phòng 32", "13:00", "GV202401");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Tự động làm mới cơ sở dữ liệu khi hạ phiên bản, tránh lỗi crash văng app
        onUpgrade(db, oldVersion, newVersion);
    }

    // ================= DỮ LIỆU MẪU BAN ĐẦU =================
    private void khoiTaoDuLieuMau(SQLiteDatabase db) {
        // Tài khoản sinh viên & giảng viên
        db.execSQL("INSERT INTO TaiKhoan VALUES ('GV202401', '123456', 'Mai Thị Thúy Hà', 'hamtt@fithou.edu.vn', 'GIANG_VIEN', NULL)");
        db.execSQL("INSERT INTO TaiKhoan VALUES ('GV202402', '123456', 'Nguyễn Thùy Linh', 'linhnt@fithou.edu.vn', 'GIANG_VIEN', NULL)");
        themGiangVienMau(db);

        // Lớp học phần
        themLopHocMau(db);

        // Đăng ký lớp cho sinh viên (Lớp Di động có đúng 3 sinh viên)
        themSinhVienMau(db, "24A1001D0308", "123456", "Trần Hồng Ngọc",
                "sv308@fithou.edu.vn", "2410A01", MA_LOP_DI_DONG_MAU,
                "K24-7E1005.22-I.2627-4.4_LT");
        themSinhVienMau(db, MA_SV_QUAN_TRI_MANG_MAU, "123456", "Hoàng Trung Đức",
                "sv0102@fithou.edu.vn", "2410A01", MA_LOP_QUAN_TRI_MANG_MAU, MA_LOP_DI_DONG_MAU);
        themSinhVienMau(db, "24A1001D0048", "123", "Đinh Trọng Quốc Bảo",
                "24a1001d0048@students.hou.edu.vn", "2410A01",
                MA_LOP_DI_DONG_MAU, "K24-7E1005.22-I.2627-4.4_LT");

        themBuoiHocMau(db);
        themDiemDanhQuanTriMangMau(db);
        themDiemDanhBaoMau(db);
        themDiemDanhDucDiDongMau(db);

        // Điểm danh mẫu cho Trần Hồng Ngọc (có mặt 6 buổi đầu môn Di động)
        db.execSQL("INSERT INTO DiemDanh (buoi_hoc_id, ma_sv, trang_thai, thoi_gian_quet) " +
                        "SELECT id, ?, 1, ? FROM BuoiHoc WHERE ma_lop = ? AND so_thu_tu BETWEEN 1 AND 6",
                new Object[]{"24A1001D0308", System.currentTimeMillis(), MA_LOP_DI_DONG_MAU});
    }

    private void themDiemDanhDucDiDongMau(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO DiemDanh (buoi_hoc_id, ma_sv, trang_thai, thoi_gian_quet) " +
                        "SELECT id, ?, 1, ? " +
                        "FROM BuoiHoc WHERE ma_lop = ? AND so_thu_tu IN (1, 2, 4, 5)",
                new Object[]{MA_SV_QUAN_TRI_MANG_MAU, System.currentTimeMillis(), MA_LOP_DI_DONG_MAU});
    }

    private void themDiemDanhBaoMau(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO DiemDanh (buoi_hoc_id, ma_sv, trang_thai, thoi_gian_quet) " +
                        "SELECT id, '24A1001D0048', 1, ? " +
                        "FROM BuoiHoc WHERE ma_lop = ? AND so_thu_tu BETWEEN 1 AND 5",
                new Object[]{System.currentTimeMillis(), MA_LOP_DI_DONG_MAU});
    }

    private void themDiemDanhQuanTriMangMau(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO DiemDanh (buoi_hoc_id, ma_sv, trang_thai) " +
                        "SELECT b.id, s.ma_sv, 1 FROM BuoiHoc b " +
                        "JOIN SinhVien_LopHoc s ON s.ma_lop = b.ma_lop " +
                        "WHERE s.ma_sv = ? AND b.ma_lop = ? " +
                        "AND b.so_thu_tu BETWEEN 1 AND 12 AND b.so_thu_tu NOT IN (3, 7, 11)",
                new Object[]{MA_SV_QUAN_TRI_MANG_MAU, MA_LOP_QUAN_TRI_MANG_MAU});
    }

    private void themSinhVienMau(SQLiteDatabase db, String maSinhVien, String matKhau,
                                String hoTen, String email, String lopNienChe,
                                String... maCacLopHoc) {
        db.execSQL("INSERT OR IGNORE INTO TaiKhoan " +
                        "(ma_nguoi_dung, mat_khau, ho_ten, email, vai_tro, lop_nien_che) " +
                        "VALUES (?, ?, ?, ?, 'SINH_VIEN', ?)",
                new Object[]{maSinhVien, matKhau, hoTen, email, lopNienChe});
        for (String maLop : maCacLopHoc) {
            db.execSQL("INSERT INTO SinhVien_LopHoc (ma_sv, ma_lop) " +
                            "SELECT ?, ? WHERE NOT EXISTS " +
                            "(SELECT 1 FROM SinhVien_LopHoc WHERE ma_sv = ? AND ma_lop = ?)",
                    new Object[]{maSinhVien, maLop, maSinhVien, maLop});
        }
    }

    private void themBuoiHocMau(SQLiteDatabase db) {
        String[] cacThu = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6"};
        SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        try (Cursor lop = db.rawQuery(
                "SELECT ma_lop, thu_hoc FROM LopHoc WHERE hoc_ky = ?",
                new String[]{"HK1 - 2026-2027"})) {
            while (lop.moveToNext()) {
                String maLop = lop.getString(0);
                int viTriThu = java.util.Arrays.asList(cacThu).indexOf(lop.getString(1));
                if (viTriThu < 0) {
                    continue;
                }

                Calendar ngayHoc = Calendar.getInstance();
                ngayHoc.clear();
                ngayHoc.set(2026, Calendar.AUGUST, 10 + viTriThu);

                for (int buoi = 1; buoi <= 12; buoi++) {
                    String ngay = dinhDangNgay.format(ngayHoc.getTime());
                    if (MA_LOP_DI_DONG_MAU.equals(maLop) && buoi <= NGAY_DI_DONG_CU.length) {
                        // Giữ ID buổi mẫu cũ để các lượt điểm danh vẫn liên kết đúng.
                        db.execSQL("UPDATE BuoiHoc SET ngay_hoc = ? " +
                                        "WHERE ma_lop = ? AND so_thu_tu = ? AND ngay_hoc = ? " +
                                        "AND NOT EXISTS (SELECT 1 FROM BuoiHoc WHERE ma_lop = ? AND ngay_hoc = ?)",
                                new Object[]{ngay, maLop, buoi, NGAY_DI_DONG_CU[buoi - 1], maLop, ngay});
                    }
                    db.execSQL("INSERT INTO BuoiHoc (ma_lop, ngay_hoc, so_thu_tu) " +
                                    "SELECT ?, ?, ? WHERE NOT EXISTS " +
                                    "(SELECT 1 FROM BuoiHoc WHERE ma_lop = ? AND ngay_hoc = ?)",
                            new Object[]{maLop, ngay, buoi, maLop, ngay});
                    ngayHoc.add(Calendar.DAY_OF_MONTH, 7);
                }
            }
        }
    }

    private void themGiangVienMau(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO TaiKhoan " +
                "(ma_nguoi_dung, mat_khau, ho_ten, email, vai_tro) VALUES " +
                "('GV202403', '123456', 'Vũ Xuân Hạnh', 'hanhvx@fithou.edu.vn', 'GIANG_VIEN'), " +
                "('GV202404', '123456', 'Thái Thanh Tùng', 'tungtt@fithou.edu.vn', 'GIANG_VIEN'), " +
                "('GV202405', '123456', 'Nguyễn Đình Dũng', 'dungnd@fithou.edu.vn', 'GIANG_VIEN'), " +
                "('GV202406', '123456', 'Lê Thị Hương', 'huonglt@fithou.edu.vn', 'GIANG_VIEN')");
    }

    private void themLopHocMau(SQLiteDatabase db) {
        themLopMau(db, MA_LOP_DI_DONG_MAU, "Lập trình cho thiết bị di động",
                "Thứ 2", "1-4", "Phòng 24", "07:30", "GV202401");
        themLopMau(db, "K24-7E1019.22-I.2627-2.1_LT", "Lập trình cho thiết bị di động",
                "Thứ 4", "1-4", "Phòng 24", "07:30", "GV202401");
        themLopMau(db, "K24-7E1019.22-I.2627-3.1_LT", "Lập trình cho thiết bị di động",
                "Thứ 6", "1-4", "Phòng 24", "07:30", "GV202401");
        themLopMau(db, "K24-7E1017.22-I.2627-1.1_LT", "Công nghệ phần mềm",
                "Thứ 3", "5-8", "Phòng 32", "13:00", "GV202401");
        themLopMau(db, "K24-7E1005.22-I.2627-4.4_LT", "Toán rời rạc",
                "Thứ 2", "5-8", "Phòng 44", "13:00", "GV202402");
        themLopMau(db, "K24-7E1005.22-I.2627-5.4_LT", "Toán rời rạc",
                "Thứ 4", "5-8", "Phòng 44", "13:00", "GV202402");
        themLopMau(db, "K24-7E1021.22-I.2627-2.2_LT", "Quản trị mạng",
                "Thứ 3", "1-4", "Phòng 51", "07:30", "GV202403");
        themLopMau(db, "K24-7E1021.22-I.2627-3.2_LT", "Quản trị mạng",
                "Thứ 5", "1-4", "Phòng 51", "07:30", "GV202403");
        themLopMau(db, "K24-7E1018.22-I.2627-5.5_LT", "Lập trình Web",
                "Thứ 3", "5-8", "Phòng 41", "13:00", "GV202405");
        themLopMau(db, "K24-7E1018.22-I.2627-6.5_LT", "Lập trình Web",
                "Thứ 5", "5-8", "Phòng 34", "13:00", "GV202404");
        themLopMau(db, "K24-7A0003.22-I.2627-3.3_LT", "Chủ nghĩa xã hội khoa học",
                "Thứ 6", "1-4", "Phòng 52", "07:30", "GV202406");
        themLopMau(db, "K24-7A0003.22-I.2627-4.3_LT", "Chủ nghĩa xã hội khoa học",
                "Thứ 6", "5-8", "Phòng 52", "13:00", "GV202406");
    }

    private void themLopMau(SQLiteDatabase db, String maLop, String tenMon, String thu,
                            String tiet, String phong, String gio, String maGiangVien) {
        db.execSQL("INSERT OR IGNORE INTO LopHoc " +
                        "(ma_lop, ten_mon, hoc_ky, thu_hoc, tiet_hoc, phong_hoc, gio_bat_dau, ma_gv) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{maLop, tenMon, "HK1 - 2026-2027", thu, tiet, phong, gio, maGiangVien});
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
        String sql = "SELECT DISTINCT l.*, t.ho_ten AS ten_giang_vien " +
                "FROM LopHoc l " +
                "JOIN SinhVien_LopHoc s ON l.ma_lop = s.ma_lop " +
                "LEFT JOIN TaiKhoan t ON l.ma_gv = t.ma_nguoi_dung " +
                "WHERE s.ma_sv = ?";
        return db.rawQuery(sql, new String[]{maSV});
    }

    Cursor layHoSoSinhVien(String maSV) {
        return getReadableDatabase().rawQuery(
                "SELECT ho_ten, lop_nien_che FROM TaiKhoan WHERE ma_nguoi_dung = ? AND vai_tro = 'SINH_VIEN'",
                new String[]{maSV});
    }

    Cursor layLichHocHomNay(String maSV, String ngay) {
        return getReadableDatabase().rawQuery(
                "SELECT l.ten_mon, l.tiet_hoc, l.gio_bat_dau, l.phong_hoc, g.ho_ten " +
                        "FROM LopHoc l LEFT JOIN TaiKhoan g ON g.ma_nguoi_dung = l.ma_gv " +
                        "WHERE EXISTS (SELECT 1 FROM SinhVien_LopHoc s WHERE s.ma_lop = l.ma_lop AND s.ma_sv = ?) " +
                        "AND EXISTS (SELECT 1 FROM BuoiHoc b WHERE b.ma_lop = l.ma_lop AND b.ngay_hoc = ?) " +
                        "ORDER BY l.gio_bat_dau, l.ma_lop",
                new String[]{maSV, ngay});
    }

    // Lấy lịch học theo thứ (để hỗ trợ demo ổn định)
    public Cursor layLichHocTheoThu(String maSV, String thuHoc) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT l.ten_mon, l.tiet_hoc, l.gio_bat_dau, l.phong_hoc, g.ho_ten " +
                "FROM LopHoc l " +
                "JOIN SinhVien_LopHoc s ON l.ma_lop = s.ma_lop " +
                "LEFT JOIN TaiKhoan g ON g.ma_nguoi_dung = l.ma_gv " +
                "WHERE s.ma_sv = ? AND l.thu_hoc = ? " +
                "ORDER BY l.gio_bat_dau ASC";
        return db.rawQuery(sql, new String[]{maSV, thuHoc});
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
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            if (!kiemTraSinhVienThuocLop(maSV, maLop)) {
                return false;
            }

            long buoiHocId;
            try (Cursor c = db.rawQuery(
                    "SELECT id FROM BuoiHoc WHERE ma_lop = ? AND ngay_hoc = ? ORDER BY id LIMIT 1",
                    new String[]{maLop, ngayHoc})) {
                if (c.moveToFirst()) {
                    buoiHocId = c.getLong(0);
                } else {
                    ContentValues buoi = new ContentValues();
                    buoi.put("ma_lop", maLop);
                    buoi.put("ngay_hoc", ngayHoc);
                    buoiHocId = db.insertOrThrow("BuoiHoc", null, buoi);
                }
            }

            ContentValues diemDanh = new ContentValues();
            diemDanh.put("trang_thai", 1);
            diemDanh.put("thoi_gian_quet", thoiGianQuet);
            String[] khoa = {Long.toString(buoiHocId), maSV};
            int daCapNhat = db.update("DiemDanh", diemDanh,
                    "buoi_hoc_id = ? AND ma_sv = ? AND (trang_thai IS NULL OR trang_thai <> 1)", khoa);
            if (daCapNhat == 0) {
                diemDanh.put("buoi_hoc_id", buoiHocId);
                diemDanh.put("ma_sv", maSV);
                db.insertWithOnConflict("DiemDanh", null, diemDanh, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    long layGioDaDiemDanh(String maSV, String maLop, String ngayHoc, long macDinh) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT d.thoi_gian_quet FROM DiemDanh d JOIN BuoiHoc b ON d.buoi_hoc_id = b.id " +
                        "WHERE d.ma_sv = ? AND b.ma_lop = ? AND b.ngay_hoc = ? AND d.trang_thai = 1 " +
                        "AND d.thoi_gian_quet IS NOT NULL ORDER BY d.thoi_gian_quet LIMIT 1",
                new String[]{maSV, maLop, ngayHoc})) {
            return c.moveToFirst() ? c.getLong(0) : macDinh;
        }
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

    // 7. Lấy thông tin hồ sơ Giảng viên
    public Cursor layHoSoGiangVien(String maGV) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT ho_ten, email FROM TaiKhoan WHERE ma_nguoi_dung = ? AND vai_tro = 'GIANG_VIEN'",
                new String[]{maGV});
    }

    // 8. Lấy danh sách buổi học của 1 lớp
    public Cursor layDanhSachBuoiHocCuaLop(String maLop) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, so_thu_tu, ngay_hoc FROM BuoiHoc WHERE ma_lop = ? ORDER BY so_thu_tu ASC",
                new String[]{maLop});
    }

    // 9. Lấy danh sách sinh viên thuộc 1 lớp
    public Cursor layDanhSachSinhVienCuaLop(String maLop) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT t.ma_nguoi_dung, t.ho_ten, t.lop_nien_che, t.email " +
                "FROM TaiKhoan t " +
                "JOIN SinhVien_LopHoc s ON t.ma_nguoi_dung = s.ma_sv " +
                "WHERE s.ma_lop = ? " +
                "ORDER BY t.ma_nguoi_dung ASC";
        return db.rawQuery(sql, new String[]{maLop});
    }

    // 10. Kiểm tra điểm danh 1 buổi của 1 sinh viên (trả về 1 nếu có mặt, 0 nếu vắng)
    public int kiemTraDiemDanhBuoi(String maSV, long buoiHocId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT trang_thai FROM DiemDanh WHERE ma_sv = ? AND buoi_hoc_id = ?",
                new String[]{maSV, String.valueOf(buoiHocId)})) {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        }
        return 0;
    }

    // 11. Cập nhật hoặc xóa điểm danh thủ công (Giảng viên đổi trạng thái)
    public boolean capNhatDiemDanhThuCong(String maSV, long buoiHocId, int trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (trangThai == 0) {
            return db.delete("DiemDanh", "ma_sv = ? AND buoi_hoc_id = ?",
                    new String[]{maSV, String.valueOf(buoiHocId)}) >= 0;
        } else {
            ContentValues cv = new ContentValues();
            cv.put("buoi_hoc_id", buoiHocId);
            cv.put("ma_sv", maSV);
            cv.put("trang_thai", 1);
            cv.put("thoi_gian_quet", System.currentTimeMillis());
            return db.insertWithOnConflict("DiemDanh", null, cv, SQLiteDatabase.CONFLICT_REPLACE) >= 0;
        }
    }

    // 12. Thống kê số lượng sinh viên có mặt / vắng trong buổi
    public int[] layThongKeDiemDanhBuoi(String maLop, long buoiHocId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int siSo = 0;
        int coMat = 0;
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(ma_sv) FROM SinhVien_LopHoc WHERE ma_lop = ?",
                new String[]{maLop})) {
            if (c.moveToFirst()) {
                siSo = c.getInt(0);
            }
        }
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(d.ma_sv) FROM DiemDanh d " +
                        "JOIN SinhVien_LopHoc s ON d.ma_sv = s.ma_sv AND s.ma_lop = ? " +
                        "WHERE d.buoi_hoc_id = ? AND d.trang_thai = 1",
                new String[]{maLop, String.valueOf(buoiHocId)})) {
            if (c.moveToFirst()) {
                coMat = c.getInt(0);
            }
        }
        return new int[]{coMat, Math.max(0, siSo - coMat), siSo};
    }

    // 13. Lấy danh sách điểm danh mới nhất trong 1 buổi học
    public Cursor layDanhSachDiemDanhMoiNhat(long buoiHocId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT t.ho_ten, d.thoi_gian_quet, d.trang_thai, t.ma_nguoi_dung " +
                "FROM DiemDanh d " +
                "JOIN TaiKhoan t ON d.ma_sv = t.ma_nguoi_dung " +
                "WHERE d.buoi_hoc_id = ? AND d.trang_thai = 1 " +
                "ORDER BY d.thoi_gian_quet DESC";
        return db.rawQuery(sql, new String[]{String.valueOf(buoiHocId)});
    }
}
