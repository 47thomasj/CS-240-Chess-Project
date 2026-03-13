package menu;

import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    
    private ArrayList<MenuOption> options;
    private String abortOption;
    private String helpString;
    private String titleString;

    public static String EXIT_OPTION = "ReturnNoneFromThisMenu";

    public Menu(String abortOption, String helpString, String titleString) {
        this.options = new ArrayList<>();
        this.abortOption = abortOption;
        this.helpString = helpString;
        this.titleString = titleString;
    }

    public void addOption(MenuOption option) {
        options.add(option);
    }

    public void printMenu() {
        System.out.println(titleString);
        System.out.println("Options:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println(i + 1 + ". " + options.get(i).toString());
        }
        System.out.println(options.size() + 1 + ". " + abortOption);
        System.out.println(options.size() + 2 + ". Help\n");
    }

    public Object interactWithMenu() {
        printMenu();
        try (Scanner scanner = new Scanner(System.in)) {
            int choice = scanner.nextInt();
            if (choice == options.size() + 1) {
                return EXIT_OPTION;
            }
            if (choice == options.size() + 2) {
                System.out.println(helpString);
            }
            return options.get(choice - 1).execute();
        }
        catch (Exception e) {
            System.out.println("Please select a valid menu option. (1-" + (options.size() + 2) + ")");
            return interactWithMenu();
        }
    }

}
