package ru.androidacademy.msk.lists;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import ru.androidacademy.msk.lists.data.Actor;

public class AddActor extends Activity {

    private final static String basicAvatar = "https://static.grouple.co/uploads/pics/avatar/e1/feb63c4a402bbc3dcac6328fa2418b_5017.jpg";

    final int REQUEST_CODE_PHOTO = 1;

    private EditText editText;
    private CheckBox checkBox;
    private ImageView imageView;
    private Button button;
    private Button okButton;

    private ArrayList<Actor> actors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_actor);

        actors = this.getIntent().getParcelableArrayListExtra("key");

        editText = findViewById(R.id.edit_name);
        checkBox = findViewById(R.id.checkbox);
        imageView = findViewById(R.id.image_view);
        button = findViewById(R.id.button);
        okButton = findViewById(R.id.ok_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Запуск камеры
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_PHOTO);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Если есть изображение
                if (ActorListAdapter.hasImage(imageView)) {
                    //Сохранить изображение
                    ActorListAdapter.writeImage(getApplicationContext(), imageView, editText.getText().toString());
                }
                //Не получать список еще раз
                ActorListActivity.ACTER_ADDED = true;
                //Создать актера
                Actor actor = new Actor(editText.getText().toString(), basicAvatar, checkBox.isChecked());
                //Добавить его в список
                actors.add(actor);

                Intent intent = new Intent(getApplicationContext(), ActorListActivity.class);
                //Передать список обратно
                intent.putParcelableArrayListExtra("okey", actors);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (resultCode == RESULT_OK) {
            if (intent != null) {
                Bundle bndl = intent.getExtras();
                if (bndl != null) {
                    Object obj = intent.getExtras().get("data");
                    if (obj instanceof Bitmap) {
                        Bitmap bitmap = (Bitmap) obj;
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }
}