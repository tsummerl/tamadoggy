package summerland.twilight.tamadoggy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import static summerland.twilight.tamadoggy.R.layout.store_item_layout;
/**
 * Created by Jake on 11/24/2017.
 */

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder>{

    private ArrayList<Const.Items> m_storeItems;

    public StoreAdapter(ArrayList<Const.Items> items){
        m_storeItems = items;
    }
    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(store_item_layout, parent, false);
        StoreAdapter.MyViewHolder viewHolder = new StoreAdapter.MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.populate(m_storeItems, getItemCount());
    }

    @Override
    public int getItemCount() {
        return m_storeItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ArrayList<Const.Items> m_storeItems;
        int m_size;
        TextView m_itemName, m_itemAmount, m_itemCost;
        Button m_buttonBuy;
        Context m_context;
        public MyViewHolder(View itemView) {
            super(itemView);
            m_itemName = itemView.findViewById(R.id.textStoreItemName);
//            m_itemAmount = itemView.findViewById(R.id.textStoreItemAmount);
            m_itemCost = itemView.findViewById(R.id.textStoreItemCost);
            m_buttonBuy = itemView.findViewById(R.id.buttonItemBuy);
            m_context = itemView.getContext();
        }
        public void populate(ArrayList<Const.Items> items, int size)
        {
            m_size = size;
            m_storeItems = items;
            Const.Items item = items.get(getAdapterPosition());
            if(item != null)
            {
                m_itemName.setText(item.itemName);
//                m_itemAmount.setText("Current: x"+item.amount);
                m_itemCost.setText("Cost: " + item.cost);
                m_buttonBuy.setOnClickListener(this);
            }
        }
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.buttonItemBuy)
            {
                try{
                    Const.Items item = m_storeItems.get(getAdapterPosition());
                    int val = item.amount;
                    int itemID = item.id;
                    if(val < 2)
                    {
                        m_storeItems.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), m_size);
                        m_size --;
                        val = 0;
                    }
                    else{
                        item.amount = item.amount - 1;
                        notifyItemChanged(getAdapterPosition());
                        val = item.amount;
                    }
                    ((MainGameActivity)m_context).buyItem(itemID, val);
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    Log.d("STORE ADPATER", "array out of bounds, click to fast?");
                }
            }
        }
    }
}
