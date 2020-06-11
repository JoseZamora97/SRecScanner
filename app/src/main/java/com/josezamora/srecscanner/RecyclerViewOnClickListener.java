package com.josezamora.srecscanner;

public interface RecyclerViewOnClickListener {

    /**
     * Method that is called when a item was clicked by user
     *
     * @param position the position of the item.
     */
    void onItemClick(int position);

    /**
     * Method that is called when a item was long clicked by user
     *
     * @param position the position of the item.
     */
    void onLongItemClick(int position);
}
