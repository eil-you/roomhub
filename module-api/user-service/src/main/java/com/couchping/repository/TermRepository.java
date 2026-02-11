package com.couchping.repository;

import com.couchping.entity.Term;
import com.couchping.entity.TermId;
import com.couchping.interfaces.TermIdOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findAllByActive(boolean active);

    // ?꾩닔 ?쎄? 踰덊샇 ?뺤씤
    List<TermId> findAllByRequiredAndActive(boolean required, boolean active);

    // 蹂듯빀????댄?, 踰꾩쟾)
    // Term findByTitleAndVersion(String title, String version);
}
