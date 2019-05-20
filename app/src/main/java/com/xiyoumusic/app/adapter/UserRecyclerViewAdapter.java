package com.xiyoumusic.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.views.CircleImageView;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UsersViewHolder> {
    List<User> users;
    Context context;
    private OnItemClickLitener mOnItemClickLitener;

    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener){
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public UserRecyclerViewAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UserRecyclerViewAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        UsersViewHolder usersViewHolder = new UsersViewHolder(view);
        return usersViewHolder;
    }

    public void onBindViewHolder(@NonNull UsersViewHolder holder, final int position) {
        User user = users.get(position);
        String txlj = user.getTxlj();
        String nc = user.getNc();
        String xh = user.getXh();
        if (txlj == null || "".equals(txlj)){
            holder.tx.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_foreground));
        }else{
            Glide.with(context)
                    .load(ConstantTools.baseUrl+txlj)
                    .into(holder.tx);
        }
        holder.nc.setText(nc);
        holder.xh.setText(xh);

        //通过为条目设置点击事件触发回调
        if (mOnItemClickLitener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public void refresh(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        CircleImageView tx;
        TextView nc;
        TextView xh;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.user_card);
            tx = itemView.findViewById(R.id.info_tx);
            nc = itemView.findViewById(R.id.info_nc);
            xh = itemView.findViewById(R.id.info_xh);
        }
    }
}
