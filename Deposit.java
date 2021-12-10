package org.darmokhval.tasks7;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public abstract class Deposit implements Comparable<Deposit>{
    protected BigDecimal amount;
    protected Integer period;
    public Deposit(BigDecimal depositAmount, Integer depositPeriod) {
        this.amount = depositAmount;
        this.period = depositPeriod;
    }
    public abstract BigDecimal income();
    public abstract BigDecimal getFinalDepositAmount();

    public static void main(String[] args) {
        BaseDeposit b1 = new BaseDeposit(new BigDecimal("1000"), 3);
        //System.out.println(b1.income());
        SpecialDeposit b2 = new SpecialDeposit(new BigDecimal("1000"), 2);
        //System.out.println(b2.income());
        LongDeposit b3 = new LongDeposit(new BigDecimal("1000"), 30);
        //System.out.println(b3.income());
        Client bob = new Client();
        bob.addDeposit(new BaseDeposit(new BigDecimal("100"), 12));
        bob.addDeposit(new BaseDeposit(new BigDecimal("200"), 12));
        bob.addDeposit(new BaseDeposit(new BigDecimal("300"), 12));
        bob.addDeposit(new BaseDeposit(new BigDecimal("400"), 12));
        bob.addDeposit(b1);
        bob.addDeposit(b2);
        bob.addDeposit(b3);
        bob.addDeposit(new LongDeposit(new BigDecimal("1000"), 20));
        System.out.println(bob.getIncomeByNumber(0));
        System.out.println(bob.maxIncome());
        System.out.println("Total income = " + bob.totalIncome());
        System.out.println(b3.canToProlong());
        System.out.println(b2.canToProlong());
        System.out.println(b3.getFinalDepositAmount());
        System.out.println(b3.income());
        bob.sortDeposit();
        /*for(Deposit d: bob) {

        }*/
    }
}
class BaseDeposit extends Deposit {

    private static final BigDecimal PERCENT = new BigDecimal("0.05");

    public BaseDeposit(BigDecimal amount, Integer period) {
        super(amount, period);
    }
    @Override
    public BigDecimal income() {
        BigDecimal income = amount;
        for (int i = 0; i < period; i++) {
            income = income.add(income.multiply(PERCENT).setScale(2, RoundingMode.HALF_EVEN));
        }
        income = income.subtract(amount);
        return income;
    }
    @Override
    public BigDecimal getFinalDepositAmount() {
        BigDecimal finalAmount = amount;
        finalAmount = finalAmount.add(income());
        return finalAmount;
    }
    @Override
    public int compareTo(Deposit deposit) {
        int finalAmount = 0;
        if (deposit != null) {
            finalAmount = getFinalDepositAmount().compareTo(deposit.getFinalDepositAmount());
        }
        return finalAmount;
    }
}
class SpecialDeposit extends Deposit implements Prolongable {

    private static BigDecimal percent = new BigDecimal("0.01");

    public SpecialDeposit(BigDecimal amount, Integer period) {
        super(amount, period);
    }
    public boolean canToProlong() {
        int minimumAmountForProlongation = 1000;
        return amount.compareTo(BigDecimal.valueOf(minimumAmountForProlongation)) > 0;
    }
    @Override
    public BigDecimal income() {
        BigDecimal income = amount;
        for (int i = 0; i < period; i++) {
            income = income.add(income.multiply(percent).setScale(2, RoundingMode.HALF_EVEN));
            percent = percent.add(new BigDecimal("0.01"));
        }
        income = income.subtract(amount);
        return income;
    }
    @Override
    public BigDecimal getFinalDepositAmount() {
        BigDecimal finalAmount = amount;
        finalAmount = finalAmount.add(income());
        return finalAmount;
    }
    @Override
    public int compareTo(Deposit deposit) {
        return getFinalDepositAmount().compareTo(deposit.getFinalDepositAmount());
    }
}
class LongDeposit extends Deposit implements Prolongable{

    private static final BigDecimal PERCENT = new BigDecimal("0.15");

    public LongDeposit(BigDecimal amount, Integer period) {
        super(amount, period);
    }
    public boolean canToProlong() {
        int maximumPeriodOfProlongation = 36;
        return period <= maximumPeriodOfProlongation;
    }
    @Override
    public BigDecimal income() {
        BigDecimal income = amount;
        int idlePeriod = 6;
        for (int i = idlePeriod; i < period; i++) {
            income = income.add(income.multiply(PERCENT).setScale(2, RoundingMode.HALF_EVEN));
        }
        income = income.subtract(amount);
        return income;
    }
    @Override
    public BigDecimal getFinalDepositAmount() {
        BigDecimal finalAmount = amount;
        finalAmount = finalAmount.add(income());
        return finalAmount;
    }
    @Override
    public int compareTo(Deposit deposit) {
        return getFinalDepositAmount().compareTo(deposit.getFinalDepositAmount());
    }
}
class Client implements Iterable<Deposit>{
    private final Deposit[] depositsArray;
    private int nextCellIndex = 0;
    private int count = 0;
    public Client() {
        this.depositsArray = new Deposit[10];
    }
    public boolean addDeposit(Deposit deposit) {              //return value of the method is never used?
        if(nextCellIndex < depositsArray.length) {
            depositsArray[nextCellIndex] = deposit;
            nextCellIndex++;
            return true;
        }
        return false;
    }
    public BigDecimal totalIncome() {
        BigDecimal totalIncome = BigDecimal.ZERO;
        for (Deposit deposit: depositsArray) {
            if (deposit != null) {
                totalIncome = totalIncome.add(deposit.income());
            }
        }
        return totalIncome;
    }
    public BigDecimal maxIncome() {
        BigDecimal maxIncome = BigDecimal.ZERO;
        for (Deposit deposit: depositsArray) {
            if (deposit != null) {
                int temporaryComparison = maxIncome.compareTo(deposit.income());
                if (temporaryComparison < 0) {
                    maxIncome = deposit.income();
                }
            }
        }
        return maxIncome;
    }
    public BigDecimal getIncomeByNumber(Integer integer) {
        var deposit = depositsArray[integer];                 // Deposit?
        if (deposit == null) {
            return BigDecimal.ZERO;
        }
        else {
            return deposit.income();
        }
    }
    public void sortDeposit() {
        Arrays.sort(depositsArray, Comparator.reverseOrder());
    }
    @Override
    public Iterator<Deposit> iterator() {
        return new DepositIterator();
    }
    class DepositIterator implements Iterator<Deposit> {
        private int index = 0;
        public boolean hasNext() {
            return index < depositsArray.length;
        }
        public Deposit next() {
            return depositsArray[index++];
        }
    }
}
interface Prolongable {
    boolean canToProlong();
}

