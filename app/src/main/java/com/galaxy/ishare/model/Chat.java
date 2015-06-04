package com.galaxy.ishare.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Zhan on 2015/5/19.
 */
public class Chat {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "fromUser")
    public String fromUser;

    @DatabaseField(columnName = "fromName")
    public String fromName;

    @DatabaseField(columnName = "toUser")
    public String toUser;

    @DatabaseField(columnName = "toName")
    public String toName;

    @DatabaseField(columnName = "time")
    public String time;

    @DatabaseField(columnName = "type")
    public int type;

    @DatabaseField(columnName = "content")
    public String content;

    @DatabaseField(columnName = "isRead")
    public int isRead;
}
