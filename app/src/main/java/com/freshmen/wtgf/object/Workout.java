package com.freshmen.wtgf.object;

/**
 * Created by nguyen on 01/08/2015.
 */
public class Workout {
    public String name;
    public String video_url;
    public int exerciseNumber;
    public int category;
    public int estimated_calories;
    public String tags;
    public String video_duration;
    public String description;
    public String video_thumbnail;

    public String getVideoThumbnail() {
        return video_thumbnail;
    }

    public void setVideoThumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getExerciseNumber() {
        return exerciseNumber;
    }

    public void setExerciseNumber(int exerciseNumber) {
        this.exerciseNumber = exerciseNumber;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getEstimated_calories() {
        return estimated_calories;
    }

    public void setEstimated_calories(int estimated_calories) {
        this.estimated_calories = estimated_calories;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
