package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Fragment.CreateRecipeFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewIngredientAdapter extends RecyclerView.Adapter<RecyclerViewIngredientAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Ingredient> ingredients;
    private CreateRecipeFragment createRecipeFragment;

    public void UpdateData(boolean type,ArrayList<Ingredient> ingredients, int pos) {
        this.ingredients = ingredients;
        if (type) notifyItemInserted(pos); else notifyItemRemoved(pos);
    }
    public void Reset(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }
    public RecyclerViewIngredientAdapter(Context context, ArrayList<Ingredient> ingredients,CreateRecipeFragment createRecipeFragment) {
        this.context = context;
        this.ingredients = ingredients;
        this.createRecipeFragment = createRecipeFragment;
    }

    @NonNull
    @Override
    public RecyclerViewIngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewIngredientAdapter.ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.edt_name.setText(ingredient.getName());
        holder.edt_quantity.setText(String.valueOf(ingredient.getMass()));
        if (ingredient.getName().trim().isEmpty()) holder.edt_name.setError("Name cannot be empty!");
        GetName(holder,ingredient);
        GetMass(holder,ingredient);
        holder.btn_remove_item_ingredient.setOnClickListener(v -> {
            createRecipeFragment.RemoveItemIngredient(holder.getAdapterPosition());
        });
    }
    private void GetMass(final ViewHolder holder,final Ingredient ingredient) {
        holder.edt_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                holder.edt_quantity.setError(null);
                if (!s.toString().isEmpty()) {
                    try {
                        ingredient.setMass(Float.parseFloat(s.toString()));
                    } catch (NumberFormatException e) {
                        holder.edt_quantity.setError("Please enter a valid quantity!");
                    }
                } else {
                    ingredient.setMass(0);
                }
            }
        });
    }
    private void GetName(final ViewHolder holder,final Ingredient ingredient) {
        holder.edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                holder.edt_name.setError(null);
                if (!s.toString().isEmpty()) {
                    ingredient.setName(s.toString());
                } else {
                    holder.edt_name.setError("Name cannot be empty!");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText edt_name,edt_quantity;
        Button btn_remove_item_ingredient;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edt_name = itemView.findViewById(R.id.edt_name);
            edt_quantity = itemView.findViewById(R.id.edt_quantity);
            btn_remove_item_ingredient = itemView.findViewById(R.id.btn_remove_item_ingredient);
        }
    }
}
