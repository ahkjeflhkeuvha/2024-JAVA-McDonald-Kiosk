import javax.imageio.ImageIO;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
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
    public HashMap<String, Integer> totOrder = new HashMap<>();
    public HashMap<String, Integer> menu = new HashMap<>();
    public static int totalRevenue = 0;
    public static int totalPrice = 0;
    Font boldfont = new Font("Pretendard", Font.BOLD, 25);
    Font regularfont = new Font("Pretendard", Font.PLAIN, 20);
    public static int menuid = 30;
    private static final String SERVER_IP = "127.0.0.1"; // 서버 IP 주소
    private static final int PORT = 12345; // 서버 포트 번호
    public static String receiptEmail = "";
    
    private double calculateTotalRevenue() {
        for (Menu menu : customer.getOrderList().keySet()) {
            int quantity = customer.getOrderList().get(menu);
            String menuName = menu.getName();
            int price = menu.getPrice();
            int num = customer.getOrderList().get(menu);
            
            totOrder.put(menuName, totOrder.getOrDefault(menuName, 0) + num);
            
            totalRevenue += price * num;
           
        }
        customer.removeAllMenu();
        return totalRevenue;
    }

    // 생성자
    public McDonaldsKiosk() {
        setTitle("McDonald's Kiosk");
        setSize(462, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadMenu();
        howToEatPage();
    }
    // <먹고가기>, <포장하기> 옵션 선택 페이지
    private void howToEatPage() {
        getContentPane().removeAll();
        repaint();

        customer.removeAllMenu();

        JButton loginButton = new JButton();
        // 로고 이미지 추가
        ImageIcon loginImage = resizeIcon(".//imgs//logo.png", 100, 100);
        customizeButton(loginButton, loginImage);
        
        loginButton.addActionListener(e -> showLoginPage());

        // 타이틀 추가
        JLabel titleLabel = new JLabel("Where will you eat today?", SwingConstants.CENTER);
        titleLabel.setFont(boldfont);

        // 로고와 타이틀 레이아웃 설정
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(loginButton, BorderLayout.CENTER);
        topPanel.add(titleLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 버튼 설정
        JButton toGoButton = new JButton();
        JButton eatInButton = new JButton();

        ImageIcon toGoImage = resizeIcon(".//imgs//togo.png", 150, 200);
        ImageIcon eatInImage = resizeIcon(".//imgs//eatin.png", 150, 200);

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
        	ImageIcon imageIcon = resizeIcon(".//imgs//Rectangle.png", 100, 100);
            JLabel backgroundJ = new JLabel();
            backgroundJ.setIcon(imageIcon);
            JPanel itemPanel = new JPanel(); 
            
            itemPanel.setLayout(new BorderLayout());
            itemPanel.setBackground(Color.white);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(20, 20 , 20 , 20));

            
            JLabel nameLabel = new JLabel(menu.getName(), SwingConstants.CENTER);
            nameLabel.setFont(boldfont);
            itemPanel.add(nameLabel, BorderLayout.NORTH);

            JLabel priceLabel = new JLabel(menu.getPrice() + "원", SwingConstants.CENTER);
            priceLabel.setFont(regularfont);
            itemPanel.add(priceLabel, BorderLayout.SOUTH);
            
            JLabel imageLabel = new JLabel(new ImageIcon(menu.getImage()));
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

        ImageIcon resizedImageIcon = resizeIcon(menu.getImage(), 200, 200); // resizeIcon 메소드 사용
        JLabel imageLabel = new JLabel(resizedImageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 이미지 중앙 정렬
        dialog.add(imageLabel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea(menu.getName() + "\n\n"
                + "가격 : " + menu.getPrice() + "\n\n"
                + menu.getDescription());
        infoArea.setFont(regularfont);
        infoArea.setEditable(false);
        dialog.add(infoArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton("장바구니에 추가");
        addButton.setBorderPainted(false);
        addButton.addActionListener(e -> {
            customer.addMenu(menu);
            dialog.dispose();
        });

        JButton cancelButton = new JButton("취소");
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
            ImageIcon resizedImageIcon = resizeIcon(menu.getImage(), 200, 200); // resizeIcon 메소드 사용
            JLabel imageLabel = new JLabel(resizedImageIcon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 이미지 중앙 정렬
            itemPanel.add(imageLabel); // 이미지 추가

            // 메뉴 수량
            int quantity = customer.getOrderList().get(menu);
            JLabel quantityLabel = new JLabel("개수 : " + quantity);
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
        JButton checkoutButton = new JButton("결제하기");
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

        JLabel titleLabel = new JLabel("주문 방법을 선택해주세요", SwingConstants.CENTER);
        titleLabel.setFont(boldfont);
        add(titleLabel, BorderLayout.NORTH);

        JButton cashButton = new JButton("현금");
        cashButton.setBorderPainted(false);
        JButton cardButton = new JButton("카드");
        cardButton.setBorderPainted(false);
        JButton payButton = new JButton("모바일 페이");
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
        
        int price = 0;
        int num = 0;
        int tot = 0;

        String receipt = "";
        receiptEmail = "";
        // 영수증 출력
        System.out.println("주문 번호 : " + orderNumber);
        System.out.println("결제 방법 : " + method);
        System.out.println("---------- 주문 내역 ----------");

        receipt += "<html>주문 번호 : " + orderNumber + "<br>결제 방법 : " + method + "<br><br>---------- 주문 내역 ----------<br>";
        receiptEmail += "주문 번호 : " + orderNumber + "\n결제 방법 : " + method + "\n\n---------- 주문 내역 ----------\n\n";
        for (Menu menu : customer.getOrderList().keySet()) {
            int quantity = customer.getOrderList().get(menu);
            receipt += menu.getName() + " x " + quantity + "<br>";
            receiptEmail += menu.getName() + " x " + quantity + "\n";
            price = menu.getPrice();
            num = customer.getOrderList().get(menu);
            tot += price * num;
            System.out.println(menu.getName() + " x " + quantity);
        }
      
        calculateTotalRevenue();

        receipt += "최종 금액 : " + tot + "원<br>";
        receiptEmail += "최종 금액 : " + tot + "원\n";
        System.out.println("최종 금액 : " + tot + "원\n");
        receipt += message + "<br></html>";

        JLabel receiptLabel = new JLabel(receipt);
        receiptLabel.setFont(regularfont);
        add(receiptLabel, BorderLayout.CENTER);


        orderNumber++;  // 파일 저장 이후에 orderNumber 증가

        

        JButton returnButton = new JButton("다음");
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
        JButton closeButton = new JButton("종료하기");
        closeButton.setBorderPainted(false);
        closeButton.setFont(regularfont);
        closeButton.addActionListener(e -> System.exit(0)); // Close the application
        
        JButton mainButton = new JButton("처음 화면으로 돌아가기");
        mainButton.setBorderPainted(false);
        mainButton.setFont(regularfont);
        mainButton.addActionListener(e -> howToEatPage()); // Return to the main page
        
        JButton emailButton = new JButton("이메일로 주문내역 전송하기");
        emailButton.setBorderPainted(false);
        emailButton.setFont(regularfont);
        emailButton.addActionListener(e -> OrderInputPanel());
        
        buttonPanel.add(closeButton);
        buttonPanel.add(mainButton);
        buttonPanel.add(emailButton);
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
    	
    	
        double totPrice = 0;

        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new BoxLayout(managerPanel, BoxLayout.Y_AXIS));
        managerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("오늘 팔린 메뉴");
        headerLabel.setFont(boldfont);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        managerPanel.add(headerLabel);

        // 판매된 메뉴와 개수를 표시하는 레이블 추가
        Set<String> keys = totOrder.keySet();
        for (String menuName : keys) {
        	double menuPrice = menu.get(menuName);
        	int menuNumber = totOrder.get(menuName); 
        	System.out.println(menuName + " : " + menuNumber + " = " + menuPrice * menuNumber);
            JLabel orderLabel = new JLabel(menuName + " : " + menuNumber + " = " + menuPrice * menuNumber);
            totPrice += menuPrice * menuNumber;
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
        

        JButton addMenu = new JButton("메뉴 추가");
        addMenu.setBorderPainted(false);
        addMenu.setFont(regularfont);
        addMenu.addActionListener(e -> createMenuInputPanel());
        JButton closeButton = new JButton("닫기");
        closeButton.setBorderPainted(false);
        closeButton.setFont(regularfont);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> {
        	System.exit(0);
        });
        managerPanel.add(closeButton);
        managerPanel.add(addMenu);

        
        add(headerLabel);
        add(managerPanel);
        
        
        revalidate();
        repaint();
    }

    private HashMap<String, Integer> newMenu = new HashMap<>();
    private ArrayList<Menu> newMenuList = new ArrayList<>();

    private JTextField nameField;
    private JTextField priceField;
    private JTextArea descriptionArea;
    private JTextField imageField;
    private JPanel mainPanel = new JPanel();
    
    private void createMenuInputPanel() {
        getContentPane().removeAll();
        mainPanel.removeAll();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));

        inputPanel.add(new JLabel("메뉴 이름:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("가격:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("설명:"));
        descriptionArea = new JTextArea(3, 20);
        inputPanel.add(new JScrollPane(descriptionArea));

        inputPanel.add(new JLabel("이미지 경로:"));
        imageField = new JTextField();
        imageField.setEditable(false);
        inputPanel.add(imageField);

        JButton selectImageButton = new JButton("이미지 선택");
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imageField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        inputPanel.add(selectImageButton);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("추가");
        addButton.addActionListener(new AddMenuAction());
        buttonPanel.add(addButton);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        revalidate();
        repaint();
    }

    // 메뉴 항목 추가 액션
    private class AddMenuAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            String imagePath = imageField.getText().trim();
            int price;

            try {
                price = Integer.parseInt(priceField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "올바른 가격을 입력하십시오.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (name.isEmpty() || description.isEmpty() || imagePath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "모든 필드를 입력하십시오.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Menu newMenuItem = new Menu(menuid++, name, price, description, imagePath);
            newMenu.put(name, price);
            newMenuList.add(newMenuItem);

            saveMenu();
            clearFields();
            JOptionPane.showMessageDialog(null, "메뉴가 추가되었습니다.");
        }
    }

    // JSON 파일에 메뉴 저장
    private void saveMenu() {
        JSONArray menuArray = new JSONArray();

        try {
        	for (Menu item : newMenuList) {
                JSONObject menuItem = new JSONObject();
                menuItem.put("id", item.getId());
                menuItem.put("name", item.getName());
                menuItem.put("price", item.getPrice());
                menuItem.put("description", item.getDescription());
                menuItem.put("image", item.getImage());
                menuArray.put(menuItem);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter("menu.json")) {
            fileWriter.write(menuArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // JSON 파일에서 메뉴 로드
    private void loadMenu() {
        File file = new File("menu.json");

        // JSON 파일이 없으면 빈 메뉴로 시작
        if (!file.exists()) {
            System.out.println("menu.json 파일이 존재하지 않아 빈 메뉴로 시작합니다.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONArray menuArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < menuArray.length(); i++) {
                JSONObject menuItem = menuArray.getJSONObject(i);
                int id = menuItem.getInt("id");
                String name = menuItem.getString("name");
                int price = menuItem.getInt("price");
                String description = menuItem.getString("description");
                String imagePath = menuItem.getString("image");

                // JSON에서 이미지 경로를 받아와 ImageIcon으로 생성 
                
                Menu item = new Menu(id, name, price, description, imagePath);
                newMenu.put(name, price);
                newMenuList.add(item);
                
                menu.put(name, price);
                menuList.add(new Menu(id, name, price, description, imagePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 필드 초기화
    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        descriptionArea.setText("");
        imageField.setText("");
        
        howToEatPage();
    }
    
    private JTextField emailField;
    private JTextArea orderDetailsArea;
    private JButton submitButton;
    
    public void OrderInputPanel() {
    	getContentPane().removeAll();
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));

        // 이메일 주소 입력 필드
        inputPanel.add(new JLabel("이메일 주소:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        // 주문 내역 입력 필드
        inputPanel.add(new JLabel("주문 내역:"));
        JLabel receiptLabel = new JLabel(receiptEmail);
        add(receiptLabel, BorderLayout.CENTER);

        // 제출 버튼
        submitButton = new JButton("주문 제출");
        submitButton.addActionListener(new SubmitOrderAction());

        // 패널에 컴포넌트 추가
        add(inputPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }

    // 주문 제출 버튼의 액션 리스너
    private class SubmitOrderAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String orderDetails = receiptEmail;

            // 입력 확인
            if (email.isEmpty() || orderDetails.isEmpty()) {
                JOptionPane.showMessageDialog(null, "이메일과 주문 내역을 입력해 주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 이메일 전송 메소드 호출
            sendOrderEmail(email, orderDetails);
        }
    }

    // 이메일 전송 메소드 (실제 구현 필요)
    private void sendOrderEmail(String email, String orderDetails) {
        // 예시로 이메일 전송 관련 메소드 구현
        String subject = "주문 내역 확인";
        String content = "고객님께서 요청하신 주문 내역입니다.\n\n" + orderDetails;
        
        // 이메일 전송 로직은 외부의 이메일 전송 메소드에서 구현
        NaverEmailSender.sendEmail(email, subject, content); // 전송 성공 여부에 따라 추가 처리 가능
    }

    public static void main(String[] args) {
        new McDonaldsKiosk().setVisible(true);
        
    }
}
