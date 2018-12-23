package ru.androidacademy.msk.lists;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.androidacademy.msk.lists.data.Actor;

public class ActorListAdapter extends RecyclerView.Adapter<ActorListAdapter.ViewHolder> {

   private final static String DIRECTORY = "/save/";
   private final static String EXTENSION = ".jpg";

    private ArrayList<Actor> actorList;
    private LayoutInflater inflater;
    private RequestManager imageLoader;

    private Context context;

    public ActorListAdapter(ArrayList<Actor> actorList, Context context) {
        this.actorList = actorList;
        inflater = LayoutInflater.from(context);
        imageLoader = Glide.with(context);
        this.context = context;
    }

    @Override
    @NonNull
    public ActorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.actor_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ActorListAdapter.ViewHolder viewHolder, int position) {

        Actor actor = actorList.get(position);
        viewHolder.textView.setText(actor.getName());

        //Достать фото из памяти
        readImage(viewHolder.imageView, viewHolder.textView.getText().toString());

        //Если картинки нет
        if (!hasImage(viewHolder.imageView)) {
            //Загрузить напрямую
            imageLoader.load(actor.getAvatar()).into(viewHolder.imageView);
        }

        viewHolder.oscarView.setVisibility(actor.isOscar() ? View.VISIBLE : View.INVISIBLE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, viewHolder.textView.getText(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

//                Toast.makeText(view.getContext(),
//                        viewHolder.textView.getText(), Toast.LENGTH_SHORT).show();

                //writeImage работает из метода onClick, но не работает из onBindViewHolder
                //writeImage(viewHolder.imageView, viewHolder.textView.getText().toString());
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (ActorListActivity.FULL_LIST)
                    createFullAlertDialog(view.getContext(), viewHolder, position);
                else
                    createMiniAlertDialog(view.getContext(), viewHolder);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView textView;
        final ImageView oscarView;

        ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.avatar);
            this.textView = view.findViewById(R.id.name);
            this.oscarView = view.findViewById(R.id.oscar);
        }
    }

    public void setActorList(ArrayList<Actor> newList) {
        this.actorList = newList;
        notifyDataSetChanged();
    }

    //Диалог для полного списка
    private void createFullAlertDialog(Context context, ViewHolder viewHolder, int position) {
        // Alert Dialog
        final String[] strings = {
                context.getString(R.string.add_oskar),
                context.getString(R.string.remove_oskar),
                context.getString(R.string.remove_actor),
                context.getString(R.string.search_internet)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(viewHolder.textView.getText().toString());
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (strings[item].equals(context.getString(R.string.add_oskar))) {

                    //viewHolder.oscarView.setVisibility(View.VISIBLE);
                    ActorListActivity.addOskar(position);
                    notifyDataSetChanged();

                } else if (strings[item].equals(context.getString(R.string.remove_oskar))) {

                    //viewHolder.oscarView.setVisibility(View.INVISIBLE);
                    ActorListActivity.removeOskar(position);
                    notifyDataSetChanged();

                } else if (strings[item].equals(context.getString(R.string.remove_actor))) {
                    // Alert Dialog
                    final String[] mCatsName = {
                            context.getString(R.string.no),
                            context.getString(R.string.yes)
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                    builder.setTitle(context.getString(R.string.are_you_sure));
                    builder.setItems(mCatsName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            if (mCatsName[item].equals(context.getString(R.string.yes))) {
                                //actorList.remove(position);
                                ActorListActivity.removeFromActors(position);
                                notifyDataSetChanged();
                            } else {
                                dialog.cancel();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (strings[item].equals(context.getString(R.string.search_internet))) {
                    webRequest(context, viewHolder.textView.getText().toString());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Диалог для НЕполного списка
    private void createMiniAlertDialog(Context context, ViewHolder viewHolder) {
        // Alert Dialog
        final String[] strings = {
                context.getString(R.string.search_internet)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(viewHolder.textView.getText().toString());
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (strings[item].equals(context.getString(R.string.search_internet))) {
                    webRequest(context, viewHolder.textView.getText().toString());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Поиск в википедии
    private void webRequest(Context context, String url) {
        url = url.replaceAll(" ", "%20");
        String finalUrl = "https://ru.wikipedia.org/wiki/" + url;
        Intent intent = new Intent(context, WebRequest.class);
        intent.putExtra("url", finalUrl);
        context.startActivity(intent);
    }

    //Достать картинку из памяти
    private void readImage(ImageView imageView, String name) {

        Bitmap bitmap = BitmapFactory.decodeFile(
                Environment.getExternalStorageDirectory() + DIRECTORY + name + EXTENSION);

        imageView.setImageBitmap(bitmap);
    }

    //Проверка есть ли изображение в ImageView
    public static boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    //Сохранить картинку из ImageView в память
    public static void writeImage(Context context, ImageView imageView, String name) {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap image = drawable.getBitmap();

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + context.getString(R.string.DIRECTORY));
        dir.mkdirs();
        File file = new File(dir, name + context.getString(R.string.EXTENSION));

        try {
            OutputStream outputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
