package org.darmokhval.tasks7;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Comparator;

public abstract class Deposit implements Comparable<Deposit>{
    protected BigDecimal amount;
    protected Integer period;
    public Deposit(BigDecimal depositAmount, Integer depositPeriod) {
        this.amount = depositAmount;
        this.period = depositPeriod;
    }
    public abstract BigDecimal income();
    public abstract BigDecimal getFinalDepositAmount();
    public abstract boolean canToProlong();

    public int compareTo(Deposit deposit) {
        return getFinalDepositAmount().compareTo(deposit.getFinalDepositAmount());
    }

    public static void main(String[] args) {
        BaseDeposit b1 = new BaseDeposit(new BigDecimal("3000"), 3);
        //System.out.println(b1.income());
        SpecialDeposit b2 = new SpecialDeposit(new BigDecimal("2000"), 2);
        //System.out.println(b2.income());
        LongDeposit b3 = new LongDeposit(new BigDecimal("1000"), 8);
        //System.out.println(b3.income());
        Client bob = new Client();
        bob.addDeposit(new BaseDeposit(new BigDecimal("100"), 12));

        bob.addDeposit(new SpecialDeposit(new BigDecimal("2000"), 12));
        bob.addDeposit(new BaseDeposit(new BigDecimal("400"), 12));
        bob.addDeposit(new BaseDeposit(new BigDecimal("400"), 19));
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
        for(Deposit d: bob) {
            if (d != null) System.out.println(d.amount + " " + d.period + " " + d.getFinalDepositAmount());
        }
        System.out.println(bob.countPossibleToProlongDeposit());
        System.out.println(bob.countProlong());
    }
}
class BaseDeposit extends Deposit {

    private static final BigDecimal PERCENT = new BigDecimal("0.05");

    public BaseDeposit(BigDecimal amount, Integer period) {
        super(amount, period);
    }
    public boolean canToProlong() {
        return false;
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
}
class Client implements Iterable<Deposit>, Comparator<Deposit>{
    private final Deposit[] depositsArray;
    private int nextCellIndex = 0;
    private int numberOfClientDeposits = 0;
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
        Deposit deposit = depositsArray[integer];
        if (deposit == null) {
            return BigDecimal.ZERO;
        }
        else {
            return deposit.income();
        }
    }
    public int countProlong() {
        int count = 0;
        for(Deposit dep: depositsArray) {
            if (dep != null && dep.canToProlong()) {
                count++;
            }
        }
        return count;
    }
    public void sortDeposit() {
        Arrays.sort(depositsArray, Comparator.nullsFirst(Comparator.reverseOrder()));
    }

    private <T> boolean isProlongable(T objects) { //how it works?
        return objects instanceof Prolongable;
    }
    private boolean filterCanProlong(Deposit object) {
        return((Prolongable) object).canToProlong();
    }
    public int countPossibleToProlongDeposit() {
        return (int) Arrays.stream(depositsArray)
                .filter(Objects::nonNull)
                .filter(this::isProlongable)
                .filter(this::filterCanProlong)
                .count();
        }

    @Override
    public int compare(Deposit dep1, Deposit dep2) {
        return dep1.getFinalDepositAmount().compareTo(dep2.getFinalDepositAmount());
    }
    @Override
    public Iterator<Deposit> iterator() {
        return new DepositIterator();
    }
    class DepositIterator implements Iterator<Deposit> {
        private String noDeposit = "There are no deposits left";
        private int index = 0;
        public boolean hasNext() {
            return index < depositsArray.length;
        }
        public Deposit next() {
            if(hasNext())  return depositsArray[index++];
            else throw new NoSuchElementException(noDeposit);
        }
    }
}
interface Prolongable {
    boolean canToProlong();
}

