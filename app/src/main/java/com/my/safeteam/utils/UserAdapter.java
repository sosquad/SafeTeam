package com.my.safeteam.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> users;


    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    public boolean isChecked(int i) {
        return users.get(i).isSelected();
    }

    public void update(List<User> u) {
        users = new ArrayList<>();
        users.addAll(u);
        notifyDataSetChanged();
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int index = i;
        View v = View.inflate(context, R.layout.item_lista_usuario, null);
        TextView tv = v.findViewById(R.id.nameuserlist);
        ImageView iv = v.findViewById(R.id.imageuserlist);
        CheckBox cb = v.findViewById(R.id.addUserToGroup);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                users.get(index).setSelected();
            }
        });
        if (users.get(i).isSelected()) {
            cb.setChecked(true);
        }
        tv.setText(users.get(i).getName());
        Glide.with(v).load(users.get(i).getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(iv);
        v.setTag(users.get(i).getEmail());

        return v;
    }
}
