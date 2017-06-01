package com.aaqib.bottomnavigationaction;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    public int index;
    public String name;
    public String description;
    public int resID;
    RectF bounds;
    float centerX, centerY;
    double angleRadians;
    float widthHalf;
    float adjustmentLeft, adjustmentRight;

    Item(int index, String name, int resID) {
        init(index, name, null, resID);
    }

    Item(int index, String name, String description, int resID) {
        init(index, name, description, resID);
    }

    private void init(int index, String name, String description, int resID) {
        this.index = index;
        this.name = name;
        this.description = description;
        this.resID = resID;
        this.bounds = new RectF();
        this.adjustmentLeft = 0;
        this.adjustmentRight = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeString(this.name);
        dest.writeInt(this.resID);
    }

    Item(Parcel in) {
        this.index = in.readInt();
        this.name = in.readString();
        this.resID = in.readInt();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}