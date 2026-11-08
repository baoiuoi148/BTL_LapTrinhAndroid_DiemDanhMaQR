package com.example.btl;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhTrangChuSinhVien extends AppCompatActivity implements View.OnClickListener {
    private ImageView mimageViewQuetQR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu_sinhvien);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_student_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        addAction();
    }

    private void addAction() {
        mimageViewQuetQR.setOnClickListener(this);
    }

    private void initView() {
        mimageViewQuetQR=findViewById(R.id.imageViewQuetQR);

    }

    @Override
    public void onClick(View v) {
        if(v==mimageViewQuetQR){
            Intent intent=new Intent(ManHinhTrangChuSinhVien.this, ManHinhDiemDanh.class);
            startActivity(intent);
        }
    }
}