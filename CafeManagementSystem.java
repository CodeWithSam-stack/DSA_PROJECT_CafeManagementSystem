import java.util.*;

/**
 * ============================================================
 *   Cafe Management System
 *   COMSATS University Islamabad, Attock Campus
 *   Course: CSC211 - Data Structures and Algorithms
 *
 *   Group Members:
 *   - Arooj Noreen     (FA24-BSE-046)
 *   - Rafia Rameen     (FA24-BSE-051)
 *   - Samrina Manahil  (FA24-BSE-052)
 *
 *   Data Structures Used (from Lab Manual):
 *   1. Singly Linked List (Lab 3)  -> Dine-In Queue
 *   2. Stack / Linked List (Lab 5) -> Home Delivery Orders
 *   3. Queue / Linked List (Lab 6) -> Dine-In FIFO Order
 *   4. Max-Heap (Lab 9)            -> Take-Away Priority (older = higher priority)
 *   5. AVL Tree (Lab 8)            -> Served Customers (fast search)
 *   6. BST (Lab 7)                 -> Customer Feedback (sorted by rating)
 * ============================================================
 */

// ─────────────────────────────────────────────────────────────
//  CUSTOMER CLASS
//  Basic data holder for every type of customer
// ─────────────────────────────────────────────────────────────
class Customer {
    int age;
    String name;
    String coffeeName;
    int quantity;
    double bill;

    Customer() {}

    Customer(int age, String name, int quantity, String coffeeName, double bill) {
        this.age       = age;
        this.name      = name;
        this.coffeeName = coffeeName;
        this.quantity  = quantity;
        this.bill      = bill;
    }
}

// ─────────────────────────────────────────────────────────────
//  TAKE-AWAY CUSTOMER NODE
//  Used inside the Max-Heap for take-away orders
// ─────────────────────────────────────────────────────────────
class TakeAwayNode {
    Customer customer;
    TakeAwayNode next = null; // linked list pointer (not used inside heap array)

    TakeAwayNode(int age, String name, int quantity, String coffeeName, double bill) {
        this.customer = new Customer(age, name, quantity, coffeeName, bill);
    }
}

// ─────────────────────────────────────────────────────────────
//  DINE-IN CUSTOMER NODE  (Singly Linked List Node - Lab 3)
//  Queue (FIFO) — first customer to arrive is served first
// ─────────────────────────────────────────────────────────────
class DineInNode {
    Customer customer;
    DineInNode next = null; // points to the next customer in line

    DineInNode(int age, String name, int quantity, String coffeeName, double bill) {
        this.customer = new Customer(age, name, quantity, coffeeName, bill);
    }
}

// ─────────────────────────────────────────────────────────────
//  HOME DELIVERY CUSTOMER NODE  (Stack Node - Lab 5)
//  Stack (LIFO) — most recent delivery order is dispatched first
// ─────────────────────────────────────────────────────────────
class HomeDeliveryNode {
    Customer customer;
    String address;
    double deliveryCharges;
    int distanceKM;
    HomeDeliveryNode next = null; // stack pointer

    HomeDeliveryNode(int age, String name, int quantity, String coffeeName,
                     double bill, String address, double deliveryCharges, int distanceKM) {
        this.customer        = new Customer(age, name, quantity, coffeeName, bill);
        this.address         = address;
        this.deliveryCharges = deliveryCharges;
        this.distanceKM      = distanceKM;
    }
}

// ─────────────────────────────────────────────────────────────
//  CAFE CLASS
//  Stores cafe info and the heads of linked structures
// ─────────────────────────────────────────────────────────────
class Cafe {
    String shopName;
    String address;
    String[] menu;   // coffee names (index 1 to 10)
    int[]    price;  // prices matching menu

    // Head of Dine-In Queue (Singly Linked List)
    DineInNode dineInHead = null;

    // Top of Home Delivery Stack (Singly Linked List used as Stack)
    HomeDeliveryNode deliveryTop = null;
}

// ─────────────────────────────────────────────────────────────
//  MAX-HEAP FOR TAKE-AWAY CUSTOMERS  (Lab 9 - Heap)
//  Priority rule: Older customer (higher age) is served first
//  Implemented using ArrayList (array-based heap)
// ─────────────────────────────────────────────────────────────
class MaxHeap {
    private List<TakeAwayNode> heap = new ArrayList<>();

    // Helper: index of parent node
    private int parent(int i) { return (i - 1) / 2; }

    // Helper: index of left child
    private int leftChild(int i) { return 2 * i + 1; }

    // Helper: index of right child
    private int rightChild(int i) { return 2 * i + 2; }

    /**
     * heapifyUp — called after insertion
     * Moves the newly added node up until heap property is restored
     * (parent age >= child age for max-heap)
     */
    private void heapifyUp(int index) {
        while (index > 0 &&
               heap.get(parent(index)).customer.age < heap.get(index).customer.age) {
            // Swap child with parent
            Collections.swap(heap, index, parent(index));
            index = parent(index);
        }
    }

    /**
     * heapifyDown — called after removal of root
     * Moves the replacement node down until heap property is restored
     */
    private void heapifyDown(int index) {
        int largest = index;
        int left    = leftChild(index);
        int right   = rightChild(index);

        // Check if left child has bigger age
        if (left < heap.size() &&
            heap.get(left).customer.age > heap.get(largest).customer.age)
            largest = left;

        // Check if right child has bigger age
        if (right < heap.size() &&
            heap.get(right).customer.age > heap.get(largest).customer.age)
            largest = right;

        // If the largest is not current node, swap and continue
        if (largest != index) {
            Collections.swap(heap, index, largest);
            heapifyDown(largest);
        }
    }

    /** Insert a new take-away customer into the heap */
    public void push(TakeAwayNode node) {
        heap.add(node);
        heapifyUp(heap.size() - 1); // fix heap property upward
    }

    /** Return the customer with the highest priority (oldest age) */
    public TakeAwayNode top() {
        if (heap.isEmpty()) throw new RuntimeException("Heap is empty!");
        return heap.get(0);
    }

    /** Remove the top (highest-priority) customer */
    public void pop() {
        if (heap.isEmpty()) throw new RuntimeException("Heap is empty!");
        // Replace root with last element and fix downward
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) heapifyDown(0);
    }

    /** Check if heap is empty */
    public boolean empty() { return heap.isEmpty(); }

    /** Return a copy (for display without destroying original) */
    public MaxHeap copy() {
        MaxHeap copy = new MaxHeap();
        copy.heap.addAll(this.heap);
        return copy;
    }
}

// ─────────────────────────────────────────────────────────────
//  SERVED CUSTOMER NODE  (AVL Tree Node - Lab 8)
//  Stored in AVL tree sorted by customer name (alphabetical)
// ─────────────────────────────────────────────────────────────
class ServedNode {
    int    age;
    String name;
    int    quantity;
    String coffeeName;
    double bill;
    String customerType; // "Take-Away", "Dine-In", or "Home-Delivery"
    ServedNode left, right; // AVL tree children

    ServedNode(int age, String name, int quantity,
               String coffeeName, double bill, String customerType) {
        this.age          = age;
        this.name         = name;
        this.quantity     = quantity;
        this.coffeeName   = coffeeName;
        this.bill         = bill;
        this.customerType = customerType;
        this.left         = null;
        this.right        = null;
    }
}

// ─────────────────────────────────────────────────────────────
//  AVL TREE FOR SERVED CUSTOMERS  (Lab 8 - AVL Tree)
//  Self-balancing BST.  Key = customer name (alphabetical)
//  Balancing ensures O(log n) search, insert, delete
// ─────────────────────────────────────────────────────────────
class ServedAVL {
    ServedNode root = null;

    public boolean isEmpty() { return root == null; }

    // Height of a node (null = 0)
    private int height(ServedNode node) {
        if (node == null) return 0;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    // Balance factor = left height minus right height
    private int balance(ServedNode node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    // LL Rotation (Right Rotation) — fixes left-left imbalance
    private ServedNode llRotate(ServedNode node) {
        ServedNode x    = node.left;
        ServedNode temp = x.right;
        x.right         = node;
        node.left       = temp;
        return x; // x becomes new subtree root
    }

    // RR Rotation (Left Rotation) — fixes right-right imbalance
    private ServedNode rrRotate(ServedNode node) {
        ServedNode x    = node.right;
        ServedNode temp = x.left;
        x.left          = node;
        node.right      = temp;
        return x; // x becomes new subtree root
    }

    // LR Rotation — fixes left-right imbalance (two rotations)
    private ServedNode lrRotate(ServedNode node) {
        node.left = rrRotate(node.left);
        return llRotate(node);
    }

    // RL Rotation — fixes right-left imbalance (two rotations)
    private ServedNode rlRotate(ServedNode node) {
        node.right = llRotate(node.right);
        return rrRotate(node);
    }

    /** Insert a served customer into the AVL tree */
    public void insert(int age, String name, int quantity,
                       String coffeeName, double bill, String type) {
        root = insertRec(age, name, quantity, coffeeName, bill, type, root);
    }

    private ServedNode insertRec(int age, String name, int quantity,
                                 String coffeeName, double bill, String type, ServedNode node) {
        // Base case: empty spot found
        if (node == null)
            return new ServedNode(age, name, quantity, coffeeName, bill, type);

        int cmp = name.compareTo(node.name); // alphabetical compare

        if (cmp < 0) {
            // name comes before current node alphabetically -> go left
            node.left = insertRec(age, name, quantity, coffeeName, bill, type, node.left);
        } else if (cmp > 0) {
            // name comes after current node alphabetically -> go right
            node.right = insertRec(age, name, quantity, coffeeName, bill, type, node.right);
        } else {
            // Duplicate name — not allowed in AVL tree
            System.out.println("Customer " + name + " already exists in served records.");
            return node;
        }

        // Check and fix balance
        int bf = balance(node);

        if (bf == 2) { // Left heavy
            if (name.compareTo(node.left.name) < 0) return llRotate(node); // LL case
            else return lrRotate(node);                                      // LR case
        }
        if (bf == -2) { // Right heavy
            if (name.compareTo(node.right.name) > 0) return rrRotate(node); // RR case
            else return rlRotate(node);                                       // RL case
        }

        return node; // balanced — no rotation needed
    }

    /** Search for a customer by name in the AVL tree */
    public ServedNode search(String name) {
        return searchRec(root, name);
    }

    private ServedNode searchRec(ServedNode node, String name) {
        if (node == null) return null; // not found
        int cmp = name.compareTo(node.name);
        if (cmp == 0) return node;      // found
        if (cmp > 0) return searchRec(node.right, name); // go right
        return searchRec(node.left, name);                // go left
    }

    /** Display all served customers (in-order = alphabetical by name) */
    public void displayAll() { inOrder(root); }

    private void inOrder(ServedNode node) {
        if (node == null) return;
        inOrder(node.left);
        printNode(node);
        inOrder(node.right);
    }

    /** Print a single served customer record */
    public void printNode(ServedNode node) {
        System.out.println("  Name         : " + node.name);
        System.out.println("  Age          : " + node.age);
        System.out.println("  Coffee       : " + node.coffeeName);
        System.out.println("  Quantity     : " + node.quantity);
        System.out.println("  Bill         : Rs. " + node.bill);
        System.out.println("  Order Type   : " + node.customerType);
        System.out.println("  ─────────────────────────────────");
    }

    /** Delete a customer by name from the AVL tree */
    public void delete(String name) { root = deleteRec(root, name); }

    private ServedNode maxNode(ServedNode node) {
        // Find the rightmost (largest) node in a subtree
        while (node.right != null) node = node.right;
        return node;
    }

    private ServedNode deleteRec(ServedNode node, String name) {
        if (node == null) return null;
        int cmp = name.compareTo(node.name);

        if (cmp < 0) {
            node.left = deleteRec(node.left, name);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, name);
        } else {
            // Node to delete found
            if (node.left == null || node.right == null) {
                // 0 or 1 child: replace with child (or null)
                node = (node.left != null) ? node.left : node.right;
            } else {
                // 2 children: replace with in-order successor (max of right subtree)
                ServedNode successor = maxNode(node.right);
                node.name         = successor.name;
                node.age          = successor.age;
                node.quantity     = successor.quantity;
                node.coffeeName   = successor.coffeeName;
                node.bill         = successor.bill;
                node.customerType = successor.customerType;
                node.right = deleteRec(node.right, successor.name);
            }
        }

        if (node == null) return null;

        // Re-balance after deletion
        int bf = balance(node);
        if (bf > 1 && name.compareTo(node.left.name) < 0)  return llRotate(node);
        if (bf > 1 && name.compareTo(node.left.name) > 0)  { node.left = llRotate(node.left); return lrRotate(node); }
        if (bf < -1 && name.compareTo(node.right.name) > 0) return rrRotate(node);
        if (bf < -1 && name.compareTo(node.right.name) < 0) return rlRotate(node);

        return node;
    }

    /** Clear all served customer records */
    public void deleteAll() {
        root = null;
        System.out.println("Served customers list cleared.");
    }

    /** Calculate total revenue from all served customers */
    public double totalEarnings() { return earningsRec(root); }

    private double earningsRec(ServedNode node) {
        if (node == null) return 0;
        return node.bill + earningsRec(node.left) + earningsRec(node.right);
    }
}

// ─────────────────────────────────────────────────────────────
//  FEEDBACK CLASS
//  Stores one customer review
// ─────────────────────────────────────────────────────────────
class Feedback {
    String customerName;
    int    rating;     // 1 to 5 stars
    String reviewText;

    Feedback(String name, int rating, String review) {
        this.customerName = name;
        this.rating       = rating;
        this.reviewText   = review;
    }
}

// ─────────────────────────────────────────────────────────────
//  FEEDBACK BST NODE
// ─────────────────────────────────────────────────────────────
class FeedbackNode {
    Feedback data;
    FeedbackNode left, right;

    FeedbackNode(Feedback fb) {
        this.data  = fb;
        this.left  = null;
        this.right = null;
    }
}

// ─────────────────────────────────────────────────────────────
//  FEEDBACK BST  (Lab 7 - Binary Search Tree)
//  Sorted by rating (1=bad -> 5=excellent)
//  In-order display gives reviews from lowest to highest rating
// ─────────────────────────────────────────────────────────────
class FeedbackBST {
    FeedbackNode root = null;

    /** Insert a new feedback, sorted by rating */
    public void insert(Feedback fb) {
        root = insertRec(root, fb);
    }

    private FeedbackNode insertRec(FeedbackNode node, Feedback fb) {
        if (node == null) return new FeedbackNode(fb); // base case
        if (fb.rating < node.data.rating)
            node.left = insertRec(node.left, fb);  // lower rating -> left
        else
            node.right = insertRec(node.right, fb); // higher rating -> right
        return node;
    }

    /** Display all feedback in sorted order (lowest to highest rating) */
    public void displayAll() {
        if (root == null) {
            System.out.println("No feedback submitted yet.");
            return;
        }
        System.out.println("  (Sorted from lowest to highest rating)");
        displayRec(root);
    }

    private void displayRec(FeedbackNode node) {
        if (node == null) return;
        displayRec(node.left);
        System.out.println("  Customer : " + node.data.customerName
                + " | Stars: " + node.data.rating + "/5"
                + " | Review: " + node.data.reviewText);
        displayRec(node.right);
    }
}

// ─────────────────────────────────────────────────────────────
//  MAIN APPLICATION CLASS
// ─────────────────────────────────────────────────────────────
public class CafeManagementSystem {

    // Global cafe object
    static Cafe cafe;

    // Data structures
    static MaxHeap   takeAwayHeap = new MaxHeap();   // Heap for take-away
    static ServedAVL servedAVL    = new ServedAVL();  // AVL for served records
    static FeedbackBST feedbackBST = new FeedbackBST(); // BST for feedback

    static Scanner scanner = new Scanner(System.in);

    // ─────────────────────────────────────────────────────
    //  TAKE-AWAY ORDER FUNCTIONS  (Using Max-Heap)
    // ─────────────────────────────────────────────────────

    /**
     * Place a take-away order.
     * The customer is pushed into the Max-Heap.
     * Older customers get served first (higher age = higher priority).
     */
    static void placeOrderTakeAway(int age, String name,
                                    String coffee, int qty, double bill) {
        TakeAwayNode node = new TakeAwayNode(age, name, qty, coffee, bill);
        takeAwayHeap.push(node); // insert into heap
        System.out.println("Take-Away order placed for: " + name +
                " (Age: " + age + ") — Rs. " + bill);
    }

    /**
     * Serve the next take-away customer (highest age first).
     * Pops from the Max-Heap and adds to the AVL served tree.
     */
    static void serveOrderTakeAway() {
        if (takeAwayHeap.empty()) {
            System.out.println("No take-away customer waiting.");
            return;
        }
        TakeAwayNode served = takeAwayHeap.top();
        takeAwayHeap.pop(); // remove from heap
        System.out.println("Served Take-Away: " + served.customer.name +
                " (Age: " + served.customer.age + ")");
        // Record in served AVL tree
        servedAVL.insert(served.customer.age, served.customer.name,
                served.customer.quantity, served.customer.coffeeName,
                served.customer.bill, "Take-Away");
    }

    // ─────────────────────────────────────────────────────
    //  DINE-IN ORDER FUNCTIONS  (Using Queue — Singly Linked List)
    //  FIFO: First customer to arrive is served first
    // ─────────────────────────────────────────────────────

    /**
     * Place a dine-in order.
     * The new customer is added at the TAIL of the linked list (enqueue).
     */
    static void placeOrderDineIn(int age, String name,
                                  String coffee, int qty, double bill) {
        DineInNode newNode = new DineInNode(age, name, qty, coffee, bill);

        if (cafe.dineInHead == null) {
            // List is empty — new node becomes head
            cafe.dineInHead = newNode;
        } else {
            // Traverse to end and attach new node
            DineInNode temp = cafe.dineInHead;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        System.out.println("Dine-In order placed for: " + name + " — Rs. " + bill);
    }

    /**
     * Serve the next dine-in customer.
     * Removes from the HEAD of the linked list (dequeue = FIFO).
     */
    static void serveOrderDineIn() {
        if (cafe.dineInHead == null) {
            System.out.println("No dine-in customer waiting.");
            return;
        }
        DineInNode served = cafe.dineInHead;
        cafe.dineInHead = cafe.dineInHead.next; // move head forward

        System.out.println("Served Dine-In: " + served.customer.name);
        servedAVL.insert(served.customer.age, served.customer.name,
                served.customer.quantity, served.customer.coffeeName,
                served.customer.bill, "Dine-In");
    }

    // ─────────────────────────────────────────────────────
    //  HOME DELIVERY ORDER FUNCTIONS  (Using Stack — Singly Linked List)
    //  LIFO: Most recent order is dispatched first (most urgent)
    // ─────────────────────────────────────────────────────

    /**
     * Place a home delivery order.
     * The new customer is added at the TOP of the stack (push).
     * Delivery charges are fixed at Rs. 50 per km.
     */
    static void placeOrderDelivery(int age, String name, String coffee,
                                    int qty, double bill, String address,
                                    int distKM, double deliveryCharges) {
        HomeDeliveryNode newNode = new HomeDeliveryNode(
                age, name, qty, coffee, bill, address, deliveryCharges, distKM);

        // Push onto stack: new node points to current top, becomes new top
        newNode.next      = cafe.deliveryTop;
        cafe.deliveryTop  = newNode;

        System.out.println("Home Delivery order placed for: " + name +
                " to " + address + " — Rs. " + bill +
                " (incl. delivery charges Rs. " + deliveryCharges + ")");
    }

    /**
     * Serve the next home delivery order.
     * Pops from the TOP of the stack (LIFO).
     */
    static void serveOrderDelivery() {
        if (cafe.deliveryTop == null) {
            System.out.println("No home delivery order waiting.");
            return;
        }
        HomeDeliveryNode served = cafe.deliveryTop;
        cafe.deliveryTop = cafe.deliveryTop.next; // pop from stack

        System.out.println("Dispatched Home Delivery: " + served.customer.name +
                " to " + served.address);
        servedAVL.insert(served.customer.age, served.customer.name,
                served.customer.quantity, served.customer.coffeeName,
                served.customer.bill, "Home-Delivery");
    }

    // ─────────────────────────────────────────────────────
    //  SERVE ALL PENDING ORDERS
    // ─────────────────────────────────────────────────────
    static void serveAllOrders() {
        System.out.println("Serving all take-away customers...");
        while (!takeAwayHeap.empty()) serveOrderTakeAway();

        System.out.println("Serving all dine-in customers...");
        while (cafe.dineInHead != null) serveOrderDineIn();

        System.out.println("Dispatching all home delivery orders...");
        while (cafe.deliveryTop != null) serveOrderDelivery();

        System.out.println("All orders served!");
    }

    // ─────────────────────────────────────────────────────
    //  DISPLAY FUNCTIONS
    // ─────────────────────────────────────────────────────

    /** Display all pending take-away orders from heap */
    static void displayTakeAway() {
        if (takeAwayHeap.empty()) {
            System.out.println("No take-away orders pending.");
            return;
        }
        MaxHeap temp = takeAwayHeap.copy(); // use copy to preserve original
        System.out.println("─── Take-Away Pending Orders ───");
        while (!temp.empty()) {
            TakeAwayNode c = temp.top();
            temp.pop();
            System.out.println("  Name     : " + c.customer.name);
            System.out.println("  Age      : " + c.customer.age);
            System.out.println("  Coffee   : " + c.customer.coffeeName);
            System.out.println("  Quantity : " + c.customer.quantity);
            System.out.println("  Bill     : Rs. " + c.customer.bill);
            System.out.println("  ─────────────────────────────────");
        }
    }

    /** Display all pending dine-in orders (traverse linked list) */
    static void displayDineIn() {
        if (cafe.dineInHead == null) {
            System.out.println("No dine-in orders pending.");
            return;
        }
        System.out.println("─── Dine-In Pending Orders ───");
        DineInNode temp = cafe.dineInHead;
        while (temp != null) { // traverse singly linked list
            System.out.println("  Name     : " + temp.customer.name);
            System.out.println("  Age      : " + temp.customer.age);
            System.out.println("  Coffee   : " + temp.customer.coffeeName);
            System.out.println("  Quantity : " + temp.customer.quantity);
            System.out.println("  Bill     : Rs. " + temp.customer.bill);
            System.out.println("  ─────────────────────────────────");
            temp = temp.next;
        }
    }

    /** Display all pending home delivery orders (traverse stack/linked list) */
    static void displayDelivery() {
        if (cafe.deliveryTop == null) {
            System.out.println("No home delivery orders pending.");
            return;
        }
        System.out.println("─── Home Delivery Pending Orders ───");
        HomeDeliveryNode temp = cafe.deliveryTop;
        while (temp != null) { // traverse stack from top to bottom
            System.out.println("  Name              : " + temp.customer.name);
            System.out.println("  Age               : " + temp.customer.age);
            System.out.println("  Coffee            : " + temp.customer.coffeeName);
            System.out.println("  Quantity          : " + temp.customer.quantity);
            System.out.println("  Address           : " + temp.address);
            System.out.println("  Distance          : " + temp.distanceKM + " km");
            System.out.println("  Delivery Charges  : Rs. " + temp.deliveryCharges);
            System.out.println("  Total Bill        : Rs. " + temp.customer.bill);
            System.out.println("  ─────────────────────────────────");
            temp = temp.next;
        }
    }

    /** Calculate and display total bill for all pending orders */
    static void totalPendingBill() {
        double takeAwayTotal = 0, dineInTotal = 0, deliveryTotal = 0;

        // Sum take-away bills from heap copy
        MaxHeap temp = takeAwayHeap.copy();
        while (!temp.empty()) {
            takeAwayTotal += temp.top().customer.bill;
            temp.pop();
        }

        // Sum dine-in bills (traverse linked list)
        DineInNode d = cafe.dineInHead;
        while (d != null) { dineInTotal += d.customer.bill; d = d.next; }

        // Sum delivery bills (traverse stack)
        HomeDeliveryNode h = cafe.deliveryTop;
        while (h != null) { deliveryTotal += h.customer.bill; h = h.next; }

        System.out.println("  Take-Away pending total   : Rs. " + takeAwayTotal);
        System.out.println("  Dine-In pending total     : Rs. " + dineInTotal);
        System.out.println("  Home Delivery pending total: Rs. " + deliveryTotal);
        System.out.println("  Grand total               : Rs. " +
                (takeAwayTotal + dineInTotal + deliveryTotal));
    }

    // ─────────────────────────────────────────────────────
    //  HELPER: Read integer input safely
    // ─────────────────────────────────────────────────────
    static int readInt() {
        while (!scanner.hasNextInt()) { scanner.next(); }
        return scanner.nextInt();
    }

    static String readString() { return scanner.next(); }

    // ─────────────────────────────────────────────────────
    //  MAIN METHOD
    // ─────────────────────────────────────────────────────
    public static void main(String[] args) {

        // ── Setup Cafe ──
        cafe           = new Cafe();
        cafe.shopName  = "Cafe Stack";
        cafe.address   = "COMSATS University Islamabad, Attock Campus";

        // Coffee menu (index 1–10)
        cafe.menu  = new String[]{"", "Espresso", "Double Espresso", "Latte",
                                   "Americano", "Macchiato", "Flat White",
                                   "Cappuccino", "Matcha", "Masala Chai", "V60 Brew","Shake"};
        cafe.price = new int[]   {0, 300, 450, 400, 350, 500, 480, 420, 380, 200, 550, 250};

        // ── Welcome Banner ──
        System.out.println("==============================================");
        System.out.println("      Welcome to " + cafe.shopName);
        System.out.println("      " + cafe.address);
        System.out.println("==============================================");
        System.out.println("MENU:");
        for (int i = 1; i <= 11; i++)
            System.out.printf("  %2d. %-20s Rs. %d%n", i, cafe.menu[i], cafe.price[i]);
        System.out.println("==============================================");

        int choice = -1;

        do {
            // ── Main Menu ──
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("--- PLACE ORDER ---");
            System.out.println(" 1. Take-Away Order");
            System.out.println(" 2. Dine-In Order");
            System.out.println(" 3. Home Delivery Order");
            System.out.println("--- SERVE ORDER ---");
            System.out.println(" 4. Serve Take-Away (Max-Heap)");
            System.out.println(" 5. Serve Dine-In   (Queue)");
            System.out.println(" 6. Serve Delivery  (Stack)");
            System.out.println(" 7. Serve ALL Orders");
            System.out.println("--- DISPLAY PENDING ---");
            System.out.println(" 8. Show Take-Away Orders");
            System.out.println(" 9. Show Dine-In Orders");
            System.out.println("10. Show Delivery Orders");
            System.out.println("11. Total Bill of Pending Orders");
            System.out.println("--- SERVED RECORDS (AVL Tree) ---");
            System.out.println("12. Show All Served Customers");
            System.out.println("13. Search Served Customer by Name");
            System.out.println("14. Delete a Served Record");
            System.out.println("15. Clear All Served Records");
            System.out.println("16. Total Earnings");
            System.out.println("--- FEEDBACK (BST) ---");
            System.out.println("17. Submit Feedback");
            System.out.println("18. View All Feedback");
            System.out.println("--- EXIT ---");
            System.out.println(" 0. Exit");
            System.out.println("=============================");
            System.out.print("Enter your choice: ");
            choice = readInt();
            System.out.println("─────────────────────────────────");

            int age, qty, coffeeIndex;
            double bill;
            String name;

            switch (choice) {

                // ── PLACE TAKE-AWAY ORDER ──
                case 1: {
                    System.out.print("Customer Name  : "); name = readString();
                    System.out.print("Customer Age   : "); age  = readInt();
                    System.out.print("Quantity       : "); qty  = readInt();
                    System.out.print("Coffee (1-10)  : "); coffeeIndex = readInt();
                    if (coffeeIndex < 1 || coffeeIndex > 10) {
                        System.out.println("Invalid coffee choice."); break;
                    }
                    bill = (double) qty * cafe.price[coffeeIndex];
                    placeOrderTakeAway(age, name, cafe.menu[coffeeIndex], qty, bill);
                    break;
                }

                // ── PLACE DINE-IN ORDER ──
                case 2: {
                    System.out.print("Customer Name  : "); name = readString();
                    System.out.print("Customer Age   : "); age  = readInt();
                    System.out.print("Quantity       : "); qty  = readInt();
                    System.out.print("Coffee (1-10)  : "); coffeeIndex = readInt();
                    if (coffeeIndex < 1 || coffeeIndex > 10) {
                        System.out.println("Invalid coffee choice."); break;
                    }
                    bill = (double) qty * cafe.price[coffeeIndex];
                    placeOrderDineIn(age, name, cafe.menu[coffeeIndex], qty, bill);
                    break;
                }

                // ── PLACE HOME DELIVERY ORDER ──
                case 3: {
                    System.out.print("Customer Name  : "); name = readString();
                    System.out.print("Customer Age   : "); age  = readInt();
                    System.out.print("Quantity       : "); qty  = readInt();
                    System.out.print("Coffee (1-10)  : "); coffeeIndex = readInt();
                    if (coffeeIndex < 1 || coffeeIndex > 10) {
                        System.out.println("Invalid coffee choice."); break;
                    }
                    System.out.print("Delivery Address: "); String address = readString();
                    System.out.print("Distance (km)  : "); int distKM = readInt();
                    double deliveryCharges = 50.0 * distKM; // Rs. 50 per km
                    bill = (double) qty * cafe.price[coffeeIndex] + deliveryCharges;
                    placeOrderDelivery(age, name, cafe.menu[coffeeIndex],
                            qty, bill, address, distKM, deliveryCharges);
                    break;
                }

                case 4:  serveOrderTakeAway(); break;
                case 5:  serveOrderDineIn();   break;
                case 6:  serveOrderDelivery(); break;
                case 7:  serveAllOrders();     break;

                case 8:  displayTakeAway(); break;
                case 9:  displayDineIn();   break;
                case 10: displayDelivery(); break;
                case 11: totalPendingBill(); break;

                // ── DISPLAY ALL SERVED CUSTOMERS (AVL in-order) ──
                case 12: {
                    if (servedAVL.isEmpty()) System.out.println("No served customers yet.");
                    else { System.out.println("─── Served Customers (alphabetical) ───"); servedAVL.displayAll(); }
                    break;
                }

                // ── SEARCH IN AVL TREE ──
                case 13: {
                    System.out.print("Enter customer name to search: "); name = readString();
                    ServedNode found = servedAVL.search(name);
                    if (found == null) System.out.println("Customer not found in served records.");
                    else servedAVL.printNode(found);
                    break;
                }

                // ── DELETE ONE RECORD FROM AVL TREE ──
                case 14: {
                    System.out.print("Enter customer name to delete: "); name = readString();
                    servedAVL.delete(name);
                    System.out.println("Record deleted (if it existed).");
                    break;
                }

                case 15: servedAVL.deleteAll(); break;

                // ── TOTAL EARNINGS ──
                case 16: {
                    System.out.printf("  Total Earnings: Rs. %.2f%n", servedAVL.totalEarnings());
                    break;
                }

                // ── SUBMIT FEEDBACK (BST insert) ──
                case 17: {
                    System.out.print("Your Name   : "); name = readString();
                    System.out.print("Rating (1-5): "); int rating = readInt();
                    if (rating < 1 || rating > 5) { System.out.println("Rating must be 1-5."); break; }
                    scanner.nextLine(); // consume newline
                    System.out.print("Your Review : "); String review = scanner.nextLine();
                    feedbackBST.insert(new Feedback(name, rating, review));
                    System.out.println("Thank you for your feedback!");
                    break;
                }

                // ── DISPLAY ALL FEEDBACK (BST in-order) ──
                case 18: {
                    System.out.println("─── Customer Feedback ───");
                    feedbackBST.displayAll();
                    break;
                }

                case 0:
                    System.out.println("Thank you for visiting " + cafe.shopName + "! Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 0);

        scanner.close();
    }
}