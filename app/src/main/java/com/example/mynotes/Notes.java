package com.example.mynotes;


import com.google.firebase.firestore.Exclude;

public class Notes {

    private String title;
    private String description;
    private String date;
    private String documentID;
    private int priority;

    private Notes() {
        //no-arg constructor needed for firestore
    }

    Notes(String title, String description, int priority, String date) {
        this.title=title;
        this.description=description;
        this.priority=priority;
        this.date=date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }


}
