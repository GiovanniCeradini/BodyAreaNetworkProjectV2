package com.example.prova_tirocinio.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.Items;
import com.example.prova_tirocinio.R;
import com.example.prova_tirocinio.databinding.FragmentProvaBinding;
import com.example.prova_tirocinio.databinding.FragmentProvaBindingImpl;

import java.util.ArrayList;
import java.util.List;

public class FragmentProva extends Fragment {

    private static final String TAG = "FragmentProva";
    private RecyclerView mRecyclerView;
    //dichiarato per passarlo alla lista
    private ItemAdapter mItemAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentProvaBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prova, container, false);
        View v = binding.getRoot();
        mRecyclerView=binding.recyclerViewProva;

        //RecyclerView requires's a LayoutManagaer to work, if they don't have one, there will be a crash
        //LinearLayout list the items vertically
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //metodo di supporto
        updateUI();

        return v;

    }

    private void updateUI(){

        //qui ottieni i singoli elementi da mostrare nella recyclerview

        Items m1=new Items("tit1","dat1");
        Items m2=new Items("tit2","dat2");

        List<Items> list=new ArrayList<Items>();
        list.add(m1);
        list.add(m2);
        Log.d(TAG, "updateUI: "+list.toString());


        //da conservare

        mItemAdapter=new ItemAdapter(list);
        mRecyclerView.setAdapter(mItemAdapter);
    }




    //HOLDER, "responsabile" di un singolo item
    //responsabile anche del binding con i widget

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Items mItem;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item,parent,false));
            itemView.setOnClickListener(this);

            mTitleTextView=(TextView) itemView.findViewById(R.id.item_title);
            mDateTextView=(TextView) itemView.findViewById(R.id.item_data);
        }

        //this bind() will be called each time a new item should be displayed in my ItemHolder

        public void bind(Items item){
            mItem=item;
            mTitleTextView.setText(item.getTitolo());
            mDateTextView.setText(item.getData());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),mItem.getTitolo()+" clicked",Toast.LENGTH_SHORT).show();
        }
    }

    //ADAPTER
    //responsible for: creating the necessary ViewHolders and binding ViewHolders to data from model layer


    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder>{

        private List<Items> itemsList;

        public ItemAdapter(List<Items> itemsList){
            this.itemsList=itemsList;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());

            return new ItemHolder(layoutInflater,parent);
        }

        //request that a given ItemHolder be bound to a particular Item
        //it must be small ad efficient
        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            Items item=itemsList.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return itemsList.size();
        }
    }
}
