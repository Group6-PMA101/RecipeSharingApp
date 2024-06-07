package com.ph41626.pma101_recipesharingapplication.Services;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.ph41626.pma101_recipesharingapplication.R;

public class ImageDialogUtil {
    public static void ShowVideoDialog(Context context, String url) {
        ImageView imageViewDetail = new ImageView(context);
        imageViewDetail.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        Glide.with(context).
                load(url).
                error(R.drawable.caption).
                placeholder(R.drawable.caption).
                into(imageViewDetail);
        imageViewDetail.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.addContentView(imageViewDetail, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(context.getDrawable(R.color.black_000));

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        dialog.show();
    }
}
