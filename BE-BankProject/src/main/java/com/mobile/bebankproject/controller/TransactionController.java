package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.TransactionResponse;
import com.mobile.bebankproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts/{acct}/transactions")
public class TransactionController {
  @Autowired TransactionService txService;

  @GetMapping
  public List<TransactionResponse> history(
      @PathVariable("acct") String acct,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size) {
    return txService
      .getHistory(acct, PageRequest.of(page,size))
      .getContent(); 
  }
}