package com.josezamora.tcscanner.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.josezamora.tcscanner.Classes.Composition;
import com.josezamora.tcscanner.Classes.IOCompositionsController;
import com.josezamora.tcscanner.R;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CompositionsRecyclerAdapter extends
        RecyclerView.Adapter<CompositionsRecyclerAdapter.CompositionsViewHolder>
        implements Filterable {

    private RecyclerViewOnClickInterface recyclerViewOnClick;
    private List<Composition> allCompositionsList;
    private IOCompositionsController compositionsController;

    public static final int LIST_ITEM = R.layout.list_composition_item;
    public static final int GRID_ITEM = R.layout.grid_composition_item;

    private int viewMode;

    public CompositionsRecyclerAdapter(IOCompositionsController compositionsController,
                                       RecyclerViewOnClickInterface recyclerViewOnClick) {

        this.compositionsController = compositionsController;
        this.recyclerViewOnClick = recyclerViewOnClick;
        this.allCompositionsList = new ArrayList<>(compositionsController.getCompositions());

        viewMode = LIST_ITEM;

    }

    public void switchViewMode(int type) {
        viewMode = type;
    }

    @NonNull
    @Override
    public CompositionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(viewMode, parent, false);

        return new CompositionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompositionsViewHolder holder, int position) {

        String name = compositionsController.getCompositions().get(position).getName();
        if (viewMode != LIST_ITEM) {
            if (name.length() >= 10) {
                name = new StringBuffer(name).substring(0, 9);
                name += "...";
            }
        }

        holder.txtName.setText(name);

        String numImages = String.valueOf(compositionsController.getCompositions().get(position).getNumImages());
        holder.txtImages.setText(numImages + "/10 Imágenes");

    }

    @Override
    public int getItemCount() {
        return compositionsController.getCompositions().size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Composition> filteredCompositions = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if(constraint.toString().isEmpty())
                filteredCompositions.addAll(allCompositionsList);
            else
                for(Composition c : allCompositionsList)
                    if(c.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredCompositions.add(c);

            filterResults.values = filteredCompositions;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            compositionsController.getCompositions().clear();
            compositionsController.getCompositions().addAll((List<Composition>) results.values);

            notifyDataSetChanged();
        }
    };

    class CompositionsViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView txtName;
        TextView txtImages;

        CompositionsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imagePreview);
            txtImages = itemView.findViewById(R.id.textViewImages);
            txtName = itemView.findViewById(R.id.textviewName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewOnClick.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerViewOnClick.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
