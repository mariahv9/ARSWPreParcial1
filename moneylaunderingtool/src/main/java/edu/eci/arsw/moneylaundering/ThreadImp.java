package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.lang.*;
import java.util.List;

public class ThreadImp extends Thread {
    List<File> files;

    public ThreadImp (List<File> files) {
        this.files = files;
    }

    @Override
    public void run() {
        for (File file : files) {
            List<Transaction> transactions = MoneyLaundering.transactionReader.readTransactionsFromFile(file);
            for (Transaction transaction : transactions) {
                synchronized (MoneyLaundering.monitor) {
                    if (MoneyLaundering.pause) {
                        try {
                            MoneyLaundering.monitor.wait();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
//                MoneyLaundering.transactionAnalyzer.addTransaction(transaction);
            }
            MoneyLaundering.amountOfFilesProcessed.incrementAndGet();
        }
        MoneyLaundering.finish.incrementAndGet();
    }
}