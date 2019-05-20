package com.xiyoumusic.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.MusicInfoActivity;
import com.xiyoumusic.app.activitys.UserInfoActivity;
import com.xiyoumusic.app.entity.Dongtai;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.DateFormat;
import com.xiyoumusic.app.views.CircleImageView;


import java.util.List;

public class DongtaiRecyclerViewAdapter extends RecyclerView.Adapter<DongtaiRecyclerViewAdapter.DongtaiViewHolder> {
    List<Dongtai> dongtaiList;
    Context context;
    private OnItemClickLitener mOnItemClickLitener;

    public interface OnItemClickLitener{
        void onItemClick(View view);
    }
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener){
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public DongtaiRecyclerViewAdapter(List<Dongtai> dongtaiList, Context context) {
        this.dongtaiList = dongtaiList;
        this.context = context;
    }

    @NonNull
    @Override
    public DongtaiRecyclerViewAdapter.DongtaiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dongtai, parent, false);
        DongtaiRecyclerViewAdapter.DongtaiViewHolder dongtaiViewHolder = new DongtaiRecyclerViewAdapter.DongtaiViewHolder(view);
        return dongtaiViewHolder;
    }

    public void refresh(List<Dongtai> dongtaiList){
        this.dongtaiList = dongtaiList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull DongtaiViewHolder holder, int position) {
        Dongtai dongtai = dongtaiList.get(position);
        String txlj = dongtai.getTxlj();
        String nc = dongtai.getNc();
        String date = DateFormat.getDateFromID(dongtai.getGqid(), DateFormat.FORMAT_YYYY_MM_DD_HH_MM_SS);
        String gqct = dongtai.getGqct();
        String gqmc = dongtai.getGqmc();
        String zzxh = dongtai.getZzxh();
        if (TextUtils.isEmpty(txlj)){
            holder.infoTx.setImageResource(R.drawable.ic_launcher);
        }else{
            Glide.with(context)
                    .load(ConstantTools.baseUrl+txlj)
                    .into(holder.infoTx);
        }
        holder.infoNc.setText(nc);
        holder.infoDate.setText(date);
        if (TextUtils.isEmpty(gqct)){
            holder.infoGqct.setImageResource(R.drawable.ic_launcher);
        }else{
            Glide.with(context)
                    .load(ConstantTools.baseUrl+gqct)
                    .into(holder.infoGqct);
        }
        holder.infoGqmc.setText(gqmc);
        holder.infoZzxh.setText(zzxh);

        //通过为条目设置点击事件触发回调
        holder.infoTx.setOnClickListener(v ->{
            Intent i = new Intent(context, UserInfoActivity.class);
            i.putExtra("xh", dongtai.getZzxh());
            i.putExtra("nc", dongtai.getNc());
            i.putExtra("txlj", dongtai.getTxlj());
            i.putExtra("sr", dongtai.getSr());
            i.putExtra("gxqm", dongtai.getGxqm());
            context.startActivity(i);
        });
        holder.infoNc.setOnClickListener(v->{
            Intent i = new Intent(context, UserInfoActivity.class);
            i.putExtra("xh", dongtai.getZzxh());
            i.putExtra("nc", dongtai.getNc());
            i.putExtra("txlj", dongtai.getTxlj());
            i.putExtra("sr", dongtai.getSr());
            i.putExtra("gxqm", dongtai.getGxqm());
            context.startActivity(i);
        });
        holder.cardView.setOnClickListener(v->{
            Music music = dongtaiList.get(position);
            MusicInfoActivity.start(context, music);
        });
        if (position == dongtaiList.size()-1){
            holder.more.setVisibility(View.VISIBLE);
            holder.more.setOnClickListener(v-> mOnItemClickLitener.onItemClick(v));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return dongtaiList.size();
    }

    public static class DongtaiViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        CircleImageView infoTx;
        TextView infoNc;
        TextView infoDate;
        ImageView infoGqct;
        TextView infoGqmc;
        TextView infoZzxh;
        Button more;
        public DongtaiViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.music_card);
            infoTx = itemView.findViewById(R.id.info_tx);
            infoNc = itemView.findViewById(R.id.info_nc);
            infoDate = itemView.findViewById(R.id.info_date);
            infoGqct = itemView.findViewById(R.id.info_gqct);
            infoGqmc = itemView.findViewById(R.id.info_gqmc);
            infoZzxh = itemView.findViewById(R.id.info_zzxh);
            more = itemView.findViewById(R.id.more);
        }
    }
}
