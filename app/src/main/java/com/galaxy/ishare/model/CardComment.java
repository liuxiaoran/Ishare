package com.galaxy.ishare.model;

/**
 * Created by liuxiaoran on 15/6/6.
 */
public class CardComment {

    public int comment_id;
    public int card_id;
    public String nickName;
    public String commenterAvatar;
    public double rating;
    public String gender;
    public String comment_time;
    public String commentContent;

    public CardComment(int comment_id, int card_id, String nickName, String commenterAvatar, double rating, String gender, String comment_time, String commentContent) {
        this.comment_id = comment_id;
        this.card_id = card_id;
        this.nickName = nickName;
        this.commenterAvatar = commenterAvatar;
        this.rating = rating;
        this.gender = gender;
        this.comment_time = comment_time;
        this.commentContent = commentContent;
    }
}
