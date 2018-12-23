package ru.androidacademy.msk.lists.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Actor implements Parcelable {

    private String name;
    private String avatar;
    private boolean oscar;


    public Actor(String name, String avatar, boolean oscar) {
        this.name = name;
        this.avatar = avatar;
        this.oscar = oscar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOscar() {
        return oscar;
    }

    public void setOscar(boolean oscar) {
        this.oscar = oscar;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(avatar);
        if(oscar)
            parcel.writeByte((byte)1);
        else
            parcel.writeByte((byte)0);
    }

    public static final Creator<Actor> CREATOR = new Creator<Actor>() {
        @Override
        public Actor createFromParcel(Parcel parcel) {
            String name = parcel.readString();
            String avatar = parcel.readString();
            Boolean oscar = false;
            if(parcel.readByte() == 1)
                oscar = true;
            return new Actor(name, avatar, oscar);
        }

        @Override
        public Actor[] newArray(int size) {
            return new Actor[size];
        }
    };
}
