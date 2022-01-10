package lifegraphy.app;

public class CategoryList {

    private String category;
    private int image;
    private Boolean checked;



    public CategoryList(String category,int image,Boolean checked) {
        this.category = category;
        this.image = image;
        this.checked = checked;


    }

    public String getCategory() {

        return category;
    }

    public int getImage() {

        return image;
    }

    public Boolean getChecked() {

        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}