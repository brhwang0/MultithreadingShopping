public class FloorClerk implements Runnable {

	private String id;
	private Thread thread;
	private boolean busy = false;

	public static long time = System.currentTimeMillis();
	public void msg(String m) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] " + this.id + ": " + m);
	}

	// Constructor
	public FloorClerk (String id) {
		this.id = "Floor Clerk " + id;
		this.thread = new Thread(this, id);
	}

	public void start() {
		thread.start();
	}

	public void join() {
		try {
			thread.join();
		}
		catch (Exception e) {}
	}

	public void interrupt() {
		thread.interrupt();
	}

	public boolean isBusy() {
		return this.busy;
	}

	public void helpCustomer() throws Exception {
		this.busy = true;
		Shopping.availClerks--;

		Customer c = Shopping.clerkLine.poll();
		msg("is helping " + c.getName() + ".");
		Thread.sleep((long) (Math.random() * 10000));			// Clerk sleeps a random time to simulate helping customer
		Shopping.assistedCustomers++;
		c.stopWaiting(); c.hasSlip = true;

		this.busy = false;
		Shopping.availClerks++;
		msg("gives " + c.getName() + " a slip to pay for their selected item.");
	}

	public void run() {

		try {

			// Clerk busy waits while the line is empty and not all the customers have been assisted yet
			while (Shopping.isClerkLineEmpty() && !Shopping.allCustomersAssisted()) {
				thread.sleep((long) (Math.random() * 10000));
			}

			while (!Shopping.allCustomersAssisted()) {
				// Clerk helps the customer
				if (!Shopping.isClerkLineEmpty() && !isBusy()) {
					helpCustomer();
				}
			}

			// All customers have been assisted, now waits for closing time
			msg("is now waiting for closing time.");
			thread.sleep(1000000);
		}

		catch (InterruptedException e) {
			// Will go home once interrupted when all customers are done.
			msg("Is now going home.");
		}

		// Other debugging exceptions
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
