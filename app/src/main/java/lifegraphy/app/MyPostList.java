package lifegraphy.app;

import java.util.Date;

public class MyPostList {

    private String name, id,caption,date;




    public MyPostList(String id, String caption, String date) {
        this.id = id;
        this.caption = caption;
        this.date = date;
    }

    public String getId() {

        return id;
    }



    public String getCaption() {
        return caption;

    }



    public String getDate() {

        return date;

    }







}