package com.example.fastdownloader.sample;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastdownloader.R;
import com.example.fastdownloader.database.DownloadModel;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder>{
    private Context context;
    private List<DownloadModel> list;

    public DownloadAdapter(Context context, List<DownloadModel> list){
        this.list=list;
        this.context=context;
    }
    @NonNull
    @Override
    public DownloadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.download_item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadAdapter.ViewHolder holder, int position) {
      DownloadModel model=list.get(position);
      int pr=(int)((model.getDownloadedBytes()*1000)/model.getTotalBytes());
      holder.progressBarOne.setProgress(pr);
      switch (model.getStatus()){
          case RUNNING:
              Log.e("dlkdkkdkd","running");
              holder.pause.setVisibility(View.VISIBLE);
              holder.start.setVisibility(View.GONE);
              holder.resume.setVisibility(View.GONE);
              holder.cancel.setVisibility(View.GONE);
              holder.textViewProgressOne.setVisibility(View.VISIBLE);
              holder.textViewProgressOne.setText(model.getDownloadedBytes()+"/"+model.getTotalBytes());
              break;
          case COMPLETED:
              Log.e("dlkdkkdkd","COMPLETED");
              holder.pause.setVisibility(View.GONE);
              holder.start.setVisibility(View.GONE);
              holder.resume.setVisibility(View.GONE);
              holder.cancel.setVisibility(View.GONE);
              holder.textViewProgressOne.setVisibility(View.VISIBLE);
              holder.textViewProgressOne.setText(model.getDownloadedBytes()+"/"+model.getTotalBytes());
              break;
          case UNKNOWN:
              Log.e("dlkdkkdkd","UNKNOWN");
              holder.pause.setVisibility(View.GONE);
              holder.start.setVisibility(View.GONE);
              holder.resume.setVisibility(View.GONE);
              holder.cancel.setVisibility(View.GONE);
              holder.textViewProgressOne.setVisibility(View.VISIBLE);
              holder.textViewProgressOne.setText(model.getDownloadedBytes()+"/"+model.getTotalBytes());
              break;
          case PAUSED:
              Log.e("dlkdkkdkd","PAUSED");
          case FAILED:
              Log.e("dlkdkkdkd","FAILED");
          case CANCELLED:
              Log.e("dlkdkkdkd","FAILED");
          default:
              Log.e("dlkdkkdkd","Qued");
              holder.pause.setVisibility(View.GONE);
              holder.start.setVisibility(View.GONE);
              holder.resume.setVisibility(View.VISIBLE);
              holder.cancel.setVisibility(View.GONE);
              holder.textViewProgressOne.setVisibility(View.VISIBLE);
              holder.textViewProgressOne.setText(model.getDownloadedBytes()+"/"+model.getTotalBytes());
              break;

      }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void refresh(List<DownloadModel> list) {
        this.list=list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button start,pause,resume,cancel;
        private TextView textViewProgressOne;
        private ProgressBar progressBarOne;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            start=itemView.findViewById(R.id.buttonStart);
            pause=itemView.findViewById(R.id.buttonPause);
            resume=itemView.findViewById(R.id.buttonResum);
            cancel=itemView.findViewById(R.id.buttonCancel);
            textViewProgressOne=itemView.findViewById(R.id.textViewProgressOne);
            progressBarOne=itemView.findViewById(R.id.progressBarOne);

        }
    }
}
