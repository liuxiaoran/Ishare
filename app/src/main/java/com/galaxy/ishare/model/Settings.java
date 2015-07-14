package com.galaxy.ishare.model;

/**
 * Created by liuxiaoran on 15/7/14.
 */
public class Settings {

    public boolean receiveNewMessage;
    public boolean openVoice;
    public boolean openShock;

    public boolean isReceiveNewMessage() {
        return receiveNewMessage;
    }

    public void setReceiveNewMessage(boolean receiveNewMessage) {
        this.receiveNewMessage = receiveNewMessage;
    }

    public boolean isOpenVoice() {
        return openVoice;
    }

    public void setOpenVoice(boolean openVoice) {
        this.openVoice = openVoice;
    }

    public boolean isOpenShock() {
        return openShock;
    }

    public void setOpenShock(boolean openShock) {
        this.openShock = openShock;
    }


}
