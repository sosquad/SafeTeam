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
    private List<User> staticUsers;
    private boolean isStatic;

    public UserAdapter(Context context, List<User> users, boolean isStatic) {
        this.context = context;
        this.users = users;
        this.isStatic = isStatic;
        if (isStatic) {
            staticUsers = users;
        }
    }
    public boolean isChecked(int i) {
        return users.get(i).isSelected();
    }

    public void update(List<User> u) {
        List<User> filteredList = new ArrayList<>();
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

    static class ViewHolder {
        protected TextView name;
        protected ImageView avatar;
        protected CheckBox checkBox;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = View.inflate(context, R.layout.item_lista_usuario, null);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.nameuserlist);
            viewHolder.checkBox = view.findViewById(R.id.addUserToGroup);
            viewHolder.avatar = view.findViewById(R.id.imageuserlist);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int getPosition = (Integer) compoundButton.getTag();
                    users.get(getPosition).setSelected(b);
                }
            });

            view.setTag(viewHolder);
            view.setTag(R.id.nameuserlist, viewHolder.name);
            view.setTag(R.id.imageuserlist, viewHolder.avatar);
            view.setTag(R.id.addUserToGroup, viewHolder.checkBox);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.checkBox.setTag(i);
        viewHolder.name.setText(users.get(i).getName());
        viewHolder.checkBox.setChecked(users.get(i).isSelected());
        Glide.with(view).load(users.get(i).getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(viewHolder.avatar);
        return view;
    }
}
