package com.homework.java;

import com.homework.java.db.DB;
import com.homework.java.db.Database;
import com.homework.java.selects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {
        Database.getInstance().setDefaultDB(DB.POSTGRES);
        workWithClients();
    }

    public static void workWithClients() {
        ClientService clientService = new ClientService();
        create(clientService);
        System.out.println("\n\n");
        getById(clientService);
        System.out.println("\n\n");
        setName(clientService);
        System.out.println("\n\n");
        deleteById(clientService);
        System.out.println("\n\n");
        listAll(clientService);
    }

    public static void create(ClientService clientService) {
        long id = clientService.create("Name 1");
        System.out.println(id);
        System.out.println(clientService.getById(id));
    }

    public static void getById(ClientService clientService) {
        System.out.println(clientService.getById(5));

        //System.out.println(clientService.getById(1000));     // error Id відсутня в таблиці
    }

    public static void setName(ClientService clientService) {
        long id = clientService.create("Test Name");
        System.out.println(id);
        System.out.println(clientService.getById(id));
        clientService.setName(id, "Normal Name");
        System.out.println(clientService.getById(id));
    }

    private static void deleteById(ClientService clientService) {
        long id = clientService.create("name for delete");
        System.out.println(id);
        System.out.println(clientService.getById(id));
        clientService.deleteById(id);
        // System.out.println(clientService.getById(id));  // error Id відсутня в таблиці - because we delete it
    }

    private static void listAll(ClientService clientService) {
        for (Client client : clientService.listAll()) {
            System.out.println(client);
        }
    }

    public static void selects() {
        List<List<? extends ResPrint>> selectsResults = new ArrayList<>();

        selectsResults.add(DatabaseQueryService.findMaxProjectsClient());
        selectsResults.add(DatabaseQueryService.findMaxProjectsWorker());
        selectsResults.add(DatabaseQueryService.findLongestProject());
        selectsResults.add(DatabaseQueryService.findMaxSalaryWorker());
        selectsResults.add(DatabaseQueryService.findYoungestEldestWorkers());
        selectsResults.add(DatabaseQueryService.printProjectPrices());

        int i;
        for (List<? extends ResPrint> selectsResult : selectsResults) {
            for (i = 1; i <= selectsResult.size(); i++) {
                System.out.print(i);
                System.out.print(" - ");
                System.out.println(selectsResult.get(i - 1));
            }
            System.out.println();
        }

        Database.close();
    }
}