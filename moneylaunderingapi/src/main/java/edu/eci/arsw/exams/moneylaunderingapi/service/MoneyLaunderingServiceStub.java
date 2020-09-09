package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.MoneyLaunderingException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {
    HashMap<String, SuspectAccount>suspectAccountHashMap;

    public MoneyLaunderingServiceStub (){
        suspectAccountHashMap = new HashMap<String, SuspectAccount>();
        SuspectAccount account = new SuspectAccount();
        account.setAccountId("1");
        account.setAmountOfSmallTransactions(10);
        suspectAccountHashMap.put(account.getAccountId(), account);
    }

    @Override
    public void addSuspectAccounts (SuspectAccount suspectAccount) throws MoneyLaunderingException {
        if (suspectAccountHashMap.containsKey(suspectAccount.getAccountId())){
            throw new MoneyLaunderingException("Already exists");
        }else {
            suspectAccountHashMap.put(suspectAccount.getAccountId(), suspectAccount);
        }
    }

    @Override
    public void updateAccountStatus(SuspectAccount suspectAccount) throws MoneyLaunderingException {
        ArrayList<SuspectAccount> suspectAccounts = new ArrayList<>();
        suspectAccountHashMap.entrySet().forEach((input) -> {
            if (input.getKey().equals(suspectAccount.getAccountId())){
                suspectAccounts.add(input.getValue());
            }
        });
        if (suspectAccounts.isEmpty()){
            throw new MoneyLaunderingException("Not Found");
        }
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId){
        return suspectAccountHashMap.get(accountId);
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        ArrayList<SuspectAccount> suspectAccounts = new ArrayList<SuspectAccount>();
        suspectAccountHashMap.entrySet().forEach((input) ->{
            suspectAccounts.add(input.getValue());
        });
        return suspectAccounts;
    }
}