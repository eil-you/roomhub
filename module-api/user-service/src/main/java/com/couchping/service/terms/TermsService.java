package com.couchping.service.terms;

import com.couchping.entity.Term;
import com.couchping.model.RegisterTermRequest;
import com.couchping.model.ReviseTermRequest;
import com.couchping.repository.TermRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermRepository termRepository;

    @Transactional
    public void registerTerm(RegisterTermRequest registerTermRequest) {
        termRepository.save(new Term(registerTermRequest));
    }

    @Transactional
    public void reviseTerm(ReviseTermRequest reviseTermRequest) {
        Term updateTerm = new Term(reviseTermRequest);
        termRepository.save(updateTerm);
    }

}
