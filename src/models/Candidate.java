package models;

public class Candidate {
    private String id;
    private String name;
    private String photo;
    private int votes;

    public Candidate(String id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.votes = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhoto() { return photo; }
    public int getVotes() { return votes; }
    public void addVote() { this.votes++; }
    public void setVotes(int votes) { this.votes = votes; }
}