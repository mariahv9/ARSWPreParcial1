package edu.eci.arsw.exams.moneylaunderingapi;

import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import edu.eci.arsw.exams.moneylaunderingapi.service.MoneyLaunderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/fraud-bank-accounts")
public class MoneyLaunderingController {

    @Autowired
    MoneyLaunderingService moneyLaunderingService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> offendingAccounts() {
        List<SuspectAccount> suspectAccounts = null;
        try {
            suspectAccounts = moneyLaunderingService.getSuspectAccounts();
            return new ResponseEntity<>(suspectAccounts, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("ERROR 500", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping (value = "/{accountId}", method = RequestMethod.GET)
    public ResponseEntity<?> accountStatus (@PathVariable String accountId) {
        try {
            return new ResponseEntity<>(moneyLaunderingService.getAccountStatus(accountId), HttpStatus.ACCEPTED);
        }catch (Exception e){
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping (value = "/{accountId}", method = RequestMethod.PUT)
    public ResponseEntity<?> suspectAccount (@PathVariable String accountId, @RequestBody SuspectAccount suspectAccount){
        try {
            moneyLaunderingService.updateAccountStatus(suspectAccount);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}