import javax.swing.*;
import org.json.JSONArray;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class McDonaldsKiosk extends JFrame {
    private ArrayList<Menu> menuList = new ArrayList<>();
    private Customer customer = new Customer(); // 고객 객체 추가
    private static int orderNumber = 1; // 대기 번호

    // 메뉴 클래스 정의
    class Menu {
        private String name;
        private double price;
        private String description;
        private ImageIcon image;

        Menu(String name, double price, String description, ImageIcon image) {
            this.name = name;
            this.price = price;
            this.description = description;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getDescription() {
            return description;
        }

        public ImageIcon getImage() {
            return image;
        }
    }

    // 고객 클래스 정의
    class Customer {
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
    }

    // 생성자
    public McDonaldsKiosk() {
        setTitle("McDonald's Kiosk");
        setSize(462, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 처음에 <먹고가기>, <포장하기> 옵션 선택 페이지 표시
        howToEatPage();
    }

    // <먹고가기>, <포장하기> 옵션 선택 페이지
    private void howToEatPage() {
        getContentPane().removeAll();
        repaint();

        JLabel titleLabel = new JLabel("Where will you eat today?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        add(titleLabel, BorderLayout.NORTH);

        JButton toGoButton = new JButton("Take Out");
        JButton eatInButton = new JButton("Eat In");
        
        ImageIcon toGoImage = new ImageIcon(".//imgs//togo.png");

        toGoButton.setFont(new Font("Arial", Font.BOLD, 20));
        toGoButton.setIcon(toGoImage);
        eatInButton.setFont(new Font("Arial", Font.BOLD, 20));
        eatInButton.setIcon(toGoImage);

        JPanel buttonPanel = new JPanel();
        
        buttonPanel.add(toGoButton);
        buttonPanel.add(eatInButton);
        add(buttonPanel);

        // 버튼 클릭 시 메뉴 페이지로 이동
        toGoButton.addActionListener(e -> displayMenuPage());
        eatInButton.addActionListener(e -> displayMenuPage());

        revalidate();
        repaint();
    }

    // 메뉴 페이지
    private void displayMenuPage() {
        getContentPane().removeAll();
        repaint();

        loadMenu();

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1, 10, 10));

        for (Menu menu : menuList) {
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BorderLayout());
            itemPanel.setBackground(Color.white);

            
            JLabel nameLabel = new JLabel(menu.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            itemPanel.add(nameLabel, BorderLayout.NORTH);

            JLabel priceLabel = new JLabel("$" + menu.getPrice(), SwingConstants.CENTER);
            priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
            itemPanel.add(priceLabel, BorderLayout.SOUTH);
            
            JLabel imageLabel = new JLabel(menu.getImage());
            itemPanel.add(imageLabel, BorderLayout.CENTER);

            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showMenuPopup(menu);
                }
            });

            menuPanel.add(itemPanel);
        }

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        add(scrollPane, BorderLayout.CENTER);

        JButton cartButton = new JButton("Go to Cart");
        cartButton.setFont(new Font("Arial", Font.BOLD, 20));
        cartButton.addActionListener(e -> showCartPage());
        add(cartButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // 메뉴 팝업 창
    private void showMenuPopup(Menu menu) {
        JDialog dialog = new JDialog(this, menu.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);

        JLabel imageLabel = new JLabel(menu.getImage());
        dialog.add(imageLabel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea(menu.getName() + "\n\n"
                + "Price: $" + menu.getPrice() + "\n\n"
                + "Description: " + menu.getDescription());
        infoArea.setFont(new Font("Arial", Font.PLAIN, 18));
        infoArea.setEditable(false);
        dialog.add(infoArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(e -> {
            customer.addMenu(menu);
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // 장바구니 페이지
    private void showCartPage() {
        getContentPane().removeAll();
        repaint();

        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new GridLayout(0, 1));

        for (Menu menu : customer.getOrderList().keySet()) {
        	JLabel image = new JLabel(menu.getImage());
            int quantity = customer.getOrderList().get(menu);
            JLabel itemLabel = new JLabel(menu.getName() + " x " + quantity);
            cartPanel.add(image);
            cartPanel.add(itemLabel);
        }

        add(cartPanel, BorderLayout.CENTER);

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 20));
        checkoutButton.addActionListener(e -> paymentPage());
        add(checkoutButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // 결제 페이지
    private void paymentPage() {
        getContentPane().removeAll();
        repaint();

        JLabel titleLabel = new JLabel("Select Payment Method", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        add(titleLabel, BorderLayout.NORTH);

        JButton cashButton = new JButton("Cash");
        JButton cardButton = new JButton("Card");
        JButton payButton = new JButton("Pay");

        cashButton.setFont(new Font("Arial", Font.BOLD, 20));
        cardButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(cashButton);
        buttonPanel.add(cardButton);
        buttonPanel.add(payButton);
        add(buttonPanel, BorderLayout.CENTER);

        cashButton.addActionListener(e -> showPaymentPopup("Cash", "영수증을 가지고 카운터로 이동하세요."));
        cardButton.addActionListener(e -> showPaymentPopup("Card", "카드를 꽂아주세요."));
        payButton.addActionListener(e -> showPaymentPopup("Pay", "바코드를 찍어주세요."));

        revalidate();
        repaint();
    }

    // 결제 안내 팝업
    private void showPaymentPopup(String method, String message) {
    	getContentPane().removeAll();
        repaint();
        
        
        String receipt = "";
        
        // 영수증 출력
        System.out.println("Order Number: " + orderNumber);
        System.out.println("Payment Method: " + method);
        System.out.println("Items Ordered:");
        
        receipt += "<html>Order Number: " + orderNumber + "<br>Payment Method: " + method + "<br>Items Ordered:\n";
        
        for (Menu menu : customer.getOrderList().keySet()) {
            receipt += menu.getName() + " x " + customer.getOrderList().get(menu) + "<br>";
            System.out.println(menu.getName() + " x " + customer.getOrderList().get(menu));
        }
        
        System.out.println("Total: $" + calculateTotal());
        System.out.println("-------------------------");
        receipt += "<br>Total: $" + calculateTotal();
        receipt += "<br>-------------------------</html>";
        
        orderNumber++; // 결제 완료 후 대기 번호 증가
        
        try {
        	Thread.sleep(2000);
        	totalPage(receipt);
        } catch(InterruptedException e) {
        	e.printStackTrace();
        }
    }

    private void totalPage(String receipt) {
    	getContentPane().removeAll();
        repaint();
        
        JLabel titleLabel = new JLabel(receipt, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBounds(100, 100, 400, 500);
        add(titleLabel);
        
        revalidate();
        repaint();
    }
    
    // 총 금액 계산
    private double calculateTotal() {
        double total = 0;
        for (Menu menu : customer.getOrderList().keySet()) {
            total += menu.getPrice() * customer.getOrderList().get(menu);
        }
        return total;
    }

    // 메뉴 데이터 불러오기
    private void loadMenu() {
        try (BufferedReader reader = new BufferedReader(new FileReader("menu.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONArray menuArray = new JSONArray(jsonContent.toString());

            for (int i = 0; i < menuArray.length(); i++) {
                String name = menuArray.getJSONObject(i).getString("name");
                double price = menuArray.getJSONObject(i).getDouble("price");
                String description = menuArray.getJSONObject(i).getString("description");
                ImageIcon image = new ImageIcon(menuArray.getJSONObject(i).getString("image"));

                menuList.add(new Menu(name, price, description, image));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new McDonaldsKiosk().setVisible(true);
    }
}
