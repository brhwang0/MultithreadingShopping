import java.util.*;
import java.util.concurrent.*;

public class Shopping {

	public static int numCustomer = 12;
	public static Customer[] customer;
	public static int numClerk = 3;
	public static FloorClerk[] clerk;
	public static int numCashier = 2;
	public static Cashier[] cashier;

	public static int assistedCustomers = 0;
	public static int paidCustomers = 0;
	public static int availClerks = numClerk;

	public static ConcurrentLinkedQueue<Customer> clerkLine = new ConcurrentLinkedQueue<Customer>();
	public static ConcurrentLinkedQueue<Customer> cashLine = new ConcurrentLinkedQueue<Customer>();
	public static ConcurrentLinkedQueue<Customer> creditLine = new ConcurrentLinkedQueue<Customer>();

	// Ask if user wants to enter custom values
	public static void userInput() {

		Scanner sc = new Scanner(System.in);
		System.out.println("The default values for this program are: Customers = 12, Clerks = 3, Cashiers = 2");
		System.out.println("Would you like to enter custom values for customer, clerk, and cashier?");
		System.out.print("Please enter Y/N: ");
		while (true) {
			String s = sc.nextLine();
			char c = Character.toLowerCase(s.charAt(0));

			if (s.length() != 1) {
				System.out.print("Invalid input. Please enter Y or N: ");
			}

			else if (c == 'n') {
				break;
			}

			else if (c == 'y') {
				try {
					System.out.print("Please enter a value for number of customers: ");
				    numCustomer = sc.nextInt();
					System.out.println("Number of customers set to " + numCustomer + ".");

					System.out.print("Please enter a value for number of clerks: ");
				    numClerk = sc.nextInt();
					System.out.println("Number of clerks set to " + numClerk + ".");

					System.out.print("Please enter a value for number of cashiers: ");
				    numCashier = sc.nextInt();
					System.out.println("Number of cashiers set to " + numCashier + ".");

					System.out.println("\nValues changed. Starting the program.\n");
					break;
				}
				catch (InputMismatchException e) {
					System.out.print("Invalid input. Would you still like to enter custom values for customer, clerk, and cashier? ");
					sc.nextLine();
				}
			}

			else {
				System.out.print("Invalid input. Please enter Y or N: ");
			}
		}
	}

	public static void joinClerkLine (Customer c) {
		c.waiting = true;
		clerkLine.add(c);
	}

	public static void joinCashLine (Customer c) {
		c.waiting = true;
		cashLine.add(c);
	}

	public static void joinCreditLine (Customer c) {
		c.waiting = true;
		creditLine.add(c);
	}

	public static synchronized boolean allCustomersAssisted() {
		return assistedCustomers == numCustomer;
	}

	public static synchronized boolean allCustomersPaid() {
		return paidCustomers == numCustomer;
	}

	public static synchronized boolean isClerkLineEmpty() {
		return clerkLine.isEmpty();
	}

	public static synchronized boolean isCashLineEmpty() {
		return cashLine.isEmpty();
	}

	public static synchronized boolean isCreditLineEmpty() {
		return creditLine.isEmpty();
	}

	public static void main(String[] args) {
		userInput();

		customer = new Customer[numCustomer];
		clerk = new FloorClerk[numClerk];
		cashier = new Cashier[numCashier];

		// Creates and starts the Clerk threads
		for (int i = 0; i < numClerk; i++) {
			clerk[i] = new FloorClerk(Integer.toString(i));
			clerk[i].start();
		}

		// Creates and starts the Customer threads
		for (int i = 0; i < numCustomer; i++) {
			customer[i] = new Customer(Integer.toString(i));
			customer[i].start();
		}

		// Creates and starts the Cashier threads
		// Every other Cashier thread will accept credit
		for (int i = 0; i < numCashier; i++) {
			cashier[i] = new Cashier(Integer.toString(i));

			if (i % 2 == 0) {
				cashier[i].cashPay = true;
			}

			cashier[i].start();
		}
	}
}
