package org.skillsrock.task.integration;

import static org.skillsrock.task.exception.ExceptionMessage.INVALID_JSON_FORMAT_EXCEPTION_MESSAGE;
import static org.skillsrock.task.exception.ExceptionMessage.NOT_ENOUGH_MONEY_EXCEPTION_MESSAGE;
import static org.skillsrock.task.exception.ExceptionMessage.VALIDATION_EXCEPTION_MESSAGE;
import static org.skillsrock.task.exception.ExceptionMessage.WALLET_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.skillsrock.task.util.Constant.AMOUNT_FIELD_NAME;
import static org.skillsrock.task.util.Constant.GET_WALLET_BALANCE_URL;
import static org.skillsrock.task.util.Constant.INVALID_JSON_FIELD_NAME;
import static org.skillsrock.task.util.Constant.MAX_AMOUNT_OF_TEST_WALLET;
import static org.skillsrock.task.util.Constant.MIN_AMOUNT_OF_TEST_WALLET;
import static org.skillsrock.task.util.Constant.OPERATION_TYPE_FIELD_NAME;
import static org.skillsrock.task.util.Constant.UPDATE_WALLET_BALANCE_URL;
import static org.skillsrock.task.util.Constant.WALLET_ID_FIELD_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skillsrock.task.entity.Wallet;
import org.skillsrock.task.repository.TransactionRepository;
import org.skillsrock.task.repository.WalletRepository;
import org.skillsrock.task.util.TestDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WalletIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WalletRepository walletRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private ObjectMapper objectMapper;


  private Wallet wallet;

  @BeforeEach
  void setUp() {
    transactionRepository.deleteAll();
    walletRepository.deleteAll();

    wallet = TestDataFactory.createWallet();
    wallet.setBalance(BigDecimal.valueOf(MIN_AMOUNT_OF_TEST_WALLET));

    walletRepository.save(wallet);
  }

  @Test
  void getWalletBalance_RequestIsValid_StatusIsOk() throws Exception {
    mockMvc.perform(get(GET_WALLET_BALANCE_URL + wallet.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.walletId").value(wallet.getId().toString()))
        .andExpect(jsonPath("$.balance").value(wallet.getBalance()));
  }

  @Test
  void getWalletBalance_WalletDoesNotExist_StatusIsNotFound() throws Exception {
    mockMvc.perform(get(GET_WALLET_BALANCE_URL + UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(WALLET_NOT_FOUND_EXCEPTION_MESSAGE));
  }

  @Test
  void updateWalletBalance_RequestIsValid_StatusIsOk() throws Exception {
    mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(
                TestDataFactory.createWalletDepositUpdateRequest(
                    wallet.getId(), wallet.getBalance()))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.walletId").value(wallet.getId().toString()))
        .andExpect(jsonPath("$.balance").value(
            (wallet.getBalance().add(wallet.getBalance())).toString()));
  }

  @Test
  void updateWalletBalance_WalletDoesNotExist_StatusIsNotFound() throws Exception {
    mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(
                TestDataFactory.createWalletDepositUpdateRequest(
                    UUID.randomUUID(), wallet.getBalance()))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(WALLET_NOT_FOUND_EXCEPTION_MESSAGE));
  }

  @Test
  void updateWalletBalance_NotEnoughMoneyInWallet_StatusIsBadRequest() throws Exception {
    mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(
                TestDataFactory.createWalletWithdrawUpdateRequest(
                    wallet.getId(), BigDecimal.valueOf(MAX_AMOUNT_OF_TEST_WALLET)))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(NOT_ENOUGH_MONEY_EXCEPTION_MESSAGE));
  }

  @ParameterizedTest
  @MethodSource("nullValueProvider")
  void updateWalletBalance_ValueHasNull_StatusIsBadRequest(
      String json,
      String expectedErrorField) throws Exception {
    mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedErrorField + ": " + VALIDATION_EXCEPTION_MESSAGE));
  }

  private static Stream<Arguments> nullValueProvider() {
    return Stream.of(
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": null, \"" +
                OPERATION_TYPE_FIELD_NAME + "\": \"DEPOSIT\", \"" +
                AMOUNT_FIELD_NAME + "\": 100}", WALLET_ID_FIELD_NAME),
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": \"" + UUID.randomUUID() +
                "\", \"" + OPERATION_TYPE_FIELD_NAME + "\": null, \"" +
                AMOUNT_FIELD_NAME + "\": 100}", OPERATION_TYPE_FIELD_NAME),
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": \"" + UUID.randomUUID() +
                "\", \"" + OPERATION_TYPE_FIELD_NAME + "\": \"DEPOSIT\", \"" +
                AMOUNT_FIELD_NAME + "\": null}", AMOUNT_FIELD_NAME));
  }

  @ParameterizedTest
  @MethodSource("invalidKeyProvider")
  void updateWalletBalance_invalidKeyInJson_StatusIsBadRequest(
      String json,
      String expectedErrorField) throws Exception {
    mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedErrorField + ": " + VALIDATION_EXCEPTION_MESSAGE));
  }

  private static Stream<Arguments> invalidKeyProvider() {
    return Stream.of(
        Arguments.of(
            "{\"" + INVALID_JSON_FIELD_NAME + "\": \"" + UUID.randomUUID() + "\", \"" +
                OPERATION_TYPE_FIELD_NAME + "\": \"DEPOSIT\", \"" +
                AMOUNT_FIELD_NAME + "\": 100}", WALLET_ID_FIELD_NAME),
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": \"" + UUID.randomUUID() +
                "\", \"" + INVALID_JSON_FIELD_NAME + "\": \"DEPOSIT\", \"" +
                AMOUNT_FIELD_NAME + "\": 100}", OPERATION_TYPE_FIELD_NAME),
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": \"" + UUID.randomUUID() +
                "\", \"" + OPERATION_TYPE_FIELD_NAME + "\": \"DEPOSIT\", \"" +
                INVALID_JSON_FIELD_NAME + "\": 100}", AMOUNT_FIELD_NAME));
  }

  @ParameterizedTest
  @MethodSource("invalidValueProvider")
  void updateWalletBalance_ValueIsInvalid_StatusIsBadRequest(String json) throws Exception {
    mockMvc.perform(post(UPDATE_WALLET_BALANCE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(INVALID_JSON_FORMAT_EXCEPTION_MESSAGE));
  }

  private static Stream<Arguments> invalidValueProvider() {
    return Stream.of(
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": \"" + INVALID_JSON_FIELD_NAME + "\", \"" +
                OPERATION_TYPE_FIELD_NAME + "\": \"DEPOSIT\", \"" +
                AMOUNT_FIELD_NAME + "\": 100}"),
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": \"" + UUID.randomUUID() +
                "\", \"" + OPERATION_TYPE_FIELD_NAME + "\": \"" + INVALID_JSON_FIELD_NAME + "\", \"" +
                AMOUNT_FIELD_NAME + "\": 100}"),
        Arguments.of(
            "{\"" + WALLET_ID_FIELD_NAME + "\": \"" + UUID.randomUUID() +
                "\", \"" + OPERATION_TYPE_FIELD_NAME + "\": \"DEPOSIT\", \"" +
                AMOUNT_FIELD_NAME + "\": \"" + INVALID_JSON_FIELD_NAME + "\"}"));
  }


}
