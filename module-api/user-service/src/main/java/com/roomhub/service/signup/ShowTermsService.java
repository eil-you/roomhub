package com.roomhub.service.signup;

import com.roomhub.entity.Term;
import com.roomhub.model.TermsResponse;
import com.roomhub.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowTermsService {

    private final TermRepository termRepository;

    public List<TermsResponse> getTerms() {
        List<Term> terms = termRepository.findAllByActive(true);
        List<TermsResponse> termsResponse = new ArrayList<>();
        for (Term term : terms) {
            termsResponse.add(new TermsResponse(term));
        }
        return termsResponse;
    }

}
