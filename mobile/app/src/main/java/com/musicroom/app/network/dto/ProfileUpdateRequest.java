package com.musicroom.app.network.dto;

import java.util.List;

public final class ProfileUpdateRequest {
    public final ProfileDto.PublicInfo publicInfo;
    public final ProfileDto.FriendsInfo friendsInfo;
    public final ProfileDto.PrivateInfo privateInfo;
    public final List<String> musicPreferences;

    public ProfileUpdateRequest(ProfileDto.PublicInfo publicInfo, ProfileDto.FriendsInfo friendsInfo,
                                ProfileDto.PrivateInfo privateInfo, List<String> musicPreferences) {
        this.publicInfo = publicInfo;
        this.friendsInfo = friendsInfo;
        this.privateInfo = privateInfo;
        this.musicPreferences = musicPreferences;
    }
}
