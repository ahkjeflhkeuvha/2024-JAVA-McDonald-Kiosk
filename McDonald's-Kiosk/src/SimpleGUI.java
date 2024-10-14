import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleGUI extends JFrame {
    private JButton button;
    private JLabel logoLabel;
    private ImageIcon icon;

    public SimpleGUI() {
        // 기본적인 JFrame 설정
        setTitle("McDonald Kiosk");
        setSize(462, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout()); // 레이아웃 매니저 설정

        // 이미지 로드 및 크기 조정
        icon = new ImageIcon(".//imgs//logo.png");  // 이미지 파일의 경로 설정
        Image image = icon.getImage(); // ImageIcon을 Image로 변환
        Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH); // 원하는 크기로 조정 (300x300)
        ImageIcon scaledIcon = new ImageIcon(scaledImage); // 다시 ImageIcon으로 변환

        // 로고 라벨 추가
        logoLabel = new JLabel("Welcome to McDonald", JLabel.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 40)); // 글꼴 설정
        logoLabel.setIcon(scaledIcon); // 조정된 이미지 사용

        add(logoLabel);
        button = new JButton("주문하기");
        button.setBackground(Color.WHITE);

        // 수직 정렬 설정
        logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM); // 아이콘 아래에 텍스트 배치
        logoLabel.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 중앙 정렬

        add(button);

        // 버튼 액션 리스너 추가
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                howToEatPage();
            }
        });
    }
    
    private JButton toGo;
    private JButton inRes;
    private ImageIcon toGoImg;
    
    private void howToEatPage() {
        // 현재 화면을 초기화하고 새로운 페이지 구성
        getContentPane().removeAll();
        repaint(); // 기존 화면을 지우기 위해 필요

        // 새로운 화면 설정
        JLabel nextPageLabel = new JLabel("Where will you eat today?", SwingConstants.CENTER);
        nextPageLabel.setFont(new Font("Arial", Font.BOLD, 30));
        add(nextPageLabel, BorderLayout.CENTER);
        
        toGo = new JButton("Take Out");
        inRes = new JButton("Eat In");
        
        toGoImg = new ImageIcon(".//imgs//togo.png"); // 이미지 파일의 경로 설정
        
        // 버튼에 이미지 추가
        toGo.setIcon(toGoImg);
        inRes.setIcon(toGoImg);

        // 수직 정렬 설정
        toGo.setVerticalTextPosition(SwingConstants.BOTTOM);
        toGo.setHorizontalTextPosition(SwingConstants.CENTER);
        toGo.setBackground(Color.WHITE);
        inRes.setVerticalTextPosition(SwingConstants.BOTTOM);
        inRes.setHorizontalTextPosition(SwingConstants.CENTER);
        inRes.setBackground(Color.WHITE);

        // 버튼 추가
        add(toGo);
        add(inRes);

        // 새 화면 표시
        revalidate();
        repaint(); // 다시 그리기
    }

    public static void main(String[] args) {
        // GUI 실행
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SimpleGUI().setVisible(true);
            }
        });
    }
}
