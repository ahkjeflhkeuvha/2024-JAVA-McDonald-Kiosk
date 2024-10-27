import javax.imageio.ImageIO;
import javax.swing.*;
import org.json.JSONArray;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class McDonaldsKiosk extends JFrame {
    public ArrayList<Menu> menuList = new ArrayList<>();
    public Customer customer = new Customer();
    public static int orderNumber = 1;
    public List<HashMap<Menu, Integer>> allOrders = new ArrayList<>();
    public HashMap<Menu, Integer> totOrder = new HashMap<>();
    public HashMap<String, Double> menu = new HashMap<>();
    Font boldfont = new Font("Pretendard", Font.BOLD, 25);
    Font regularfont = new Font("Pretendard", Font.PLAIN, 20);
    
    private double calculateTotalRevenue() {
        double totalRevenue = 0;
        for(HashMap<Menu, Integer> order : allOrders) {
        	Set<Menu> menuClass = order.keySet();
        	Iterator<Menu> it = menuClass.iterator();
        	
        	while(it.hasNext()) {
        		Menu m = it.next();
                int num = order.get(m);
                
                System.out.println(m.getName() + " : " + num);
                // Update totOrder
                
                totOrder.put(m, totOrder.getOrDefault(m, 0) + num);
                
                totalRevenue += m.getPrice() + num;
        	}
        }

        return totalRevenue;
    }

    // 생성자
    public McDonaldsKiosk() {
        setTitle("McDonald's Kiosk");
        setSize(462, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        howToEatPage();
    }
    // <먹고가기>, <포장하기> 옵션 선택 페이지
    private void howToEatPage() {
        getContentPane().removeAll();
        repaint();

        customer.removeAllMenu();

        // 로고 이미지 추가
        BufferedImage img = null;
        Image resizedImage = null;
        ImageIcon imageIcon = null;
        try {
            img = ImageIO.read(new File(".//imgs//logo.png"));
            resizedImage = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(resizedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(imageIcon);

        // 타이틀 추가
        JLabel titleLabel = new JLabel("Where will you eat today?", SwingConstants.CENTER);
        titleLabel.setFont(boldfont);

        // 로고와 타이틀 레이아웃 설정
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(imageLabel, BorderLayout.CENTER);
        topPanel.add(titleLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 버튼 설정
        JButton toGoButton = new JButton("Take Out");
        JButton eatInButton = new JButton("Eat In");

        // 각 버튼에 사용할 이미지 아이콘 (리사이징)
        ImageIcon toGoImage = resizeIcon(".//imgs//Automobile.png", 100, 100);
        ImageIcon eatInImage = resizeIcon(".//imgs//Convenience Store.png", 100, 100);

        // 버튼 디자인 설정
        customizeButton(toGoButton, toGoImage);
        customizeButton(eatInButton, eatInImage);

        // 버튼을 가로로 배치하고 여백 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0)); // 가로 배치 및 버튼 간격 설정
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // 패널 여백 설정
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(toGoButton);
        buttonPanel.add(eatInButton);
        add(buttonPanel, BorderLayout.CENTER);

        // 버튼 클릭 시 메뉴 페이지로 이동
        toGoButton.addActionListener(e -> displayMenuPage());
        eatInButton.addActionListener(e -> displayMenuPage());

        revalidate();
        repaint();
    }

    private void customizeButton(JButton button, ImageIcon icon) {
        button.setFont(boldfont);
        button.setIcon(icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 위치 중앙
        button.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트 위치 하단
        button.setContentAreaFilled(false); // 배경 비우기
        button.setBorderPainted(false); // 테두리 비활성화
        button.setFocusPainted(false); // 포커스 테두리 비활성화
    }

    // 이미지 리사이징 메서드
    private ImageIcon resizeIcon(String filePath, int width, int height) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filePath));
            Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    
    // 메뉴 페이지
    private void displayMenuPage() {
        getContentPane().removeAll();
        repaint();

        loadMenu();

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1, 100, 30));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20 , 20 , 20));

        for (Menu menu : menuList) {
        	BufferedImage img = null;
        	Image resizedImage = null;
        	ImageIcon imageIcon = null;
        	try {
        		img = ImageIO.read(new File(".//imgs//Rectangle.png"));
                resizedImage = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(resizedImage);
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
            JLabel backgroundJ = new JLabel();
            backgroundJ.setIcon(imageIcon);
            JPanel itemPanel = new JPanel(); 
            
            itemPanel.setLayout(new BorderLayout());
            itemPanel.setBackground(Color.white);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(20, 20 , 20 , 20));

            
            JLabel nameLabel = new JLabel(menu.getName(), SwingConstants.CENTER);
            nameLabel.setFont(boldfont);
            itemPanel.add(nameLabel, BorderLayout.NORTH);

            JLabel priceLabel = new JLabel("$" + menu.getPrice(), SwingConstants.CENTER);
            priceLabel.setFont(regularfont);
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
        cartButton.setFont(regularfont);
        cartButton.setBorderPainted(false);
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
        infoArea.setFont(regularfont);
        infoArea.setEditable(false);
        dialog.add(infoArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton("Add to Cart");
        addButton.setBorderPainted(false);
        addButton.addActionListener(e -> {
            customer.addMenu(menu);
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBorderPainted(false);
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

        // 스크롤 가능하도록 JPanel과 JScrollPane 사용
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS)); // 아이템을 세로로 나열
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20 , 20 , 20));

        for (Menu menu : customer.getOrderList().keySet()) {
            
            JPanel itemPanel = new JPanel(); // 각 아이템을 위한 패널 생성
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS)); // 세로로 정렬
            itemPanel.setBackground(Color.white); // 개별 아이템 배경 설정
            itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10 , 10 , 10)); // 내부 여백 추가
            itemPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬 설정

            // 메뉴 이름
            JLabel nameLabel = new JLabel(menu.getName());
            nameLabel.setFont(boldfont);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬 설정
            itemPanel.add(nameLabel);

            // 메뉴 이미지
            JLabel imageLabel = new JLabel(menu.getImage());
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 이미지 중앙 정렬
            itemPanel.add(imageLabel); // 이미지 추가

            // 메뉴 수량
            int quantity = customer.getOrderList().get(menu);
            JLabel quantityLabel = new JLabel("Quantity: " + quantity);
            quantityLabel.setFont(regularfont);
            quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬 설정
            itemPanel.add(quantityLabel);

            // 개별 아이템 패널을 cartPanel에 추가
            cartPanel.add(itemPanel);
        }

        // 스크롤 가능한 패널 추가
        JScrollPane scrollPane = new JScrollPane(cartPanel); 
        add(scrollPane, BorderLayout.CENTER); 

        // 결제 버튼 추가
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setBorderPainted(false);
        checkoutButton.setFont(regularfont);
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
        titleLabel.setFont(boldfont);
        add(titleLabel, BorderLayout.NORTH);

        JButton cashButton = new JButton("Cash");
        cashButton.setBorderPainted(false);
        JButton cardButton = new JButton("Card");
        cardButton.setBorderPainted(false);
        JButton payButton = new JButton("Pay");
        payButton.setBorderPainted(false);
        
        
        ImageIcon cashImage = new ImageIcon(".//imgs//Coin.png");
        cashButton.setIcon(cashImage);
        
        ImageIcon cardImage = new ImageIcon(".//imgs//Credit Card.png");
        cardButton.setIcon(cardImage);
        
        ImageIcon payImage = new ImageIcon(".//imgs//Mobile Phone.png");
        payButton.setIcon(payImage);

        cashButton.setFont(regularfont);
        cardButton.setFont(regularfont);
        payButton.setFont(regularfont);

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

    private void showPaymentPopup(String method, String message) {
        getContentPane().removeAll();
        
        double price = 0.0;
        int num = 0;
        double tot = 0.0;

        String receipt = "";

        // 영수증 출력
        System.out.println("Order Number: " + orderNumber);
        System.out.println("Payment Method: " + method);
        System.out.println("Items Ordered:");

        receipt += "<html>Order Number: " + orderNumber + "<br>Payment Method: " + method + "<br>Items Ordered:<br>";
        for (Menu menu : customer.getOrderList().keySet()) {
            int quantity = customer.getOrderList().get(menu);
            receipt += menu.getName() + " x " + quantity + "<br>";
            price = menu.getPrice();
            num = customer.getOrderList().get(menu);
            tot += price * num;
            System.out.println(menu.getName() + " x " + quantity);
        }

        receipt += "total price : " + tot + "$\n";
        System.out.println("total price : " + tot + "$\n");
        receipt += "</html>";

        JLabel receiptLabel = new JLabel(receipt);
        receiptLabel.setFont(regularfont);
        add(receiptLabel, BorderLayout.CENTER);


        orderNumber++;  // 파일 저장 이후에 orderNumber 증가

        

        JButton returnButton = new JButton("Return to Main");
        returnButton.setBorderPainted(false);
        returnButton.setFont(regularfont);
        returnButton.addActionListener(e -> {
        	orderEnd();
        });
        add(returnButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
        
    
    public void orderEnd() {
        getContentPane().removeAll();
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3));
        JButton closeButton = new JButton("Exit");
        closeButton.setBorderPainted(false);
        closeButton.setFont(regularfont);
        closeButton.addActionListener(e -> System.exit(0)); // Close the application
        
        JButton mainButton = new JButton("Return to Main");
        mainButton.setBorderPainted(false);
        mainButton.setFont(regularfont);
        mainButton.addActionListener(e -> howToEatPage()); // Return to the main page
        
        JButton loginButton = new JButton("Login");
        loginButton.setBorderPainted(false);
        loginButton.setFont(regularfont);
        loginButton.addActionListener(e -> showLoginPage()); // 로그인 페이지로 이동
        
        buttonPanel.add(closeButton);
        buttonPanel.add(mainButton);
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.CENTER);
        
        allOrders.add(customer.getOrderList());
        
        revalidate();
        repaint();
    }

    public void showLoginPage() {
        JDialog loginDialog = new JDialog((JFrame) null, "비밀번호 입력", true);
        loginDialog.setSize(400, 200);
        loginDialog.setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel passwordLabel = new JLabel("비밀번호를 입력하세요:");
        passwordLabel.setFont(boldfont);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("로그인");
        loginButton.setBorderPainted(false);
        loginButton.setFont(regularfont);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String enteredPassword = new String(passwordField.getPassword());
                String correctPassword = "1234";  // 사장님 비밀번호

                if (enteredPassword.equals(correctPassword)) {
                    showManagerPage();  // 점주 페이지 표시
                    loginDialog.dispose();  // 로그인 창 닫기
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "비밀번호가 틀렸습니다.");
                }
            }
        });

        loginDialog.add(loginPanel, BorderLayout.CENTER);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);
    }

    public void showManagerPage() {
        // 점주 페이지 GUI
    	getContentPane().removeAll();
    	System.out.println("show ManagerPage ");
    	
    	
        double totPrice = calculateTotalRevenue();

        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new BoxLayout(managerPanel, BoxLayout.Y_AXIS));
        managerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("오늘 팔린 메뉴");
        headerLabel.setFont(boldfont);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        managerPanel.add(headerLabel);

        // 판매된 메뉴와 개수를 표시하는 레이블 추가
        Set<Menu> keys = totOrder.keySet();
        for (Menu menuName : keys) {
        	String menuString = menuName.getName();
        	double menuPrice = menuName.getPrice();
        	int menuNumber = totOrder.get(menuName); 
        	System.out.println(menuString + " : " + menuPrice + "원 => " + menuPrice + " * " + menuNumber + " = " + menuPrice * menuNumber);
            JLabel orderLabel = new JLabel(menuString + " : " + menuPrice + "원 => " + menuPrice + " * " + menuNumber + " = " + menuPrice * menuNumber);
            orderLabel.setFont(regularfont);
            orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            managerPanel.add(orderLabel);
        }

        // 총 매출 금액을 표시하는 레이블 추가
        System.out.println("총 매출 금액: " + totPrice + "원");
        JLabel revenueLabel = new JLabel("총 매출 금액: " + totPrice + "원");
        revenueLabel.setFont(boldfont);
        revenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        managerPanel.add(revenueLabel);
        

        JButton closeButton = new JButton("닫기");
        closeButton.setBorderPainted(false);
        closeButton.setFont(regularfont);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> {
        	System.exit(0);
        });
        managerPanel.add(closeButton);

        
        add(headerLabel);
        add(managerPanel);
        
        
        revalidate();
        repaint();
    }



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

                
                menu.put(name, price);
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
