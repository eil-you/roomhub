package com.roomhub.entity;

import com.roomhub.model.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "agreement")
@Getter
@Setter
@NoArgsConstructor
public class Agreement extends baseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private TermId termId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Agreement(long id, long userId, TermId termIds, Status status) {
        this.id = id;
        this.userId = userId;
        this.termId = termIds;
        this.status = status;
    }

    public static List<Agreement> of(long id, long userId, List<TermId> termIds, Status status) {
        return termIds.stream()
                .map(termId -> new Agreement(id, userId, termId, status))
                .collect(Collectors.toList());
    }

}
