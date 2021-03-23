import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 500, HEIGHT = 500;

    private Thread thread;

    private boolean running;

    private boolean right = true, left = false, up = false, down =false;

    private BodyPart b;
    private ArrayList<BodyPart> snake;
    private ArrayList<Apple> apples;
    private ArrayList<Chance> chances;
    private ArrayList<Bomb> bombs;

    private Random r;

    private int x = 10, y = 10, size = 5;
    private int bombNumber = 4;
    private int score = 0;
    private int ticks = 0;
    private int tickCount = 2000000;
    private final int rate = 50000;

    public GamePanel() {
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        snake = new ArrayList<BodyPart>();
        apples = new ArrayList<Apple>();
        chances = new ArrayList<Chance>();
        bombs = new ArrayList<Bomb>();
        r = new Random();
        start();
    }

    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {

        running = false;
        repaint();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        snake.trimToSize();
        if (snake.size() == 0) {
            b = new BodyPart(x, y, 10);
            snake.add(b);
        }

        ticks++;

        if (ticks > tickCount) {

            if (right) x++;
            if (left) x--;
            if (up) y--;
            if (down) y++;

            ticks = 0;
            b = new BodyPart(x, y, 10);
            snake.add(b);

            if (snake.size() > size) {
                snake.remove(0);
            }
        }

        if (apples.size() == 0) apples.add(new Apple(r.nextInt(49), r.nextInt(49) , 10));
        if (chances.size() == 0) chances.add(new Chance(r.nextInt(49), r.nextInt(49), 10));

        // Multiple bombs
        while (bombs.size() < bombNumber) {
            bombs.add(new Bomb(r.nextInt(49), r.nextInt(49), 10));
        }

        // Gain apple
        for (int i = 0; i < apples.size(); i++) {
            if (x == apples.get(i).getX() && y == apples.get(i).getY()) {
                score++;
                if (tickCount - rate > 200000) {
                    tickCount -= rate;
                }
                // Increase difficulty
                if (score % 5 == 1) {
                    bombNumber++;
                }
                size ++;
                apples.remove(i);
                i++;
            }
        }

        // Gain chance
        for (int i = 0; i < chances.size(); i++) {
            if (x == chances.get(i).getX() && y == chances.get(i).getY()) {
                double random = r.nextDouble();
                if (random > (2 / 3)) {
                    if (size > 2) {
                        size -= 2;
                    } else if (size == 2) {
                        size--;
                    } else if (size == 1) {
                        bombNumber--;
                    }
                } else {
                    size += 2;
                    bombNumber--;
                }
                chances.remove(i);
                i++;
            }
        }

        // Touch bomb
        for (int i = 0; i < bombs.size(); i++) {
            if (x == bombs.get(i).getX() && y == bombs.get(i).getY()) {
                stop();
            }
        }

        // Collide body part
        for (int i = 0; i < snake.size(); i++) {
            if (x == snake.get(i).getX() && y == snake.get(i).getY()) {
                if (i != snake.size() - 1) {
                    stop();
                }
            }
        }

        // Collide Board
        if (x < 0 || x > 49 || y < 0 || y > 49 ) {
            stop();
        }
    }

    public void paint(Graphics g){

        g.clearRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < snake.size(); i++) {
            snake.get(i).draw(g);
        }
        for (int i = 0; i < apples.size(); i++) {
            apples.get(i).draw(g);
        }
        for (int i = 0; i < chances.size(); i++) {
            chances.get(i).draw(g);
        }
        for (int i = 0; i < bombs.size(); i++) {
            bombs.get(i).draw(g);
        }

        // Game Over Paint
        if (!running) {
            g.clearRect(0, 0, WIDTH, HEIGHT);
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File("res/gameover.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(image, 72, 50, null);
            g.setFont(new Font("helvetica", Font.ITALIC, 34));
            g.setColor(Color.MAGENTA);
            g.drawString("Your score: " + score, 150, 300);
        }

    }

    @Override
    public void run() {
        while (running) {
                tick();
                repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_RIGHT && !left) {
            right = true;
            up = false;
            down = false;
        }

        if (key == KeyEvent.VK_LEFT && !right) {
            left = true;
            up = false;
            down = false;
        }

        if (key == KeyEvent.VK_UP && !down) {
            up = true;
            left = false;
            right = false;
        }
        if (key == KeyEvent.VK_DOWN && !up) {
            down = true;
            left = false;
            right = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
