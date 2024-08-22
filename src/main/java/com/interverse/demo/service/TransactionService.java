package com.interverse.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interverse.demo.dto.TransactionDto;
import com.interverse.demo.model.Transaction;
import com.interverse.demo.model.TransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionRepository transRepo;
	
	public TransactionDto convert(Transaction transaction) {
		TransactionDto transactionDto = new TransactionDto();
		
		transactionDto.setId(transaction.getId());
		transactionDto.setTransactionNo(transaction.getTransactionNo());
		transactionDto.setPaymentMethod(transaction.getPaymentMethod());
		transactionDto.setAmount(transaction.getAmount());
		transactionDto.setStatus(transaction.getStatus());
		transactionDto.setAdded(transaction.getAdded());
		transactionDto.setUser(transaction.getUser());
		
		return transactionDto;
	}
	
	public TransactionDto addTransaction(Transaction transaction) {
		
		return convert((transRepo.save(transaction)));
	}
	
	public List<TransactionDto> findMyTransaction(Integer userId) {
		
		List<Transaction> transactionList = transRepo.findByUserId(userId);
		List<TransactionDto> transactionDtoList = transactionList.stream()
		        .map(this::convert)
		        .collect(Collectors.toList());
		
		return transactionDtoList;
	}

	

}