package PongV2;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Pong implements ActionListener, KeyListener {

    public static Pong pong;

    public int width = 1000, height = 1000;
    public Renderer renderer;

    public Paddle player1;
    public Paddle player2;

    public Ball ball;

    public boolean bot = false, selectingDifficulty;

    public boolean w, s, up, down;

    public int gameStatus = 0, scoreLimit; // 0 = stopped, 1 = paused, 2 = playing

    public Random random;

    public int botMoves;

    public int botCooldown = 0;

    public int botDifficulty;

    public Pong() {

        Timer timer = new Timer(20, this);
        JFrame jFrame = new JFrame("Pong");
        random = new Random();

        renderer = new Renderer();

        jFrame.setSize(width + 15, height + 35);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(renderer);
        jFrame.addKeyListener(this);

        timer.start();
    }

    public void start() {
        gameStatus = 2;
        player1 = new Paddle(this, 1);
        player2 = new Paddle(this, 2);
        ball = new Ball(this);
    }


    public void update() {
        if (w) {
            player1.move(true);
        }
        if (s) {
            player1.move(false);
        }
        if (!bot) {
            if (up) {
                player2.move(true);
            }
            if (down) {
                player2.move(false);
            }
        } else {
            if (botDifficulty == 2) {
                if (player2.y + player2.height / 2 < ball.y) {
                    player2.move(false);
                    botMoves++;
                }
                if (player2.y + player2.height / 2 > ball.y) {
                    player2.move(true);
                    botMoves++;
                }
            } else if (botDifficulty == 1 || botDifficulty == 0) {
                if (botCooldown > 0) {
                    botCooldown--;
                    if (botCooldown == 0) {
                        botMoves = 0;
                    }
                }

                if (botMoves < 10) {
                    if (player2.y + player2.height / 2 < ball.y) {
                        player2.move(false);
                        botMoves++;
                    }
                    if (player2.y + player2.height / 2 > ball.y) {
                        player2.move(true);
                        botMoves++;
                    }
                    if (botDifficulty == 0) {
                        botCooldown = 20;
                    }
                    if (botDifficulty == 1) {
                        botCooldown = 10;
                    }
                }
            }
            ball.update(player1, player2);
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameStatus == 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("PONG", width / 2 - 80, 50);
            if (!selectingDifficulty) {
                g.setFont(new Font("Arial", 1, 20));
                g.drawString("Press SPACE to play", width / 2 - 100, height / 2 - 50);
                g.drawString("Press SHIFT to play with bot", width / 2 - 140, height / 2 - 20);
                g.drawString("Score limit: " + scoreLimit, width / 2 - 80, height / 2 + 10);
            }

            if (selectingDifficulty) {
                String string = botDifficulty == 2 ? "Godlike" : (botDifficulty == 1 ? "Normal" : "Easy");

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", 1, 20));
                g.drawString("Bot Difficulty: " + string, width / 2 - 105, height / 2 - 20);
                g.setFont(new Font("Arial", 1, 20));
                g.drawString("Press SPACE to play", width / 2 - 100, height / 2 - 50);
            }
        }
        if (gameStatus == 2 || gameStatus == 1) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(6));
            g.drawLine(width / 2, 0, width / 2, 400);
            g.drawLine(width / 2, 600, width / 2, height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString(String.valueOf(player2.score), width / 2 - width / 4, 50);

            g.drawOval(width / 2 - 100, height / 2 - 100, 200, 200);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString(String.valueOf(player1.score), width / 2 + width / 4, 50);

            player1.render(g);
            player2.render(g);
            ball.render(g);
        }
        if (gameStatus == 1) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 40));
            g.drawString("PAUSED", width / 2 - 80, height / 2 + 14);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (gameStatus == 2) {
            update();
        }

        renderer.repaint();

    }

    public static void main(String[] args) {

        pong = new Pong();

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int id = e.getKeyCode();

        if (id == KeyEvent.VK_W) {
            w = true;
        } else if (id == KeyEvent.VK_S) {
            s = true;
        } else if (id == KeyEvent.VK_UP) {
            up = true;
        } else if (id == KeyEvent.VK_DOWN) {
            down = true;
        } else if (id == KeyEvent.VK_RIGHT) {
            if (selectingDifficulty) {
                if (botDifficulty < 2) {
                    botDifficulty++;
                }
            } else if (gameStatus == 0) {
                if (scoreLimit < 10) {
                    scoreLimit++;
                }
            }
        } else if (id == KeyEvent.VK_LEFT) {
            if (selectingDifficulty) {
                if (botDifficulty > 0) {
                    botDifficulty--;
                }
            } else if (gameStatus == 0) {
                if (scoreLimit > 1) {
                    scoreLimit--;
                }
            }
        } else if (id == KeyEvent.VK_SHIFT && gameStatus == 0) {
            bot = true;
            selectingDifficulty = true;
        } else if (id == KeyEvent.VK_ESCAPE && gameStatus == 2) {
            gameStatus = 0;
            selectingDifficulty = false;
        } else if (id == KeyEvent.VK_SPACE) {
            if (gameStatus == 0) {
                if (!selectingDifficulty) {
                    bot = false;
                }
                selectingDifficulty = false;
                start();
            } else if (gameStatus == 2) {
                gameStatus = 1;
            } else if (gameStatus == 1) {
                gameStatus = 2;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int id = e.getKeyCode();

        if (id == KeyEvent.VK_W) {
            w = false;
        } else if (id == KeyEvent.VK_S) {
            s = false;
        } else if (id == KeyEvent.VK_UP) {
            up = false;
        } else if (id == KeyEvent.VK_DOWN) {
            down = false;
        }
    }
}
