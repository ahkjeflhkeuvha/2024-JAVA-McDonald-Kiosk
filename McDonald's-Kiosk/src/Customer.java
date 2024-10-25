import java.util.HashMap;

public class Customer {
    private HashMap<Menu, Integer> orderList = new HashMap<>();

    void addMenu(Menu menu) {
        orderList.put(menu, orderList.getOrDefault(menu, 0) + 1);
    }

    void removeMenu(Menu menu) {
        if (orderList.containsKey(menu)) {
            orderList.put(menu, orderList.get(menu) - 1);
            if (orderList.get(menu) == 0) {
                orderList.remove(menu);
            }
        }
    }

    HashMap<Menu, Integer> getOrderList() {
        return orderList;
    }
    
    void removeAllMenu(HashMap orderList) {
    	orderList.clear();
    }
}