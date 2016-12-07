package com.neogeekscamp.workshop2.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neogeekscamp.workshop2.R;
import com.neogeekscamp.workshop2.model.MessageModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.neogeekscamp.workshop2.model.MessageModel.active;
import static com.neogeekscamp.workshop2.model.MessageModel.inactive;

/**
 * Created by M. Asrof Bayhaqqi on 11/26/2016.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<MessageModel> messageList = new ArrayList<>();
    private ArrayList<MessageModel> messageListSearch = new ArrayList<>();
    private SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");

    private Filter filter;

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ChatFilter();
        return filter;
    }

    private class ChatFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();
            String substr = constraint.toString().toLowerCase();
            if (substr == null || substr.length() == 0) {
                result.values = messageListSearch;
                result.count = messageListSearch.size();
            } else {
                final ArrayList<MessageModel> nlist = new ArrayList<MessageModel>();
                int count = messageListSearch.size();

                for (int i = 0; i<count; i++) {
                    final MessageModel message = messageListSearch.get(i);
                    String value = "", value2 = "";
                    value = message.getUsername().toLowerCase();
                    value2 = message.getMessage().toLowerCase();
                    if (value.contains(substr) || value2.contains(substr)) {
                        nlist.add(message);
                    }
                }
                result.values = nlist;
                result.count = nlist.size();
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            messageList = (ArrayList<MessageModel>) results.values;
            notifyDataSetChanged();
        }
    }


    public ChatAdapter(ArrayList<MessageModel> messageList, Context context) {
        super();
        this.messageList = messageList;
        this.messageListSearch = messageList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case active:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_item, parent, false);
                return new ActiveViewHolder(view);
            case inactive:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inactive_item, parent, false);
                return new InactiveViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final MessageModel message = messageList.get(position);
        if (message != null) {
            switch (message.getType()) {
                case active:
                    ((ActiveViewHolder) holder).tvUsernameActive.setText("" + message.getUsername());
                    if (!message.getMessage().isEmpty() && message.getImage().isEmpty()) {
                        ((ActiveViewHolder) holder).tvMessageActive.setVisibility(View.VISIBLE);
                        ((ActiveViewHolder) holder).ivImageActive.setVisibility(View.GONE);
                        ((ActiveViewHolder) holder).tvMessageActive.setText("" + message.getMessage());
                    }
                    if (!message.getImage().isEmpty() && message.getMessage().isEmpty()) {
                        ((ActiveViewHolder) holder).ivImageActive.setVisibility(View.VISIBLE);
                        ((ActiveViewHolder) holder).tvMessageActive.setVisibility(View.GONE);
                        ((ActiveViewHolder) holder).ivImageActive.setImageBitmap(decodeImage(message.getImage()));
                        ((ActiveViewHolder) holder).listActive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog imageDialog = new Dialog(context, R.style.ImageDialog);
                                imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                imageDialog.setContentView(R.layout.image_dialog);
                                imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.MATCH_PARENT);

                                Toolbar toolbar = (Toolbar) imageDialog.findViewById(R.id.toolbar);
                                toolbar.setNavigationIcon(R.drawable.ic_left_arrow);
                                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        imageDialog.dismiss();
                                    }
                                });
                                ImageView image = (ImageView) imageDialog.findViewById(R.id.image);
                                image.setImageBitmap(decodeImage(message.getImage()));
                                PhotoViewAttacher mAttacher = new PhotoViewAttacher(image);
                                mAttacher.update();
                                imageDialog.show();
                            }
                        });
                    }
                    try {
                        Date dateParse = df1.parse(message.getTime());
                        String dateFormat = df2.format(dateParse);
                        ((ActiveViewHolder) holder).tvTimeActive.setText("" + dateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((ActiveViewHolder) holder).listActive.setTag(position);
                    break;
                case inactive:
                    ((InactiveViewHolder) holder).tvUsernameInactive.setText("" + message.getUsername());
                    if (!message.getMessage().isEmpty() && message.getImage().isEmpty()) {
                        ((InactiveViewHolder) holder).tvMessageInactive.setVisibility(View.VISIBLE);
                        ((InactiveViewHolder) holder).ivImageInactive.setVisibility(View.GONE);
                        ((InactiveViewHolder) holder).tvMessageInactive.setText("" + message.getMessage());
                    }
                    if (!message.getImage().isEmpty() && message.getMessage().isEmpty()) {
                        ((InactiveViewHolder) holder).ivImageInactive.setVisibility(View.VISIBLE);
                        ((InactiveViewHolder) holder).tvMessageInactive.setVisibility(View.GONE);
                        ((InactiveViewHolder) holder).ivImageInactive.setImageBitmap(decodeImage(message.getImage()));
                        ((InactiveViewHolder) holder).listInactive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog imageDialog = new Dialog(context, R.style.ImageDialog);
                                imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                imageDialog.setContentView(R.layout.image_dialog);
                                imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.MATCH_PARENT);

                                Toolbar toolbar = (Toolbar) imageDialog.findViewById(R.id.toolbar);
                                toolbar.setNavigationIcon(R.drawable.ic_left_arrow);
                                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        imageDialog.dismiss();
                                    }
                                });
                                ImageView image = (ImageView) imageDialog.findViewById(R.id.image);
                                image.setImageBitmap(decodeImage(message.getImage()));
                                PhotoViewAttacher mAttacher = new PhotoViewAttacher(image);
                                mAttacher.update();
                                imageDialog.show();
                            }
                        });
                    }
                    try {
                        Date dateParse = df1.parse(message.getTime());
                        String dateFormat = df2.format(dateParse);
                        ((InactiveViewHolder) holder).tvTimeInactive.setText("" + dateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((InactiveViewHolder) holder).listInactive.setTag(position);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (messageList == null)
            return 0;
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList != null) {
            MessageModel message = messageList.get(position);
            if (message != null) {
                return message.getType();
            }
        }
        return 0;
    }


    public static class ActiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_username_active)
        TextView tvUsernameActive;
        @BindView(R.id.tv_message_active)
        TextView tvMessageActive;
        @BindView(R.id.iv_image_active)
        ImageView ivImageActive;
        @BindView(R.id.tv_time_active)
        TextView tvTimeActive;
        @BindView(R.id.list_active)
        LinearLayout listActive;

        public ActiveViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class InactiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_username_inactive)
        TextView tvUsernameInactive;
        @BindView(R.id.tv_message_inactive)
        TextView tvMessageInactive;
        @BindView(R.id.iv_image_inactive)
        ImageView ivImageInactive;
        @BindView(R.id.tv_time_inactive)
        TextView tvTimeInactive;
        @BindView(R.id.list_inactive)
        LinearLayout listInactive;

        public InactiveViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
