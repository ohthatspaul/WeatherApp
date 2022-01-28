package com.example.paul_weather_task.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    private int id;
    private int parentId=-1;
    private String enName="";
    private String initialName="";
    private String name;

    private String weather_id="";
    private int level =0;

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public City(int id, int parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    protected City(Parcel in) {
        id = in.readInt();
        parentId = in.readInt();
        enName = in.readString();
        initialName = in.readString();
        name = in.readString();
        weather_id = in.readString();
        level = in.readInt();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public String toString() {
        return String.format("%s (%s)",name,enName);

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getInitialName() {
        return initialName;
    }

    public void setInitialName(String initialName) {
        this.initialName = initialName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj==null || getClass() != obj.getClass())return false;
        City city = (City) obj;
        if (id != city.id)return false;
        if(parentId!=city.parentId)return false;
        if(level!=city.level)return false;
        if(!name.equals(city.name))return false;
        if(enName!=null ? !initialName.equals(city.initialName):city.initialName!=null) return false;

        return weather_id!=null ? weather_id.equals(city.weather_id):city.weather_id==null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(parentId);
        parcel.writeString(enName);
        parcel.writeString(initialName);
        parcel.writeString(name);
        parcel.writeString(weather_id);
        parcel.writeInt(level);

    }
}
