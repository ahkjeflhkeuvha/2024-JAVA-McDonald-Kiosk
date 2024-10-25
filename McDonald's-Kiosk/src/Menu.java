import javax.swing.ImageIcon;

public class Menu {
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
