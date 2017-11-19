package summerland.twilight.tamadoggy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

import static summerland.twilight.tamadoggy.R.layout.inventory_item_layout;

/**
 * Created by twilightsummerland on 2017-11-07.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private ArrayList<Const.CurrentItems> currentItems;

    public ItemAdapter(HashMap<Integer, Const.CurrentItems> items)
    {
        currentItems = new ArrayList<Const.CurrentItems>(items.values());
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(inventory_item_layout,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.populate(currentItems, getItemCount());
    }

    @Override
    public int getItemCount()
    {
        return currentItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ArrayList<Const.CurrentItems> m_currentItems;
        int m_size;
        TextView m_itemName, m_itemAmount;
        Button m_buttonUse;
        Context m_context;
        public MyViewHolder(View itemView) {
            super(itemView);
            m_itemName = itemView.findViewById(R.id.textItemName);
            m_itemAmount = itemView.findViewById(R.id.textItemAmount);
            m_buttonUse = itemView.findViewById(R.id.buttonItemUse);
            m_context = itemView.getContext();
        }
        public void populate(ArrayList<Const.CurrentItems> items, int size)
        {
            m_size = size;
            m_currentItems = items;
            Const.CurrentItems item = items.get(getAdapterPosition());
            if(item != null)
            {
                m_itemName.setText(item.itemName);
                m_itemAmount.setText(""+item.amount);
                m_buttonUse.setOnClickListener(this);
            }
        }
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.buttonItemUse)
            {
                Const.CurrentItems item = m_currentItems.get(getAdapterPosition());
                Toast.makeText(m_context,
                        "You have clicked " + item.itemName,Toast.LENGTH_SHORT).show();
                int val = item.amount;
                if(val < 2)
                {
                    m_currentItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), m_size);
                    m_size --;
                }
                else{
                    item.amount = item.amount - 1;
                    notifyItemChanged(getAdapterPosition());
                }


            }
        }
    }
}
