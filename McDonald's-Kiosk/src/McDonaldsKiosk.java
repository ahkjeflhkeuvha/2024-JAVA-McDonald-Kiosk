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
    public List<Customer> allOrders = new ArrayList<>();
 
    private double calculateTotalRevenue() {
        double totalRevenue = 0;
        for(Customer order : allOrders) {
        	Set<Menu> keys = order.getOrderList().keySet();
        	Iterator<Menu> it = keys.iterator();
        	
        	while(it.hasNext()) {
        		Menu m = it.next();
        		double price = m.getPrice();
        		int num = order.getOrderList().get(m);
        		totalRevenue += num * price;
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

        // 로고 이미지 추가
        BufferedImage img = null;
    	Image resizedImage = null;
    	ImageIcon imageIcon = null;
    	try {
    		img = ImageIO.read(new File(".//imgs//logo.png"));
            resizedImage = img.getScaledInstance(100, 100, SwingConstants.CENTER);
            imageIcon = new ImageIcon(resizedImage);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(imageIcon);

        // 타이틀 추가
        JLabel titleLabel = new JLabel("Where will you eat today?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

        // 로고와 타이틀 레이아웃 설정
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.CENTER);
        topPanel.add(titleLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 버튼 설정
        JButton toGoButton = new JButton("Take Out");
        JButton eatInButton = new JButton("Eat In");

        ImageIcon toGoImage = new ImageIcon(".//imgs//togo.png");
        toGoButton.setFont(new Font("Arial", Font.BOLD, 20));
        toGoButton.setIcon(toGoImage);
        eatInButton.setFont(new Font("Arial", Font.BOLD, 20));
        eatInButton.setIcon(toGoImage);
        
        // 버튼을 가로로 배치
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 2, 0, 0)); // 가로 배치 및 간격 조절
        buttonPanel.add(toGoButton);
        buttonPanel.add(eatInButton);
        add(buttonPanel, BorderLayout.CENTER);

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
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬 설정
            itemPanel.add(nameLabel);

            // 메뉴 이미지
            JLabel imageLabel = new JLabel(menu.getImage());
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 이미지 중앙 정렬
            itemPanel.add(imageLabel); // 이미지 추가

            // 메뉴 수량
            int quantity = customer.getOrderList().get(menu);
            JLabel quantityLabel = new JLabel("Quantity: " + quantity);
            quantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
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
        receiptLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        add(receiptLabel, BorderLayout.CENTER);

        allOrders.add(customer);

        orderNumber++;  // 파일 저장 이후에 orderNumber 증가

        

        JButton returnButton = new JButton("Return to Main");
        returnButton.setFont(new Font("Arial", Font.BOLD, 20));
        returnButton.addActionListener(e -> {
        	orderEnd();
        });
        add(returnButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
        
    
    private void orderEnd() {
        getContentPane().removeAll();
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        JButton closeButton = new JButton("Exit");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.addActionListener(e -> System.exit(0)); // Close the application
        
        JButton mainButton = new JButton("Return to Main");
        mainButton.setFont(new Font("Arial", Font.BOLD, 20));
        mainButton.addActionListener(e -> howToEatPage()); // Return to the main page
        
        JButton loginButton = new JButton("Login");
        mainButton.setFont(new Font("Arial", Font.BOLD, 20));
        mainButton.addActionListener(e -> showLoginPage()); // Return to the main page
        
        buttonPanel.add(closeButton);
        buttonPanel.add(mainButton);
        add(buttonPanel, BorderLayout.CENTER);
        
        allOrders.add(customer);
        customer.removeAllMenu();
        
        revalidate();
        repaint();
    }

    
    public static void showLoginPage() {
        JFrame loginFrame = new JFrame("비밀번호 입력");
        loginFrame.setSize(400, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel passwordLabel = new JLabel("비밀번호를 입력하세요:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("로그인");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String enteredPassword = new String(passwordField.getPassword());
                String correctPassword = "1234";  // 사장님 비밀번호

                if (enteredPassword.equals(correctPassword)) {
                    loginFrame.dispose();  // 로그인 창 닫기
                } else {
                    JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.");
                }
            }
        });

        loginFrame.add(loginPanel, BorderLayout.CENTER);
        loginFrame.setVisible(true);
       
    }
    
    private void showManagerPage() {
       
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
