package ru.androidacademy.msk.lists.data;

public class Actor {

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
}
