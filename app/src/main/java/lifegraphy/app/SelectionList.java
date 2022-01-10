package lifegraphy.app;

import android.media.Image;

import java.util.Date;

public class SelectionList {

    private String title, description,identifier;
    private int image;


    public SelectionList(String identifier, String title, String description, int image) {

        this.image = image;
        this.identifier = identifier;
        this.title = title;
        this.description = description;

    }


    public int getImage() {
        return image;

    }

    public String getIdentifier() {
        return identifier;

    }

    public String getTitle() {
        return title;

    }

    public String getDescription() {
        return description;

    }





}