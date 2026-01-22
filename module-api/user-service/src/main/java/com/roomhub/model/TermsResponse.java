package com.roomhub.model;

import com.roomhub.entity.Term;
import lombok.Getter;

@Getter
public class TermsResponse {
    private String title;
    private String version;
    private boolean required;

    public TermsResponse(Term term) {
        this.version = term.getVersion();
        this.title = term.getTitle();
        this.required = term.isRequired();
    }

}
