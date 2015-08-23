package com.biz.timux.popularmovies1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaojianxun on 15/8/19.
 */
public class MyMovie implements Parcelable {

    private int id;
    private String title;
    private double popularity;
    private double vote_avg;
    private String releaseDate;
    private String description;
    private String posterPath;
    private String backdropPath;
    private boolean video;

    private final String baseUrl = "https://image.tmdb.org/t/p/w185";

    public MyMovie(){

    }

    public MyMovie(int id, String title, double popularity, double vote_avg,
                   String releaseDate, String description,
                   String posterPath, String backdropPath,
                   boolean video) {
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.vote_avg = vote_avg;
        this.releaseDate = releaseDate;
        this.description = description;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.video = video;
    }

    //construct the path for loading image
    public String getPath() {

        return baseUrl+posterPath;
    }

    public String getIconPath() {

        if (backdropPath == null){
            return baseUrl+posterPath;
        }else {
            return baseUrl + backdropPath;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVote_avg() {
        return vote_avg;
    }

    public void setVote_avg(double vote_avg) {
        this.vote_avg = vote_avg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
    
    public MyMovie(Parcel parcel){
        id = parcel.readInt();
        title = parcel.readString();
        popularity = parcel.readDouble();
        vote_avg = parcel.readDouble();
        releaseDate = parcel.readString();
        description = parcel.readString();
        posterPath = parcel.readString();
        backdropPath = parcel.readString();
        video = parcel.readByte() != 0;
        
    }
    
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeDouble(popularity);
        parcel.writeDouble(vote_avg);
        parcel.writeString(releaseDate);
        parcel.writeString(description);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeByte(((byte) (video ? 1 : 0)));
        
    }
    
    public static final Parcelable.Creator<MyMovie> CREATOR =
        new Parcelable.Creator<MyMovie>() {
            public MyMovie createFromParcel(Parcel parcel) {
                return new MyMovie(parcel);
            }

            public MyMovie[] newArray(int i) {
                return new MyMovie[i];
            }
        };
}
