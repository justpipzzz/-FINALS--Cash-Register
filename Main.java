import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class Main {
    static Scanner input = new Scanner(System.in);

    static LocalDateTime myDateObj = LocalDateTime.now();
    static DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    static String formattedDate = myDateObj.format(myFormatObj);

    static ArrayList <String> userName = new ArrayList<>();
    static ArrayList <String> passWord = new ArrayList<>();

    static ArrayList <String> menuCode = new ArrayList<>();
    static ArrayList <String> menuItems = new ArrayList<>();
    static ArrayList <Double> menuPrice = new ArrayList<>();

    static ArrayList<String> buyCode = new ArrayList<>();
    static ArrayList<String> buyItem = new ArrayList<>();
    static ArrayList<Double> buyPrice = new ArrayList<>();
    static ArrayList<Integer> buyQuan = new ArrayList<>();

    static String logUname;

    public static void showClear(){  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }  

    public static void showSignUp(ArrayList <String> userName, ArrayList <String> passWord){
        showClear();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("ACCOUNT SIGN-UP");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Please input your username using this format:\n\"ANC_(NickName)\"");
        
        while (true) { 
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.print("Cashier Username: ");
            String cashUname = input.nextLine(); 
            
            Pattern pattern = Pattern.compile("^ANC_[A-Za-z]+$");//regex for username
            Matcher matcher = pattern.matcher(cashUname);
            
            if (!matcher.matches()){

                System.out.println("Please follow the correct format and try again.");
                continue;//back to input
            }

            userName.add(cashUname);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Username \"" + cashUname + "\" Accepted!");//uname confirmation

            break;

        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Please input your password with this criteria:");
        System.out.println("\n- Must be 8-15 characters long");
        System.out.println("- Must contain at least one(1) lowercase and uppercase letter");
        System.out.println("- Must include at least one(1) special character\n");
        while (true){
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.print("Cashier Password: ");
            String cashPword = input.nextLine();

            Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z\\d]).{8,15}$");//regex for password
            Matcher matcher = pattern.matcher(cashPword);

            if (!matcher.matches()){
                System.out.println("Please follow the correct format and try again.");
                continue;
            }

            passWord.add(cashPword);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Password Accepted!");
            
            break;
            
        }
        showClear();
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Account Created Successfully!");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Redirecting to Home Page...");


    }

    public static void showHistory() {
        File dir = new File("transactions");

        if (!dir.exists() || dir.listFiles() == null || dir.listFiles().length == 0) {
            System.out.println("No transactions found.");
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));

        System.out.println("Available Transactions:\n");
        for (int i = 0; i < files.length; i++) {
            System.out.println("[" + (i + 1) + "]" + " - " + files[i].getName());
        }

        try {
            System.out.print("\nEnter transaction number to view: ");
            int choice = Integer.parseInt(input.nextLine());
            if (choice >= 1 && choice <= files.length) {
                File selected = files[choice - 1];
                System.out.println("\n===== Transaction Details =====");
                BufferedReader reader = new BufferedReader(new FileReader(selected));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("Error reading transaction: " + e.getMessage());
        }
    }

    public static void showSaveReceipt(ArrayList<String> buyCode, ArrayList<String> buyItem, ArrayList<Integer> buyQuan, ArrayList<Double> buyPrice, double payment, double change) {
        try {
            File dir = new File("transactions");
            if (!dir.exists()) dir.mkdir();

            int fileIndex = 1;
            File receiptFile;
            do {
                receiptFile = new File(dir, String.format("transaction_%02d.txt", fileIndex));
                fileIndex++;
            } while (receiptFile.exists());

            try (PrintWriter writer = new PrintWriter(new FileWriter(receiptFile))) {
                writer.println("============================================");
                writer.println("        OFFICIAL TRANSACTION RECEIPT     ");
                writer.println("============================================");
                writer.println("Date and Time: " + formattedDate);
                writer.println("Cashier Username: " + logUname + "\n");
                writer.println("Receipt:");
            
                double total = 0;
                for (int i = 0; i < buyItem.size(); i++) {
                    double subtotal = buyQuan.get(i) * buyPrice.get(i);
                    writer.printf("%-5s %-15s %2d    Php %7.2f    Php %7.2f\n", buyCode.get(i), buyItem.get(i), buyQuan.get(i), buyPrice.get(i), subtotal);
                    total += subtotal;
                }

                writer.println("--------------------------------------------");
                writer.printf("Your Total: Php %7.2f\n", total);
                writer.printf("Payment: Php %7.2f\n", payment);
                writer.printf("Change: Php %7.2f\n", change);
                writer.println("============================================");
            }

            System.out.println("Receipt saved as " + receiptFile.getName());

        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }


    public static void showEditQuantity(ArrayList<String> buyCode, ArrayList<String> buyItem, ArrayList<Integer> buyQuan, ArrayList<String> menuCode, ArrayList<String> menuItem, ArrayList<Double> menuPrice, ArrayList<Double> buyPrice) {
        showClear();
        System.out.println("Edit Item Quantity");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        for (int i = 0; i < buyCode.size(); i++) {
            System.out.printf("[%d] - %s (%s) - Current Quantity: %d\n", i + 1, buyItem.get(i), buyCode.get(i), buyQuan.get(i));
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        try {
            System.out.print("Enter the item number to edit: ");
            int index = input.nextInt() - 1;

            if (index >= 0 && index < buyQuan.size()) {
                System.out.print("Enter new quantity for " + buyItem.get(index) + ": ");
                int newQty = input.nextInt();
                if (newQty > 0) {
                    buyQuan.set(index, newQty);
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println("Quantity updated successfully.");
                    input.nextLine();
                } else {
                    System.out.println("Quantity must be greater than 0.");

                }
            } else {
                showClear();
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Invalid item number.");
                input.nextLine();
            }
        } catch (Exception e) {
            showClear();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Invalid input. Please try again.");
            input.nextLine(); // clear buffer
        }

        showReceipt(menuCode, menuItem, menuPrice, buyCode, buyItem, buyPrice, buyQuan);
    }

    public static void showRemoveBuyItem(){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Which item will be removed? Type \"None\" if none. Fill out the necesary fields:");
            System.out.print("Enter Menu Code: ");
            String remMC = input.nextLine().toUpperCase();
            if (buyCode.contains(remMC)){
                for (int i = 0; i < buyCode.size(); i++){
                    if (remMC.contentEquals(buyCode.get(i))){
                        buyCode.remove(i);
                        buyItem.remove(i);
                        buyPrice.remove(i);
                        buyQuan.remove(i);
                        showClear();
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        System.out.println("Item successfully removed!");
                    }
                }
                
                showReceipt(menuCode, menuItems, menuPrice, buyCode, buyItem, buyPrice, buyQuan);
            } else if (remMC.equalsIgnoreCase("NONE")){
                showClear();
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Back to Receipt...");
                showReceipt(menuCode, menuItems, menuPrice, buyCode, buyItem, buyPrice, buyQuan);
            } else {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Item Not Found");
                showReceipt(menuCode, menuItems, menuPrice, buyCode, buyItem, buyPrice, buyQuan);
    }
    }

    public static void showRemoveMenuItem(ArrayList<String> menuCode, ArrayList<String> menuItems, ArrayList<Double> menuPrice) {
            System.out.println("Aling Nena's Eatery Menu:");
            for (int i = 0; i < menuItems.size(); i++){
                System.out.print(menuCode.get(i) + "\t");
                System.out.print(menuItems.get(i) + "\t");
                System.out.printf("Php %.2f\n", menuPrice.get(i));
            }

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Which item will be removed? Fill out the necesary fields:");
            System.out.print("Enter Menu Code: ");
            String remMC = input.nextLine().toUpperCase();
            for (int i = 0; i < menuCode.size(); i++){
                if (remMC.contentEquals(menuCode.get(i))){
                    menuCode.remove(i);
                    menuItems.remove(i);
                    menuPrice.remove(i);
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println("Item successfully removed!");
                }
            }
    }

    public static void showAddMenu(ArrayList<String> menuCode, ArrayList<String> menuItems, ArrayList<Double> menuPrice) {
        System.out.println("To add a menu item, fill out the necessary fields:");
        boolean isRunning = true;

        while(isRunning){
            System.out.print("Enter Menu Code: ");
            String addMC = input.nextLine().toUpperCase();
            menuCode.add(addMC);
            
            System.out.print("Enter Menu Item: ");
            String addMI = input.nextLine();
            menuItems.add(addMI);
            
            System.out.print("Enter Menu Price: Php ");
            double addMP;
            while (true) { 
                try {
                    addMP = input.nextDouble();

                    if (addMP <= 0) {
                        System.out.println("Input must be greater than zero. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please try again.");
                    continue;
                }

                break;
            }
            input.nextLine();
            menuPrice.add(addMP);
            
            System.out.println("Would you like to add another?");
            System.out.print("Y or N: ");
            char ynOpt = input.next().toLowerCase().charAt(0);
            input.nextLine();
            
            switch (ynOpt) {
                case 'y':
                    isRunning = true;
                    break;
                case 'n':
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println("Item/s successfully added!");
                    isRunning = false;
                    return;
                default:
                    System.out.print("Invalid input. Please try again.");
                    isRunning = true;
                    break;
            }
        }
    }

    public static void showCustom(ArrayList<String> menuCode, ArrayList<String> menuItems, ArrayList<Double> menuPrice) {
        showClear();
        System.out.println("Welcome to Menu Customization! What would you like to do?");
        boolean isRunning = true;
        boolean isRunningTwo = true;
        String ynOpt;

        do{
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("1 - Add New Item \n2 - Remove Item \n3 - Clear Menu \n4 - Back");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.print("Enter your option: ");
            int numOpt = input.nextInt();
            input.nextLine();
            switch (numOpt) {
                case 1:
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    showAddMenu(menuCode, menuItems, menuPrice);
                    isRunning = true;
                    break;
                case 2:
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    showRemoveMenuItem(menuCode, menuItems, menuPrice);
                    isRunning = true;
                    break;
                case 3:
                    showClear();
                    isRunningTwo = true;
                    while (isRunningTwo){
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        System.out.println("Are you sure you want to clear all menu items?:");
                        System.out.print("Y or N: ");
                        ynOpt = input.nextLine().toLowerCase();
                        if (ynOpt.equals("y")){
                            menuCode.clear();
                            menuItems.clear();
                            menuPrice.clear();
                            showClear();
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            System.out.println("Menu Board cleared successfully!");
                            isRunningTwo = false;
                        } else if (ynOpt.equals("n")){
                            showClear();
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            System.out.println("Clear Menu cancelled.");
                            isRunningTwo = false;
                        } else{
                            System.out.println("Invalid input. Please try again");
                        }
                    }   break;
                case 4:
                    isRunning = false;
                    break;
                default:
                    break;
            }
        }while(isRunning);
    }

    public static void showReceipt(ArrayList<String> menuCode, ArrayList<String> menuItem, ArrayList<Double> menuPrice, ArrayList<String> buyCode, ArrayList<String> buyItem, ArrayList<Double> buyPrice, ArrayList<Integer> buyQuan){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Reciept: ");
        double totalAll = 0;
        boolean isRunning = true;

        for (int i = 0; i < buyCode.size(); i++) {
            double prodTotal = buyQuan.get(i) * buyPrice.get(i);
            totalAll += prodTotal;
            System.out.println(buyCode.get(i) + "\t" + buyItem.get(i) + "\t" + buyQuan.get(i) + "\tPhp " + buyPrice.get(i) + "\tPhp " + prodTotal);
        }
        System.out.println("\t\t\t\t___________________________");
        System.out.printf("\t\t\t\tYour Total:\tPhp %.2f\n", totalAll);

        System.out.println("Do you want to edit your transaction?");
        System.out.println("1 - Add Item/s");
        System.out.println("2 - Remove Item/s");
        System.out.println("3 - Edit Item Quantity");
        System.out.println("4 - No");
        System.out.print("Enter Choice: ");
        String choice = input.nextLine();

        if (choice.equals("1")){
            showClear();
            showTransaction(menuCode, menuItem, menuPrice, buyCode, buyItem, buyPrice, buyQuan);

        }else if(choice.equals("2")){
            showRemoveBuyItem();
                
        }else if(choice.equals("3")){
            showEditQuantity(buyCode, buyItem, buyQuan, menuCode, menuItem, menuPrice, buyPrice);

        }else if(choice.equals("4")){
            double payment;

            while (true) { 
                System.out.print("\t\t\t\tPayment: \tPhp ");
                try {
                    payment = input.nextDouble();
        
                    if (payment <= 0) {
                        System.out.println("Payment should be greater than 0!");
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input");
        
                    continue;
                }

                break;
            }

            double change = payment - totalAll;
            System.out.printf("\t\t\t\tChange: \tPhp %.2f\n", change);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Transaction complete!");
            showSaveReceipt(buyCode, buyItem, buyQuan, buyPrice, payment, change);
            buyCode.clear();
            buyItem.clear();
            buyPrice.clear();
            buyQuan.clear();
            isRunning = false;
        }
    }

    public static void showTransaction(ArrayList<String> menuCode, ArrayList<String> menuItem, ArrayList<Double> menuPrice, ArrayList<String> buyCode, ArrayList<String> buyItem, ArrayList<Double> buyPrice, ArrayList<Integer> buyQuan){
        System.out.println("Place customer's order/s below, type \"done\" to proceed to checkout:");
        boolean ordering = true;

        while (ordering){
            System.out.print("Menu Code: ");
            String buyMenu = input.nextLine();
            if (buyMenu.equalsIgnoreCase("done")){
                showClear();
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Redirecting to Receipt...");
                showReceipt(menuCode, menuItem, menuPrice, buyCode, buyItem, buyPrice, buyQuan);
                ordering = false;
                return;
            }
            boolean isFound = false;
            for (int i = 0; i < menuCode.size(); i++){
                if (buyMenu.equalsIgnoreCase(menuCode.get(i))){
                    buyCode.add(menuCode.get(i));
                    buyItem.add(menuItem.get(i));
                    buyPrice.add(menuPrice.get(i));
                    System.out.print("Enter Quantity: ");
                    int prodQuan = input.nextInt();
                    input.nextLine();
                    buyQuan.add(prodQuan);
                    isFound = true;
                    break;
                }
            }
            if (!isFound){
                System.out.println("Invalid menu code. Please try again.");
            }
        }
    }

    public static void showMenu(ArrayList<String> menuCode, ArrayList<String> menuItem, ArrayList<Double> menuPrice){
        showClear();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Aling Nena's Eatery Menu:");
        for (int i = 0; i < menuItem.size(); i++){
            System.out.print(menuCode.get(i) + "\t");
            System.out.print(menuItem.get(i) + "\t");
            System.out.printf("Php %.2f\n", menuPrice.get(i));
        }
    }
    
    public static void showWelcome(){

        System.out.println("Welcome to Aling Nena's Cash Register!");
        System.out.println("Cashier Username: " + logUname);
        System.out.println("Time-in: " + formattedDate);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        boolean isRunning = true;

        System.out.println("What would you like to do today?");
        while (isRunning){

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("1 - New Transaction \n2 - Menu Items \n3 - Menu Options \n4 - Transaction History \n5 - Log-out");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            System.out.print("Enter your option: ");
            int option = input.nextInt();
            input.nextLine();

            switch (option){
                case 1:
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    showTransaction(menuCode, menuItems, menuPrice, buyCode, buyItem, buyPrice, buyQuan);//create a new transaction
                    break;
                case 2:
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    showMenu(menuCode, menuItems, menuPrice);//displays menu
                    break;
                case 3:
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    showCustom(menuCode, menuItems, menuPrice);
                    break;
                case 4:
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    showHistory();
                    break;
                case 5:
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    while (true){
                        System.out.print("Are you sure? Y or N: ");
                        String ynOpt = input.nextLine();

                        if (ynOpt.equalsIgnoreCase("y")){
                            showClear();
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            System.out.println("Time-out: " + formattedDate);
                            return;
                        } else if (ynOpt.equalsIgnoreCase("n")){
                            break;
                        } else {
                            System.out.println("Invalid input. Please try again.");
                            continue;
                        }
                    }
                default:
                    break;
            }
        }
    }

    public static void showLogIn(){
        showClear();
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        while (true){
            System.out.print("Enter Username: ");
            logUname = input.nextLine();

            boolean isFound = false;

            for (int i = 0; i < userName.size(); i++){
                if (userName.get(i).equals(logUname)){
                    isFound = true;

                    while (true){
                        System.out.print("Enter Password: ");
                        String logPword = input.nextLine();

                        if (passWord.get(i).equals(logPword)){
                            showClear();
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            System.out.println("Log-in Successful! Redirecting to Welcome Page...");
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            return;
                            
                        } else {
                            System.out.println("\n Incorrect Password. Please try again.");
                        }
                    }
                }
            }
            if (!isFound){
                System.out.println("User does not exist. Please try again.");
            }
        }

    }

    public static void showHomePage(){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Welcome to Aling Nena's Cash Register!");
        
        boolean isChoice = true;

        while (isChoice){
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("1 - Sign-Up");
            System.out.println("2 - Log-in");
            System.out.println("3 - Exit");
            System.out.print("Enter choice: ");
            int option = input.nextInt();
            input.nextLine();

            switch (option){
                case 1:
                    showClear();
                    showSignUp(userName, passWord); // redirect to sign-up page
                    break;
                case 2:
                    showClear();
                    showLogIn(); //redirect to log-in page
                    showWelcome(); //if log-in successful, redirect to welcome page
                    break;
                case 3:
                    showClear();
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println("Thank you for using Aling Nena's Cash Register! <3");
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.exit(0); //close cash register
                default:
                    break;
            }
        }
        

    }
    public static void main(String[] args) {

        //default menu codes, items, prices
        menuCode.add("P1");
        menuCode.add("P2");
        menuCode.add("B1");
        menuCode.add("B2");
        menuCode.add("C1");
        menuCode.add("C2");

        menuItems.add("Pork Adobo");
        menuItems.add("Pork Chops");
        menuItems.add("Beef Caldereta");
        menuItems.add("Beef Tapa");
        menuItems.add("Fried Chicken");
        menuItems.add("Chicken Curry");

        menuPrice.add(119.00);
        menuPrice.add(99.00);
        menuPrice.add(129.00);
        menuPrice.add(129.00);
        menuPrice.add(99.00);
        menuPrice.add(149.00);

        showHomePage(); //redirect to home page
    }
}
