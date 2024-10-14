import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class MenuApp extends JFrame {
    private JList<String> menuList;
    private DefaultListModel<String> listModel;

    public MenuApp() {
        loadMenu();
    }

    private void loadMenu() {
        try (FileReader reader = new FileReader("menu.json")) {
            StringBuilder jsonBuilder = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                System.out.print((char)ch);
            }

     
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuApp().setVisible(true);
        });
    }
}
