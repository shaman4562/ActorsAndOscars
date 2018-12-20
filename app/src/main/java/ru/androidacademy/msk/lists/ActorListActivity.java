package ru.androidacademy.msk.lists;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.androidacademy.msk.lists.data.Actor;
import ru.androidacademy.msk.lists.data.DataUtils;

public class ActorListActivity extends AppCompatActivity {

    private Toolbar toolbar;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ActorListAdapter adapter;
    List<Actor> actors;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_list);

        initToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.list);

        layoutManager= new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        actors = DataUtils.generateActors();

        adapter = new ActorListAdapter(actors, this);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Actor> list = new ArrayList<Actor>();
        int id = item.getItemId();

        switch(id){
            case R.id.has_oskar :
                for (Actor actor : actors){
                    if(actor.isOscar()){
                        list.add(actor);
                    }
                    adapter.setActorList(list);
                }
                return true;
            case R.id.dont_has_oskar :
                for (Actor actor : actors){
                    if(!actor.isOscar()){
                        list.add(actor);
                    }
                    adapter.setActorList(list);
                }
                return true;
            case R.id.all_actors :
                    adapter.setActorList(DataUtils.generateActors());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name_2);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Actor> list = new ArrayList<Actor>();
                int id = item.getItemId();

                switch(id){
                    case R.id.has_oskar :
                        for (Actor actor : actors){
                            if(actor.isOscar()){
                                list.add(actor);
                            }
                            adapter.setActorList(list);
                        }
                        return false;
                    case R.id.dont_has_oskar :
                        for (Actor actor : actors){
                            if(!actor.isOscar()){
                                list.add(actor);
                            }
                            adapter.setActorList(list);
                        }
                        return false;
                    case R.id.all_actors :
                        adapter.setActorList(DataUtils.generateActors());
                        return false;
                }
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.main_menu);
    }
}
