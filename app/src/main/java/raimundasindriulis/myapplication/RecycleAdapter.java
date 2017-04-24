package raimundasindriulis.myapplication;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private List<User> items;
    private int itemLayout;
    private Context _context;
    private int lastAnimatedPosition = -1;
    private float x = 0;
    int f;
    static String ROOT_URL = ".ieskok.lt/";


    public RecycleAdapter(List<User> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this._context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        f = ((items.get(position).id%10 <= 5) ? 1 : 2);
        runEnterAnimation(holder.itemView, position);
        holder.text.setText(items.get(position).vardas);
        holder.age.setText(items.get(position).age);
        holder.loco.setText(items.get(position).loco);
        Picasso.with(_context).load("http://f" + f + ROOT_URL + items.get(position).id + ".jpg").into(holder.image);
        holder.setClickListener(new MainActivity.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                ((MainActivity) _context).detailActivity(view, items.get(position).vardas, items.get(position).age, items.get(position).loco, items.get(position).id, f);
            }
        });

    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView text;
        public ImageView image;
        public CardView card;
        public TextView age;
        public TextView loco;
        private MainActivity.ItemClickListener clickListener;
        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView)itemView.findViewById(R.id.title);
            age = (TextView)itemView.findViewById(R.id.age);
            loco = (TextView)itemView.findViewById(R.id.loco);
            image = (ImageView)itemView.findViewById(R.id.image);
            card = (CardView)itemView.findViewById(R.id.rowCard);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public void setClickListener(MainActivity.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }


        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition(), false);
        }
        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }

    }


    public void swap(List<User> data){
        this.items.clear();
        this.items.addAll(data);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    public void removeAt(int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, this.items.size());
    }

    private void runEnterAnimation(View view, int position) {
        if (position >= items.size()) {
            lastAnimatedPosition = -1;
            return;
        }
        if(this.x==0){
            DisplayMetrics metrics = _context.getResources().getDisplayMetrics();
            this.x = (float) metrics.widthPixels;
        }
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationX(x);
            view.animate()
                    .translationX(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(500 + (200*position))
                    .start();
        }
    }

    }







