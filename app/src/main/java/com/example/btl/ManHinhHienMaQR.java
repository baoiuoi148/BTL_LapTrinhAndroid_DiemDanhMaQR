package com.example.btl;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ManHinhHienMaQR extends AppCompatActivity implements View.OnClickListener {
    private TextView mtextViewThoiGian, mtextViewMaLop, mtextViewKhungGio;
    private TextView tvDaDiemDanh, tvTongSiSo, tvCoMat, tvChuaCo;
    private ImageView mimageViewQR;
    private CountDownTimer countDownTimer;
    private LinearLayout mlinearLayoutTrangChu, containerDiemDanhGanDay;
    private Spinner spinnerChonBuoiHoc;

    private String maLop = "K24-7E1019.22-I.2627-1.1_LT";
    private String ngayDiemDanhHienTai = "14/09/2026";
    private long buoiHocIdHienTai = -1;
    private int soThuTuHienTai = 6;

    private static class ThongTinBuoi {
        long id;
        int soThuTu;
        String ngayHoc;

        ThongTinBuoi(long id, int soThuTu, String ngayHoc) {
            this.id = id;
            this.soThuTu = soThuTu;
            this.ngayHoc = ngayHoc;
        }

        @Override
        public String toString() {
            return "Buổi " + soThuTu + " (" + ngayHoc + ")";
        }
    }

    private final List<ThongTinBuoi> danhSachBuoi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_hien_ma_qr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String paramMaLop = getIntent().getStringExtra("EXTRA_MA_LOP");
        if (paramMaLop != null && !paramMaLop.trim().isEmpty()) {
            maLop = paramMaLop;
        }

        initView();
        addAction();
        napDanhSachBuoiHoc();
        initThoiGian();
        XacNhanDangXuat.attach(this);
    }

    private void initView() {
        mtextViewThoiGian = findViewById(R.id.textViewThoiGian);
        mtextViewMaLop = findViewById(R.id.textViewMaLop);
        mtextViewKhungGio = findViewById(R.id.textView9);
        mimageViewQR = findViewById(R.id.imageViewQR);
        mlinearLayoutTrangChu = findViewById(R.id.linearLayoutTrangChu);
        spinnerChonBuoiHoc = findViewById(R.id.spinnerChonBuoiHoc);
        tvDaDiemDanh = findViewById(R.id.tvDaDiemDanh);
        tvTongSiSo = findViewById(R.id.tvTongSiSo);
        tvCoMat = findViewById(R.id.tvCoMat);
        tvChuaCo = findViewById(R.id.tvChuaCo);
        containerDiemDanhGanDay = findViewById(R.id.containerDiemDanhGanDay);

        if (mtextViewMaLop != null) {
            mtextViewMaLop.setText(maLop);
        }
    }

    private void napDanhSachBuoiHoc() {
        danhSachBuoi.clear();
        int viTriMacDinh = 0;
        try (DatabaseHelper db = new DatabaseHelper(this);
             Cursor c = db.layDanhSachBuoiHocCuaLop(maLop)) {
            while (c.moveToNext()) {
                long id = c.getLong(0);
                int stt = c.getInt(1);
                String ngay = c.getString(2);
                danhSachBuoi.add(new ThongTinBuoi(id, stt, ngay));
                if (stt == 6) {
                    viTriMacDinh = danhSachBuoi.size() - 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (danhSachBuoi.isEmpty()) {
            danhSachBuoi.add(new ThongTinBuoi(1, 6, "14/09/2026"));
        }

        ArrayAdapter<ThongTinBuoi> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, danhSachBuoi);
        spinnerChonBuoiHoc.setAdapter(adapter);

        if (viTriMacDinh < danhSachBuoi.size()) {
            spinnerChonBuoiHoc.setSelection(viTriMacDinh);
            ThongTinBuoi b = danhSachBuoi.get(viTriMacDinh);
            buoiHocIdHienTai = b.id;
            ngayDiemDanhHienTai = b.ngayHoc;
            soThuTuHienTai = b.soThuTu;
        }

        spinnerChonBuoiHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ThongTinBuoi chon = danhSachBuoi.get(position);
                buoiHocIdHienTai = chon.id;
                ngayDiemDanhHienTai = chon.ngayHoc;
                soThuTuHienTai = chon.soThuTu;
                if (mtextViewKhungGio != null) {
                    mtextViewKhungGio.setText("Buổi " + soThuTuHienTai + " (" + ngayDiemDanhHienTai + "): 07:30 - 08:30");
                }
                initQR();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void addAction() {
        if (mlinearLayoutTrangChu != null) {
            mlinearLayoutTrangChu.setOnClickListener(this);
        }
    }

    private void initThoiGian() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long giayConLai = millisUntilFinished / 1000;
                long phut = giayConLai / 60;
                long giay = giayConLai % 60;
                if (mtextViewThoiGian != null) {
                    mtextViewThoiGian.setText(String.format(Locale.ROOT, "%d:%02d", phut, giay));
                }
                if (giayConLai % 5 == 0 && giayConLai > 0) {
                    initQR();
                }
            }

            @Override
            public void onFinish() {
                if (mtextViewThoiGian != null) {
                    mtextViewThoiGian.setText("0:00");
                }
                Toast.makeText(ManHinhHienMaQR.this, "Hết thời gian điểm danh!", Toast.LENGTH_LONG).show();
                if (mimageViewQR != null) {
                    mimageViewQR.setAlpha(0.2f);
                    mimageViewQR.setEnabled(false);
                }
            }
        };
        countDownTimer.start();
    }

    private void initQR() {
        try {
            String tokenRandom = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            JSONObject qrData = new JSONObject();
            qrData.put("maLop", maLop);
            qrData.put("ngay", ngayDiemDanhHienTai);
            qrData.put("token", tokenRandom);
            qrData.put("timestamp", System.currentTimeMillis());

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrData.toString(), BarcodeFormat.QR_CODE, 500, 500);
            if (mimageViewQR != null) {
                mimageViewQR.setImageBitmap(bitmap);
                mimageViewQR.setAlpha(1.0f);
                mimageViewQR.setEnabled(true);
            }

            capNhatThongKeVaDanhSach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void capNhatThongKeVaDanhSach() {
        if (buoiHocIdHienTai <= 0) return;
        try (DatabaseHelper db = new DatabaseHelper(this)) {
            int[] tk = db.layThongKeDiemDanhBuoi(maLop, buoiHocIdHienTai);
            int coMat = tk[0];
            int chuaCo = tk[1];
            int siSo = tk[2];

            if (tvDaDiemDanh != null) tvDaDiemDanh.setText(String.valueOf(coMat));
            if (tvTongSiSo != null) tvTongSiSo.setText("/" + siSo);
            if (tvCoMat != null) tvCoMat.setText(String.valueOf(coMat));
            if (tvChuaCo != null) tvChuaCo.setText(String.valueOf(chuaCo));

            if (containerDiemDanhGanDay != null) {
                containerDiemDanhGanDay.removeAllViews();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);
                try (Cursor c = db.layDanhSachDiemDanhMoiNhat(buoiHocIdHienTai)) {
                    if (c.getCount() == 0) {
                        TextView tvTrong = new TextView(this);
                        tvTrong.setText("Chưa có sinh viên nào điểm danh buổi này.");
                        tvTrong.setTextColor(0xFF888888);
                        tvTrong.setTextSize(13);
                        tvTrong.setPadding(0, 8, 0, 8);
                        containerDiemDanhGanDay.addView(tvTrong);
                    } else {
                        while (c.moveToNext()) {
                            String ten = c.getString(0);
                            long thoiGian = c.getLong(1);
                            String maSV = c.getString(3);
                            String gio = (thoiGian > 0) ? sdf.format(new Date(thoiGian)) : "07:35:00";

                            TextView tvSV = new TextView(this);
                            tvSV.setText(gio + "  -  " + ten + " (" + maSV + ") có mặt");
                            tvSV.setTextColor(0xFF16A34A);
                            tvSV.setTextSize(14);
                            tvSV.setPadding(0, 6, 0, 6);
                            containerDiemDanhGanDay.addView(tvSV);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mlinearLayoutTrangChu) {
            finish();
        }
    }
}
