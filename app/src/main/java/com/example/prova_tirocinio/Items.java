package com.example.prova_tirocinio;

import androidx.annotation.NonNull;

public class Items {

    private String titolo;
    private String data;

    public Items(String titolo, String data) {
        this.titolo=titolo;
        this.data=data;
    }

    public String getData() {
        return this.data;
    }

    public String getTitolo() {
        return this.titolo;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    @NonNull
    @Override
    public String toString() {
        return this.titolo+" "+this.data;
    }
}
