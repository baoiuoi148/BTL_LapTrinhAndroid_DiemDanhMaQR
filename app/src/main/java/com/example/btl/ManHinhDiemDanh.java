package com.example.btl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONObject;

import java.util.List;

public class ManHinhDiemDanh extends AppCompatActivity {
    private DecoratedBarcodeView mbarcodeScannerViewQuetMa;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private boolean daQuet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_diem_danh);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initCamera();
    }

    private void initCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            batDauQuet();
        }
    }
    private void batDauQuet() {
        daQuet = false;
        mbarcodeScannerViewQuetMa.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null && !daQuet) {
                    daQuet = true;
                    String duLieuQR = result.getText();
                    xuLyDiemDanh(duLieuQR);
                }
            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {}
        });
    }
    private void xuLyDiemDanh(String duLieuQR) {
        try {
            JSONObject json = new JSONObject(duLieuQR);
            String maLopQuet = json.getString("maLop");
            long thoiGianTao = json.getLong("timestamp");
            String maLopCuaSinhVien = "K24-7E1019.22-I.2627-1.1_LT";
            if (!maLopQuet.equals(maLopCuaSinhVien)) {
                Toast.makeText(this, "LỖI: Bạn quét nhầm mã lớp khác!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            if (System.currentTimeMillis() - thoiGianTao > 10000) {
                Toast.makeText(this, "Mã QR đã hết hạn (chỉ có hiệu lực trong 5s)!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Toast.makeText(this, "Điểm danh THÀNH CÔNG!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Mã QR không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                batDauQuet();
                mbarcodeScannerViewQuetMa.resume();
            } else {
                Toast.makeText(this, "Cần cấp quyền Camera để điểm danh!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void initView() {
        mbarcodeScannerViewQuetMa=findViewById(R.id.barcodeScannerViewQuetMa);
        mbarcodeScannerViewQuetMa.getStatusView().setText("Đặt mã QR vào giữa khung để quét");
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mbarcodeScannerViewQuetMa != null && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mbarcodeScannerViewQuetMa.resume();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mbarcodeScannerViewQuetMa.pause();
    }
}