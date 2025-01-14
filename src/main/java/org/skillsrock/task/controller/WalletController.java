package org.skillsrock.task.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.skillsrock.task.dto.WalletResponseDTO;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;
import org.skillsrock.task.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class WalletController {

  private final WalletService walletService;

  @PostMapping("/wallet")
  public ResponseEntity<WalletResponseDTO> updateWalletBalance(
      @Valid @RequestBody WalletUpdateRequestDTO walletUpdateRequestDTO) {
    WalletResponseDTO response = walletService.updateWalletBalance(walletUpdateRequestDTO);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/wallets/{wallet_uuid}")
  public ResponseEntity<WalletResponseDTO> getWalletBalance(@PathVariable UUID wallet_uuid) {
    WalletResponseDTO response = walletService.getWalletBalance(wallet_uuid);
    return ResponseEntity.ok(response);
  }
}
