package com.ph41626.pma101_recipesharingapplication.Services;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.ph41626.pma101_recipesharingapplication.R;

public class VideoDialogUtil {
    public static void ShowVideoDialog(Context context,String url) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_video_player, null);

        final SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
        PlayerView playerView = dialogView.findViewById(R.id.player_view);
        playerView.setPlayer(player);

        Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
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

        dialog.setOnDismissListener(dialogInterface -> {
            if (player != null) {
                player.release();
            }
        });

        dialog.show();

        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);

        player.prepare();
        player.play();
    }
}
