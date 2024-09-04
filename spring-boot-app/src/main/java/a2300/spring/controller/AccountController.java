package a2300.spring.controller;

import a2300.spring.client.ExchangeClient;
import a2300.spring.db.AccountDao;
import a2300.spring.dto.AccountDto;
import a2300.spring.dto.MoneyTransferDto;
import a2300.spring.exception.DirtyTrickException;
import a2300.spring.exception.ResourceNotFoundException;
import a2300.spring.mapper.AccountMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    public static final String DIRTY_TRICK_HEADER = "Dirty-Trick-Header";
    public static final String FAIL_TRANSFER = "FAIL_TRANSFER";

    private final AccountDao dao;
    private final AccountMapper mapper;
    private final ExchangeClient exchangeClient;

    @GetMapping
    @Transactional(readOnly = true)
    public List<AccountDto> getAll() {
        return dao.findAll().stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AccountDto getById(@PathVariable("id") Long id) {
        return dao.findById(id)
                .map(mapper::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    @PostMapping
    @Transactional
    public AccountDto create(@Valid @RequestBody AccountDto account) {
        return Optional.of(account)
                .map(mapper::mapToDb)
                .map(dao::save)
                .map(mapper::mapToDto)
                .get();
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void transfer(@Valid @RequestBody MoneyTransferDto transfer,
                         @RequestHeader(value = DIRTY_TRICK_HEADER, required = false) String dirtyTrick) {

        var sender = dao.findById(transfer.senderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        var recipient = dao.findById(transfer.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        var exchangeRateValue = exchangeClient.getExchangeRate(sender.getCurrency())
                .rates().getOrDefault(recipient.getCurrency(), 1.0);

        sender.setMoneyAmount(sender.getMoneyAmount() - transfer.moneyAmount());
        dao.save(sender);

        if (FAIL_TRANSFER.equals(dirtyTrick)) {
            throw new DirtyTrickException("Error during money transfer");
        }

        recipient.setMoneyAmount(recipient.getMoneyAmount() + (int) (transfer.moneyAmount() * exchangeRateValue));
        dao.save(recipient);
    }
}
