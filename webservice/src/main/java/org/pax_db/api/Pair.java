package org.pax_db.api;

import java.io.Serializable;

public class Pair implements Serializable {

    String first;
    String second;

    public Pair() {

    }

    public Pair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    private static final long serialVersionUID = -2813218143833065608L;
}
