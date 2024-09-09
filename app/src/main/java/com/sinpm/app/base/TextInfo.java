package com.sinpm.app.base;

import android.graphics.Color;

public class TextInfo {

    private String text;
    private int color;
    private boolean isRed;

    public TextInfo(String text) {
        this.color = Color.WHITE;
        this.text = text;
    }

    public TextInfo(String text, boolean isRed) {
        this.text = text;
        if (isRed) {
            this.color = Color.RED;
        } else {
            this.color = Color.WHITE;
        }
    }

    public TextInfo() {
        this.color = Color.WHITE;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

    }

    public void setText(String text, boolean isRed) {
        this.text = text;
        this.color = Color.RED;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
