package com.xiyoumusic.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.views.CircleImageView;

import java.util.List;
import java.util.Map;

public class UserInfoAdapter extends BaseAdapter {
    private List<Map<String, String>> userInfo;
    private LayoutInflater layoutInflater;
    private Context context;
    public ViewHolder viewHolder;

    public UserInfoAdapter(Context context,List<Map<String, String>> data){
        this.context=context;
        this.userInfo=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public void refresh(List<Map<String, String>> data){
        userInfo = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.user_info_list, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.infoName = view.findViewById(R.id.info_name);
            viewHolder.infoImage = view.findViewById(R.id.info_image);
            viewHolder.infoText = view.findViewById(R.id.info_text);
            viewHolder.modify = view.findViewById(R.id.modify);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        String key = userInfo.get(i).keySet().iterator().next();
        String value = userInfo.get(i).get(key);
        if ("txlj".equals(key)){
            viewHolder.infoName.setText("头像");
            viewHolder.infoText.setVisibility(View.GONE);
            if ("".equals(value)){
                viewHolder.infoImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_foreground));
            }else{
                Glide.with(context)
                        .load(ConstantTools.baseUrl+value)
                        .into(viewHolder.infoImage);
            }
            viewHolder.infoImage.setVisibility(View.VISIBLE);
            viewHolder.modify.setVisibility(View.VISIBLE);
        }else{
            if ("xh".equals(key)){
                viewHolder.infoName.setText("学号");
                viewHolder.infoImage.setVisibility(View.GONE);
                viewHolder.modify.setVisibility(View.GONE);
                viewHolder.infoText.setText(value);
            }else {
                switch (key){
                    case "nc":
                        viewHolder.infoName.setText("昵称");
                        viewHolder.infoImage.setVisibility(View.GONE);
                        viewHolder.infoText.setText(value);
                        break;
                    case "sr":
                        viewHolder.infoName.setText("生日");
                        viewHolder.infoImage.setVisibility(View.GONE);
                        viewHolder.infoText.setText(value);
                        break;
                    case "gxqm":
                        viewHolder.infoName.setText("个性签名");
                        viewHolder.infoImage.setVisibility(View.GONE);
                        viewHolder.infoText.setText(("".equals(value)?"该用户有点懒，没有写个性签名":value));
                        break;
                    default:break;
                }
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        return userInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return userInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    class ViewHolder{
        TextView infoName;
        CircleImageView infoImage;
        TextView infoText;
        ImageView modify;
    }
}
