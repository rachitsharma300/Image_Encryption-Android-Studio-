package com.rachitsharma300.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rachitsharma300.database.AppDatabase;
import com.rachitsharma300.database.Image;
import com.rachitsharma300.services.App;
import com.rachitsharma300.services.AuthService;
import com.rachitsharma300.services.ExecService;
import com.rachitsharma300.util.Callback;
import com.rachitsharma300.util.CryptoUtil;
import com.rachitsharma300.util.DateUtil;
import com.rachitsharma300.util.IOUtil;
import com.rachitsharma300.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    /**
     * string for Intent.putExtra() when navigating to imageViewActivity
     */
    public static final String IMG_NAME = "img_title";
    private List<String> imageNames = Collections.synchronizedList(new ArrayList<>());
    private Context context;

    @Inject
    protected AppDatabase db;
    @Inject
    protected ExecService es;
    @Inject
    protected AuthService auth;

    public ImageListAdapter(Context context) {
        App.getAppComponent().inject(this);
        this.context = context;
        this.loadImageNamesFromDb();
    }

    // this method is for inflating the view of each item.
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acitivity_each_item,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // loading resources for each ViewHolder
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // setup the name for each image item
        holder.setNameStr(imageNames.get(position));

        // setup the onClickListener for the layout of whole Recycler layout
        holder.getItem_layout().setOnClickListener(view -> {
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra(IMG_NAME, imageNames.get(holder.getAdapterPosition()));
            context.startActivity(intent);
        });
    }

    /**
     * Create a dialog that asks whether the user want to delete the item
     *
     * @param positiveCallback callback for positive ("YES") button, set to {@code NULL} if no
     *                         operation is needed
     * @param negativeCallback callback for negative ("NO") button, set to {@code NULL} if no
     *                         operation is needed
     */
    public void createDeleteDialog(Callback positiveCallback, Callback negativeCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(R.string.delete_dialog_title)
                .setPositiveButton(context.getString(R.string.positiveBtnTxt), (dia, id) -> {
                    if (positiveCallback != null)
                        positiveCallback.func();
                })
                .setNegativeButton(context.getString(R.string.negativeBtnTxt), (dia, id) -> {
                    if (negativeCallback != null)
                        negativeCallback.func();
                });
        AlertDialog dia = builder.create();
        dia.show();
    }

    @Override
    public int getItemCount() {
        return imageNames.size();
    }

    /**
     * Swap the content of two items (ViewHolders).
     *
     * @param l index of an item
     * @param r index of an item
     * @return whether two items are indeed swapped
     */
    public boolean swapPosition(int l, int r) {
        int len = imageNames.size();
        if (l >= 0 && l < len && r >= 0 && r < len) {
            String temp = imageNames.get(l);
            imageNames.set(l, imageNames.get(r));
            imageNames.set(r, temp);
            return true;
        }
        return false;
    }

    /**
     * Load the whole list of image names from db in a separate {@code Thread}
     */
    private void loadImageNamesFromDb() {
        es.submit(() -> {
            this.imageNames.clear();
            this.imageNames.addAll(db.imgDao().getImageNames());
        });
    }

    /**
     * Delete the actual image file with the specified name. Regardless of whether the actual file is
     * * deleted, a message will be created to notify the user.
     *
     * @param name of the image
     * @return whether the file is actually deleted
     */
    public boolean deleteImageFile(String name) {
        Image img = this.db.imgDao().getImage(name);
        if (img != null && IOUtil.deleteFile(img.getPath())) {
            this.db.imgDao().deleteImage(img);
            MsgToaster.msgShort((Activity) context, String.format("%s deleted.", name));
            return true;
        } else {
            MsgToaster.msgShort((Activity) context, R.string.file_not_deleted_msg);
            return false;
        }
    }


    /**
     * Insert a image name to the end of the list, this method only affects the recyclerview not
     * the actual file.
     *
     * @param name image name
     */
    public void addImageName(String name) {
        addImageName(name, imageNames.size());
    }

    /**
     * Insert a image name to a specific index, this method only affects the recyclerview not
     * the actual file.
     *
     * @param name image name
     */
    public void addImageName(String name, int index) {
        ((Activity) context).runOnUiThread(() -> {
            this.imageNames.add(index, name);
            this.notifyItemInserted(index);
        });
    }

    /**
     * Remove an image from ths list. This method only affects the recyclerview not the actual file.
     *
     * @param index index in the {@code imageNames}
     * @return the name of the image being deleted
     */
    public void deleteImageName(int index) {
        this.imageNames.remove(index);
        this.notifyItemRemoved(index);
    }

    // each view holder holds data of each item
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private RelativeLayout item_layout;
        private ImageView thumbnailIv;
        private TextView timestampTv;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView);
            item_layout = itemView.findViewById(R.id.item_layout);
            thumbnailIv = itemView.findViewById(R.id.thumbnailIv);
            thumbnailIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            timestampTv = itemView.findViewById(R.id.timestampTv);
        }

        /**
         * Set the name string to the textview {@code name} and init the operation to load thumbnail
         * and timestamp
         *
         * @param name
         */
        public void setNameStr(String name) {
            this.getName().setText(name);
            es.submit(() -> {
                Image img = db.imgDao().getImage(name);
                this.setTimestamp(img.getTimestamp());
                this.loadThumbnail(img.getThumbnailPath());
            });
        }

        private void loadThumbnail(String path) {
            try {
                byte[] decrypted = CryptoUtil.decrypt(IOUtil.read(new File(path)), auth.getImgKey());
                Bitmap bitmap = ImageUtil.decodeBitmap(decrypted);
                ((Activity) context).runOnUiThread(() -> {
                    thumbnailIv.setImageBitmap(bitmap);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setTimestamp(long timestamp) {
            this.timestampTv.setText(String.format("Added on: %s", DateUtil.toDateStr(new Date(timestamp))));
        }

        public TextView getName() {
            return this.name;
        }

        public String getNameStr() {
            return this.name.getText().toString();
        }

        public RelativeLayout getItem_layout() {
            return this.item_layout;
        }
    }
}
