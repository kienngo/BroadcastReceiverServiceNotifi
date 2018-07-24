package com.example.admin.myapplication.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.Music;
import com.example.admin.myapplication.presenter.listener.OnClickItemView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHoler> {
    private final int INIT_INDEX = 0;

    private List<Music> mLstMusic;
    private Context mContext;
    private OnClickItemView mOnClickItemView;

    public MusicAdapter(List<Music> lstMusic, Context context, OnClickItemView onClickItemView) {
        this.mLstMusic = lstMusic;
        this.mContext = context;
        this.mOnClickItemView = onClickItemView;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music, parent, false);
        return new MyViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, int position) {
        Music music = mLstMusic.get(position);

        holder.mTextAlbum.setText(music.getmAuthorSong());
        holder.mTextTitle.setText(music.getmNameSong());
    }

    @Override
    public int getItemCount() {
        return mLstMusic != null ? mLstMusic.size() : INIT_INDEX;
    }

    class MyViewHoler extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_album)
        private TextView mTextAlbum;
        @BindView(R.id.textview_title)
        private TextView mTextTitle;
        @BindView(R.id.image_music)
        private ImageView mImageMusic;
        @BindView(R.id.view_item)
        private LinearLayout mViewItem;

        public MyViewHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickItemView.onClickItem(mViewItem, getLayoutPosition());
                }
            });
        }
    }
}
