package com.xiyoumusic.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiyoumusic.app.R;

import java.util.List;

public class SuggestionAdapter extends BaseAdapter {
    public List<String> suggestions;
    public Context context;
    public LayoutInflater layoutInflater;
    public ViewHolder viewHolder;

    public SuggestionAdapter(Context context, List<String> data){
        this.context=context;
        this.suggestions=data;
        this.layoutInflater=LayoutInflater.from(context);
    }
    public void refresh(List<String> data){
        suggestions = data;
        notifyDataSetChanged();
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.search_suggestion_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.suggestionText = convertView.findViewById(R.id.text1);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.suggestionText.setText(suggestions.get(position));
        return convertView;
    }

    class ViewHolder{
        TextView suggestionText;
    }
}
