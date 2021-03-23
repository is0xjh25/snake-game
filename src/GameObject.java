import java.awt.*;

public abstract class GameObject {

    private int x, y, width, height;
    private final Color color;

    public GameObject(int x, int y, int tileSize, Color color) {
        this.color = color;
        this.x = x;
        this.y = y;
        width = tileSize;
        height = tileSize;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x * width, y * height, height, width);
    }
}
