package com.musicroom.app.network.dto;

public final class EventDto {
    public String id;
    public String name;
    public String description;
    public String ownerId;
    public String ownerName;
    public Visibility visibility;
    public VoteLicense voteLicense;
    public Double latitude;
    public Double longitude;
    public Integer radiusMeters;
    /** ISO-8601 date-time with offset */
    public String voteStart;
    public String voteEnd;
    public int trackCount;
}
