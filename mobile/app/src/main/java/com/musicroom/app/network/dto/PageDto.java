package com.musicroom.app.network.dto;

import java.util.List;

/** One page of a paginated list. */
public final class PageDto<T> {
    public List<T> items;
    public int page;
    public int size;
    public long totalItems;
}
