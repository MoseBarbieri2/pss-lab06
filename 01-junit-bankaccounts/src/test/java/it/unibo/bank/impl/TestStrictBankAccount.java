package it.unibo.bank.impl;

import it.unibo.bank.api.AccountHolder;
import it.unibo.bank.api.BankAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.instrument.UnmodifiableClassException;

import static it.unibo.bank.impl.SimpleBankAccount.MANAGEMENT_FEE;
import static it.unibo.bank.impl.StrictBankAccount.TRANSACTION_FEE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link StrictBankAccount} class.
 */
class TestStrictBankAccount {
    private static final int AMOUNT = 100;

    // Create a new AccountHolder and a StrictBankAccount for it each time tests are executed.
    private AccountHolder mRossi;
    private BankAccount bankAccount;

    /**
     * Prepare the tests.
     */
    @BeforeEach
    public void setUp() {
        this.mRossi = new AccountHolder("Mario", "Rossi", 1);
        this.bankAccount = new StrictBankAccount(mRossi, 0.0);
    }

    /**
     * Test the initial state of the StrictBankAccount.
     */
    @Test
    public void testInitialization() {
        assertEquals(0.0, bankAccount.getBalance());
        assertEquals(0, bankAccount.getTransactionsCount());
        assertEquals(mRossi, bankAccount.getAccountHolder());
    }

    /**
     * Perform a deposit of 100€, compute the management fees, and check that the balance is correctly reduced.
     */
    @Test
    public void testManagementFees() {
        bankAccount.deposit(bankAccount.getAccountHolder().getUserID(), AMOUNT);
        assertEquals(AMOUNT, bankAccount.getBalance());
        assertEquals(1, bankAccount.getTransactionsCount());
        final double feeAmount = MANAGEMENT_FEE + bankAccount.getTransactionsCount() * TRANSACTION_FEE;
        bankAccount.chargeManagementFees(bankAccount.getAccountHolder().getUserID());
        assertEquals(AMOUNT - feeAmount, bankAccount.getBalance());
    }

    /**
     * Test that withdrawing a negative amount causes a failure.
     */
    @Test
    public void testNegativeWithdraw() {
        assertEquals(0.0, bankAccount.getBalance());
        try {
            bankAccount.withdraw(bankAccount.getAccountHolder().getUserID(), -1);
            assertEquals(0, bankAccount.getTransactionsCount());
            fail("You can't withdraw a bank account");
        } catch ( final IllegalArgumentException e) {
            assertEquals(0.0, bankAccount.getBalance());
        }
    }

    /**
     * Test that withdrawing more money than it is in the account is not allowed.
     */
    @Test
    public void testWithdrawingTooMuch() {
        try {
            bankAccount.withdraw(bankAccount.getAccountHolder().getUserID(), 1);
            assertEquals(0, bankAccount.getTransactionsCount());
            fail("You can't withdraw more than your amount");
        } catch (final IllegalArgumentException e){
            assertEquals(0.0, bankAccount.getBalance());
        }
    }
}
