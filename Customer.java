public class Customer implements Runnable {

	private String id;
	private Thread thread;
	public boolean waiting;
	public boolean payWithCash = true;
	public boolean hasSlip = false;

	private boolean finishedShopping = false;

	public static long time = System.currentTimeMillis();
	public void msg(String m) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
	}

	// Constructor
	public Customer (String id) {
		this.id = "Customer " + id;
		this.thread = new Thread(this, id);
		this.waiting = false;
	}

	public String getName() {
		return id;
	}

	public void start() {
		thread.start();
	}

	public void join(long l) throws Exception {
		thread.join(l);
	}

	public long getId() {
		return thread.getId();
	}

	public boolean isAlive() {
		return thread.isAlive();
	}

	public void stopWaiting() {
		this.waiting = false;
	}

	public boolean allCustomersFinished() {
		for (int i = 0; i < Shopping.customer.length; i++) {
			if (Shopping.customer[i].finishedShopping == false) {
				return false;
			}
		}
		return true;
	}

	public boolean everyoneJoined() {
		for (int i = 1; i < Shopping.numCustomer; i++) {
			if (Shopping.customer[i].isAlive()) {
				return false;
			}
		}
		return true;
	}

	public void run() {
		try {

			// Customer walks into a showroom and browses, simulated by sleep(random time)
			thread.sleep((long) (Math.random() * 10000));
			msg("is browsing the showroom.");
			thread.sleep((long) (Math.random() * 10000));

			// Customer is undecided upon an item and yields twice
			msg("has found an item but is undecided.");
			thread.yield(); thread.yield();

			// The customer is now decided and goes to line up for a clerk
			msg("has decided on an item. Lines up to see a clerk.");
			Shopping.joinClerkLine(this);

			// Simulates the customer waiting on line for a clerk
			while (this.waiting == true) {
				thread.sleep((long) (Math.random() * 5000));
			}

			// The customer has been given a pay slip. Now goes to the cashier
			msg("is now going to the cashier.");
			int oldP = thread.getPriority();
			thread.sleep(30);
			thread.setPriority(10);
			msg("is now at the cashier.");
			thread.setPriority(oldP);

			// Randomly determines if customer wants to pay with cash or credit
			if (Math.random() < 0.5) {
				payWithCash = false;
			}

			if (payWithCash) {
				msg("decides to pay with cash.");
				Shopping.joinCashLine(this);
			}
			else {
				msg("decides to pay with a credit card.");
				Shopping.joinCreditLine(this);
			}

			// Simulates the customer waiting to pay
			while (this.waiting == true) {
				thread.sleep((long) (Math.random() * 5000));
			}

			// Customers are now done shopping and waits for everyone else to finish shopping
			msg("is finished shopping and waits at the cafeteria.");
			this.finishedShopping = true;

			// Busy wait for everyone else to finish shopping
			while (!allCustomersFinished()) {
			}

			// Everyone has finished shopping. Now we need to join and have the first customer tell everyone to leave
			long prevCustomer = thread.getId() - 1;

			while (!everyoneJoined() && Shopping.customer[0].getId() == prevCustomer + 1) {
				for (int i = Shopping.numCustomer - 1; i > 0; i--) {
					System.out.println("[" + (System.currentTimeMillis() - time) + "] " + "Customer " + i + ": is joining " + (i - 1) + " to get ready to leave.");
					thread.sleep(200);
					Shopping.customer[i].join(Shopping.customer[i].getId() - 1);
				}
				for (int i = Shopping.numCustomer - 1; i > 0; i--) {
					System.out.println("[" + (System.currentTimeMillis() - time) + "] " + "Customer " + i + ": is now leaving.");
					thread.sleep(100);
				}
			}

			// First customer notifies everyone to go home once everyone has joined
			if (Shopping.customer[0].getId() == prevCustomer + 1 && everyoneJoined()) {
				msg("announces that everyone is finished shopping. It is now closing time.");

				// Wakes up all the clerks
				for (int i = 0; i < Shopping.numClerk; i++) {
					Shopping.clerk[i].interrupt();
				}

				// Wakes up all the cashiers
				for (int i = 0; i < Shopping.numCashier; i++) {
					Shopping.cashier[i].interrupt();
				}
			}
		}

		// Debugging exceptions
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
