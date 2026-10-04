package com.musicroom.app.network.dto;

public final class CreatePlaylistRequest {
    public final String name;
    public final String description;
    public final Visibility visibility;
    public final EditLicense editLicense;

    public CreatePlaylistRequest(String name, String description, Visibility visibility,
                                 EditLicense editLicense) {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.editLicense = editLicense;
    }
}
