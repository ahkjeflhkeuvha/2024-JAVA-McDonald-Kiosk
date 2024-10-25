public class OrderMenu {
    private String menuName;
    private int quantity;
    private double price;

    public OrderMenu(String menuName, int quantity, double price) {
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice() {
        return quantity * price;
    }
}
