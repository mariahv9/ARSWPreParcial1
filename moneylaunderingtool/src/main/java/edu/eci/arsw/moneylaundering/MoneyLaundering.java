package edu.eci.arsw.moneylaundering;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

public class MoneyLaundering {
    public static TransactionReader transactionReader;
    public static Object monitor = new Object();
    public static boolean pause = false;
    public static TransactionAnalyzer transactionAnalyzer;
    public static AtomicInteger amountOfFilesProcessed, finish;
    private int amountOfFilesTotal;
    private ThreadImp[] threads;

    public MoneyLaundering() {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
        finish = new AtomicInteger();
    }

    public void processTransactionData(int threadsCant, MoneyLaundering moneyLaundering) {
        threads = new ThreadImp [threadsCant];
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        ArrayList<ArrayList> threadsDivide = divideThreads(transactionFiles, threadsCant);
        for (int i = 0; i < threadsDivide.size(); i++){
            threads [i] = new ThreadImp(threadsDivide.get(i));
            threads [i].start();
        }
        for (int i = 0; i < threads.length; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(File transactionFile : transactionFiles)
        {            
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for(Transaction transaction : transactions)
            {
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
    }

    //punto 1
    public ArrayList<ArrayList> divideThreads (List<File> files, int threadsCant) {
        int cont = 0;
        ArrayList<ArrayList> divideThreads = new ArrayList<>();
        for (int i = 0; i < threadsCant; i++){
             divideThreads.add(new ArrayList<File>());
        }
        for (int i = 0; i < files.size(); i++){
            divideThreads.get(cont).add(files.get(i));
            cont++;
            if (cont == threadsCant){
                cont = 0;
            }
        }
        return divideThreads;
    }

    //punto 2
    public void pause (){
        if (pause) {
            pause = false;
            synchronized (monitor){
                monitor.notifyAll();
            }
            System.out.println ("It's Running");
        } else {
            pause = true;
            System.out.println("It's pause");
        }
    }

    public List<String> getOffendingAccounts() {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList() {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public static void main(String[] args) {
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        int cant = 5;
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData(cant, moneyLaundering));
        processingThread.start();
        while(finish.get() != cant) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (finish.get() != cant) {
                moneyLaundering.pause();
                String output = "It's processed %d out of %d files.\nFound %d suspect accounts of the files.";
                List<String> suspectAcc = moneyLaundering.getOffendingAccounts();
                output = String.format(output, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, suspectAcc.size());
                System.out.println(output);
            }
            if (line.contains("exit"))
                break;
        }

        String message = "End -> Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
        List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
        String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
        message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
        System.out.println(message);
    }
}