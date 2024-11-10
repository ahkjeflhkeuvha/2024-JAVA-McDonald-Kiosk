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
    public static String receipt = "";
    
    private int calculateTotalRevenue() {
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
        ImageIcon loginImage = resizeIcon(".//imgs//logo.png", 150, 150);
        customizeButton(loginButton, loginImage);
        
        loginButton.addActionListener(e -> showLoginPage());

        // 타이틀 추가
        JLabel titleLabel = new JLabel("Where will you eat today?", SwingConstants.CENTER);
        titleLabel.setFont(boldfont);

        // 로고와 타이틀 레이아웃 설정
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 20, 50));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(loginButton, BorderLayout.CENTER);
        topPanel.add(titleLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 버튼 설정
        JButton toGoButton = new JButton();
        JButton eatInButton = new JButton();

        ImageIcon toGoImage = resizeIcon(".//imgs//togo.png", 200, 250);
        ImageIcon eatInImage = resizeIcon(".//imgs//eatin.png", 200, 250);

        // 버튼 디자인 설정
        customizeButton(toGoButton, toGoImage);
        customizeButton(eatInButton, eatInImage);

        // 버튼을 가로로 배치하고 여백 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0)); // 가로 배치 및 버튼 간격 설정
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 패널 여백 설정
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
        System.out.println("눌렸음");
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
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        
        ImageIcon moveToCart = resizeIcon(".//imgs//moveToCart.png", 200, 40);
        JButton cartButton = new JButton(moveToCart);
        cartButton.setContentAreaFilled(false);
        cartButton.setBorderPainted(false);
        cartButton.setFocusPainted(false);
        cartButton.setMargin(new Insets(0, 0, 0, 0));
        cartButton.setIcon(moveToCart);
        cartButton.setPreferredSize(new Dimension(150, 40));
        cartButton.addActionListener(e -> showCartPage());
        buttonPanel.add(cartButton);

        ImageIcon Cancel = resizeIcon(".//imgs//Cancel.png", 200, 40);
        JButton cancelButton = new JButton(Cancel);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setMargin(new Insets(0, 0, 0, 0));
        cancelButton.setIcon(Cancel);
        cancelButton.setPreferredSize(new Dimension(200, 40));
        buttonPanel.add(cancelButton);
        cancelButton.addActionListener(e -> howToEatPage());
        
        

        add(buttonPanel, BorderLayout.SOUTH);
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

        // JTextArea 생성 및 스크롤 가능하도록 JScrollPane에 추가
        JTextArea infoArea = new JTextArea(menu.getName() + "\n\n"
                + "가격 : " + menu.getPrice() + "원\n\n"
                + menu.getDescription());
        infoArea.setFont(regularfont);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true); // 줄 바꿈 설정
        infoArea.setWrapStyleWord(true); // 단어 단위로 줄 바꿈

        // 세로 스크롤만 활성화, 가로 스크롤 비활성화
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton();
        ImageIcon addToCart = resizeIcon(".//imgs//addToCart.png", 150, 30);
        addButton.setContentAreaFilled(false);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.setIcon(addToCart);
        addButton.setPreferredSize(new Dimension(150, 30));
        addButton.addActionListener(e -> {
            customer.addMenu(menu);
            dialog.dispose();
        });

        JButton cancelButton = new JButton();
        ImageIcon Cancel = resizeIcon(".//imgs//Cancel.png", 150, 30);
        cancelButton.setIcon(Cancel);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setMargin(new Insets(0, 0, 0, 0));
        cancelButton.setIcon(Cancel);
        cancelButton.setPreferredSize(new Dimension(150, 30));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    // 장바구니 페이지
    private void showCartPage() {
        // 화면 초기화
        getContentPane().removeAll();
        revalidate(); // 레이아웃을 다시 계산하도록 함
        repaint();

        // 스크롤 가능하도록 JPanel과 JScrollPane 사용
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new GridLayout(0, 1, 100, 30)); // 메뉴 항목들을 세로로 나열
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 주문 목록에 있는 메뉴 항목들 처리
        for (Menu menu : customer.getOrderList().keySet()) {
            System.out.println(menu.getName() + " " + menu.getImage());
            
            // 개별 메뉴 항목을 위한 패널
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BorderLayout());
            itemPanel.setBackground(Color.white);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // 메뉴 이름
            JLabel nameLabel = new JLabel(menu.getName(), SwingConstants.CENTER);
            nameLabel.setFont(boldfont);
            itemPanel.add(nameLabel, BorderLayout.NORTH);

            // 메뉴 가격
            JLabel priceLabel = new JLabel(menu.getPrice() + "원", SwingConstants.CENTER);
            priceLabel.setFont(regularfont);
            itemPanel.add(priceLabel, BorderLayout.SOUTH);

            // 메뉴 이미지
            JLabel imageLabel = new JLabel(new ImageIcon(menu.getImage()));
            itemPanel.add(imageLabel, BorderLayout.CENTER);

            // cartPanel에 개별 메뉴 항목 추가
            cartPanel.add(itemPanel);
        }

        // 스크롤 가능한 패널 추가
        JScrollPane scrollPane = new JScrollPane(cartPanel); 
        add(scrollPane, BorderLayout.CENTER); 

        // 버튼 패널 설정
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2)); // 버튼을 2개 가로로 배치

        // 결제 버튼 추가
        ImageIcon checkoutIcon = resizeIcon(".//imgs//checkout.png", 200, 40);
        JButton checkoutButton = new JButton(checkoutIcon);
        checkoutButton.setContentAreaFilled(false);
        checkoutButton.setBorderPainted(false);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setMargin(new Insets(0, 0, 0, 0));
        checkoutButton.setPreferredSize(new Dimension(200, 40));
        checkoutButton.addActionListener(e -> paymentPage());
        buttonPanel.add(checkoutButton);

        // 취소 버튼 추가
        ImageIcon cancelIcon = resizeIcon(".//imgs//Cancel.png", 200, 40);
        JButton cancelButton = new JButton(cancelIcon);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setMargin(new Insets(0, 0, 0, 0));
        cancelButton.setPreferredSize(new Dimension(200, 40));
        cancelButton.addActionListener(e -> displayMenuPage());
        buttonPanel.add(cancelButton);

        // 버튼 패널을 화면 하단에 추가
        add(buttonPanel, BorderLayout.SOUTH);

        // 레이아웃을 다시 계산하고 화면을 갱신
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

        JButton cashButton = new JButton();
        cashButton.setBorderPainted(false);
        cashButton.setContentAreaFilled(false);
        cashButton.setBorderPainted(false);
        cashButton.setFocusPainted(false);
        cashButton.setMargin(new Insets(0, 0, 0, 0));
        JButton cardButton = new JButton();
        cardButton.setBorderPainted(false);
        cardButton.setContentAreaFilled(false);
        cardButton.setBorderPainted(false);
        cardButton.setFocusPainted(false);
        cardButton.setMargin(new Insets(0, 0, 0, 0));
        JButton payButton = new JButton();
        payButton.setBorderPainted(false);
        payButton.setContentAreaFilled(false);
        payButton.setBorderPainted(false);
        payButton.setFocusPainted(false);
        payButton.setMargin(new Insets(0, 0, 0, 0));
        
        
        ImageIcon cashImage = resizeIcon(".//imgs//coin.png", 200, 250);
        cashButton.setIcon(cashImage);
        
        ImageIcon cardImage = resizeIcon(".//imgs//coin-2.png", 200, 250);
        cardButton.setIcon(cardImage);
        
        ImageIcon payImage = resizeIcon(".//imgs//coin-1.png", 200, 250);
        payButton.setIcon(payImage);

        cashButton.setFont(regularfont);
        cardButton.setFont(regularfont);
        payButton.setFont(regularfont);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
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
        // 화면 초기화
        getContentPane().removeAll();
        revalidate(); // 레이아웃을 다시 계산하도록 함
        repaint();

        int price = 0;
        int num = 0;
        int tot = 0;

        receipt = "";
        receiptEmail = "";

        // 영수증 출력
        System.out.println("주문 번호 : " + orderNumber);
        System.out.println("결제 방법 : " + method);
        System.out.println("---------- 주문 내역 ----------");

        receipt += "<html><div style='text-align:center; font-size:18px; font-weight:bold; margin-top:100px;'>주문 번호 : " + orderNumber + "<br>결제 방법 : " + method + "<br><br>---------- 주문 내역 ----------<br>";
        receiptEmail += "<html><div style='text-align:center; font-size:18px; font-weight:bold; margin-top:100px;'>주문 번호 : " + orderNumber + "<br>결제 방법 : " + method + "<br><br>---------- 주문 내역 ----------<br>";
        
        for (Menu menu : customer.getOrderList().keySet()) {
            int quantity = customer.getOrderList().get(menu);
            receipt += "<div style='font-size:16px;'>" + menu.getName() + " x " + quantity + "<br></div>";
            receiptEmail += "<div style='font-size:16px;'>" + menu.getName() + " x " + quantity + "<br></div>";
            price = menu.getPrice();
            num = customer.getOrderList().get(menu);
            tot += price * num;
            System.out.println(menu.getName() + " x " + quantity);
        }

        calculateTotalRevenue();

        receipt += "<br><div style='font-size:18px; font-weight:bold;'>최종 금액 : " + tot + "원</div>";
        receiptEmail += "<br><div style='font-size:18px; font-weight:bold;'>최종 금액 : " + tot + "원</div>";
        System.out.println("최종 금액 : " + tot + "원\n");
        receipt += "<br><div style='font-size:14px; color:gray;'>" + message + "<br><br></div></html>";

        // 영수증 레이블 설정
        JLabel receiptLabel = new JLabel(receipt);
        receiptLabel.setFont(regularfont);  // 폰트 크기 조정
        receiptLabel.setHorizontalAlignment(JLabel.CENTER);
        receiptLabel.setVerticalAlignment(JLabel.TOP);
        JScrollPane receiptScroll = new JScrollPane(receiptLabel);
        receiptScroll.setPreferredSize(new Dimension(400, 300));  // 스크롤 가능한 영역으로 설정
        add(receiptScroll, BorderLayout.CENTER);

        // 주문 번호 증가
        orderNumber++;

        // '다음' 버튼 설정 (next.png 이미지 사용)
        ImageIcon nextIcon = resizeIcon(".//imgs//next.png", 400, 40);
        JButton returnButton = new JButton(nextIcon);
        returnButton.setContentAreaFilled(false);
        returnButton.setBorderPainted(false);
        returnButton.setFocusPainted(false);
        returnButton.setMargin(new Insets(0, 0, 0, 0));
        returnButton.setPreferredSize(new Dimension(400, 40));
        returnButton.addActionListener(e -> {
            orderEnd();
        });
        add(returnButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

        
    
    public void orderEnd() {
        // 화면 초기화
        getContentPane().removeAll();
        revalidate(); // 레이아웃을 다시 계산하도록 함
        repaint();

        // 버튼 패널 설정 (GridLayout 사용)
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // 여백 추가

        // "종료하기" 버튼
        ImageIcon exitIcon = resizeIcon(".//imgs//exit.png", 320, 200);
        JButton closeButton = new JButton(exitIcon);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setPreferredSize(new Dimension(400, 250));
        closeButton.addActionListener(e -> System.exit(0));  // 애플리케이션 종료
        buttonPanel.add(closeButton);

        // "처음 화면으로 돌아가기" 버튼
        ImageIcon returnIcon = resizeIcon(".//imgs//return.png", 320, 200);
        JButton mainButton = new JButton(returnIcon);
        mainButton.setContentAreaFilled(false);
        mainButton.setBorderPainted(false);
        mainButton.setFocusPainted(false);
        mainButton.setMargin(new Insets(0, 0, 0, 0));
        mainButton.setPreferredSize(new Dimension(400, 250));
        mainButton.addActionListener(e -> howToEatPage());  // 메인 화면으로 돌아가기
        buttonPanel.add(mainButton);

        // "이메일로 주문내역 전송하기" 버튼
        ImageIcon emailIcon = resizeIcon(".//imgs//email.png", 320, 200);
        JButton emailButton = new JButton(emailIcon);
        emailButton.setContentAreaFilled(false);
        emailButton.setBorderPainted(false);
        emailButton.setFocusPainted(false);
        emailButton.setMargin(new Insets(0, 0, 0, 0));
        emailButton.setPreferredSize(new Dimension(400, 250));
        emailButton.addActionListener(e -> OrderInputPanel());  // 주문 내역 이메일 전송
        buttonPanel.add(emailButton);

        // 버튼 패널 화면에 추가
        add(buttonPanel, BorderLayout.CENTER);

        // 주문 리스트 저장 (모든 주문 추가)
        allOrders.add(customer.getOrderList());

        // 레이아웃을 다시 계산하고 화면을 갱신
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
        passwordField.setMaximumSize(new Dimension(400, 40));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(passwordField);

        JButton loginButton = new JButton();
        ImageIcon loginIcon = resizeIcon(".//imgs//login.png", 400, 40);
        loginButton.setIcon(loginIcon);
        loginButton.setMaximumSize(new Dimension(400, 40));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false); // 배경 제거
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
        System.out.println("show ManagerPage");

        int totPrice = 0;

        // 메인 패널 생성 (세로로 구성)
        JPanel managerPanel = new JPanel();
        
        managerPanel.setLayout(new BoxLayout(managerPanel, BoxLayout.Y_AXIS));
        managerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        managerPanel.setBackground(Color.white); // 배경색 설정

        // 헤더 레이블 (타이틀)
        JLabel headerLabel = new JLabel("오늘 팔린 메뉴");
        headerLabel.setFont(boldfont);  // 폰트 스타일 변경
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setForeground(Color.darkGray);  // 타이틀 색상 설정
        managerPanel.add(headerLabel);

        // 판매된 메뉴와 개수를 표시하는 레이블 추가
        Set<String> keys = totOrder.keySet();
        for (String menuName : keys) {
            int menuPrice = menu.get(menuName);
            int menuNumber = totOrder.get(menuName); 
            System.out.println(menuName + " : " + menuNumber + " = " + menuPrice * menuNumber);
            
            JLabel orderLabel = new JLabel(menuName + " : " + menuNumber + "개 = " + menuPrice * menuNumber + "원");
            totPrice += menuPrice * menuNumber;
            orderLabel.setFont(regularfont);  // 기본 폰트 설정
            orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            orderLabel.setForeground(Color.darkGray);  // 레이블 색상 설정
            managerPanel.add(orderLabel);
        }

        // 총 매출 금액을 표시하는 레이블 추가
        System.out.println("총 매출 금액: " + totPrice + "원");
        JLabel revenueLabel = new JLabel("총 매출 금액: " + totPrice + "원");
        revenueLabel.setFont(regularfont);  // 폰트 스타일 변경
        revenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        revenueLabel.setForeground(new Color(0, 128, 0));  // 총 매출 색상(그린)
        managerPanel.add(revenueLabel);

        // 버튼 패널 설정
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // 버튼 두 개를 가로로 배치
        buttonPanel.setBackground(Color.white); // 배경색 설정

        // 메뉴 추가 버튼
        ImageIcon addMenuIcon = resizeIcon(".//imgs//addMenu.png", 40, 40);  // 아이콘 설정
        JButton addMenuButton = new JButton("메뉴 추가", addMenuIcon);
        addMenuButton.setFont(regularfont);
        addMenuButton.setContentAreaFilled(false);  // 버튼 배경 투명
        addMenuButton.setBorderPainted(false);  // 버튼 테두리 제거
        addMenuButton.setFocusPainted(false);  // 버튼 포커스 효과 제거
        addMenuButton.setHorizontalTextPosition(SwingConstants.CENTER);  // 텍스트 중앙 정렬
        addMenuButton.setVerticalTextPosition(SwingConstants.BOTTOM);  // 아이콘 아래에 텍스트 배치
        addMenuButton.addActionListener(e -> createMenuInputPanel());
        buttonPanel.add(addMenuButton);

        // 닫기 버튼
        ImageIcon closeButtonIcon = resizeIcon(".//imgs//close.png", 40, 40);  // 아이콘 설정
        JButton closeButton = new JButton("닫기", closeButtonIcon);
        closeButton.setFont(regularfont);
        closeButton.setContentAreaFilled(false);  // 버튼 배경 투명
        closeButton.setBorderPainted(false);  // 버튼 테두리 제거
        closeButton.setFocusPainted(false);  // 버튼 포커스 효과 제거
        closeButton.setHorizontalTextPosition(SwingConstants.CENTER);  // 텍스트 중앙 정렬
        closeButton.setVerticalTextPosition(SwingConstants.BOTTOM);  // 아이콘 아래에 텍스트 배치
        closeButton.addActionListener(e -> howToEatPage());  // 애플리케이션 종료
        buttonPanel.add(closeButton);

        // 버튼 패널 추가
        managerPanel.add(buttonPanel);

        // 화면에 추가
        add(managerPanel, BorderLayout.CENTER);

        // 레이아웃 재계산 및 화면 갱신
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
    
    private void createMenuInputPanel() {
        getContentPane().removeAll();
        mainPanel.removeAll();

        // 입력 패널 생성 (5x2 그리드 레이아웃)
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 20, 20)); // 여백을 추가하여 깔끔하게
        inputPanel.setLayout(new BorderLayout(20, 0)); // 패널 레이아웃을 BorderLayout으로 설정하고 좌우 여백 20 추가
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 패널 여백 설정
        inputPanel.setBackground(Color.white); // 배경색 설정

        // 메뉴 이름 입력 필드
        JLabel nameLabel = new JLabel("메뉴 이름");
        nameLabel.setFont(regularfont);
        nameLabel.setForeground(Color.darkGray);
        inputPanel.add(nameLabel);
        nameField = new JTextField();
        inputPanel.add(nameField);

        // 가격 입력 필드
        JLabel priceLabel = new JLabel("가격");
        priceLabel.setFont(regularfont);
        priceLabel.setForeground(Color.darkGray);
        inputPanel.add(priceLabel);
        priceField = new JTextField();
        inputPanel.add(priceField);

        // 설명 입력 필드
        JLabel descriptionLabel = new JLabel("설명");
        descriptionLabel.setFont(regularfont);
        descriptionLabel.setForeground(Color.darkGray);
        inputPanel.add(descriptionLabel);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true); // 줄 바꿈 허용
        descriptionArea.setWrapStyleWord(true); // 단어 단위로 줄 바꿈
        descriptionArea.setFont(regularfont);
        inputPanel.add(new JScrollPane(descriptionArea));

        // 이미지 경로 입력 필드
        JLabel imageLabel = new JLabel("이미지 경로");
        imageLabel.setFont(regularfont);
        imageLabel.setForeground(Color.darkGray);
        inputPanel.add(imageLabel);
        imageField = new JTextField();
        imageField.setEditable(false);
        inputPanel.add(imageField);

        // 이미지 미리보기 JLabel 추가
        JLabel previewLabel = new JLabel();
        previewLabel.setFont(regularfont);
        previewLabel.setForeground(Color.darkGray);
        inputPanel.add(previewLabel);
        
        // 미리보기 이미지 표시할 JLabel
        JLabel imagePreview = new JLabel();
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(imagePreview);

        // 이미지 선택 버튼
        JButton selectImageButton = new JButton();
        ImageIcon addImage = resizeIcon(".//imgs//addImage.png", 400, 40);
        selectImageButton.setIcon(addImage);
        selectImageButton.setFont(regularfont);
        selectImageButton.setForeground(Color.white); // 텍스트 색상
        selectImageButton.setFocusPainted(false);  // 포커스 효과 제거
        selectImageButton.setPreferredSize(new Dimension(400, 40));
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imageField.setText(selectedFile.getAbsolutePath());

                    // 이미지 미리보기
                    try {
                        ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // 이미지 크기 조정
                        imagePreview.setIcon(new ImageIcon(image));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // 버튼 패널 (추가 버튼)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setBackground(Color.white); // 배경색 설정
        JButton addButton = new JButton();
        ImageIcon addIcon = new ImageIcon(".//imgs//add.png");
        addButton.setIcon(addIcon);
        addButton.setFont(regularfont);
        addButton.setFocusPainted(false);  // 포커스 효과 제거
        addButton.addActionListener(new AddMenuAction());
        buttonPanel.add(selectImageButton);
        buttonPanel.add(addButton);

        // 메인 패널에 입력 패널과 버튼 패널 추가
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 화면에 추가
        add(mainPanel);

        // 레이아웃 재계산 및 화면 갱신
        revalidate();
        repaint();
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

        // 이메일 입력 패널 설정 (가로 배치)
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.X_AXIS)); // 수평(BoxLayout)
        emailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 여백 설정

        // 이메일 입력 필드
        JLabel emailLabel = new JLabel("이메일 주소");
        emailLabel.setFont(regularfont); // 폰트 설정
        emailLabel.setForeground(Color.darkGray);
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // 가로는 최대, 높이 30
        emailField.setFont(regularfont); // 폰트 설정

        emailPanel.add(emailLabel);
        emailPanel.add(Box.createRigidArea(new Dimension(10, 0))); // 간격 추가
        emailPanel.add(emailField);

        // 주문 내역 패널 설정 (수직 배치)
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS)); // 수직(BoxLayout)
        receiptPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 여백 설정

        JLabel orderDetailsLabel = new JLabel("주문 내역");
        orderDetailsLabel.setFont(regularfont);
        orderDetailsLabel.setForeground(Color.darkGray);
        orderDetailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬

        // receiptLabel을 감싸는 패널을 추가하여 가운데 정렬을 보장
        JPanel receiptLabelPanel = new JPanel();
        receiptLabelPanel.setLayout(new BoxLayout(receiptLabelPanel, BoxLayout.X_AXIS)); // 수평 정렬
        receiptLabelPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬

        JLabel receiptLabel = new JLabel("<html>" + receiptEmail + "</html>");
        receiptLabel.setFont(regularfont); // 주문 내역 폰트 설정
        receiptLabel.setHorizontalAlignment(SwingConstants.CENTER); // 가로 가운데 정렬

        receiptLabelPanel.add(receiptLabel); // receiptLabel을 패널에 추가

        // 패널에 추가
        receiptPanel.add(orderDetailsLabel);
        receiptPanel.add(receiptLabelPanel); // receiptLabelPanel을 receiptPanel에 추가

        // 주문 제출 버튼
        submitButton = new JButton();
        ImageIcon sendOrder = resizeIcon(".//imgs//sendOrder.png", 400, 40);
        submitButton.setIcon(sendOrder);
        submitButton.setFont(regularfont);
        submitButton.setForeground(Color.white); // 버튼 텍스트 색상
        submitButton.setFocusPainted(false);  // 포커스 효과 제거
        submitButton.setPreferredSize(new Dimension(400, 40)); // 버튼 크기 설정
        submitButton.addActionListener(new SubmitOrderAction());

        // 버튼 패널 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white); // 배경색 설정
        buttonPanel.add(submitButton);

        // 전체 레이아웃에 추가
        add(emailPanel, BorderLayout.NORTH); // 상단에 이메일 패널 추가
        add(receiptPanel, BorderLayout.CENTER); // 중간에 주문 내역 패널 추가
        add(buttonPanel, BorderLayout.SOUTH); // 하단에 버튼 패널 추가

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
            howToEatPage();
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
