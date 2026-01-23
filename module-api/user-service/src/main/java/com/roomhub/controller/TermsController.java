package com.roomhub.controller;

import com.roomhub.model.CommonResponse;
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
@RequiredArgsConstructor
public class TermsController {

    private static final int SUCCESS_CODE = 0;
    private final TermsService termsService;

    @PostMapping("/register")
    public CommonResponse<Void> registerTerm(@RequestBody RegisterTermRequest registerTermRequest) {
        registerTermRequest.check();
        termsService.registerTerm(registerTermRequest);
        return new CommonResponse<>(SUCCESS_CODE, "Term registered successfully", null);
    }

    @PostMapping("/revise")
    public CommonResponse<Void> reviseTerm(@RequestBody ReviseTermRequest reviseTermRequest) {
        reviseTermRequest.check();
        termsService.reviseTerm(reviseTermRequest);
        return new CommonResponse<>(SUCCESS_CODE, "Term revised successfully", null);
    }

}
