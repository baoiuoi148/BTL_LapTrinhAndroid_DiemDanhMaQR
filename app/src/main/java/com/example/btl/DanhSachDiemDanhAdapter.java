package com.example.btl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

final class DanhSachDiemDanhAdapter<T> extends RecyclerView.Adapter<DanhSachDiemDanhAdapter.Dong> {
    interface HienThiDong<T> {
        void gan(View view, T duLieu, int viTri);
    }

    private final int layout;
    private final List<T> danhSach;
    private final HienThiDong<T> hienThiDong;

    DanhSachDiemDanhAdapter(int layout, List<T> danhSach, HienThiDong<T> hienThiDong) {
        this.layout = layout;
        this.danhSach = danhSach;
        this.hienThiDong = hienThiDong;
    }

    @NonNull
    @Override
    public Dong onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Dong(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Dong holder, int position) {
        hienThiDong.gan(holder.itemView, danhSach.get(position), position);
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static final class Dong extends RecyclerView.ViewHolder {
        Dong(View itemView) {
            super(itemView);
        }
    }
}
