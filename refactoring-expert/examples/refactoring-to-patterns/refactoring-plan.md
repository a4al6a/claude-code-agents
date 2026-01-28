# Refactoring to Patterns Plan - OrderProcessingSystem.java

## Executive Summary

This refactoring plan applies Joshua Kerievsky's "Refactoring to Patterns" principles to transform the OrderProcessingSystem from procedural, conditional-heavy code into a well-structured design using appropriate patterns. The key insight from Kerievsky's work is that **patterns should emerge through incremental refactoring, not be applied upfront**.

Each refactoring is broken into small, safe steps that maintain working code throughout the transformation.

**Key Transformations:**
- State-altering conditionals → **State Pattern**
- Algorithm-selecting conditionals → **Strategy Pattern**
- Conditional dispatcher → **Command Pattern**
- Version-specific code → **Adapter Pattern**
- Hard-coded notifications → **Observer Pattern**
- One/many distinctions → **Composite Pattern**
- Complex construction → **Builder Pattern**

**Estimated Scope:** 10 major refactoring sequences
**Risk Level:** Medium (with comprehensive test coverage)

---

## Kerievsky's Core Principles Applied

Before diving into specific refactorings, remember these guiding principles:

1. **Start Simple**: Don't introduce patterns until complexity demands them
2. **Evolve Through Need**: Each step should address a real pain point
3. **Small Steps**: Every transformation maintains working, tested code
4. **Reversibility**: If a pattern proves unnecessary, we can refactor away from it
5. **Three Strikes Rule**: Wait for three instances before abstracting

---

## Phase 1: Foundation Refactorings (Prepare for Patterns)

These refactorings establish a clean foundation before introducing patterns.

### 1.1 Chain Constructors

**Smell:** Four Order constructors with duplicated initialization code (Lines 37-106)

**Kerievsky Technique:** Chain Constructors

**Rationale:** Eliminates duplication and centralizes construction logic, preparing for Creation Methods.

**Step-by-Step Mechanics:**

1. Identify the "catch-all" constructor (the one with most parameters):
```java
public Order(String orderId, String customerId, String customerType,
             String shippingAddress, String billingAddress, boolean isGift, String giftMessage)
```

2. Make the simplest constructor call the next more complex one:

**Before:**
```java
public Order(String orderId, String customerId) {
    this.orderId = orderId;
    this.customerId = customerId;
    this.customerType = "regular";
    this.status = "pending";
    this.items = new ArrayList<>();
    // ... 10+ more initializations
}
```

**After:**
```java
public Order(String orderId, String customerId) {
    this(orderId, customerId, "regular");
}
```

3. Chain the second constructor:
```java
public Order(String orderId, String customerId, String customerType) {
    this(orderId, customerId, customerType, null);
}
```

4. Chain the third constructor:
```java
public Order(String orderId, String customerId, String customerType, String shippingAddress) {
    this(orderId, customerId, customerType, shippingAddress, shippingAddress, false, null);
}
```

5. The catch-all constructor contains all initialization:
```java
public Order(String orderId, String customerId, String customerType,
             String shippingAddress, String billingAddress, boolean isGift, String giftMessage) {
    this.orderId = orderId;
    this.customerId = customerId;
    this.customerType = customerType;
    this.shippingAddress = shippingAddress;
    this.billingAddress = billingAddress != null ? billingAddress : shippingAddress;
    this.isGift = isGift;
    this.giftMessage = giftMessage;
    this.status = "pending";
    this.items = new ArrayList<>();
    this.totalAmount = 0.0;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.discountPercentage = 0.0;
    initializeServices();
}

private void initializeServices() {
    this.emailService = new EmailService();
    this.smsService = new SMSService();
    this.inventoryService = new InventoryService();
    this.analyticsService = new AnalyticsService();
}
```

**Test:** Run all existing tests to verify behavior unchanged.

**Risk:** Low - Mechanical transformation with no behavior change.

---

### 1.2 Replace Constructors with Creation Methods

**Smell:** Constructor signatures don't communicate intent

**Kerievsky Technique:** Replace Constructors with Creation Methods

**Rationale:** Creation Methods have intention-revealing names that document when to use each constructor variation.

**Step-by-Step Mechanics:**

1. Create Creation Method for the first constructor:
```java
public static Order createSimpleOrder(String orderId, String customerId) {
    return new Order(orderId, customerId);
}
```

2. Find all clients calling `new Order(orderId, customerId)` and replace with `Order.createSimpleOrder(orderId, customerId)`.

3. Create intention-revealing Creation Methods for each use case:
```java
public static Order createRegularOrder(String orderId, String customerId) {
    return new Order(orderId, customerId, "regular");
}

public static Order createPremiumOrder(String orderId, String customerId) {
    return new Order(orderId, customerId, "premium");
}

public static Order createVIPOrder(String orderId, String customerId) {
    return new Order(orderId, customerId, "vip");
}

public static Order createGiftOrder(String orderId, String customerId,
                                     String shippingAddress, String giftMessage) {
    return new Order(orderId, customerId, "regular", shippingAddress, shippingAddress, true, giftMessage);
}

public static Order createOrderWithShipping(String orderId, String customerId,
                                            String customerType, String shippingAddress) {
    return new Order(orderId, customerId, customerType, shippingAddress);
}
```

4. Replace all constructor calls with appropriate Creation Methods.

5. Make constructors package-private or private (if all external calls replaced).

**Final Result:**
```java
// Client code - clear intent
Order regularOrder = Order.createRegularOrder("ORD-001", "CUST-123");
Order giftOrder = Order.createGiftOrder("ORD-002", "CUST-456", "123 Main St", "Happy Birthday!");
Order vipOrder = Order.createVIPOrder("ORD-003", "CUST-789");
```

**Risk:** Low - Naming improvement with no behavior change.

---

### 1.3 Replace Primitive Status with Enum

**Smell:** Status field is String, risking invalid values (Line 17)

**Fowler Technique:** Replace Type Code with Class (simplified to Enum)

**Rationale:** Provides type safety and prepares for State pattern.

**Step-by-Step Mechanics:**

1. Create OrderStatus enum:
```java
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
```

2. Change status field type:
```java
private OrderStatus status;
```

3. Update catch-all constructor:
```java
this.status = OrderStatus.PENDING;
```

4. Update all status comparisons:

**Before:**
```java
if (status.equals("pending")) {
```

**After:**
```java
if (status == OrderStatus.PENDING) {
```

5. Update status assignments:
```java
status = OrderStatus.CONFIRMED;
```

6. Update getStatus() return type (may require updating clients).

**Risk:** Low - Compile-time verification catches all usages.

---

### 1.4 Replace Primitive CustomerType with Enum

**Smell:** CustomerType field is String (Line 16)

**Same approach as 1.3:**

```java
public enum CustomerType {
    REGULAR,
    PREMIUM,
    VIP
}
```

This prepares for the Strategy pattern in Phase 2.

---

## Phase 2: Replace State-Altering Conditionals with State

**Smell:** Methods confirm(), ship(), deliver(), cancel() contain identical conditional structures checking status (Lines 113-195)

**Kerievsky Technique:** Replace State-Altering Conditionals with State

**This is the largest and most impactful refactoring.**

### Why State Pattern Here?

The code exhibits classic State pattern indicators:
- Object behavior depends on its state
- State transitions follow specific rules
- Same conditional structure repeated in multiple methods
- Adding new states would require modifying multiple methods

### Step-by-Step Mechanics

#### Step 1: Create State Interface

```java
public interface OrderState {
    void confirm(Order order);
    void ship(Order order);
    void deliver(Order order);
    void cancel(Order order);

    String getStatusName();
}
```

#### Step 2: Create Concrete State for Current State (Pending)

Start with PendingState since it's the initial state:

```java
public class PendingState implements OrderState {

    @Override
    public void confirm(Order order) {
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot confirm order with no items");
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order.setState(new ConfirmedState());
        order.setUpdatedAt(LocalDateTime.now());
        order.notifyObservers(OrderEvent.CONFIRMED);
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Cannot ship pending order - must confirm first");
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Cannot deliver pending order");
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        order.setState(new CancelledState());
        order.setUpdatedAt(LocalDateTime.now());
        order.notifyObservers(OrderEvent.CANCELLED);
    }

    @Override
    public String getStatusName() {
        return "pending";
    }
}
```

#### Step 3: Create Remaining State Classes

**ConfirmedState.java:**
```java
public class ConfirmedState implements OrderState {

    @Override
    public void confirm(Order order) {
        throw new IllegalStateException("Order is already confirmed");
    }

    @Override
    public void ship(Order order) {
        order.setStatus(OrderStatus.SHIPPED);
        order.setState(new ShippedState());
        order.setUpdatedAt(LocalDateTime.now());
        order.notifyObservers(OrderEvent.SHIPPED);
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Cannot deliver unshipped order");
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        order.setState(new CancelledState());
        order.setUpdatedAt(LocalDateTime.now());
        order.notifyObservers(OrderEvent.CANCELLED);
    }

    @Override
    public String getStatusName() {
        return "confirmed";
    }
}
```

**ShippedState.java:**
```java
public class ShippedState implements OrderState {

    @Override
    public void confirm(Order order) {
        throw new IllegalStateException("Cannot confirm shipped order");
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Order is already shipped");
    }

    @Override
    public void deliver(Order order) {
        order.setStatus(OrderStatus.DELIVERED);
        order.setState(new DeliveredState());
        order.setUpdatedAt(LocalDateTime.now());
        order.notifyObservers(OrderEvent.DELIVERED);
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Cannot cancel shipped order - request return instead");
    }

    @Override
    public String getStatusName() {
        return "shipped";
    }
}
```

**DeliveredState.java:**
```java
public class DeliveredState implements OrderState {

    @Override
    public void confirm(Order order) {
        throw new IllegalStateException("Cannot confirm delivered order");
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Cannot ship delivered order");
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Order is already delivered");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Cannot cancel delivered order - request return instead");
    }

    @Override
    public String getStatusName() {
        return "delivered";
    }
}
```

**CancelledState.java:**
```java
public class CancelledState implements OrderState {

    @Override
    public void confirm(Order order) {
        throw new IllegalStateException("Cannot confirm cancelled order");
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Cannot ship cancelled order");
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Cannot deliver cancelled order");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Order is already cancelled");
    }

    @Override
    public String getStatusName() {
        return "cancelled";
    }
}
```

#### Step 4: Add State Field to Order

```java
public class Order {
    private OrderState state;

    // In catch-all constructor:
    this.state = new PendingState();

    // Package-private setter for state classes
    void setState(OrderState state) {
        this.state = state;
    }
}
```

#### Step 5: Delegate Methods to State

Replace the conditional methods with simple delegation:

```java
public void confirm() {
    state.confirm(this);
}

public void ship() {
    state.ship(this);
}

public void deliver() {
    state.deliver(this);
}

public void cancel() {
    state.cancel(this);
}
```

#### Step 6: Remove Old Conditional Logic

Delete the entire conditional structure from each method - now handled by State classes.

**Final Order Methods:**
```java
public void confirm() {
    state.confirm(this);
}

public void ship() {
    state.ship(this);
}

public void deliver() {
    state.deliver(this);
}

public void cancel() {
    state.cancel(this);
}
```

**Benefits Achieved:**
- State transition rules encapsulated in State classes
- Adding new states requires only a new class (Open/Closed)
- State machine is visualizable by looking at State classes
- Each State class is independently testable

**Risk:** High - Significant structural change. Requires comprehensive test coverage.

---

## Phase 3: Replace Hard-Coded Notifications with Observer

**Smell:** Hard-coded notification calls in each state transition (Lines 113-195)

**Kerievsky Technique:** Replace Hard-Coded Notifications with Observer

### Step-by-Step Mechanics

#### Step 1: Create OrderEvent Enum

```java
public enum OrderEvent {
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
```

#### Step 2: Create OrderObserver Interface

```java
public interface OrderObserver {
    void onOrderEvent(Order order, OrderEvent event);
}
```

#### Step 3: Add Observer Infrastructure to Order

```java
public class Order {
    private List<OrderObserver> observers = new ArrayList<>();

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    void notifyObservers(OrderEvent event) {
        for (OrderObserver observer : observers) {
            observer.onOrderEvent(this, event);
        }
    }
}
```

#### Step 4: Convert Services to Observers

```java
public class EmailNotificationObserver implements OrderObserver {
    private EmailService emailService;

    public EmailNotificationObserver(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onOrderEvent(Order order, OrderEvent event) {
        switch (event) {
            case CONFIRMED:
                emailService.sendOrderConfirmation(order);
                break;
            case SHIPPED:
                emailService.sendShippingNotification(order);
                break;
            case DELIVERED:
                emailService.sendDeliveryConfirmation(order);
                break;
            case CANCELLED:
                emailService.sendCancellationNotification(order);
                break;
        }
    }
}

public class SMSNotificationObserver implements OrderObserver {
    private SMSService smsService;

    @Override
    public void onOrderEvent(Order order, OrderEvent event) {
        switch (event) {
            case CONFIRMED:
            case SHIPPED:
            case DELIVERED:
            case CANCELLED:
                smsService.sendNotification(order, event);
                break;
        }
    }
}

public class InventoryObserver implements OrderObserver {
    private InventoryService inventoryService;

    @Override
    public void onOrderEvent(Order order, OrderEvent event) {
        switch (event) {
            case CONFIRMED:
                inventoryService.reserveItems(order);
                break;
            case SHIPPED:
                inventoryService.updateShippedItems(order);
                break;
            case CANCELLED:
                inventoryService.releaseReservedItems(order);
                break;
            default:
                // No inventory action needed
        }
    }
}

public class AnalyticsObserver implements OrderObserver {
    private AnalyticsService analyticsService;

    @Override
    public void onOrderEvent(Order order, OrderEvent event) {
        analyticsService.trackEvent(order, event);
    }
}
```

#### Step 5: Update State Classes to Use notifyObservers()

Already shown in Phase 2 - State classes call `order.notifyObservers(OrderEvent.X)`.

#### Step 6: Remove Hard-Coded Service Fields from Order

```java
// REMOVE these fields from Order:
// private EmailService emailService;
// private SMSService smsService;
// private InventoryService inventoryService;
// private AnalyticsService analyticsService;
```

#### Step 7: Register Observers During Order Creation

In the factory or where orders are created:

```java
public static Order createOrderWithObservers(String orderId, String customerId) {
    Order order = Order.createRegularOrder(orderId, customerId);
    order.addObserver(new EmailNotificationObserver(new EmailService()));
    order.addObserver(new SMSNotificationObserver(new SMSService()));
    order.addObserver(new InventoryObserver(new InventoryService()));
    order.addObserver(new AnalyticsObserver(new AnalyticsService()));
    return order;
}
```

**Benefits:**
- Order decoupled from notification services
- Easy to add/remove notification channels
- Easy to test Order without notifications
- Notification logic centralized per service

**Risk:** Medium - Requires updating how Orders are created.

---

## Phase 4: Replace Conditional Logic with Strategy

**Smell:** calculateTotal() has complex conditional selecting pricing algorithm by customerType (Lines 205-260)

**Kerievsky Technique:** Replace Conditional Logic with Strategy

### Step-by-Step Mechanics

#### Step 1: Create PricingStrategy Interface

```java
public interface PricingStrategy {
    double calculateDiscount(double subtotal, Order order);
    double calculateShipping(double subtotal, Order order);
    double getTaxRate();
}
```

#### Step 2: Extract Strategy for Each Customer Type

**RegularPricingStrategy.java:**
```java
public class RegularPricingStrategy implements PricingStrategy {

    @Override
    public double calculateDiscount(double subtotal, Order order) {
        if (subtotal > 100) {
            return subtotal * 0.05; // 5% for orders over $100
        }
        return 0.0;
    }

    @Override
    public double calculateShipping(double subtotal, Order order) {
        if (subtotal < 50) {
            return 9.99;
        } else if (subtotal < 100) {
            return 4.99;
        }
        return 0.0; // Free shipping over $100
    }

    @Override
    public double getTaxRate() {
        return 0.08; // 8% tax
    }
}
```

**PremiumPricingStrategy.java:**
```java
public class PremiumPricingStrategy implements PricingStrategy {

    @Override
    public double calculateDiscount(double subtotal, Order order) {
        return subtotal * 0.10; // Always 10% discount
    }

    @Override
    public double calculateShipping(double subtotal, Order order) {
        if (subtotal < 25) {
            return 4.99;
        }
        return 0.0; // Free shipping over $25
    }

    @Override
    public double getTaxRate() {
        return 0.08;
    }
}
```

**VIPPricingStrategy.java:**
```java
public class VIPPricingStrategy implements PricingStrategy {

    @Override
    public double calculateDiscount(double subtotal, Order order) {
        double discount = subtotal * 0.15; // Base 15% discount

        // VIP bonus tiers
        if (subtotal > 500) {
            discount += subtotal * 0.05; // +5% over $500
        }
        if (subtotal > 1000) {
            discount += subtotal * 0.05; // +5% over $1000
        }
        return discount;
    }

    @Override
    public double calculateShipping(double subtotal, Order order) {
        return 0.0; // Always free shipping
    }

    @Override
    public double getTaxRate() {
        return 0.05; // Reduced tax rate
    }
}
```

#### Step 3: Create Strategy Factory

```java
public class PricingStrategyFactory {

    private static final Map<CustomerType, PricingStrategy> strategies = Map.of(
        CustomerType.REGULAR, new RegularPricingStrategy(),
        CustomerType.PREMIUM, new PremiumPricingStrategy(),
        CustomerType.VIP, new VIPPricingStrategy()
    );

    public static PricingStrategy getStrategy(CustomerType customerType) {
        PricingStrategy strategy = strategies.get(customerType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown customer type: " + customerType);
        }
        return strategy;
    }
}
```

#### Step 4: Refactor calculateTotal() to Use Strategy

```java
public double calculateTotal() {
    double subtotal = calculateSubtotal();

    PricingStrategy strategy = PricingStrategyFactory.getStrategy(customerType);

    double discount = strategy.calculateDiscount(subtotal, this);
    double shipping = strategy.calculateShipping(subtotal, this);
    double taxRate = strategy.getTaxRate();

    // Apply additional discounts (promo code, manual discount)
    if (promoCode != null && !promoCode.isEmpty()) {
        discount += calculatePromoDiscount(subtotal);
    }
    if (discountPercentage > 0) {
        discount += subtotal * (discountPercentage / 100.0);
    }

    double total = subtotal - discount + shipping;
    total += total * taxRate;

    this.totalAmount = total;
    return total;
}

private double calculateSubtotal() {
    double subtotal = 0.0;
    for (OrderItem item : items) {
        subtotal += item.getPrice() * item.getQuantity();
    }
    return subtotal;
}
```

**Benefits:**
- Each pricing strategy independently testable
- Adding new customer types requires only new strategy class
- Pricing rules clearly encapsulated
- Open/Closed principle satisfied

**Risk:** Medium - Requires creating new classes and updating calculateTotal().

---

## Phase 5: Replace Conditional Dispatcher with Command

**Smell:** OrderActionHandler.handleAction() is a large conditional dispatcher (Lines 330-395)

**Kerievsky Technique:** Replace Conditional Dispatcher with Command

### Step-by-Step Mechanics

#### Step 1: Create Command Interface

```java
public interface OrderCommand {
    void execute(Order order, Map<String, Object> params);

    // Optional: for undo support
    default void undo(Order order) {
        throw new UnsupportedOperationException("Undo not supported for this command");
    }
}
```

#### Step 2: Create Concrete Commands

**ConfirmOrderCommand.java:**
```java
public class ConfirmOrderCommand implements OrderCommand {
    private OrderRepository repository;
    private AuditLogger auditLogger;

    public ConfirmOrderCommand(OrderRepository repository, AuditLogger auditLogger) {
        this.repository = repository;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(Order order, Map<String, Object> params) {
        order.confirm();
        repository.save(order);
        auditLogger.log("Order confirmed: " + order.getOrderId());
    }
}
```

**ShipOrderCommand.java:**
```java
public class ShipOrderCommand implements OrderCommand {
    private OrderRepository repository;
    private AuditLogger auditLogger;

    public ShipOrderCommand(OrderRepository repository, AuditLogger auditLogger) {
        this.repository = repository;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(Order order, Map<String, Object> params) {
        order.ship();
        repository.save(order);
        auditLogger.log("Order shipped: " + order.getOrderId());
    }
}
```

**CancelOrderCommand.java:**
```java
public class CancelOrderCommand implements OrderCommand {
    private OrderRepository repository;
    private AuditLogger auditLogger;

    public CancelOrderCommand(OrderRepository repository, AuditLogger auditLogger) {
        this.repository = repository;
        this.auditLogger = auditLogger;
    }

    @Override
    public void execute(Order order, Map<String, Object> params) {
        String reason = (String) params.get("reason");
        order.cancel();
        repository.save(order);
        auditLogger.log("Order cancelled: " + order.getOrderId() + ", reason: " + reason);
    }
}
```

**ApplyDiscountCommand.java:**
```java
public class ApplyDiscountCommand implements OrderCommand {
    private OrderRepository repository;
    private AuditLogger auditLogger;

    @Override
    public void execute(Order order, Map<String, Object> params) {
        double discount = (Double) params.get("discount");
        order.setDiscountPercentage(discount);
        order.calculateTotal();
        repository.save(order);
        auditLogger.log("Discount applied to: " + order.getOrderId());
    }
}
```

*Continue for remaining commands...*

#### Step 3: Create Command Registry

```java
public class OrderActionHandler {
    private Map<String, OrderCommand> commands = new HashMap<>();

    public OrderActionHandler(OrderRepository repository, AuditLogger auditLogger) {
        // Register all commands
        commands.put("confirm", new ConfirmOrderCommand(repository, auditLogger));
        commands.put("ship", new ShipOrderCommand(repository, auditLogger));
        commands.put("deliver", new DeliverOrderCommand(repository, auditLogger));
        commands.put("cancel", new CancelOrderCommand(repository, auditLogger));
        commands.put("update_shipping", new UpdateShippingCommand(repository, auditLogger));
        commands.put("apply_discount", new ApplyDiscountCommand(repository, auditLogger));
        commands.put("apply_promo", new ApplyPromoCommand(repository, auditLogger));
        commands.put("add_item", new AddItemCommand(repository, auditLogger));
        commands.put("print_invoice", new PrintInvoiceCommand(repository, auditLogger));
        commands.put("send_reminder", new SendReminderCommand(repository, auditLogger));
    }

    public void handleAction(String action, Order order, Map<String, Object> params) {
        OrderCommand command = commands.get(action);
        if (command == null) {
            throw new IllegalArgumentException("Unknown action: " + action);
        }
        command.execute(order, params);
    }

    // New capability: execute multiple commands
    public void executeMacro(Order order, List<String> actions, Map<String, Object> params) {
        for (String action : actions) {
            handleAction(action, order, params);
        }
    }
}
```

**Benefits:**
- Each command independently testable
- Easy to add new commands
- Supports undo/redo if implemented
- Supports macro recording
- Open/Closed principle satisfied

**Risk:** Medium - Requires creating command classes.

---

## Phase 6: Extract Adapter for Payment API Versions

**Smell:** PaymentProcessor has version-specific conditionals (Lines 408-500)

**Kerievsky Technique:** Extract Adapter

### Step-by-Step Mechanics

#### Step 1: Create Common Interface

```java
public interface PaymentGateway {
    PaymentResult processPayment(Order order, PaymentDetails details);
    RefundResult refundPayment(String transactionId, double amount);
}
```

#### Step 2: Create Adapter for Each Version

**LegacyPaymentAdapter.java (v1):**
```java
public class LegacyPaymentAdapter implements PaymentGateway {
    private LegacyPaymentGateway gateway;

    public LegacyPaymentAdapter() {
        this.gateway = new LegacyPaymentGateway();
        gateway.setMerchantId("MERCHANT_001");
        gateway.setApiKey("legacy_key_123");
    }

    @Override
    public PaymentResult processPayment(Order order, PaymentDetails details) {
        Map<String, String> legacyParams = new HashMap<>();
        legacyParams.put("amount", String.valueOf((int)(order.getTotalAmount() * 100)));
        legacyParams.put("currency", "USD");
        legacyParams.put("card_number", details.getCardNumber());
        legacyParams.put("card_exp", details.getExpiryMonth() + "/" + details.getExpiryYear());
        legacyParams.put("card_cvv", details.getCvv());
        legacyParams.put("order_ref", order.getOrderId());

        String response = gateway.submitTransaction(legacyParams);

        if (response.startsWith("OK:")) {
            return new PaymentResult(true, response.substring(3), null);
        }
        return new PaymentResult(false, null, response);
    }

    @Override
    public RefundResult refundPayment(String transactionId, double amount) {
        String response = gateway.refund(transactionId, (int)(amount * 100));
        return new RefundResult(response.startsWith("OK"), response);
    }
}
```

**ModernPaymentAdapter.java (v2):**
```java
public class ModernPaymentAdapter implements PaymentGateway {
    private ModernPaymentAPI api;

    public ModernPaymentAdapter() {
        this.api = new ModernPaymentAPI();
        api.authenticate("modern_api_key", "modern_secret");
    }

    @Override
    public PaymentResult processPayment(Order order, PaymentDetails details) {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(order.getTotalAmount());
        request.setCurrency("USD");
        request.setCardNumber(details.getCardNumber());
        request.setExpiryMonth(details.getExpiryMonth());
        request.setExpiryYear(details.getExpiryYear());
        request.setCvv(details.getCvv());
        request.setOrderReference(order.getOrderId());
        request.setCustomerEmail(details.getEmail());

        PaymentResponse response = api.createCharge(request);

        if (response.isSuccessful()) {
            return new PaymentResult(true, response.getChargeId(), null);
        }
        return new PaymentResult(false, null, response.getErrorMessage());
    }

    @Override
    public RefundResult refundPayment(String transactionId, double amount) {
        RefundResponse response = api.createRefund(transactionId, amount);
        return new RefundResult(response.isSuccessful(), response.getMessage());
    }
}
```

**LatestPaymentAdapter.java (v3):**
```java
public class LatestPaymentAdapter implements PaymentGateway {
    private LatestPaymentService service;

    public LatestPaymentAdapter() {
        this.service = new LatestPaymentService();
        service.initialize("v3_credentials");
    }

    @Override
    public PaymentResult processPayment(Order order, PaymentDetails details) {
        ChargeRequest charge = ChargeRequest.builder()
            .amount(order.getTotalAmount())
            .currency("USD")
            .paymentMethod(PaymentMethod.card(
                details.getCardNumber(),
                details.getExpiryMonth(),
                details.getExpiryYear(),
                details.getCvv()
            ))
            .metadata(Map.of(
                "order_id", order.getOrderId(),
                "customer_id", order.getCustomerId()
            ))
            .build();

        ChargeResult result = service.charge(charge);

        return new PaymentResult(
            result.getStatus() == ChargeStatus.SUCCEEDED,
            result.getId(),
            result.getFailureReason()
        );
    }

    @Override
    public RefundResult refundPayment(String transactionId, double amount) {
        return service.refund(transactionId, amount);
    }
}
```

#### Step 3: Create Factory

```java
public class PaymentGatewayFactory {

    public static PaymentGateway create(int apiVersion) {
        switch (apiVersion) {
            case 1: return new LegacyPaymentAdapter();
            case 2: return new ModernPaymentAdapter();
            case 3: return new LatestPaymentAdapter();
            default: throw new IllegalArgumentException("Unsupported API version: " + apiVersion);
        }
    }
}
```

#### Step 4: Simplify PaymentProcessor

```java
public class PaymentProcessor {
    private PaymentGateway gateway;

    public PaymentProcessor(int apiVersion) {
        this.gateway = PaymentGatewayFactory.create(apiVersion);
    }

    public PaymentResult processPayment(Order order, PaymentDetails details) {
        return gateway.processPayment(order, details);
    }

    public RefundResult refundPayment(String transactionId, double amount) {
        return gateway.refundPayment(transactionId, amount);
    }
}
```

**Benefits:**
- Version-specific code encapsulated in adapters
- Adding new API versions requires only new adapter
- Each adapter independently testable
- PaymentProcessor now simple and focused

**Risk:** Medium - Requires creating adapter classes.

---

## Phase 7: Replace One/Many Distinctions with Composite

**Smell:** OrderItem handling treats bundles and single items differently (Lines 282-315)

**Kerievsky Technique:** Replace One/Many Distinctions with Composite

### Step-by-Step Mechanics

#### Step 1: Create Component Interface

```java
public interface OrderComponent {
    double getTotal();
    int getItemCount();
    String getDescription();
    void accept(OrderVisitor visitor); // For future Visitor pattern
}
```

#### Step 2: Refactor OrderItem to Implement Interface

```java
public class OrderItem implements OrderComponent {
    private String productId;
    private String productName;
    private double price;
    private int quantity;

    public OrderItem(String productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public double getTotal() {
        return price * quantity;
    }

    @Override
    public int getItemCount() {
        return quantity;
    }

    @Override
    public String getDescription() {
        return productName + " x" + quantity;
    }

    @Override
    public void accept(OrderVisitor visitor) {
        visitor.visit(this);
    }

    // Getters...
}
```

#### Step 3: Create Bundle as Composite

```java
public class OrderBundle implements OrderComponent {
    private String bundleId;
    private String bundleName;
    private List<OrderComponent> components = new ArrayList<>();
    private double discountRate = 0.10; // 10% bundle discount

    public OrderBundle(String bundleId, String bundleName) {
        this.bundleId = bundleId;
        this.bundleName = bundleName;
    }

    public void add(OrderComponent component) {
        components.add(component);
    }

    public void remove(OrderComponent component) {
        components.remove(component);
    }

    @Override
    public double getTotal() {
        double subtotal = components.stream()
            .mapToDouble(OrderComponent::getTotal)
            .sum();
        return subtotal * (1 - discountRate); // Apply bundle discount
    }

    @Override
    public int getItemCount() {
        return components.stream()
            .mapToInt(OrderComponent::getItemCount)
            .sum();
    }

    @Override
    public String getDescription() {
        return "[BUNDLE] " + bundleName;
    }

    @Override
    public void accept(OrderVisitor visitor) {
        visitor.visit(this);
        for (OrderComponent component : components) {
            component.accept(visitor);
        }
    }

    public List<OrderComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
}
```

#### Step 4: Update Order to Use OrderComponent

```java
public class Order {
    private List<OrderComponent> items = new ArrayList<>();

    public void addItem(OrderComponent item) {
        items.add(item);
        updatedAt = LocalDateTime.now();
    }

    public double getItemsTotal() {
        return items.stream()
            .mapToDouble(OrderComponent::getTotal)
            .sum();
    }

    public int getTotalItemCount() {
        return items.stream()
            .mapToInt(OrderComponent::getItemCount)
            .sum();
    }
}
```

**Benefits:**
- Uniform treatment of single items and bundles
- Supports arbitrarily nested bundles
- No more isBundle() checks
- Polymorphic operations

**Risk:** Medium - Changes OrderItem interface.

---

## Phase 8: Move Accumulation to Collecting Parameter

**Smell:** generateReport() uses inefficient string concatenation (Lines 520-585)

**Kerievsky Technique:** Move Accumulation to Collecting Parameter + Compose Method

### Step-by-Step Mechanics

#### Step 1: Replace String with StringBuilder

```java
public String generateReport(List<Order> orders) {
    StringBuilder report = new StringBuilder();
    // ... rest uses report.append()
    return report.toString();
}
```

#### Step 2: Extract Methods with Collecting Parameter

```java
public String generateReport(List<Order> orders) {
    StringBuilder report = new StringBuilder();
    ReportMetrics metrics = new ReportMetrics();

    writeHeader(report, orders.size());
    writeOrderDetails(report, orders, metrics);
    writeSummary(report, metrics);

    return report.toString();
}

private void writeHeader(StringBuilder report, int orderCount) {
    report.append("=== ORDER REPORT ===\n");
    report.append("Generated: ").append(LocalDateTime.now()).append("\n");
    report.append("Total Orders: ").append(orderCount).append("\n\n");
}

private void writeOrderDetails(StringBuilder report, List<Order> orders, ReportMetrics metrics) {
    for (Order order : orders) {
        writeOrderSection(report, order, metrics);
    }
}

private void writeOrderSection(StringBuilder report, Order order, ReportMetrics metrics) {
    report.append("Order: ").append(order.getOrderId()).append("\n");
    report.append("  Customer: ").append(order.getCustomerId())
          .append(" (").append(order.getCustomerType()).append(")\n");
    report.append("  Status: ").append(order.getStatus()).append("\n");
    report.append("  Items:\n");

    writeItems(report, order.getItems(), metrics);

    report.append("  Total: $").append(String.format("%.2f", order.getTotalAmount())).append("\n\n");

    metrics.addRevenue(order.getTotalAmount());
    metrics.incrementStatus(order.getStatus());
}

private void writeItems(StringBuilder report, List<OrderComponent> items, ReportMetrics metrics) {
    for (OrderComponent item : items) {
        item.accept(new ReportItemVisitor(report, metrics));
    }
}

private void writeSummary(StringBuilder report, ReportMetrics metrics) {
    report.append("=== SUMMARY ===\n");
    report.append("Total Revenue: $").append(String.format("%.2f", metrics.getTotalRevenue())).append("\n");
    report.append("Total Items Sold: ").append(metrics.getTotalItems()).append("\n");
    // ... status breakdown
}
```

#### Step 3: Create ReportMetrics as Collecting Object

```java
public class ReportMetrics {
    private double totalRevenue = 0;
    private int totalItems = 0;
    private Map<String, Integer> statusCounts = new HashMap<>();

    public void addRevenue(double amount) {
        totalRevenue += amount;
    }

    public void addItems(int count) {
        totalItems += count;
    }

    public void incrementStatus(String status) {
        statusCounts.merge(status, 1, Integer::sum);
    }

    // Getters...
}
```

**Benefits:**
- Efficient StringBuilder usage
- Composed method with clear structure
- Metrics accumulated cleanly
- Each section method independently testable

**Risk:** Low - Refactoring within single class.

---

## Phase 9: Move Accumulation to Visitor

**Smell:** calculateMetrics() uses instanceof chain (Lines 587-620)

**Kerievsky Technique:** Move Accumulation to Visitor

### Step-by-Step Mechanics

#### Step 1: Create Visitor Interface

```java
public interface EntityVisitor {
    void visit(Order order);
    void visit(OrderItem item);
    void visit(Customer customer);
}
```

#### Step 2: Add accept() to Entity Classes

```java
public interface Visitable {
    void accept(EntityVisitor visitor);
}

public class Order implements Visitable {
    @Override
    public void accept(EntityVisitor visitor) {
        visitor.visit(this);
    }
}

public class Customer implements Visitable {
    @Override
    public void accept(EntityVisitor visitor) {
        visitor.visit(this);
    }
}
```

#### Step 3: Create MetricsVisitor

```java
public class MetricsVisitor implements EntityVisitor {
    private double totalOrderValue = 0;
    private double totalProductValue = 0;
    private int orderCount = 0;
    private int productCount = 0;
    private int customerCount = 0;
    private double customerSpending = 0;

    @Override
    public void visit(Order order) {
        totalOrderValue += order.getTotalAmount();
        orderCount++;
    }

    @Override
    public void visit(OrderItem item) {
        totalProductValue += item.getPrice() * item.getQuantity();
        productCount += item.getQuantity();
    }

    @Override
    public void visit(Customer customer) {
        customerCount++;
        customerSpending += customer.getTotalSpent();
    }

    public Map<String, Double> getMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("totalOrderValue", totalOrderValue);
        metrics.put("averageOrderValue", orderCount > 0 ? totalOrderValue / orderCount : 0);
        metrics.put("totalProductValue", totalProductValue);
        metrics.put("productCount", (double) productCount);
        metrics.put("customerCount", (double) customerCount);
        metrics.put("averageCustomerSpending", customerCount > 0 ? customerSpending / customerCount : 0);
        return metrics;
    }
}
```

#### Step 4: Refactor calculateMetrics()

```java
public Map<String, Double> calculateMetrics(List<Visitable> entities) {
    MetricsVisitor visitor = new MetricsVisitor();
    for (Visitable entity : entities) {
        entity.accept(visitor);
    }
    return visitor.getMetrics();
}
```

**Benefits:**
- No more instanceof checks
- Type-specific logic in Visitor
- Easy to add new entity types
- Easy to add new operations (new Visitor)

**Risk:** Medium - Requires adding accept() to entity classes.

---

## Phase 10: Encapsulate Composite with Builder

**Smell:** NotificationBuilder has tedious manual construction (Lines 625-695)

**Kerievsky Technique:** Encapsulate Composite with Builder

### Step-by-Step Mechanics

#### Step 1: Create Fluent Builder

```java
public class NotificationBuilder {
    private Notification notification;
    private Deque<List<NotificationSection>> sectionStack = new ArrayDeque<>();
    private List<NotificationSection> currentSections;

    public NotificationBuilder(String type, String recipientId) {
        notification = new Notification();
        notification.setType(type);
        notification.setRecipientId(recipientId);
        notification.setTimestamp(LocalDateTime.now());
        currentSections = new ArrayList<>();
        notification.setSections(currentSections);
    }

    public NotificationBuilder header(String content) {
        addSection("header", content);
        return this;
    }

    public NotificationBuilder status(String content) {
        addSection("status", content);
        return this;
    }

    public NotificationBuilder footer(String content) {
        addSection("footer", content);
        return this;
    }

    public NotificationBuilder startSection(String type) {
        NotificationSection section = new NotificationSection();
        section.setType(type);
        section.setSubsections(new ArrayList<>());
        currentSections.add(section);

        sectionStack.push(currentSections);
        currentSections = section.getSubsections();
        return this;
    }

    public NotificationBuilder endSection() {
        currentSections = sectionStack.pop();
        return this;
    }

    public NotificationBuilder item(String content) {
        addSection("item", content);
        return this;
    }

    public NotificationBuilder giftMessage(String message) {
        if (message != null && !message.isEmpty()) {
            addSection("gift-message", message);
        }
        return this;
    }

    private void addSection(String type, String content) {
        NotificationSection section = new NotificationSection();
        section.setType(type);
        section.setContent(content);
        currentSections.add(section);
    }

    public Notification build() {
        return notification;
    }
}
```

#### Step 2: Refactor buildOrderNotification()

```java
public Notification buildOrderNotification(Order order, String type) {
    NotificationBuilder builder = new NotificationBuilder(type, order.getCustomerId())
        .header("Order " + order.getOrderId())
        .status("Status: " + order.getStatus())
        .startSection("items");

    for (OrderComponent item : order.getItems()) {
        if (item instanceof OrderBundle) {
            OrderBundle bundle = (OrderBundle) item;
            builder.startSection("bundle")
                   .item(bundle.getDescription());
            for (OrderComponent bundled : bundle.getComponents()) {
                builder.item(bundled.getDescription());
            }
            builder.endSection();
        } else {
            builder.item(item.getDescription());
        }
    }

    builder.endSection()
           .footer("Total: $" + String.format("%.2f", order.getTotalAmount()))
           .giftMessage(order.getGiftMessage());

    return builder.build();
}
```

**Benefits:**
- Fluent API is readable and less error-prone
- Nesting handled automatically
- Impossible to create malformed notifications
- Builder encapsulates construction complexity

**Risk:** Low - Improves existing builder pattern.

---

## Summary: Refactoring Sequence

| Phase | Refactoring | Pattern | Risk | Dependencies |
|-------|-------------|---------|------|--------------|
| 1.1 | Chain Constructors | - | Low | None |
| 1.2 | Replace Constructors with Creation Methods | Factory Method | Low | 1.1 |
| 1.3 | Replace Primitive Status with Enum | - | Low | None |
| 1.4 | Replace Primitive CustomerType with Enum | - | Low | None |
| 2 | Replace State-Altering Conditionals | State | High | 1.3 |
| 3 | Replace Hard-Coded Notifications | Observer | Medium | 2 |
| 4 | Replace Conditional Logic | Strategy | Medium | 1.4 |
| 5 | Replace Conditional Dispatcher | Command | Medium | None |
| 6 | Extract Adapter | Adapter | Medium | None |
| 7 | Replace One/Many Distinctions | Composite | Medium | None |
| 8 | Move Accumulation to Collecting Parameter | - | Low | 7 |
| 9 | Move Accumulation to Visitor | Visitor | Medium | 7 |
| 10 | Encapsulate Composite with Builder | Builder | Low | None |

## When NOT to Apply These Patterns

Following Kerievsky's principle of avoiding over-engineering:

1. **Don't add Strategy** if only one pricing algorithm exists
2. **Don't add State** if order statuses never change
3. **Don't add Command** if actions are never undone or recorded
4. **Don't add Observer** if only one notification channel exists
5. **Don't add Composite** if bundles are never nested
6. **Don't add Visitor** if only one operation traverses entities

Each pattern should emerge from actual, demonstrated need - not speculation.

---

## Test Strategy

For each refactoring phase:

1. **Before**: Ensure comprehensive tests exist for current behavior
2. **During**: Run tests after each small step
3. **After**: Add pattern-specific tests (e.g., State transition tests, Strategy algorithm tests)

**Key Test Categories:**
- State transition tests (valid and invalid)
- Pricing calculation tests per customer type
- Command execution and undo tests
- Payment adapter tests per version
- Composite total calculation tests
- Visitor accumulation tests
