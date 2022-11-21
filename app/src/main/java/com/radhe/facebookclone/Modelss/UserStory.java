package com.radhe.facebookclone.Modelss;

public class UserStory {

    private String storyImage;
    private  long storyAt;

    public UserStory(String storyImage, long storyAt) {
        this.storyImage = storyImage;
        this.storyAt = storyAt;
    }

    public UserStory() {
    }

    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}
