package com.xiyoumusic.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.emoji.widget.EmojiTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.ModifyUserInfoActivity;
import com.xiyoumusic.app.activitys.UserInfoActivity;
import com.xiyoumusic.app.entity.CommentBean;
import com.xiyoumusic.app.entity.HuiFu;
import com.xiyoumusic.app.entity.HuifuDetails;
import com.xiyoumusic.app.entity.PingLun;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.DateFormat;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.views.CircleImageView;

import java.util.List;

public class PinglunAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "PinglunAdapter";
    private List<CommentBean> commentBeans;
    private Context context;
    private PinglunOnMoreClickListener listener;

    public PinglunAdapter(List<CommentBean> commentBeans, Context context) {
        this.commentBeans = commentBeans;
        this.context = context;
    }

    public void setOnMoreClickListener(PinglunOnMoreClickListener listener) {
        this.listener = listener;
    }

    public void refresh(List<CommentBean> commentBeans){
        this.commentBeans = commentBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return commentBeans.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(commentBeans.get(groupPosition).getHuiFuList() == null){
            return 0;
        }else {
            return commentBeans.get(groupPosition).getHuiFuList().size()>0 ? commentBeans.get(groupPosition).getHuiFuList().size():0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return commentBeans.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return commentBeans.get(groupPosition).getHuiFuList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolder groupHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_pinglun, parent, false);
            groupHolder = new ViewHolder(convertView);
            convertView.setTag(groupHolder);
        }else {
            groupHolder = (ViewHolder) convertView.getTag();
        }
        CommentBean commentBean = commentBeans.get(groupPosition);

        if(!TextUtils.isEmpty(commentBean.getTxlj())) {
            Glide.with(context)
                    .load(ConstantTools.baseUrl + commentBean.getTxlj())
                    .into(groupHolder.infoTx);
        }else{
            groupHolder.infoTx.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
        }
        groupHolder.infoTx.setOnClickListener(v -> {
            if (commentBean.getPlzxh().equals(SPTool.getSharedPreferences().getString("xh", "000000000000"))){
                jumpToModifyUser();
            }else {
                jumpToUserInfo(commentBean.getPlzxh()
                        , commentBean.getPlznc()
                        , commentBean.getTxlj()
                        , commentBean.getSr()
                        , commentBean.getGxqm());
            }
        });
        groupHolder.infoNc.setText(commentBean.getPlznc());
        groupHolder.infoNc.setOnClickListener(v -> {
            if (commentBean.getPlzxh().equals(SPTool.getSharedPreferences().getString("xh", "000000000000"))){
                jumpToModifyUser();
            }else {
                jumpToUserInfo(commentBean.getPlzxh()
                        , commentBean.getPlznc()
                        , commentBean.getTxlj()
                        , commentBean.getSr()
                        , commentBean.getGxqm());
            }
        });
        groupHolder.infoDate.setText(DateFormat.getDateFromID(commentBean.getPlid(), DateFormat.FORMAT_YYYY_MM_DD_HH_MM_SS));
        groupHolder.infoHfnr.setText(commentBean.getPlnr());
        groupHolder.ivMore.setOnClickListener(v -> {
            if (listener != null){
                listener.onMoreClick(groupPosition, -1, v);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder childHolder;
        if(convertView == null){

            int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    32, context.getResources().getDisplayMetrics());

            convertView = LayoutInflater.from(context).inflate(R.layout.layout_pinglun,parent, false);
            convertView.setPadding(left,0,0,0);
            childHolder = new ViewHolder(convertView);
            convertView.setTag(childHolder);
        }
        else {
            childHolder = (ViewHolder) convertView.getTag();
        }
        HuifuDetails huifuDetails = commentBeans.get(groupPosition).getHuiFuList().get(childPosition);

        if(!TextUtils.isEmpty(huifuDetails.getHfztxlj())){
            Glide.with(context)
                    .load(ConstantTools.baseUrl + huifuDetails.getHfztxlj())
                    .into(childHolder.infoTx);
        }else{
            childHolder.infoTx.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
        }
        childHolder.infoTx.setOnClickListener(v -> {
            if (huifuDetails.getHfzxh().equals(SPTool.getSharedPreferences().getString("xh", "000000000000"))){
                jumpToModifyUser();
            }else {
                jumpToUserInfo(huifuDetails.getHfzxh()
                        , huifuDetails.getHfznc()
                        , huifuDetails.getHfztxlj()
                        , huifuDetails.getHfzsr()
                        , huifuDetails.getHfzgxqm());
            }
        });
        childHolder.infoNc.setText(huifuDetails.getHfznc());
        childHolder.infoNc.setOnClickListener(v -> {
            if (huifuDetails.getHfzxh().equals(SPTool.getSharedPreferences().getString("xh", "000000000000"))){
                jumpToModifyUser();
            }else {
                jumpToUserInfo(huifuDetails.getHfzxh()
                        , huifuDetails.getHfznc()
                        , huifuDetails.getHfztxlj()
                        , huifuDetails.getHfzsr()
                        , huifuDetails.getHfzgxqm());
            }
        });
        childHolder.infoDate.setText(DateFormat.getDateFromID(huifuDetails.getHfid(), DateFormat.FORMAT_YYYY_MM_DD_HH_MM_SS));

        SpannableString spanText=new SpannableString("@"+huifuDetails.getBhfznc()+" "+huifuDetails.getHfnr());
        spanText.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#2196F3"));       //设置文件颜色
                ds.setUnderlineText(false);      //设置下划线
            }

            @Override
            public void onClick(View view) {
                jumpToUserInfo(huifuDetails.getBhfzxh()
                        , huifuDetails.getBhfznc()
                        , huifuDetails.getBhfztxlj()
                        , huifuDetails.getBhfzsr()
                        , huifuDetails.getBhfzgxqm());
            }
        }, 0, huifuDetails.getBhfznc().length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        warnText.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        childHolder.infoHfnr.setText(spanText);
        childHolder.infoHfnr.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件

        childHolder.ivMore.setOnClickListener(v -> {
            if (listener != null){
                listener.onMoreClick(groupPosition, childPosition, v);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private class ViewHolder{
        private CircleImageView infoTx;
        private TextView infoNc;
        private TextView infoDate;
        private EmojiTextView infoHfnr;
        private ImageView ivMore;
        public ViewHolder(View view){
            infoTx = view.findViewById(R.id.info_tx);
            infoNc = view.findViewById(R.id.info_nc);
            infoDate = view.findViewById(R.id.info_date);
            infoHfnr = view.findViewById(R.id.info_hfnr);
            ivMore = view.findViewById(R.id.iv_more);
        }
    }

    public void jumpToUserInfo(String xh, String nc, String txlj, String sr, String gxqm){
        Intent i = new Intent(context, UserInfoActivity.class);
        i.putExtra("xh", xh);
        i.putExtra("nc", nc);
        i.putExtra("txlj", txlj);
        i.putExtra("sr", sr);
        i.putExtra("gxqm", gxqm);
        context.startActivity(i);
    }

    public void jumpToModifyUser(){
        Intent i = new Intent(context, ModifyUserInfoActivity.class);
        context.startActivity(i);
    }
}
