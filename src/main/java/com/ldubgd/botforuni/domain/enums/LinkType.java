package com.ldubgd.botforuni.domain.enums;

public enum LinkType {
    GET_DOC("file/get-doc");

    private final String link;

    LinkType(String link) {
        this.link = link;
    }


    @Override
    public String toString() {
        return link;
    }
}
