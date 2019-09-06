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

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.item_lista_usuario, null);
        TextView tv = v.findViewById(R.id.nameuserlist);
        ImageView iv = v.findViewById(R.id.imageuserlist);
        System.out.println(users.get(i).getName());
        tv.setText(users.get(i).getName());
        Glide.with(v).load(users.get(i).getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(iv);
        v.setTag(users.get(i).getEmail());

        return v;
    }
}
