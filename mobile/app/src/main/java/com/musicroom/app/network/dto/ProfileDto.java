package com.musicroom.app.network.dto;

import java.util.List;

/** Full profile of the current user (GET /me). Other users only get the sections they may see. */
public final class ProfileDto {
    public String id;
    public String email;
    public boolean emailVerified;
    public PublicInfo publicInfo;
    public FriendsInfo friendsInfo;
    public PrivateInfo privateInfo;
    public List<String> musicPreferences;
    /** e.g. "google" */
    public List<String> linkedProviders;

    public static final class PublicInfo {
        public String displayName;
        public String bio;
    }

    public static final class FriendsInfo {
        public String realName;
        public String city;
    }

    public static final class PrivateInfo {
        public String phone;
        /** ISO date, YYYY-MM-DD */
        public String birthDate;
    }
}
