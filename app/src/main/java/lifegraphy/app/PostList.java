package lifegraphy.app;

import java.util.Date;
import java.util.List;

public class PostList {

    private String post_id, post_caption,user_id,user_name,user_details,date;
    private int image,profile;
    private List<String> tags;


    public PostList(String post_id, String post_caption, String user_id, String date, List<String> tags) {
        this.post_id = post_id;
        this.post_caption = post_caption;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_details = user_details;
        this.date = date;
        this.image = image;
        this.profile = profile;
        this.tags = tags;

    }

    public String getPost_id() {

        return post_id;
    }


    public String getPost_caption() {
        return post_caption;

    }

    public String getUser_id() {
        return user_id;

    }

    public String getUser_details() {
        return user_details;

    }

    public String  getDate() {

        return date;

    }

    public int getImage() {
        return image;

    }

    public int getProfile() {
        return profile;

    }

    public List<String> getTags() {
        return tags;
    }
}