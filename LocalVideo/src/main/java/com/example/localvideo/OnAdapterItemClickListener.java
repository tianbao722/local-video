package com.example.localvideo;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public interface OnAdapterItemClickListener {

    void OnAdapterItemClickListener(RecyclerView.ViewHolder holder, View view, int position);
}
