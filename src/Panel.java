import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable {

	private static JFrame frame;
	private static Panel panel;
	private boolean startScreen;

	private Random rand;

	private final int WIDTH, HEIGHT;

	int angle = 0;
	int translate = 0;

	Image playPick, cpuPick;

	AffineTransform player, cpu;

	int rockXoff, paperXoff, scissorsXoff;

	Image rock, paper, scissors;
	Image selectedRock, selectedPaper, selectedScissors;
	Image bigRock, bigPaper, bigScissors;
	Image circle, circle1;
	Image O, X, tie, pick;

	private int rockX = 100, paperX = 250, scissorsX = 400;

	private boolean rockSelected, paperSelected, scissorsSelected;
	private boolean selected;

	private boolean blink;

	private Image status;

	private static final long serialVersionUID = 1L;

	Thread thread;

	public Panel(int width, int height) {

		WIDTH = width;
		HEIGHT = height;

		this.setSize(width, height);

		rand = new Random();

		rockXoff = -5;
		paperXoff = 0;
		scissorsXoff = 5;

		rockSelected = paperSelected = scissorsSelected = false;

		MA ma = new MA();

		this.addMouseMotionListener(ma);
		this.addMouseListener(ma);

		thread = new Thread(this);
		thread.start();

		restart();

		getPictures();

	}

	@Override
	public void paint(Graphics g) {
		
		update(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.WHITE);

		g2d.fillRect(0, 0, WIDTH, HEIGHT);

		if (startScreen) {
			// paint start screen

			g2d.drawImage(pick, 90, 140, null);

			g2d.drawImage(circle, 75 + rockXoff, 475, null);
			g2d.drawImage(circle, 225 + paperXoff, 475, null);
			g2d.drawImage(circle, 375 + scissorsXoff, 475, null);

			if (selected) {
				// draw inflated images

				if (rockSelected) {
					g2d.drawImage(circle1, 65 + rockXoff, 465, null);

					g2d.drawImage(selectedRock, rockX + rockXoff - 10, 490,
							null);
					g2d.drawImage(paper, paperX + paperXoff, 500, null);
					g2d.drawImage(scissors, scissorsX + scissorsXoff, 500, null);

				} else if (paperSelected) {
					g2d.drawImage(circle1, 215 + paperXoff, 465, null);

					g2d.drawImage(rock, rockX + rockXoff, 500, null);
					g2d.drawImage(selectedPaper, paperX + paperXoff - 10, 490,
							null);
					g2d.drawImage(scissors, scissorsX + scissorsXoff, 500, null);

				} else if (scissorsSelected) {
					g2d.drawImage(circle1, 365 + scissorsXoff, 465, null);

					g2d.drawImage(rock, rockX + rockXoff, 500, null);
					g2d.drawImage(paper, paperX + paperXoff, 500, null);
					g2d.drawImage(selectedScissors, scissorsX + scissorsXoff
							- 10, 490, null);

				}

			} else {

				// regular images
				g2d.drawImage(rock, rockX + rockXoff, 500, null);
				g2d.drawImage(paper, paperX + paperXoff, 500, null);
				g2d.drawImage(scissors, scissorsX + scissorsXoff, 500, null);

			}

		} else {
			// paint rock paper scissors!
			if (blink) {
				g2d.drawImage(status, 100, 50, null);
			}

			g2d.drawImage(playPick, player, null);
			g2d.drawImage(cpuPick, cpu, null);

		}
	}

	private void mouseUpdate(int x, int y) {
		if (circleCheck(rockX + 50, 550, x, y, 75)) {
			// rock
			selected = rockSelected = true;
			paperSelected = scissorsSelected = false;

		} else if (circleCheck(paperX + 50, 550, x, y, 75)) {
			// paper
			selected = paperSelected = true;
			rockSelected = scissorsSelected = false;

		} else if (circleCheck(scissorsX + 50, 550, x, y, 75)) {
			// scissors
			selected = scissorsSelected = true;
			rockSelected = paperSelected = false;

		} else {
			rockSelected = paperSelected = scissorsSelected = selected = false;

		}

	}

	private boolean circleCheck(int x, int y, int x1, int y1, int radius) {
		if (Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2)) < radius) {
			return true;

		}

		return false;

	}

	private void click(int x, int y) {

		// deal with clicks
		if (startScreen) {

			// selecting rock paper or scissors!
			if (!selected) {
				return;

			}

			/* person selects a thing! */
			startScreen = false;

			int selected;

			if (rockSelected) {
				selected = 0;
				playPick = bigRock;
			} else if (paperSelected) {
				selected = 1;
				playPick = bigPaper;
			} else if (scissorsSelected) {
				selected = 2;
				playPick = bigScissors;
			} else {
				// this should never happen
				return;
			}

			int cpu = rand.nextInt(3);

			switch (cpu) {
			case 0:
				cpuPick = bigRock;
				break;
			case 1:
				cpuPick = bigPaper;
				break;
			case 2:
				cpuPick = bigScissors;
			}

			if (cpu == selected) {
				// tie
				status = tie;

			} else if ((selected - cpu + 3) % 3 == 1) {
				// win!
				status = O;

			} else {
				// lost
				status = X;

			}

			// start animation thread!

			new Thread(WinThread).start();

		}
	}

	private Thread WinThread = new Thread(new Runnable() {

		@Override
		public void run() {
			// blink the circle, reset things.

			int angle = -90;

			double onedegree = Math.toRadians(1);
			player.translate(-232, 400);
			cpu.translate(534, 400);
			player.rotate(Math.toRadians(angle), 210, 285);
			cpu.rotate(Math.toRadians(-angle), 75, 285);
			while (angle < 90) {

				player.rotate(onedegree, 210, 285);
				cpu.rotate(-onedegree, 75, 285);
				angle++;
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			int blinkTime = 200;
			try {
				blink = true;
				Thread.sleep(blinkTime);
				blink = false;
				Thread.sleep(blinkTime);
				blink = true;
				Thread.sleep(blinkTime);
				blink = false;
				Thread.sleep(blinkTime);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			restart();

		}

	});

	private void gameUpdate() {
		if (startScreen) {
			// move background
		} else {
			// make descision
		}

	}

	@Override
	public void run() {
		while (true) {
			// TODO: way better thread handling

			gameUpdate();
			repaint();

			try {
				Thread.sleep(18);

			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		}
	}

	private void restart() {

		// resets all values

		startScreen = true;

		player = new AffineTransform();
		cpu = new AffineTransform();

	}

	private class MA extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			click(e.getX(), e.getY());

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseUpdate(e.getX(), e.getY());
		}

	}

	private void getPictures() {
		// get actual pictures

		try {
			rock = getPicture("rock.png", 100, 100);
			paper = getPicture("paper.png", 100, 100);
			scissors = getPicture("scissors.png", 100, 100);

			selectedRock = getPicture("rock.png", 120, 120);
			selectedPaper = getPicture("paper.png", 120, 120);
			selectedScissors = getPicture("scissors.png", 120, 120);

			bigRock = getPicture("rock.png", 300, 300);
			bigPaper = getPicture("paper.png", 300, 300);
			bigScissors = getPicture("scissors.png", 300, 300);

			circle = getPicture("blue_circle.png", 150, 150);
			circle1 = getPicture("green_circle.png", 170, 170);

			O = getPicture("o.png", 400, 400);
			X = getPicture("x.png", 400, 400);
			tie = getPicture("tie.png", 400, 400);

			pick = getPicture("pick!.png", 420, 240);

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	private Image getPicture(String path, int width, int height)
			throws IOException {
		return ImageIO.read(this.getClass().getResourceAsStream(path))
				.getScaledInstance(width, height, 0);

	}

	@Override
	public void update(Graphics g) {
		System.out.println("update");
		Graphics offgc;
		Image offscreen = null;

		// create the offscreen buffer and associated Graphics
		offscreen = createImage(WIDTH, HEIGHT);
		offgc = offscreen.getGraphics();

		// clear the exposed area
		offgc.setColor(Color.WHITE);
		offgc.fillRect(0, 0, WIDTH, HEIGHT);
		offgc.setColor(getForeground());

		// do normal redraw
		paint(offgc);

		// transfer offscreen to window
		g.drawImage(offscreen, 0, 0, this);
	}

	public static void main(String[] args) {

		int width = 600;
		int height = 800;

		frame = new JFrame("Jan, Ken, PaKASgNOAEJgbiOJ");
		panel = new Panel(width, height);

		frame.add(panel);
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
