package io.zuppelli.userservice.resource.dto;

public class PageDTO {
    private boolean next;
    private boolean prev;
    private String pageHash;

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public boolean isPrev() {
        return prev;
    }

    public void setPrev(boolean prev) {
        this.prev = prev;
    }

    public String getPageHash() {
        return pageHash;
    }

    public void setPageHash(String pageHash) {
        this.pageHash = pageHash;
    }
}
