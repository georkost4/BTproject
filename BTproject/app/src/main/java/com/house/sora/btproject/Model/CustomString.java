package com.house.sora.btproject.Model;

/**
 * Created by SoRa on 3/6/2016.
 */
public class CustomString
{
    private String string;
    private int who;

    public CustomString(String string,int who)
    {
        this.string = string;
        this.who = who;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getWho() {
        return who;
    }

    public void setWho(int who) {
        this.who = who;
    }
}
