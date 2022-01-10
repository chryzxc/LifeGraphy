package lifegraphy.app;


public class ProductionList {

    private String production_id, production_name,numOfMembers,description,minimum,maximum;



    public ProductionList(String production_id, String production_name,String numOfMembers, String description, String minimum, String maximum) {

        this.production_id = production_id;
        this.production_name = production_name;
        this.numOfMembers = numOfMembers;
        this.description = description;

        this.minimum = minimum;
        this.maximum = maximum;

    }

    public String getProduction_id() {

        return production_id;
    }

    public String getProduction_name() {
        return production_name;

    }

    public String getNumOfMembers() {

        return numOfMembers;
    }

    public String getDescription() {
        return description;

    }

    public String getMinimum(){
        return minimum;
    }

    public String getMaximum(){
        return maximum;
    }




}