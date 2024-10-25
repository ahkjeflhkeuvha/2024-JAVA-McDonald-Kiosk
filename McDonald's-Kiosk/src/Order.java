import java.util.HashMap;

public class Order {
    private HashMap<Menu, Integer> items;
    private double totalAmount; // 주문 금액 저장
    private int orderNumber;

    Order(HashMap<Menu, Integer> items, double totalAmount, int orderNumber) {
        this.items = new HashMap<>(items); // 주문 내역 복사
        this.totalAmount = totalAmount; // 총 금액 저장
        this.orderNumber = orderNumber;
    }

    public HashMap<Menu, Integer> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getOrderNumber() {
        return orderNumber;
    }
}