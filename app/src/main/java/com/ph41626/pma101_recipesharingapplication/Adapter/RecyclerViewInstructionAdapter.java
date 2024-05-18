package com.ph41626.pma101_recipesharingapplication.Adapter;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.findObjectById;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ph41626.pma101_recipesharingapplication.Fragment.CreateRecipeFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewInstructionAdapter extends RecyclerView.Adapter<RecyclerViewInstructionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Instruction> instructions;
    private CreateRecipeFragment createRecipeFragment;

    public void UpdateData(boolean type,ArrayList<Instruction> instructions, int pos) {
        this.instructions = instructions;
        if (type) notifyItemInserted(pos);
        else {
            notifyItemRemoved(pos);
            for (int i = pos; i < instructions.size(); i++) {
                notifyItemChanged(i);
            }
        }

    }
    public void Reset(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
        notifyDataSetChanged();
    }
    public RecyclerViewInstructionAdapter(Context context, ArrayList<Instruction> instructions, CreateRecipeFragment createRecipeFragment) {
        this.context = context;
        this.instructions = instructions;
        this.createRecipeFragment = createRecipeFragment;
    }

    @NonNull
    @Override
    public RecyclerViewInstructionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instruction,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewInstructionAdapter.ViewHolder holder, int position) {
        Instruction instruction = instructions.get(position);
        holder.layout_image.removeAllViews();
        instruction.setOrder(holder.getAdapterPosition() + 1);
        holder.tv_order.setText(String.valueOf(instruction.getOrder()));
        holder.edt_content.setText(instruction.getContent());
        if (instruction.getContent().trim().isEmpty()) holder.edt_content.setError("Content cannot be empty!");
        GetContent(holder,instruction);
        holder.btn_remove_item_instruction.setOnClickListener(v -> {
            createRecipeFragment.ShowRemoveInstructionDialog(holder.getAdapterPosition(),instruction);
        });
        holder.btn_add_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecipeFragment.ChooseImage(instruction,holder.getAdapterPosition());
            }
        });
        if (instruction.getMediaIds() != null && !instruction.getMediaIds().isEmpty()) {
            for (String mediaId:instruction.getMediaIds()) {
                Media media = findObjectById(createRecipeFragment.mediaList,mediaId);
                View view = LayoutInflater.from(context).inflate(R.layout.item_instruction_thumbnail,null,false);
                ImageView img = view.findViewById(R.id.img_thumbnail);
                LinearLayout btn_remove_thumbnail = view.findViewById(R.id.btn_remove_thumbnail);
                btn_remove_thumbnail.setOnClickListener(v -> {
                    createRecipeFragment.RemoveItemThumbnail(mediaId,instruction,holder.getAdapterPosition());
                });
                Glide.with(context).load(media.getUrl()).error(R.drawable.add_image).into(img);
                holder.layout_image.addView(view,0);
            }
        }
    }
    private void GetContent(final ViewHolder holder,final Instruction instruction) {
        holder.edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                holder.edt_content.setError(null);
                if (!s.toString().isEmpty()) {
                    instruction.setContent(s.toString());
                } else {
                    holder.edt_content.setError("Content cannot be empty!");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructions != null ? instructions.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order;
        Button btn_remove_item_instruction,btn_add_media;
        EditText edt_content;
        LinearLayout layout_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edt_content = itemView.findViewById(R.id.edt_content);
            tv_order = itemView.findViewById(R.id.tv_order);
            btn_remove_item_instruction = itemView.findViewById(R.id.btn_remove_item_instruction);
            btn_add_media = itemView.findViewById(R.id.btn_add_media);
            layout_image = itemView.findViewById(R.id.layout_image);
        }
    }
}
