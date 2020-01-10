package io.zuppelli.userservice.model;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;

import java.util.List;

public class Page<T> {
    private List<T> elements;
    private boolean next;
    private boolean prev;
    private String pageHash;

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

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

    public void setPageHash(CassandraPageRequest pageRequest, String hash) {
        setPageHash(hash);

        if( null != pageRequest.getPagingState()) {
            setPageHash(Base64.toBase64String(pageRequest.getPagingState().toBytes()));
        }
    }
}
