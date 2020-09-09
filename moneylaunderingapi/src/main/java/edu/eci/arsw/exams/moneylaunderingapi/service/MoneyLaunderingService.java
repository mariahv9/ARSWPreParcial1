package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.MoneyLaunderingException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import java.util.List;

public interface MoneyLaunderingService {
    void updateAccountStatus(SuspectAccount suspectAccount) throws MoneyLaunderingException;
    void addSuspectAccounts (SuspectAccount suspectAccount) throws MoneyLaunderingException;
    SuspectAccount getAccountStatus(String accountId);
    List<SuspectAccount> getSuspectAccounts();
}
