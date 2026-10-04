package com.musicroom.app.network.dto;

import java.util.List;

/** Permission given by a user to a friend to control one of their devices. */
public final class DelegationDto {
    public String id;
    public String deviceId;
    public String deviceName;
    public String ownerId;
    public String ownerName;
    public String delegateId;
    public String delegateName;
    /** e.g. PLAY_PAUSE, SKIP, VOLUME */
    public List<String> permissions;
    public String expiresAt;
}
