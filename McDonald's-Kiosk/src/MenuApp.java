import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuApp extends JFrame {
    private JList<String> menuList;
    private DefaultListModel<String> listModel;

    public MenuApp() {
        // JFrame 설정
        setTitle("Menu");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 메뉴 리스트 모델과 JList 초기화
        listModel = new DefaultListModel<>();
        menuList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(menuList);
        add(scrollPane, BorderLayout.CENTER);

        // 메뉴 불러오기
        loadMenu();
    }

    class Menu {
    	private String name;
    	private double price;
    	private String description;
    	
    	Menu(String name, double price, String description){
    		this.name = name;
    		this.price = price;
    		this.description = description;
    	}
    }
    
    private void loadMenu() {
        try (FileReader reader = new FileReader("menu.json")) {
            StringBuilder jsonBuilder = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                jsonBuilder.append((char) ch);
            }

            ArrayList<Menu> al = new ArrayList<>();
            // JSON 데이터 파싱
            JSONArray menuArray = new JSONArray(jsonBuilder.toString());

            for (int i = 0; i < menuArray.length(); i++) {
                String menuName = menuArray.getJSONObject(i).getString("name");
                Double menuPrice = Double.parseDouble(menuArray.getJSONObject(i).getString("price"));
                String menuDescription = menuArray.getJSONObject(i).getString("description");
                
                al.add(new Menu(menuName, menuPrice, menuDescription));
            }
            
            for(int i = 0; i<al.size(); i++) {
            	System.out.println(al.get(i));
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuApp().setVisible(true);
        });
    } 
}
