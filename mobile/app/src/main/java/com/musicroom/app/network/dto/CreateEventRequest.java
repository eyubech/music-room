package com.musicroom.app.network.dto;

/** Location and time window fields are only sent with {@link VoteLicense#LOCATION_AND_TIME}. */
public final class CreateEventRequest {
    public final String name;
    public final String description;
    public final Visibility visibility;
    public final VoteLicense voteLicense;
    public Double latitude;
    public Double longitude;
    public Integer radiusMeters;
    public String voteStart;
    public String voteEnd;

    public CreateEventRequest(String name, String description, Visibility visibility,
                              VoteLicense voteLicense) {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.voteLicense = voteLicense;
    }
}
