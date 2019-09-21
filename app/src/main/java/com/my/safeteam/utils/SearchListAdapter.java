package com.my.safeteam.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;

import java.util.List;

public class SearchListAdapter extends BaseAdapter {
    private Context context;
    private List<User> users;

    public SearchListAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView email;
        protected ImageView avatar;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = View.inflate(context, R.layout.user_list_team, null);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.member_name);
            viewHolder.email = view.findViewById(R.id.member_email);
            viewHolder.avatar = view.findViewById(R.id.member_avatar);

            view.setTag(viewHolder);
            view.setTag(R.id.member_name, viewHolder.name);
            view.setTag(R.id.member_email, viewHolder.email);
            view.setTag(R.id.member_avatar, viewHolder.avatar);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(users.get(i).getName());
        viewHolder.email.setText(users.get(i).getEmail());

        Glide.with(view.getContext().getApplicationContext()).load(users.get(i).getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(viewHolder.avatar);
        return view;
    }
}
