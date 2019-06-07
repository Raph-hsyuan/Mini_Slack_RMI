package minislack;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String userName = "UNKNOWN";
        MiniSlackClient client;
        try {
            client = new MiniSlackClient();
            System.out.println("##Service Connected##");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("##Connection Failure##");
            return;
        }

        System.out.println("## Welcome to mini slack ## written by huangshenyuan and liujiaqi");
        while (!client.isOnline()) {
            System.out.println("> Please Enter USERNAME : ");
            userName = sc.nextLine();
            System.out.println("> Please Enter PASSWORD : ");
            client.login(userName, sc.nextLine());
        }
        System.out.println("## Welcome " + userName + " ##");
        System.out.println("## Enter 'help' see helps ##");
        printHelp();

        while (true) {
            System.out.println("> Please Enter Your Command :");
            switch (sc.nextLine()) {
                case "help":
                    printHelp();
                    break;
                case "mygroups":
                    System.out.println(client.getMyGroups());
                    break;
                case "allgroups":
                    System.out.println(client.getAllGroups());
                    break;
                case "send":
                    System.out.println("> Send to which group?");
                    String group = sc.nextLine();
                    System.out.println("> Your msg :");
                    client.send(sc.nextLine(),group);
                case "enter":
                    break;
                case "subscribe":
                    break;
                case "unsubscribe":
                    break;
                case "exit":
                    client.logout();
                    return;
                default:
                    System.out.println("> Unknown Command");
            }
        }

    }

    static void printHelp() {
        System.out.println("## Enter 'allgroups' see all groups ##");
        System.out.println("## Enter 'mygroups' see my groups ##");
        System.out.println("## Enter 'enter' choose a group to enter ##");
        System.out.println("## Enter 'subscribe' choose a group to subscribe ##");
        System.out.println("## Enter 'unsubscribe' choose a group to unsubscribe ##");
        System.out.println("## Enter 'send' choose a group to send ##");
        System.out.println("## Enter 'exit' logout and exit ##");

    }

}