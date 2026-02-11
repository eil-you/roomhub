package com.couchping.entity;

import com.couchping.model.RegisterTermRequest;
import com.couchping.model.ReviseTermRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "term")
@Getter
@Setter
@NoArgsConstructor
public class Term extends BaseTimeEntity {

    @EmbeddedId
    private TermId id;

    @Column(nullable = false)
    private boolean required;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, length = 5)
    private String createdBy;

    @Column(nullable = false, length = 5)
    private String updatedBy;

    public Term(RegisterTermRequest dto) {
        this.id = new TermId(dto.title(), dto.version());
        this.required = dto.required();
        this.active = dto.active();
        this.createdBy = dto.createdBy();
        this.updatedBy = dto.updatedBy();
    }

    public Term(ReviseTermRequest dto) {
        this.id = new TermId(dto.title(), dto.version());
        this.required = dto.required();
        this.active = dto.active();
        this.updatedBy = dto.updatedBy();
    }

    public String getTitle() {
        return id.getTitle();
    }

    public String getVersion() {
        return id.getVersion();
    }

}
