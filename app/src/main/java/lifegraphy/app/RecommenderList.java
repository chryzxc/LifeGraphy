package lifegraphy.app;

public class RecommenderList implements Comparable<RecommenderList>{

    private String id;
    private int score;




    public RecommenderList(String id, int score) {
        this.id = id;
        this.score = score;

    }

    public String getId() {

        return id;
    }



    public int getScore() {
        return score;


    }

    @Override
    public int compareTo(RecommenderList another) {
        return getScore() - another.getScore();
    }






}