public class Cashier implements Runnable {
	
	private String id;
	private Thread thread;
	private boolean busy = false;
	public boolean cashPay;
	
	public static long time = System.currentTimeMillis();
	public void msg(String m) {
		System.out.println("[" + (System.currentTimeMillis() - time) + "] " + this.id + ": " + m);
	}
	
	// Constructor
	public Cashier (String id) {
		this.id = "Cashier " + id;
		this.thread = new Thread(this, id);
	}
	
	public void start() {
		thread.start();
	}
	
	public void interrupt() {
		thread.interrupt();
	}
	
	public boolean isBusy() {
		return this.busy;
	}
	
	public void helpCashCustomer() throws Exception {
		this.busy = true;
		Shopping.availClerks--;
		
		Customer c = Shopping.cashLine.poll();
		msg("proccesses " + c.getName() + "'s order.");
		Thread.sleep((long) (Math.random() * 1000));			// Cashier sleeps a random time to simulate helping customer
		Shopping.paidCustomers++;
		c.stopWaiting();
		
		this.busy = false;
		msg("has finished helping " + c.getName() + ".");
	}
	
	public void helpCreditCustomer() throws Exception {
		this.busy = true;
		
		Customer c = Shopping.creditLine.poll();
		msg("is helping to process " + c.getName() + "'s order.");
		Thread.sleep((long) (Math.random() * 5000));			// Cashier sleeps a random time to simulate helping customer
		Shopping.paidCustomers++;
		c.stopWaiting();
		
		this.busy = false;
		msg("has finished helping " + c.getName() + ".");
	}
	
	public void run() {
		try {
			
			// Cashier busy waits while the line is empty and not all the customers have paid yet
			while (Shopping.isCashLineEmpty() && !Shopping.allCustomersAssisted()) {
				thread.sleep((long) (Math.random() * 10000));
			}
			
			while (!Shopping.allCustomersPaid()) {
				
				// Cashier helps the customer
				if (!Shopping.isCashLineEmpty() && !isBusy() && cashPay == true) {
					helpCashCustomer();
				}
				
				if (!Shopping.isCreditLineEmpty() && !isBusy() && cashPay == false) {
					helpCreditCustomer();
				}
			}
			
			// All customers have paid, now waits for closing time
			msg("is now waiting for closing time.");
			thread.sleep(1000000);
		}
		
		// Will go home once interrupted when all customers are done.
		catch (InterruptedException e) {
			msg("Is now going home.");
		}
		
		// Other debugging exceptions
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
