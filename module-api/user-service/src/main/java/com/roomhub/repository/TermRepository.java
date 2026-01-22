package com.roomhub.repository;

import com.roomhub.entity.Term;
import com.roomhub.entity.TermId;
import com.roomhub.interfaces.TermIdOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findAllByActive(boolean active);

    // 필수 약관 번호 확인
    List<TermId> findAllByRequiredAndActive(boolean required, boolean active);

    // 복합키(타이틀, 버전)
    // Term findByTitleAndVersion(String title, String version);
}
