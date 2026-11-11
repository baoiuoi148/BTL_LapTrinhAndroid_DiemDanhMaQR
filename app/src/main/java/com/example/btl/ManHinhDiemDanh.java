package com.example.btl;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ManHinhDiemDanh extends AppCompatActivity {
    private static final long QR_HIEU_LUC_MS = 10_000;

    private DecoratedBarcodeView mayQuet;
    private ImageView nutFlash;
    private View popup;
    private boolean dangMo;
    private boolean dangQuet;
    private boolean batFlash;
    private Bundle ketQua;

    private final androidx.activity.result.ActivityResultLauncher<String> xinQuyenCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), daCap -> {
                if (daCap) {
                    batDauQuet();
                } else {
                    hienLoi("Cần cấp quyền camera để quét mã điểm danh.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_diem_danh);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        mayQuet = findViewById(R.id.barcodeScannerViewQuetMa);
        mayQuet.getBarcodeView().setDecoderFactory(
                new DefaultDecoderFactory(Collections.singletonList(BarcodeFormat.QR_CODE)));
        mayQuet.setStatusText("Đặt mã QR vào giữa khung để quét");
        popup = findViewById(R.id.scrollViewKetQuaDiemDanh);
        nutFlash = findViewById(R.id.imageViewFlash);
        nutFlash.setVisibility(getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) ? View.VISIBLE : View.INVISIBLE);
        nutFlash.setOnClickListener(view -> {
            if (dangQuet) {
                datFlash(!batFlash);
            }
        });

        ThanhDieuHuong.gan(this, this::quetLai);
        findViewById(R.id.buttonVeTrangChuKetQua).setOnClickListener(view ->
                ThanhDieuHuong.veTrangChu(this));
        findViewById(R.id.imageViewLichSu).setOnClickListener(view ->
                startActivity(new Intent(this, ManHinhLichSuDiemDanh.class)));

        Bundle ketQuaDaLuu = savedInstanceState == null ? null : savedInstanceState.getBundle("ketQua");
        if (ketQuaDaLuu != null) {
            hienKetQua(ketQuaDaLuu);
        } else if (savedInstanceState == null && !coQuyenCamera()) {
            xinQuyenCamera.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        quetLai();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dangMo = true;
        batDauQuet();
    }

    @Override
    protected void onPause() {
        dangMo = false;
        dungQuet();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("ketQua", ketQua);
    }

    private boolean coQuyenCamera() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void quetLai() {
        dungQuet();
        ketQua = null;
        popup.setVisibility(View.GONE);
        mayQuet.setVisibility(View.VISIBLE);
        if (coQuyenCamera()) {
            batDauQuet();
        } else {
            xinQuyenCamera.launch(Manifest.permission.CAMERA);
        }
    }

    private void batDauQuet() {
        if (!dangMo || dangQuet || ketQua != null || !coQuyenCamera()) {
            return;
        }
        dangQuet = true;
        mayQuet.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (dangQuet) {
                    xuLyDiemDanh(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {}
        });
        mayQuet.resume();
    }

    private void dungQuet() {
        if (dangQuet) {
            dangQuet = false;
            datFlash(false);
            mayQuet.getBarcodeView().stopDecoding();
            mayQuet.pause();
        }
    }

    private void datFlash(boolean bat) {
        batFlash = bat;
        if (bat) {
            mayQuet.setTorchOn();
        } else {
            mayQuet.setTorchOff();
        }
        nutFlash.setContentDescription(bat ? "Tắt đèn flash" : "Bật đèn flash");
        nutFlash.setSelected(bat);
    }

    private void xuLyDiemDanh(String duLieuQr) {
        try {
            JSONObject qr = new JSONObject(duLieuQr);
            String maLop = qr.getString("maLop");
            String ngay = qr.getString("ngay");
            long thoiGianTao = qr.getLong("timestamp");
            SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
            dinhDangNgay.setLenient(false);
            if (!ngay.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")
                    || qr.getString("token").trim().isEmpty() || thoiGianTao <= 0) {
                hienLoi("Mã QR điểm danh không hợp lệ.");
                return;
            }
            dinhDangNgay.parse(ngay);

            DuLieuDiemDanh.LopHoc lop = DuLieuDiemDanh.timLop(maLop);
            if (lop == null) {
                hienLoi("Mã QR không thuộc lớp bạn đang học.");
                return;
            }
            long bayGio = System.currentTimeMillis();
            if (thoiGianTao < bayGio - QR_HIEU_LUC_MS) {
                hienLoi("Mã QR đã hết hạn. Hãy quét mã mới trên màn hình giảng viên.");
                return;
            }
            if (thoiGianTao > bayGio + QR_HIEU_LUC_MS) {
                hienLoi("Thời gian trên mã QR không hợp lệ.");
                return;
            }

            long gioDiemDanh = DuLieuDiemDanh.ghiNhan(this, lop, ngay, bayGio);
            Bundle thanhCong = new Bundle();
            thanhCong.putString("maLop", lop.ma);
            thanhCong.putLong("gioDiemDanh", gioDiemDanh);
            hienKetQua(thanhCong);
        } catch (JSONException | ParseException e) {
            hienLoi("Mã QR điểm danh không hợp lệ.");
        }
    }

    private void hienLoi(String lyDo) {
        Bundle thatBai = new Bundle();
        thatBai.putString("lyDo", lyDo);
        hienKetQua(thatBai);
    }

    private void hienKetQua(Bundle ketQuaMoi) {
        dungQuet();
        ketQua = ketQuaMoi;
        mayQuet.setVisibility(View.INVISIBLE);

        DuLieuDiemDanh.LopHoc lop = DuLieuDiemDanh.timLop(ketQua.getString("maLop"));
        boolean thanhCong = lop != null;
        ImageView icon = findViewById(R.id.imageViewKetQuaDiemDanh);
        icon.setImageResource(thanhCong ? R.drawable.ic_success_big : R.drawable.ic_that_bai_big);

        TextView tieuDe = findViewById(R.id.textViewTieuDeKetQua);
        tieuDe.setText(thanhCong ? "Điểm danh thành công!" : "Điểm danh thất bại");
        TextView thongBao = findViewById(R.id.textViewMonHocKetQua);
        TextView giangVien = findViewById(R.id.textViewGiangVienKetQua);
        TextView gio = findViewById(R.id.textViewGioDiemDanh);
        int hienThiChiTiet = thanhCong ? View.VISIBLE : View.GONE;
        giangVien.setVisibility(hienThiChiTiet);
        gio.setVisibility(hienThiChiTiet);
        findViewById(R.id.textViewNhanGioDiemDanh).setVisibility(hienThiChiTiet);
        if (thanhCong) {
            thongBao.setText("Môn học: " + lop.ten);
            giangVien.setText("Giảng viên: " + lop.giangVien);
            gio.setText(DuLieuDiemDanh.dinhDangGio(ketQua.getLong("gioDiemDanh"), "HH:mm:ss"));
        } else {
            thongBao.setText(ketQua.getString("lyDo"));
        }

        popup.scrollTo(0, 0);
        popup.setVisibility(View.VISIBLE);
    }
}
