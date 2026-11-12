package com.example.btl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class XacNhanDangXuat {

    public static void attach(Activity activity) {
        View logoutBtn = activity.findViewById(R.id.imageViewTaiKhoan);
        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> hienThongBao(activity));
        }
    }

    public static void hienThongBao(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(activity, ManHinhDangNhap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}