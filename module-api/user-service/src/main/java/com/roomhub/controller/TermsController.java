package com.roomhub.controller;

import com.roomhub.model.RegisterTermRequest;
import com.roomhub.model.ReviseTermRequest;
import com.roomhub.service.terms.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/terms")
@RequiredArgsConstructor()
public class TermsController {

    private final TermsService registerTermService;

    @PostMapping("/register")
    public void registerTerm(@RequestBody RegisterTermRequest registerTermRequest) {
        registerTermRequest.check();
        registerTermService.registerTerm(registerTermRequest);
    }

    @PostMapping("/revice")
    public void reviceTerm(@RequestBody ReviseTermRequest reviseTermRequest) {
        reviseTermRequest.check();
        registerTermService.reviceTerm(reviseTermRequest);

    }

}
