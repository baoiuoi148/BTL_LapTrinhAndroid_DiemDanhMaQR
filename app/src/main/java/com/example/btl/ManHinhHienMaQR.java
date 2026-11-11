package com.example.btl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.util.UUID;

public class ManHinhHienMaQR extends AppCompatActivity implements View.OnClickListener {
    private TextView mtextViewThoiGian, mtextViewMaLop;
    private ImageView mimageViewQR;
    private ImageView mimageViewUserHeader;
    private CountDownTimer countDownTimer;
    private final String MA_LOP = "K24-7E1019.22-I.2627-1.1_LT";
    private final String NGAY_DIEM_DANH = "09/11/2026";

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
        initView();
        initQR();
        initThoiGian();
        addAction();
    }

    private void addAction() {
        if (mimageViewUserHeader != null) {
            mimageViewUserHeader.setOnClickListener(this);
        }
    }

    private void initThoiGian() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long giayConLai = millisUntilFinished / 1000;
                long phut = giayConLai / 60;
                long giay = giayConLai % 60;
                mtextViewThoiGian.setText(String.format("%d:%02d", phut, giay));
                if (giayConLai % 5 == 0 && giayConLai > 0) {
                    initQR();
                }
            }
            @Override
            public void onFinish() {
                mtextViewThoiGian.setText("0:00");
                Toast.makeText(ManHinhHienMaQR.this, "Hết thời gian điểm danh!", Toast.LENGTH_LONG).show();
                mimageViewQR.setAlpha(0.2f);
                mimageViewQR.setEnabled(false);
            }
        };
        countDownTimer.start();
    }

    private void initQR() {
        try {
            String tokenRandom = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            JSONObject qrData = new JSONObject();
            qrData.put("maLop", MA_LOP);
            qrData.put("ngay", NGAY_DIEM_DANH);
            qrData.put("token", tokenRandom);
            qrData.put("timestamp", System.currentTimeMillis());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrData.toString(), BarcodeFormat.QR_CODE, 500, 500);
            mimageViewQR.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mtextViewThoiGian = findViewById(R.id.textViewThoiGian);
        mtextViewMaLop = findViewById(R.id.textViewMaLop);
        mimageViewQR = findViewById(R.id.imageViewQR);
        mimageViewUserHeader = findViewById(R.id.imageViewUserHeader);
    }

    private void xacNhanDangXuat() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(ManHinhHienMaQR.this, ManHinhDangNhap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v == mimageViewUserHeader) {
            xacNhanDangXuat();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
