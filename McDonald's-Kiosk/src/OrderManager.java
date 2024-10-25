import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private List<OrderMenu> orders;

    public OrderManager() {
        this.orders = new ArrayList<>();
    }

    public void addOrder(String menuName, int quantity, double price) {
        OrderMenu newOrder = new OrderMenu(menuName, quantity, price);
        orders.add(newOrder);
    }

    public int getTotalQuantity() {
        return orders.stream().mapToInt(OrderMenu::getQuantity).sum();
    }

    public double getTotalRevenue() {
        return orders.stream().mapToDouble(OrderMenu::getTotalPrice).sum();
    }
}