package com.josezamora.tcscanner.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.josezamora.tcscanner.Firebase.Classes.CloudComposition;
import com.josezamora.tcscanner.Interfaces.RecyclerViewOnClickInterface;
import com.josezamora.tcscanner.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


@SuppressWarnings("unchecked")
public class CompositionsRecyclerAdapter extends
        RecyclerView.Adapter<CompositionsRecyclerAdapter.CompositionsViewHolder>
        implements Filterable {

    private RecyclerViewOnClickInterface recyclerViewOnClick;
    private List<CloudComposition> allCompositionsList;

    public static final int LIST_ITEM = R.layout.list_composition_item;
    public static final int GRID_ITEM = R.layout.grid_composition_item;

    private int viewMode;

    public CompositionsRecyclerAdapter(List<CloudComposition> compositions, RecyclerViewOnClickInterface recyclerViewOnClick) {

        this.recyclerViewOnClick = recyclerViewOnClick;
        this.allCompositionsList = compositions;

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

        String name = allCompositionsList.get(position).getName();
        if (viewMode != LIST_ITEM) {
            if (name.length() >= 10) {
                name = new StringBuffer(name).substring(0, 9);
                name += "...";
            }
        }

        holder.txtName.setText(name);
    }

    @Override
    public int getItemCount() {
        return allCompositionsList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<CloudComposition> filteredCompositions = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if(constraint.toString().isEmpty())
                filteredCompositions.addAll(allCompositionsList);
            else
                for(CloudComposition c : allCompositionsList)
                    if(c.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredCompositions.add(c);

            filterResults.values = filteredCompositions;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allCompositionsList.clear();
            allCompositionsList.addAll((List<CloudComposition>) results.values);
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
            txtName = itemView.findViewById(R.id.textviewName);
            txtImages = itemView.findViewById(R.id.textViewImages);

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
