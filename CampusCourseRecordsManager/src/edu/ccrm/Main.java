package edu.ccrm;

import edu.ccrm.cli.CLIInterface;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Campus Course & Records Manager...");

        // Demonstrate assertions (enable with -ea VM option)
        assert args.length >= 0 : "Arguments should not be negative";

        try {
            CLIInterface cli = new CLIInterface();
            cli.start();
        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}