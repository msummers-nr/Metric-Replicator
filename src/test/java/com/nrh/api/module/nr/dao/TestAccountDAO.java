package com.nrh.api.module.nr.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestAccountDAO {
  
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AccountRepository accountRepo;

  private static final int accountId = 100;
  private static final String accountName = "Test Account";

  @Test
	public void lookupDefaultAccount() {
    
    // Create an account
    Account testAccount = new Account(accountId);
    testAccount.setAccountName(accountName);
    entityManager.persist(testAccount);
    entityManager.flush();

    // Lookup the test account
    Account foundAccount = accountRepo.findByAccountName(accountName);

    assertEquals(foundAccount.getAccountId(), testAccount.getAccountId());
  }
}