package com.roomhub.entity;

import com.roomhub.model.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "agreement")
@Getter
@Setter
@NoArgsConstructor
public class Agreement extends BaseTimeEntity {

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

    public Agreement(long userId, TermId termId, Status status) {
        this.userId = userId;
        this.termId = termId;
        this.status = status;
    }

    public static List<Agreement> of(long userId, List<TermId> termIds, Status status) {
        if (termIds == null) {
            return Collections.emptyList();
        }
        return termIds.stream()
                .map(termId -> new Agreement(userId, termId, status))
                .collect(Collectors.toList());
    }

}
