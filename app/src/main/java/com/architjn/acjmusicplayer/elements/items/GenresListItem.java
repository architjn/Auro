package com.architjn.acjmusicplayer.elements.items;

/**
 * Created by architjn on 10/06/15.
 */
public class GenresListItem {

    long id;
    String name;

    public GenresListItem(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

}
