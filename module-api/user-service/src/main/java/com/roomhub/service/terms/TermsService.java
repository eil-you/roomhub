package com.roomhub.service.terms;

import com.roomhub.entity.Term;
import com.roomhub.model.RegisterTermRequest;
import com.roomhub.model.ReviseTermRequest;
import com.roomhub.repository.TermRepository;
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
