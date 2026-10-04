package com.musicroom.app.ui.common;

import android.content.Context;

import androidx.annotation.Nullable;

import com.musicroom.app.R;
import com.musicroom.app.network.dto.EditLicense;
import com.musicroom.app.network.dto.Visibility;
import com.musicroom.app.network.dto.VoteLicense;

/** Display names of the API enums. */
public final class Labels {

    private Labels() {
    }

    public static String visibility(Context context, @Nullable Visibility visibility) {
        return context.getString(visibility == Visibility.PRIVATE
                ? R.string.visibility_private : R.string.visibility_public);
    }

    /** Null for the default license, which needs no label. */
    @Nullable
    public static String voteLicense(Context context, @Nullable VoteLicense license) {
        if (license == VoteLicense.INVITED_ONLY) {
            return context.getString(R.string.license_invited_short);
        }
        if (license == VoteLicense.LOCATION_AND_TIME) {
            return context.getString(R.string.license_location_time_short);
        }
        return null;
    }

    /** Null for the default license, which needs no label. */
    @Nullable
    public static String editLicense(Context context, @Nullable EditLicense license) {
        return license == EditLicense.INVITED_ONLY
                ? context.getString(R.string.license_invited_short) : null;
    }

    /** "by Owner · 3 tracks", plus the license when it is not the default. */
    public static String ownerLine(Context context, String owner, int trackCount,
                                   @Nullable String license) {
        String tracks = context.getResources().getQuantityString(R.plurals.track_count,
                trackCount, trackCount);
        String line = context.getString(R.string.owner_subtitle, owner, tracks);
        return license == null ? line : line + " · " + license;
    }
}
