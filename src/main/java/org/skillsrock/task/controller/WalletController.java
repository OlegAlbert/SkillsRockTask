package org.skillsrock.task.controller;

import org.skillsrock.task.dto.WalletDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class WalletController {

  @PostMapping("/wallet")
  public ResponseEntity<String> updateWalletBalance(
      @RequestBody WalletDTO walletDTO
  ) {
    return ResponseEntity.ok("Wallet Balance");
  }

  @GetMapping("/wallets/{WALLET_UUID}")
  public ResponseEntity<String> getWalletBalance(
      @PathVariable String WALLET_UUID
  ) {
    return ResponseEntity.ok("Wallet Balance");
  }

}
