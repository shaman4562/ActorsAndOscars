package ru.androidacademy.msk.lists;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import ru.androidacademy.msk.lists.data.Actor;
import ru.androidacademy.msk.lists.data.DataUtils;


public class ActorListActivity extends AppCompatActivity {

    public static boolean FIRST_START = true;
    public static boolean ACTER_ADDED = false; //Метка чтобы не получать список заново после добавления актера
    public static boolean FULL_LIST = true; //Метка чтобы выбирать подходящий диалог

    SharedPreferences sPref;

    RecyclerView recyclerView;
    StaggeredGridLayoutManager layoutManager;
    ActorListAdapter adapter;
    private static ArrayList<Actor> actors;

    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_actor_list);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        fab = findViewById(R.id.fab);

        initToolbar();

        //Загрузить из SharedPreferences
        loadData();

        if (ACTER_ADDED) {
            assert savedInstanceState != null;
            //Получить список с новым актером
            actors = this.getIntent().getParcelableArrayListExtra("okey");

        } else {
            //Не получать список заново, если был добавлен актер
            actors = DataUtils.generateActors();
            ACTER_ADDED = false;
        }

        //Первый запуск приложения
        if (FIRST_START) {
            //Сохраненить фотографии
            SavingImage savingImage = new SavingImage();
            savingImage.execute();
            FIRST_START = false;

            Toast.makeText(this, "Первый запуск", Toast.LENGTH_SHORT).show();
        }

        //layoutManager= new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager = new StaggeredGridLayoutManager(1, RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ActorListAdapter(actors, this);
        recyclerView.setAdapter(adapter);

        //Прокрутка чтобы вызвать onBindViewHolder адаптера для отображения первых элементов
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        recyclerView.smoothScrollToPosition(0);

        //Сокрытие кнопки при скроле
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                }
                if (dy <= 0) {
                    fab.show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddActor.class);
                intent.putParcelableArrayListExtra("key", actors);
                startActivity(intent);
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ArrayList<Actor> list = new ArrayList<Actor>();
                int id = item.getItemId();

                switch (id) {
                    case R.id.default_settings:
                        actors = DataUtils.generateActors();
                        FULL_LIST = true;
                        adapter.setActorList(actors);
                        return false;
                    case R.id.has_oskar:
                        for (Actor actor : actors) {
                            if (actor.isOscar()) {
                                list.add(actor);
                            }
                            FULL_LIST = false;
                            adapter.setActorList(list);
                        }
                        return false;
                    case R.id.dont_has_oskar:
                        for (Actor actor : actors) {
                            if (!actor.isOscar()) {
                                list.add(actor);
                            }
                            FULL_LIST = false;
                            adapter.setActorList(list);
                        }
                        return false;
                    case R.id.all_actors:
                        FULL_LIST = true;
                        adapter.setActorList(actors);
                        return false;
                }
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.main_menu);
    }

    @SuppressLint("StaticFieldLeak")
    private class SavingImage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            for (Actor actor : actors) {
                Bitmap bitmap = null;

                try {
                    bitmap = Glide.with(getApplicationContext()).asBitmap()
                            .load(actor.getAvatar()).into(200, 200).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                File path = Environment.getExternalStorageDirectory();
                File directory = new File(path +
                        getApplicationContext().getString(R.string.DIRECTORY));
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, actor.getName() +
                        getApplicationContext().getString(R.string.add_oskar));

                try {
                    OutputStream outputStream = new FileOutputStream(file);
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    }
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

    //Сохранение в SharedPreferences
    private void saveData() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean("first_start", FIRST_START);
        editor.apply();
    }

    //Загрузка из SharedPreferences
    private void loadData() {
        sPref = getPreferences(MODE_PRIVATE);
        FIRST_START = sPref.getBoolean("first_start", true);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Сохранение состояния при выходе
        saveData();
    }

    public static void removeFromActors(int position) {
        actors.remove(position);
    }

    public static void addOskar(int position) {
        actors.get(position).setOscar(true);
    }

    public static void removeOskar(int position) {
        actors.get(position).setOscar(false);
    }
}
