package com.example.recipeexjobb;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.MyViewHolder>  {

    private Context context;
    private int numberOfIngredients;
    private List<IngredientItem> ingredientsList;
    private EventListener listener;

    public interface EventListener{
        void removeIngredient(int position);
        void editIngredientAmount(int position,double amount);
        void editIngredientMeasurement(int position, int unit);
        void editIngredientName(int position, String name);
    }




    public  class MyViewHolder extends RecyclerView.ViewHolder { //removed static from class

        EditText setAmount, setIngredient;
        Spinner setUnit;
        ImageButton removeIngredientButton;

        public MyViewHolder(View view){
            super(view);

            setAmount = view.findViewById(R.id.setAmountTextView);
            setAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(setAmount.getText().toString().length() > 0){
                        listener.editIngredientAmount(getAdapterPosition(), Double.parseDouble(setAmount.getText().toString()));
                    }
                    else {
                        listener.editIngredientAmount(getAdapterPosition(), 0);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            setIngredient = view.findViewById(R.id.setIngredientTextView);
            setIngredient.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.editIngredientName(getAdapterPosition(), setIngredient.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            setUnit = view.findViewById(R.id.setUnitSpinner);
            setUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    listener.editIngredientMeasurement(getAdapterPosition(), setUnit.getSelectedItemPosition());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            removeIngredientButton = view.findViewById(R.id.removeIngredientButton);

            removeIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.removeIngredient(getAdapterPosition());
                }
            });

        }
    }

    public AddIngredientAdapter(Context context, List<IngredientItem> ingredientsList, EventListener listener){

        this.context = context;
        this.ingredientsList = ingredientsList;
        this.listener = listener;



    }


    @NonNull
    @Override
    public AddIngredientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_recipe_item_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddIngredientAdapter.MyViewHolder holder, int position) {
        if(ingredientsList != null){

            holder.setAmount.setText(String.valueOf(ingredientsList.get(position).getAmount()));
            holder.setIngredient.setText(ingredientsList.get(position).getIngredient());
            holder.setUnit.setSelection(ingredientsList.get(position).getUnit());

        }



    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }
}
