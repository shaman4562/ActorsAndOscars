package ru.androidacademy.msk.lists;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ru.androidacademy.msk.lists.data.Actor;

public class ActorListAdapter extends RecyclerView.Adapter<ActorListAdapter.ViewHolder> {

    List<Actor> actorList;
    LayoutInflater inflater;
    RequestManager imageLoader;

    public ActorListAdapter(List<Actor> actorList, Context context) {
        this.actorList = actorList;
        inflater = LayoutInflater.from(context);
        imageLoader = Glide.with(context);

    }

    @Override
    public ActorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.actor_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActorListAdapter.ViewHolder viewHolder, int position) {

        Actor actor = actorList.get(position);
        viewHolder.textView.setText(actor.getName());
        imageLoader.load(actor.getAvatar()).into(viewHolder.imageView);
        viewHolder.oscarView.setVisibility(actor.isOscar() ? View.VISIBLE : View.INVISIBLE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),
                        viewHolder.textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                createAlertDialog(view.getContext(), view, viewHolder, position);
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

    public void setActorList(List<Actor> newList) {
        this.actorList = newList;
        notifyDataSetChanged();
    }

    private void createAlertDialog(Context context, View view, ViewHolder viewHolder, int position) {
        // Alert Dialog
        final String[] mCatsName = {
                context.getString(R.string.add_oskar),
                context.getString(R.string.remove_oskar),
                context.getString(R.string.remove_actor),
                context.getString(R.string.search_internet)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(viewHolder.textView.getText().toString());
        builder.setItems(mCatsName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (mCatsName[item].equals(context.getString(R.string.add_oskar))) {

                    viewHolder.oscarView.setVisibility(View.VISIBLE);

                } else if (mCatsName[item].equals(context.getString(R.string.remove_oskar))) {

                    viewHolder.oscarView.setVisibility(View.INVISIBLE);

                } else if (mCatsName[item].equals(context.getString(R.string.remove_actor))) {
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
                                actorList.remove(position);
                                notifyDataSetChanged();
                            } else {
                                dialog.cancel();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (mCatsName[item].equals(context.getString(R.string.search_internet))){
                    webRequest(view.getContext(), viewHolder.textView.getText().toString());
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

    public void webRequest(Context context, String url) {
        url = url.replaceAll(" ", "%20");
        String finalUrl = "https://ru.wikipedia.org/wiki/" + url;
        Intent intent = new Intent(context, WebRequest.class);
        intent.putExtra("url", finalUrl);
        context.startActivity(intent);
    }
}
