package org.skillsrock.task.controller;

import static org.mockito.Mockito.when;
import static org.skillsrock.task.exception.ExceptionMessage.WALLET_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.skillsrock.task.util.Constant.GET_WALLET_BALANCE_URL;
import static org.skillsrock.task.util.Constant.MIN_AMOUNT_OF_TEST_WALLET;
import static org.skillsrock.task.util.Constant.NEGATIVE_AMOUNT_OF_TEST_WALLET;
import static org.skillsrock.task.util.Constant.UPDATE_WALLET_BALANCE_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.skillsrock.task.dto.WalletResponseDTO;
import org.skillsrock.task.dto.WalletUpdateRequestDTO;
import org.skillsrock.task.exception.NotEnoughMoneyException;
import org.skillsrock.task.exception.WalletNotFoundException;
import org.skillsrock.task.service.WalletService;
import org.skillsrock.task.util.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WalletController.class)
class WalletControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private WalletService walletService;

  private final UUID walletId = UUID.randomUUID();


  @Test
  void getWalletBalance_RequestIsValid_StatusIsOk() throws Exception {
    // Arrange
    WalletResponseDTO response = new WalletResponseDTO(
        walletId, BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));

    when(walletService.getWalletBalance(walletId))
        .thenReturn(response);

    // Act
    var result = mockMvc.perform(get(GET_WALLET_BALANCE_URL + walletId)
            .contentType(MediaType.APPLICATION_JSON));

    // Assert
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.walletId").value(walletId.toString()))
        .andExpect(jsonPath("$.balance").value(response.balance()));
  }

  @Test
  void getWalletBalance_WalletDoesNotExist_StatusIsNotFound() throws Exception {
    // Arrange
    UUID invalidWalletId = UUID.randomUUID();
    when(walletService.getWalletBalance(invalidWalletId))
        .thenThrow(new WalletNotFoundException());

    // Act
    var result = mockMvc.perform(get(GET_WALLET_BALANCE_URL + invalidWalletId)
        .contentType(MediaType.APPLICATION_JSON));

    // Assert
    result.andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(WALLET_NOT_FOUND_EXCEPTION_MESSAGE));
  }

  @Test
  void updateWalletBalance_RequestIsValid_StatusIsOk() throws Exception {
    // Arrange
    WalletUpdateRequestDTO request = TestDataFactory.createWalletDepositUpdateRequest(
        walletId, BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));
    WalletResponseDTO response = new WalletResponseDTO(request.walletId(), request.amount());
    when(walletService.updateWalletBalance(request))
        .thenReturn(response);

    // Act
    var result = mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(request)));

    // Assert
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.walletId").value(walletId.toString()))
        .andExpect(jsonPath("$.balance").value(response.balance()));
  }

  @Test
  void updateWalletBalance_AmountIsNegative_StatusIsBadRequest() throws Exception {
    // Arrange
    WalletUpdateRequestDTO request = TestDataFactory.createWalletDepositUpdateRequest(
        walletId, BigDecimal.valueOf(NEGATIVE_AMOUNT_OF_TEST_WALLET));
    when(walletService.updateWalletBalance(request))
        .thenThrow(new NotEnoughMoneyException());

    // Act
    var result = mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(request)));

    // Assert
    result.andExpect(status().isBadRequest());
  }

  @Test
  void updateWalletBalance_WalletIdDoesNotExist_StatusIsNotFound() throws Exception {
    // Arrange
    WalletUpdateRequestDTO request = TestDataFactory.createWalletDepositUpdateRequest(
        walletId, BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));
    when(walletService.updateWalletBalance(request))
        .thenThrow(new WalletNotFoundException());

    // Act
    var result = mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(request)));

    // Assert
    result.andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(WALLET_NOT_FOUND_EXCEPTION_MESSAGE));
  }

  @Test
  void updateWalletBalance_InvalidValuesInJSON_StatusIsBadRequest() throws Exception {
    // Arrange
    WalletUpdateRequestDTO request = new WalletUpdateRequestDTO(null, null, null);

    // Act
    var result = mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(request)));

    // Assert
    result.andExpect(status().isBadRequest());
  }
}
