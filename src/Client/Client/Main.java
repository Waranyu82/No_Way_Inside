package Client.Client;

import javax.swing.JFrame;

public class Main {
    public static JFrame window = new JFrame();
    public static void main(String[] args) throws Exception {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("No Way Inside");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // gamePanel.initBufferStrategy();

        gamePanel.setupgame();
        gamePanel.startGameThread();

    }
}