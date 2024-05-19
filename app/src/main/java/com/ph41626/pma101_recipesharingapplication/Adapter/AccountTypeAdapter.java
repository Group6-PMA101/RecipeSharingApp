package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ph41626.pma101_recipesharingapplication.R;

public class AccountTypeAdapter extends ArrayAdapter<CharSequence> {
    private Context context;
    private CharSequence[] mData;
    public AccountTypeAdapter(Context context, int resource, CharSequence[] objects) {
        super(context, resource, objects);
        this.mData = objects;
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_type,parent,false);
        }
        TextView tv_content = convertView.findViewById(R.id.tv_content);
        tv_content.setText(mData[position]);
        tv_content.setTextColor(context.getResources().getColor(R.color.white_000));
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_down);
        drawable.setColorFilter(ContextCompat.getColor(context, R.color.white_000), PorterDuff.Mode.SRC_IN);
        tv_content.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                drawable,
                null);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_type,parent,false);
        }
        TextView tv_content = convertView.findViewById(R.id.tv_content);
        tv_content.setText(mData[position]);
        return convertView;
    }
}
