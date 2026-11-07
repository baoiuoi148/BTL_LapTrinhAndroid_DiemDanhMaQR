package com.example.btl;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManHinhTrangChuGiaoVien extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout mframeLayoutDiemDanhLop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu_giaovien);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_lecturer_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        addAction();
    }

    private void addAction() {
        mframeLayoutDiemDanhLop.setOnClickListener(this);
    }

    private void initView() {
        mframeLayoutDiemDanhLop=findViewById(R.id.frameLayoutDiemDanhLop);
    }

    @Override
    public void onClick(View v) {
        if(v==mframeLayoutDiemDanhLop){
            Intent intent=new Intent(ManHinhTrangChuGiaoVien.this, ManHinhLichGiangDay.class);
            startActivity(intent);
        }
    }
}