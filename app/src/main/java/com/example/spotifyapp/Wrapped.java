package com.example.spotifyapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Wrapped implements Serializable {

    private ArrayList<String> artists;
    private ArrayList<String> tracks;
    private ArrayList<String> genres;
    private String image;
    private String name;
    private ArrayList<String> trackImages;

    public Wrapped() {
        artists = new ArrayList<String>();
        tracks = new ArrayList<String>();
        genres = new ArrayList<String>();
    }

    public void addToArtist(String a) {
        artists.add(a);
    }

    public void addToTrack(String t) {
        tracks.add(t);
    }

    public void addToGenre(String g) {
        genres.add(g);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getArtists() {
        return artists;
    }

    public ArrayList<String> getTracks() {
        return tracks;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    public void setTracks(ArrayList<String> tracks) {
        this.tracks = tracks;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public ArrayList<String> getTrackImages() {
        return trackImages;
    }

    public void setTrackImages(ArrayList<String> trackImages) {
        this.trackImages = trackImages;
    }
}
