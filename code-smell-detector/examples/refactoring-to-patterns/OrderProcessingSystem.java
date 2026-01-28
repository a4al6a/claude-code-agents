package com.example.orders;

import java.util.*;
import java.time.LocalDateTime;

/**
 * Order Processing System - Example for Refactoring to Patterns
 *
 * This code contains multiple opportunities for pattern-directed refactoring
 * according to Joshua Kerievsky's "Refactoring to Patterns" principles.
 */
public class OrderProcessingSystem {

    // ============================================================================
    // ORDER CLASS - Multiple pattern opportunities
    // ============================================================================

    public static class Order {
        private String orderId;
        private String customerId;
        private String customerType; // "regular", "premium", "vip"
        private String status; // "pending", "confirmed", "shipped", "delivered", "cancelled"
        private List<OrderItem> items;
        private double totalAmount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String shippingAddress;
        private String billingAddress;
        private String paymentMethod;
        private boolean isGift;
        private String giftMessage;
        private double discountPercentage;
        private String promoCode;

        // Hard-coded notification targets
        private EmailService emailService;
        private SMSService smsService;
        private InventoryService inventoryService;
        private AnalyticsService analyticsService;

        // SMELL: Multiple constructors with unclear purposes (Chain Constructors opportunity)
        public Order(String orderId, String customerId) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.customerType = "regular";
            this.status = "pending";
            this.items = new ArrayList<>();
            this.totalAmount = 0.0;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            this.isGift = false;
            this.discountPercentage = 0.0;
            this.emailService = new EmailService();
            this.smsService = new SMSService();
            this.inventoryService = new InventoryService();
            this.analyticsService = new AnalyticsService();
        }

        public Order(String orderId, String customerId, String customerType) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.customerType = customerType;
            this.status = "pending";
            this.items = new ArrayList<>();
            this.totalAmount = 0.0;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            this.isGift = false;
            this.discountPercentage = 0.0;
            this.emailService = new EmailService();
            this.smsService = new SMSService();
            this.inventoryService = new InventoryService();
            this.analyticsService = new AnalyticsService();
        }

        public Order(String orderId, String customerId, String customerType, String shippingAddress) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.customerType = customerType;
            this.shippingAddress = shippingAddress;
            this.billingAddress = shippingAddress;
            this.status = "pending";
            this.items = new ArrayList<>();
            this.totalAmount = 0.0;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            this.isGift = false;
            this.discountPercentage = 0.0;
            this.emailService = new EmailService();
            this.smsService = new SMSService();
            this.inventoryService = new InventoryService();
            this.analyticsService = new AnalyticsService();
        }

        public Order(String orderId, String customerId, String customerType,
                     String shippingAddress, String billingAddress, boolean isGift, String giftMessage) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.customerType = customerType;
            this.shippingAddress = shippingAddress;
            this.billingAddress = billingAddress;
            this.status = "pending";
            this.items = new ArrayList<>();
            this.totalAmount = 0.0;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            this.isGift = isGift;
            this.giftMessage = giftMessage;
            this.discountPercentage = 0.0;
            this.emailService = new EmailService();
            this.smsService = new SMSService();
            this.inventoryService = new InventoryService();
            this.analyticsService = new AnalyticsService();
        }

        // SMELL: State-altering conditionals (Replace State-Altering Conditionals with State)
        public void confirm() {
            if (status.equals("pending")) {
                if (items.isEmpty()) {
                    throw new IllegalStateException("Cannot confirm order with no items");
                }
                status = "confirmed";
                updatedAt = LocalDateTime.now();

                // Hard-coded notifications
                emailService.sendOrderConfirmation(this);
                smsService.sendOrderConfirmation(this);
                inventoryService.reserveItems(this);
                analyticsService.trackOrderConfirmed(this);
            } else if (status.equals("confirmed")) {
                throw new IllegalStateException("Order is already confirmed");
            } else if (status.equals("shipped")) {
                throw new IllegalStateException("Cannot confirm shipped order");
            } else if (status.equals("delivered")) {
                throw new IllegalStateException("Cannot confirm delivered order");
            } else if (status.equals("cancelled")) {
                throw new IllegalStateException("Cannot confirm cancelled order");
            }
        }

        public void ship() {
            if (status.equals("pending")) {
                throw new IllegalStateException("Cannot ship pending order - must confirm first");
            } else if (status.equals("confirmed")) {
                status = "shipped";
                updatedAt = LocalDateTime.now();

                // Hard-coded notifications
                emailService.sendShippingNotification(this);
                smsService.sendShippingNotification(this);
                inventoryService.updateShippedItems(this);
                analyticsService.trackOrderShipped(this);
            } else if (status.equals("shipped")) {
                throw new IllegalStateException("Order is already shipped");
            } else if (status.equals("delivered")) {
                throw new IllegalStateException("Cannot ship delivered order");
            } else if (status.equals("cancelled")) {
                throw new IllegalStateException("Cannot ship cancelled order");
            }
        }

        public void deliver() {
            if (status.equals("pending")) {
                throw new IllegalStateException("Cannot deliver pending order");
            } else if (status.equals("confirmed")) {
                throw new IllegalStateException("Cannot deliver unshipped order");
            } else if (status.equals("shipped")) {
                status = "delivered";
                updatedAt = LocalDateTime.now();

                // Hard-coded notifications
                emailService.sendDeliveryConfirmation(this);
                smsService.sendDeliveryConfirmation(this);
                analyticsService.trackOrderDelivered(this);
            } else if (status.equals("delivered")) {
                throw new IllegalStateException("Order is already delivered");
            } else if (status.equals("cancelled")) {
                throw new IllegalStateException("Cannot deliver cancelled order");
            }
        }

        public void cancel() {
            if (status.equals("pending")) {
                status = "cancelled";
                updatedAt = LocalDateTime.now();

                emailService.sendCancellationNotification(this);
                analyticsService.trackOrderCancelled(this);
            } else if (status.equals("confirmed")) {
                status = "cancelled";
                updatedAt = LocalDateTime.now();

                emailService.sendCancellationNotification(this);
                smsService.sendCancellationNotification(this);
                inventoryService.releaseReservedItems(this);
                analyticsService.trackOrderCancelled(this);
            } else if (status.equals("shipped")) {
                throw new IllegalStateException("Cannot cancel shipped order - request return instead");
            } else if (status.equals("delivered")) {
                throw new IllegalStateException("Cannot cancel delivered order - request return instead");
            } else if (status.equals("cancelled")) {
                throw new IllegalStateException("Order is already cancelled");
            }
        }

        // SMELL: Algorithm-selecting conditional (Replace Conditional Logic with Strategy)
        public double calculateTotal() {
            double subtotal = 0.0;

            for (OrderItem item : items) {
                subtotal += item.getPrice() * item.getQuantity();
            }

            // Complex pricing logic based on customer type
            double discount = 0.0;
            double shippingCost = 0.0;
            double taxRate = 0.0;

            if (customerType.equals("regular")) {
                // Regular customer pricing
                if (subtotal > 100) {
                    discount = subtotal * 0.05; // 5% for orders over $100
                }
                if (subtotal < 50) {
                    shippingCost = 9.99;
                } else if (subtotal < 100) {
                    shippingCost = 4.99;
                } else {
                    shippingCost = 0.0; // Free shipping over $100
                }
                taxRate = 0.08; // 8% tax

            } else if (customerType.equals("premium")) {
                // Premium customer pricing
                discount = subtotal * 0.10; // Always 10% discount
                if (subtotal < 25) {
                    shippingCost = 4.99;
                } else {
                    shippingCost = 0.0; // Free shipping over $25
                }
                taxRate = 0.08;

            } else if (customerType.equals("vip")) {
                // VIP customer pricing
                discount = subtotal * 0.15; // Always 15% discount
                shippingCost = 0.0; // Always free shipping
                taxRate = 0.05; // Reduced tax rate

                // VIP bonus: extra discount on large orders
                if (subtotal > 500) {
                    discount += subtotal * 0.05; // Additional 5%
                }
                if (subtotal > 1000) {
                    discount += subtotal * 0.05; // Additional 5% more
                }
            }

            // Apply promo code discount
            if (promoCode != null && !promoCode.isEmpty()) {
                discount += calculatePromoDiscount(subtotal);
            }

            // Apply manual discount percentage
            if (discountPercentage > 0) {
                discount += subtotal * (discountPercentage / 100.0);
            }

            double total = subtotal - discount + shippingCost;
            total += total * taxRate;

            this.totalAmount = total;
            return total;
        }

        private double calculatePromoDiscount(double subtotal) {
            // SMELL: Type-switching conditional
            if (promoCode.startsWith("PERCENT")) {
                int percent = Integer.parseInt(promoCode.substring(7));
                return subtotal * (percent / 100.0);
            } else if (promoCode.startsWith("FIXED")) {
                return Double.parseDouble(promoCode.substring(5));
            } else if (promoCode.startsWith("BOGO")) {
                // Buy one get one - find cheapest item
                double cheapest = Double.MAX_VALUE;
                for (OrderItem item : items) {
                    if (item.getPrice() < cheapest) {
                        cheapest = item.getPrice();
                    }
                }
                return cheapest;
            } else if (promoCode.equals("FREESHIP")) {
                return 0; // Handled elsewhere
            }
            return 0;
        }

        // SMELL: One/many distinctions (Replace One/Many Distinctions with Composite)
        public void addItem(OrderItem item) {
            items.add(item);
            updatedAt = LocalDateTime.now();
        }

        public void addItems(List<OrderItem> newItems) {
            for (OrderItem item : newItems) {
                items.add(item);
            }
            updatedAt = LocalDateTime.now();
        }

        public double getItemsTotal() {
            double total = 0;
            for (OrderItem item : items) {
                if (item.isBundle()) {
                    // Bundle items have special pricing
                    for (OrderItem bundledItem : item.getBundledItems()) {
                        total += bundledItem.getPrice() * bundledItem.getQuantity() * 0.9; // 10% bundle discount
                    }
                } else {
                    total += item.getPrice() * item.getQuantity();
                }
            }
            return total;
        }

        public int getTotalItemCount() {
            int count = 0;
            for (OrderItem item : items) {
                if (item.isBundle()) {
                    for (OrderItem bundledItem : item.getBundledItems()) {
                        count += bundledItem.getQuantity();
                    }
                } else {
                    count += item.getQuantity();
                }
            }
            return count;
        }

        // Getters
        public String getOrderId() { return orderId; }
        public String getCustomerId() { return customerId; }
        public String getCustomerType() { return customerType; }
        public String getStatus() { return status; }
        public List<OrderItem> getItems() { return items; }
        public double getTotalAmount() { return totalAmount; }
        public String getShippingAddress() { return shippingAddress; }
        public boolean isGift() { return isGift; }
        public String getGiftMessage() { return giftMessage; }

        public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
        public void setDiscountPercentage(double percentage) { this.discountPercentage = percentage; }
    }

    // ============================================================================
    // ORDER ITEM CLASS - Composite pattern opportunity
    // ============================================================================

    public static class OrderItem {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        private boolean isBundle;
        private List<OrderItem> bundledItems;

        public OrderItem(String productId, String productName, double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.isBundle = false;
            this.bundledItems = new ArrayList<>();
        }

        public OrderItem(String productId, String productName, List<OrderItem> bundledItems) {
            this.productId = productId;
            this.productName = productName;
            this.isBundle = true;
            this.bundledItems = bundledItems;
            this.quantity = 1;
            // Calculate bundle price as sum of items
            this.price = 0;
            for (OrderItem item : bundledItems) {
                this.price += item.getPrice() * item.getQuantity();
            }
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public boolean isBundle() { return isBundle; }
        public List<OrderItem> getBundledItems() { return bundledItems; }
    }

    // ============================================================================
    // ORDER ACTION HANDLER - Command pattern opportunity
    // ============================================================================

    public static class OrderActionHandler {
        private OrderRepository repository;
        private AuditLogger auditLogger;

        public OrderActionHandler(OrderRepository repository, AuditLogger auditLogger) {
            this.repository = repository;
            this.auditLogger = auditLogger;
        }

        // SMELL: Conditional dispatcher (Replace Conditional Dispatcher with Command)
        public void handleAction(String action, Order order, Map<String, Object> params) {
            if (action.equals("confirm")) {
                order.confirm();
                repository.save(order);
                auditLogger.log("Order confirmed: " + order.getOrderId());

            } else if (action.equals("ship")) {
                order.ship();
                repository.save(order);
                auditLogger.log("Order shipped: " + order.getOrderId());

            } else if (action.equals("deliver")) {
                order.deliver();
                repository.save(order);
                auditLogger.log("Order delivered: " + order.getOrderId());

            } else if (action.equals("cancel")) {
                String reason = (String) params.get("reason");
                order.cancel();
                repository.save(order);
                auditLogger.log("Order cancelled: " + order.getOrderId() + ", reason: " + reason);

            } else if (action.equals("update_shipping")) {
                String newAddress = (String) params.get("address");
                // Would update shipping address
                repository.save(order);
                auditLogger.log("Shipping updated for: " + order.getOrderId());

            } else if (action.equals("apply_discount")) {
                double discount = (Double) params.get("discount");
                order.setDiscountPercentage(discount);
                order.calculateTotal();
                repository.save(order);
                auditLogger.log("Discount applied to: " + order.getOrderId());

            } else if (action.equals("apply_promo")) {
                String promoCode = (String) params.get("promoCode");
                order.setPromoCode(promoCode);
                order.calculateTotal();
                repository.save(order);
                auditLogger.log("Promo applied to: " + order.getOrderId());

            } else if (action.equals("add_item")) {
                OrderItem item = (OrderItem) params.get("item");
                order.addItem(item);
                order.calculateTotal();
                repository.save(order);
                auditLogger.log("Item added to: " + order.getOrderId());

            } else if (action.equals("print_invoice")) {
                // Would generate and print invoice
                auditLogger.log("Invoice printed for: " + order.getOrderId());

            } else if (action.equals("send_reminder")) {
                // Would send reminder email
                auditLogger.log("Reminder sent for: " + order.getOrderId());

            } else {
                throw new IllegalArgumentException("Unknown action: " + action);
            }
        }
    }

    // ============================================================================
    // PAYMENT PROCESSOR - Adapter pattern opportunity
    // ============================================================================

    public static class PaymentProcessor {
        private int apiVersion;

        public PaymentProcessor(int apiVersion) {
            this.apiVersion = apiVersion;
        }

        // SMELL: Version-specific conditionals (Extract Adapter)
        public PaymentResult processPayment(Order order, PaymentDetails details) {
            if (apiVersion == 1) {
                // Legacy API v1
                LegacyPaymentGateway gateway = new LegacyPaymentGateway();
                gateway.setMerchantId("MERCHANT_001");
                gateway.setApiKey("legacy_key_123");

                Map<String, String> legacyParams = new HashMap<>();
                legacyParams.put("amount", String.valueOf((int)(order.getTotalAmount() * 100))); // cents
                legacyParams.put("currency", "USD");
                legacyParams.put("card_number", details.getCardNumber());
                legacyParams.put("card_exp", details.getExpiryMonth() + "/" + details.getExpiryYear());
                legacyParams.put("card_cvv", details.getCvv());
                legacyParams.put("order_ref", order.getOrderId());

                String response = gateway.submitTransaction(legacyParams);

                if (response.startsWith("OK:")) {
                    String transactionId = response.substring(3);
                    return new PaymentResult(true, transactionId, null);
                } else {
                    return new PaymentResult(false, null, response);
                }

            } else if (apiVersion == 2) {
                // Modern API v2
                ModernPaymentAPI api = new ModernPaymentAPI();
                api.authenticate("modern_api_key", "modern_secret");

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
                } else {
                    return new PaymentResult(false, null, response.getErrorMessage());
                }

            } else if (apiVersion == 3) {
                // Latest API v3 with async support
                LatestPaymentService service = new LatestPaymentService();
                service.initialize("v3_credentials");

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

            } else {
                throw new IllegalArgumentException("Unsupported API version: " + apiVersion);
            }
        }

        // SMELL: More version-specific code
        public RefundResult refundPayment(String transactionId, double amount) {
            if (apiVersion == 1) {
                LegacyPaymentGateway gateway = new LegacyPaymentGateway();
                gateway.setMerchantId("MERCHANT_001");
                gateway.setApiKey("legacy_key_123");

                String response = gateway.refund(transactionId, (int)(amount * 100));
                return new RefundResult(response.startsWith("OK"), response);

            } else if (apiVersion == 2) {
                ModernPaymentAPI api = new ModernPaymentAPI();
                api.authenticate("modern_api_key", "modern_secret");

                RefundResponse response = api.createRefund(transactionId, amount);
                return new RefundResult(response.isSuccessful(), response.getMessage());

            } else if (apiVersion == 3) {
                LatestPaymentService service = new LatestPaymentService();
                service.initialize("v3_credentials");

                RefundResult result = service.refund(transactionId, amount);
                return result;

            } else {
                throw new IllegalArgumentException("Unsupported API version: " + apiVersion);
            }
        }
    }

    // ============================================================================
    // REPORT GENERATOR - Collecting Parameter / Visitor opportunity
    // ============================================================================

    public static class OrderReportGenerator {

        // SMELL: Bulky accumulation method (Move Accumulation to Collecting Parameter)
        public String generateReport(List<Order> orders) {
            String report = "";
            report += "=== ORDER REPORT ===\n";
            report += "Generated: " + LocalDateTime.now() + "\n";
            report += "Total Orders: " + orders.size() + "\n";
            report += "\n";

            double totalRevenue = 0;
            int totalItems = 0;
            int pendingCount = 0;
            int confirmedCount = 0;
            int shippedCount = 0;
            int deliveredCount = 0;
            int cancelledCount = 0;

            for (Order order : orders) {
                report += "Order: " + order.getOrderId() + "\n";
                report += "  Customer: " + order.getCustomerId() + " (" + order.getCustomerType() + ")\n";
                report += "  Status: " + order.getStatus() + "\n";
                report += "  Items:\n";

                for (OrderItem item : order.getItems()) {
                    if (item.isBundle()) {
                        report += "    [BUNDLE] " + item.getProductName() + "\n";
                        for (OrderItem bundled : item.getBundledItems()) {
                            report += "      - " + bundled.getProductName() + " x" + bundled.getQuantity() + " @ $" + bundled.getPrice() + "\n";
                            totalItems += bundled.getQuantity();
                        }
                    } else {
                        report += "    - " + item.getProductName() + " x" + item.getQuantity() + " @ $" + item.getPrice() + "\n";
                        totalItems += item.getQuantity();
                    }
                }

                report += "  Total: $" + String.format("%.2f", order.getTotalAmount()) + "\n";
                report += "\n";

                totalRevenue += order.getTotalAmount();

                if (order.getStatus().equals("pending")) pendingCount++;
                else if (order.getStatus().equals("confirmed")) confirmedCount++;
                else if (order.getStatus().equals("shipped")) shippedCount++;
                else if (order.getStatus().equals("delivered")) deliveredCount++;
                else if (order.getStatus().equals("cancelled")) cancelledCount++;
            }

            report += "=== SUMMARY ===\n";
            report += "Total Revenue: $" + String.format("%.2f", totalRevenue) + "\n";
            report += "Total Items Sold: " + totalItems + "\n";
            report += "Status Breakdown:\n";
            report += "  Pending: " + pendingCount + "\n";
            report += "  Confirmed: " + confirmedCount + "\n";
            report += "  Shipped: " + shippedCount + "\n";
            report += "  Delivered: " + deliveredCount + "\n";
            report += "  Cancelled: " + cancelledCount + "\n";

            return report;
        }

        // SMELL: Type-checking accumulation (Move Accumulation to Visitor)
        public Map<String, Double> calculateMetrics(List<Object> entities) {
            Map<String, Double> metrics = new HashMap<>();
            double totalOrderValue = 0;
            double totalProductValue = 0;
            int orderCount = 0;
            int productCount = 0;
            int customerCount = 0;
            double customerSpending = 0;

            for (Object entity : entities) {
                if (entity instanceof Order) {
                    Order order = (Order) entity;
                    totalOrderValue += order.getTotalAmount();
                    orderCount++;
                } else if (entity instanceof OrderItem) {
                    OrderItem item = (OrderItem) entity;
                    totalProductValue += item.getPrice() * item.getQuantity();
                    productCount += item.getQuantity();
                } else if (entity instanceof Customer) {
                    Customer customer = (Customer) entity;
                    customerCount++;
                    customerSpending += customer.getTotalSpent();
                }
            }

            metrics.put("totalOrderValue", totalOrderValue);
            metrics.put("averageOrderValue", orderCount > 0 ? totalOrderValue / orderCount : 0);
            metrics.put("totalProductValue", totalProductValue);
            metrics.put("productCount", (double) productCount);
            metrics.put("customerCount", (double) customerCount);
            metrics.put("averageCustomerSpending", customerCount > 0 ? customerSpending / customerCount : 0);

            return metrics;
        }
    }

    // ============================================================================
    // NOTIFICATION SERVICE - Builder opportunity for complex notifications
    // ============================================================================

    public static class NotificationBuilder {

        // SMELL: Complex object construction (Encapsulate Composite with Builder)
        public Notification buildOrderNotification(Order order, String type) {
            Notification notification = new Notification();
            notification.setType(type);
            notification.setRecipientId(order.getCustomerId());
            notification.setTimestamp(LocalDateTime.now());

            // Build content sections manually - tedious and error-prone
            List<NotificationSection> sections = new ArrayList<>();

            NotificationSection header = new NotificationSection();
            header.setType("header");
            header.setContent("Order " + order.getOrderId());
            sections.add(header);

            NotificationSection status = new NotificationSection();
            status.setType("status");
            status.setContent("Status: " + order.getStatus());
            sections.add(status);

            NotificationSection itemsSection = new NotificationSection();
            itemsSection.setType("items");
            List<NotificationSection> itemSubsections = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                NotificationSection itemSection = new NotificationSection();
                itemSection.setType("item");
                itemSection.setContent(item.getProductName() + " x" + item.getQuantity());

                if (item.isBundle()) {
                    List<NotificationSection> bundledSections = new ArrayList<>();
                    for (OrderItem bundled : item.getBundledItems()) {
                        NotificationSection bundledSection = new NotificationSection();
                        bundledSection.setType("bundled-item");
                        bundledSection.setContent(bundled.getProductName());
                        bundledSections.add(bundledSection);
                    }
                    itemSection.setSubsections(bundledSections);
                }

                itemSubsections.add(itemSection);
            }
            itemsSection.setSubsections(itemSubsections);
            sections.add(itemsSection);

            NotificationSection footer = new NotificationSection();
            footer.setType("footer");
            footer.setContent("Total: $" + String.format("%.2f", order.getTotalAmount()));
            sections.add(footer);

            if (order.isGift()) {
                NotificationSection giftSection = new NotificationSection();
                giftSection.setType("gift-message");
                giftSection.setContent(order.getGiftMessage());
                sections.add(giftSection);
            }

            notification.setSections(sections);
            return notification;
        }
    }

    // ============================================================================
    // SUPPORTING CLASSES (Stubs for compilation)
    // ============================================================================

    public static class EmailService {
        public void sendOrderConfirmation(Order order) {}
        public void sendShippingNotification(Order order) {}
        public void sendDeliveryConfirmation(Order order) {}
        public void sendCancellationNotification(Order order) {}
    }

    public static class SMSService {
        public void sendOrderConfirmation(Order order) {}
        public void sendShippingNotification(Order order) {}
        public void sendDeliveryConfirmation(Order order) {}
        public void sendCancellationNotification(Order order) {}
    }

    public static class InventoryService {
        public void reserveItems(Order order) {}
        public void updateShippedItems(Order order) {}
        public void releaseReservedItems(Order order) {}
    }

    public static class AnalyticsService {
        public void trackOrderConfirmed(Order order) {}
        public void trackOrderShipped(Order order) {}
        public void trackOrderDelivered(Order order) {}
        public void trackOrderCancelled(Order order) {}
    }

    public static class OrderRepository {
        public void save(Order order) {}
        public Order findById(String id) { return null; }
    }

    public static class AuditLogger {
        public void log(String message) {}
    }

    public static class PaymentDetails {
        private String cardNumber;
        private int expiryMonth;
        private int expiryYear;
        private String cvv;
        private String email;

        public String getCardNumber() { return cardNumber; }
        public int getExpiryMonth() { return expiryMonth; }
        public int getExpiryYear() { return expiryYear; }
        public String getCvv() { return cvv; }
        public String getEmail() { return email; }
    }

    public static class PaymentResult {
        private boolean success;
        private String transactionId;
        private String error;

        public PaymentResult(boolean success, String transactionId, String error) {
            this.success = success;
            this.transactionId = transactionId;
            this.error = error;
        }
    }

    public static class RefundResult {
        private boolean success;
        private String message;

        public RefundResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static class Customer {
        private String id;
        private double totalSpent;
        public double getTotalSpent() { return totalSpent; }
    }

    public static class Notification {
        private String type;
        private String recipientId;
        private LocalDateTime timestamp;
        private List<NotificationSection> sections;

        public void setType(String type) { this.type = type; }
        public void setRecipientId(String id) { this.recipientId = id; }
        public void setTimestamp(LocalDateTime ts) { this.timestamp = ts; }
        public void setSections(List<NotificationSection> s) { this.sections = s; }
    }

    public static class NotificationSection {
        private String type;
        private String content;
        private List<NotificationSection> subsections;

        public void setType(String type) { this.type = type; }
        public void setContent(String content) { this.content = content; }
        public void setSubsections(List<NotificationSection> s) { this.subsections = s; }
    }

    // Payment API stubs
    public static class LegacyPaymentGateway {
        public void setMerchantId(String id) {}
        public void setApiKey(String key) {}
        public String submitTransaction(Map<String, String> params) { return "OK:txn123"; }
        public String refund(String txnId, int amount) { return "OK"; }
    }

    public static class ModernPaymentAPI {
        public void authenticate(String key, String secret) {}
        public PaymentResponse createCharge(PaymentRequest request) { return new PaymentResponse(); }
        public RefundResponse createRefund(String txnId, double amount) { return new RefundResponse(); }
    }

    public static class PaymentRequest {
        public void setAmount(double amount) {}
        public void setCurrency(String currency) {}
        public void setCardNumber(String num) {}
        public void setExpiryMonth(int month) {}
        public void setExpiryYear(int year) {}
        public void setCvv(String cvv) {}
        public void setOrderReference(String ref) {}
        public void setCustomerEmail(String email) {}
    }

    public static class PaymentResponse {
        public boolean isSuccessful() { return true; }
        public String getChargeId() { return "charge123"; }
        public String getErrorMessage() { return null; }
    }

    public static class RefundResponse {
        public boolean isSuccessful() { return true; }
        public String getMessage() { return "Refunded"; }
    }

    public static class LatestPaymentService {
        public void initialize(String credentials) {}
        public ChargeResult charge(ChargeRequest request) { return new ChargeResult(); }
        public RefundResult refund(String txnId, double amount) { return new RefundResult(true, "OK"); }
    }

    public static class ChargeRequest {
        public static ChargeRequestBuilder builder() { return new ChargeRequestBuilder(); }
    }

    public static class ChargeRequestBuilder {
        public ChargeRequestBuilder amount(double a) { return this; }
        public ChargeRequestBuilder currency(String c) { return this; }
        public ChargeRequestBuilder paymentMethod(PaymentMethod m) { return this; }
        public ChargeRequestBuilder metadata(Map<String, String> m) { return this; }
        public ChargeRequest build() { return new ChargeRequest(); }
    }

    public static class PaymentMethod {
        public static PaymentMethod card(String num, int expMonth, int expYear, String cvv) {
            return new PaymentMethod();
        }
    }

    public static class ChargeResult {
        public ChargeStatus getStatus() { return ChargeStatus.SUCCEEDED; }
        public String getId() { return "ch_123"; }
        public String getFailureReason() { return null; }
    }

    public enum ChargeStatus { SUCCEEDED, FAILED, PENDING }
}
