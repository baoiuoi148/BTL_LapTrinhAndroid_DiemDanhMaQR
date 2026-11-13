package com.example.btl;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ManHinhDanhSachLop extends AppCompatActivity implements View.OnClickListener {
    private ImageView mimageViewHienMaQR;
    private LinearLayout mlinearLayoutTrangChu;
    private TableLayout mtableLayoutDiemDanh;
    private TextView mtextViewTenMon, mtextViewSubTitle;
    private String maLop = "K24-7E1019.22-I.2627-1.1_LT";
    private String maGV;

    private static class BuoiHocItem {
        long id;
        int soThuTu;
        String ngayHoc;

        BuoiHocItem(long id, int soThuTu, String ngayHoc) {
            this.id = id;
            this.soThuTu = soThuTu;
            this.ngayHoc = ngayHoc;
        }
    }

    private static class SinhVienItem {
        String maSV;
        String hoTen;
        String ngaySinh;

        SinhVienItem(String maSV, String hoTen, String ngaySinh) {
            this.maSV = maSV;
            this.hoTen = hoTen;
            this.ngaySinh = ngaySinh;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_danh_sach_lop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String paramMaLop = getIntent().getStringExtra("EXTRA_MA_LOP");
        if (paramMaLop != null && !paramMaLop.trim().isEmpty()) {
            maLop = paramMaLop;
        }
        maGV = ThanhDieuHuong.maNguoiDung(this);
        if (maGV == null || maGV.trim().isEmpty() || maGV.startsWith("24A")) {
            maGV = "GV202401";
        }

        initView();
        addAction();
        napBangDiemDanh();
        XacNhanDangXuat.attach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        napBangDiemDanh();
    }

    private void addAction() {
        mimageViewHienMaQR.setOnClickListener(this);
        mlinearLayoutTrangChu.setOnClickListener(this);
    }

    private void initView() {
        mimageViewHienMaQR = findViewById(R.id.imageViewHienMaQR);
        mlinearLayoutTrangChu = findViewById(R.id.linearLayoutTrangChu);
        mtableLayoutDiemDanh = findViewById(R.id.tableLayoutDiemDanh);
        mtextViewTenMon = findViewById(R.id.textView5);
        mtextViewSubTitle = findViewById(R.id.textViewSubTitle);

        if (mtextViewSubTitle != null) {
            mtextViewSubTitle.setText("Mã lớp: " + maLop + " | Sĩ số: 3");
        }
    }

    private void napBangDiemDanh() {
        if (mtableLayoutDiemDanh == null) return;
        mtableLayoutDiemDanh.removeAllViews();

        List<BuoiHocItem> dsBuoi = new ArrayList<>();
        List<SinhVienItem> dsSV = new ArrayList<>();

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            // Lấy danh sách 12 buổi học
            try (Cursor c = db.layDanhSachBuoiHocCuaLop(maLop)) {
                while (c.moveToNext()) {
                    dsBuoi.add(new BuoiHocItem(c.getLong(0), c.getInt(1), c.getString(2)));
                }
            }

            // Lấy danh sách 3 sinh viên
            try (Cursor c = db.layDanhSachSinhVienCuaLop(maLop)) {
                while (c.moveToNext()) {
                    String ma = c.getString(0);
                    String ten = c.getString(1);
                    String ngaySinh = layNgaySinh(ma);
                    dsSV.add(new SinhVienItem(ma, ten, ngaySinh));
                }
            }

            if (mtextViewSubTitle != null) {
                mtextViewSubTitle.setText("Mã lớp: " + maLop + " | Sĩ số: " + dsSV.size());
            }

            // 1. HÀNG TIÊU ĐỀ
            TableRow headerRow = new TableRow(this);
            headerRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            headerRow.addView(taoOHeader("STT", 50));
            headerRow.addView(taoOHeader("Mã sinh viên", 120));
            headerRow.addView(taoOHeader("Họ và tên", 150));
            headerRow.addView(taoOHeader("Ngày sinh", 105));

            for (BuoiHocItem buoi : dsBuoi) {
                String tieuDeNgay = buoi.ngayHoc.length() >= 5 ? buoi.ngayHoc.substring(0, 5) : buoi.ngayHoc;
                headerRow.addView(taoOHeader(tieuDeNgay, 75));
            }
            mtableLayoutDiemDanh.addView(headerRow);

            // 2. TỪNG HÀNG SINH VIÊN (DÙNG RADIOBUTTON, KHÔNG CÓ MÀU)
            for (int i = 0; i < dsSV.size(); i++) {
                SinhVienItem sv = dsSV.get(i);
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                row.addView(taoOCell(String.valueOf(i + 1), 50));
                row.addView(taoOCell(sv.maSV, 120));
                row.addView(taoOCell(sv.hoTen, 150));
                row.addView(taoOCell(sv.ngaySinh, 105));

                for (BuoiHocItem buoi : dsBuoi) {
                    int trangThai = db.kiemTraDiemDanhBuoi(sv.maSV, buoi.id);
                    View cellDiemDanh = taoOCellRadioButton(sv, buoi, trangThai == 1);
                    row.addView(cellDiemDanh);
                }

                mtableLayoutDiemDanh.addView(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String layNgaySinh(String maSV) {
        if ("24A1001D0048".equals(maSV)) return "14/08/2006";
        if ("24A1001D0102".equals(maSV)) return "10/05/2006";
        if ("24A1001D0308".equals(maSV)) return "20/11/2006";
        return "01/01/2006";
    }

    private TextView taoOHeader(String text, int minWidthDp) {
        TextView tv = new TextView(this);
        int dpToPx = (int) (getResources().getDisplayMetrics().density * minWidthDp);
        tv.setMinWidth(dpToPx);
        tv.setPadding(dp(10), dp(10), dp(10), dp(10));
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(Color.BLACK);
        tv.setBackground(ContextCompat.getDrawable(this, R.drawable.table_header));
        return tv;
    }

    private TextView taoOCell(String text, int minWidthDp) {
        TextView tv = new TextView(this);
        int dpToPx = (int) (getResources().getDisplayMetrics().density * minWidthDp);
        tv.setMinWidth(dpToPx);
        tv.setPadding(dp(10), dp(10), dp(10), dp(10));
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(Color.BLACK);
        tv.setBackground(ContextCompat.getDrawable(this, R.drawable.table_cell));
        return tv;
    }

    private View taoOCellRadioButton(SinhVienItem sv, BuoiHocItem buoi, boolean daCoMat) {
        FrameLayout frame = new FrameLayout(this);
        int dpToPx = (int) (getResources().getDisplayMetrics().density * 75);
        frame.setLayoutParams(new TableRow.LayoutParams(
                dpToPx, TableRow.LayoutParams.MATCH_PARENT));
        frame.setBackground(ContextCompat.getDrawable(this, R.drawable.table_cell));

        RadioButton rb = new RadioButton(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        rb.setLayoutParams(lp);
        rb.setChecked(daCoMat);
        rb.setClickable(false); // Xử lý qua frame click để quản lý trạng thái bằng Dialog

        frame.addView(rb);

        View.OnClickListener clickListener = v -> hienDialogDoiTrangThai(sv, buoi, daCoMat);
        frame.setClickable(true);
        frame.setFocusable(true);
        frame.setOnClickListener(clickListener);

        return frame;
    }

    private void hienDialogDoiTrangThai(SinhVienItem sv, BuoiHocItem buoi, boolean daCoMat) {
        String tieuDe = daCoMat ? "Xác nhận xóa điểm danh" : "Xác nhận điểm danh";
        String thongBao = daCoMat
                ? "Bạn có muốn xóa điểm danh (đánh dấu vắng) cho sinh viên " + sv.hoTen + " (" + sv.maSV + ") buổi " + buoi.soThuTu + " (" + buoi.ngayHoc + ")?"
                : "Điểm danh (có mặt) cho sinh viên " + sv.hoTen + " (" + sv.maSV + ") buổi " + buoi.soThuTu + " (" + buoi.ngayHoc + ")?";
        String nutXacNhan = daCoMat ? "Xóa điểm danh" : "Điểm danh";

        new AlertDialog.Builder(this)
                .setTitle(tieuDe)
                .setMessage(thongBao)
                .setPositiveButton(nutXacNhan, (dialog, which) -> {
                    try (DatabaseHelper db = new DatabaseHelper(ManHinhDanhSachLop.this)) {
                        int trangThaiMoi = daCoMat ? 0 : 1;
                        db.capNhatDiemDanhThuCong(sv.maSV, buoi.id, trangThaiMoi);
                        String msg = (trangThaiMoi == 1 ? "Đã điểm danh cho " : "Đã xóa điểm danh cho ") + sv.hoTen;
                        Toast.makeText(ManHinhDanhSachLop.this, msg, Toast.LENGTH_SHORT).show();
                        napBangDiemDanh();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ManHinhDanhSachLop.this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private int dp(int value) {
        return (int) (getResources().getDisplayMetrics().density * value);
    }

    @Override
    public void onClick(View v) {
        if (v == mimageViewHienMaQR) {
            Intent intent = new Intent(ManHinhDanhSachLop.this, ManHinhHienMaQR.class);
            intent.putExtra("EXTRA_MA_LOP", maLop);
            intent.putExtra(ThanhDieuHuong.EXTRA_MA_NGUOI_DUNG, maGV);
            startActivity(intent);
        }
        if (v == mlinearLayoutTrangChu) {
            Intent intent = new Intent(ManHinhDanhSachLop.this, ManHinhTrangChuGiaoVien.class);
            intent.putExtra(ThanhDieuHuong.EXTRA_MA_NGUOI_DUNG, maGV);
            startActivity(intent);
            finish();
        }
    }
}
