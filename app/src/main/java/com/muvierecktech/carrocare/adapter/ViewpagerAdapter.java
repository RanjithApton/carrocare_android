package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.ConfirmFormActivity;
import com.muvierecktech.carrocare.activity.DoorStepServiceActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.model.PagerModel;

import java.util.List;

public class ViewpagerAdapter extends RecyclerView.Adapter<ViewpagerAdapter.MyViewHolder> {

    private List<PagerModel> pagerModelList;
    private LayoutInflater layoutInflater;
    private Context context;
    private ViewPager2 viewPager2;


    public ViewpagerAdapter(List<PagerModel> pagerModelList, Context context, ViewPager2 viewPager2) {
        this.pagerModelList = pagerModelList;
        this.context = context;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_viewpager, parent,false);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.imageView.setImageResource(pagerModelList.get(position).getImage());
        holder.textView.setText(pagerModelList.get(position).getTitle());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.textView.getText().toString().equalsIgnoreCase("Door step car wash")){
                    ((DoorStepServiceActivity)context).showPopup("carwash");
//                    Constant.ONETIME_PACK_TYPE = "Door step car wash";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step Wash";
                }else if(holder.textView.getText().toString().equalsIgnoreCase("Detailing")){
                    ((DoorStepServiceActivity)context).showPopup("detailing");
//                    Constant.ONETIME_PACK_TYPE = "Door step bike wash";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step Wash";
                }else if(holder.textView.getText().toString().equalsIgnoreCase("Painting & Denting")){
                    ((DoorStepServiceActivity)context).showPopup("painting");
//                    Constant.ONETIME_PACK_TYPE = "Door step wax polish";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step AddOn";
                }
                else if(holder.textView.getText().toString().equalsIgnoreCase("Battery change")){
                    ((DoorStepServiceActivity)context).showPopup("battery");
//                    Constant.ONETIME_PACK_TYPE = "Door step wax polish";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step AddOn";
                }
//                else if(textView.getText().toString().equalsIgnoreCase("Door step car polish")){
//                    context.startActivity(new Intent(context, ConfirmFormActivity.class));
//                }
                else if(holder.textView.getText().toString().equalsIgnoreCase("Door step insurance")){
                    context.startActivity(new Intent(context, ConfirmFormActivity.class).putExtra("headername", Constant.INSURANCE));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pagerModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        CardView cardView;

       public MyViewHolder(@NonNull View itemView) {
           super(itemView);
           cardView = itemView.findViewById(R.id.card);
           imageView = itemView.findViewById(R.id.image);
           textView = itemView.findViewById(R.id.title);
       }
   }
}
