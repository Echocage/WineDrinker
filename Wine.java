package Shared;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.powerbot.ge;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(authors = { "Preston3050" }, name = "Wine", description = "Drinks Wine and Banks", version = 1.0)
public class Wine extends ActiveScript implements PaintListener {
	private static ActiveScript instance;
	static int copper = 0;
	int wineID = 1993;
	int jugID = 1935;
	int winePrice;
	int jugPrice;
	int profit;
	int numOfJugs;
	public long startTime = 0;
	int bob3;
	boolean guiDone = false;

	public long millis = 0;

	public long hours = 0;

	public long minutes = 0;

	public long seconds = 0;

	public long last = 0;
	Timer ptime = new Timer(0);
	private final Color color1 = new Color(139, 67, 227, 100);
	private final Color color2 = new Color(0, 0, 0);
	private final Color color3 = new Color(222, 255, 0, 229);

	private final BasicStroke stroke1 = new BasicStroke(1);

	private final Font font1 = new Font("Trajan Pro", 0, 14);

	public int getPrice(int id) throws IOException {

		String price;
		URL url = new URL(
				"http://services.runescape.com/m=itemdb_rs/viewitem.ws?obj="
						+ id);
		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.contains("<td>")) {
				price = line.substring(line.indexOf(">") + 1,
						line.indexOf("/") - 1);
				price = price.replace(",", "");
				try {
					return Integer.parseInt(price);
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		}
		return -1;
	}

	@Override
	public void onStop() {
		try {
			ScreenCapture2();
		} catch (IOException e) {

			e.printStackTrace();
		}
		log.info("Script Stopping =D");
	}

	public void ScreenCapture2() throws IOException {
		try {
			Robot robot = new Robot();
			Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit()
					.getScreenSize());
			BufferedImage bufferedImage = robot
					.createScreenCapture(captureSize);
			ImageIO.write(bufferedImage, "jpg", new File("ScreenShot.jpg"));
		} catch (AWTException e) {
			System.err.println("Someone call a doctor!");
		}

	}

	public static ActiveScript getInstance() {
		return instance;
	}

	private int inventoryCheck() {
		Item[] bob = Inventory.getItems();

		if (bob.length >= 28) {
			for (int c = 0; c <= 27; c++) {
				if (Widgets.get(679, 0).getChild(c).getChildId() != wineID) {
					break;

				} else {
					if (c == 27) {

						return 2;

					}
				}

			}

			for (int c = 0; c <= 27; c++) {
				if (Widgets.get(679, 0).getChild(c).getChildId() != jugID) {
					break;

				} else {
					if (c == 27) {

						return 1;

					}
				}

			}

		}
		return 0;
	}

	@Override
	protected void setup() {
		startTime = System.currentTimeMillis();
		try {
			winePrice = getPrice(wineID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			jugPrice = getPrice(jugID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		final Drink drink = new Drink();
		final Strategy drinkAction = new Strategy(drink, drink);

		provide(drinkAction);
		final Bank bank = new Bank();
		final Strategy bankAction = new Strategy(bank, bank);

		provide(bankAction);

	}

	private class Drink implements Task, Condition {
		@Override
		public void run() {

			Item[] bob = Inventory.getItems();
			for (int c = 0; c < bob.length; c++) {
				if (bob[c].getId() == wineID) {
					if (bob[c].getWidgetChild().getBoundingRectangle()
							.contains(Mouse.getLocation())) {
						Mouse.click(true);
						bob[c + 1].getWidgetChild().hover();
						numOfJugs++;
					} else {
						bob[c].getWidgetChild().click(true);
						bob[c + 1].getWidgetChild().hover();
						numOfJugs++;
					}

					Timer time = new Timer(2000);
					while (bob[c].getId() != jugID && time.isRunning()) {
						Time.sleep(100);

					}
					Time.sleep(100);

				}
			}

		}

		@Override
		public boolean validate() {
			return inventoryCheck() != 1;

		}
	}

	private class Bank implements Task, Condition {

		@Override
		public void run() {
			if (Camera.getPitch() != 93) {
				Camera.setPitch(93);
			}

			org.powerbot.game.api.methods.widget.Bank.open();
			if (org.powerbot.game.api.methods.widget.Bank.isOpen()) {
				org.powerbot.game.api.methods.widget.Bank.depositInventory();
				log.info("Bankin");
				if (copper == 1) {
					Time.sleep(3000);
				}
				if (org.powerbot.game.api.methods.widget.Bank.getItem(wineID) != null
						&& org.powerbot.game.api.methods.widget.Bank.getItem(
								wineID).getStackSize() >= 28) {

					org.powerbot.game.api.methods.widget.Bank.withdraw(wineID,
							28);

					org.powerbot.game.api.methods.widget.Bank.close();

				} else {
					log.info("Check");
					org.powerbot.game.api.methods.widget.Bank.close();
					if (checkIfMore()) {
						copper = 1;
					}

				}

			}
		}

		@Override
		public boolean validate() {

			return inventoryCheck() == 1
					|| org.powerbot.game.api.methods.widget.Bank.isOpen()
					|| inventoryCheck() == 0;
		}

	}

	public boolean checkIfMore() {
		NPC banker = NPCs
				.getNearest(org.powerbot.game.api.methods.widget.Bank.BANK_NPC_IDS);
		banker.interact("Collect");
		Time.sleep(3000);
		if (Widgets.get(109, 1).isOnScreen()) {
			if (Widgets.get(109, 23).getChild(1).getChildId() == wineID
					|| Widgets.get(109, 23).getChild(3).getChildId() == wineID) {

				Widgets.get(109, 23).getChild(1).click(true);
				Widgets.get(109, 23).getChild(3).click(true);
				Widgets.get(109, 14).click(true);
				Time.sleep(1000);
				return true;
			} else {
				Widgets.get(109, 14).click(true);
				Time.sleep(100000);
				return false;
			}
		}
		return false;
	}

	@Override
	public void onRepaint(Graphics g1) {
		profit = Math.abs(jugPrice - winePrice);
		millis = System.currentTimeMillis() - startTime;
		int bob = (int) ptime.getElapsed();
		int bob7 = (int) (profit * numOfJugs * 3600000D / bob);
		if (numOfJugs >= 20) {
			bob3 = (int) (numOfJugs * 3600000D / bob);
		}
		String u = String.valueOf(bob3);
		String m = String.valueOf(numOfJugs);
		int bob8 = (int) (profit * numOfJugs);
		String f = String.valueOf(bob8);

		hours = millis / (1000 * 60 * 60);

		millis -= hours * (1000 * 60 * 60);

		minutes = millis / (1000 * 60);

		millis -= minutes * (1000 * 60);

		seconds = millis / 1000;

		Graphics2D g = (Graphics2D) g1;
		g.setColor(Color.YELLOW);
		g.drawLine(Mouse.getX() - 5, Mouse.getY() - 5, Mouse.getX() + 5,
				Mouse.getY() + 5);
		g.drawLine(Mouse.getX() - 5, Mouse.getY() + 5, Mouse.getX() + 5,
				Mouse.getY() - 5);
		g.setColor(color1);
		g.fillRect(547, 255, 190, 260);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRect(547, 255, 190, 260);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString("EchoWine", 605, 275);
		g.drawString("Jugs Made: " + numOfJugs, 561, 360);
		g.setColor(color2);
		g.drawLine(605, 277, 684, 277);
		g.setColor(color3);
		g.drawString("Jugs Per Hour: " + u, 562, 414);
		g.drawString("Profit:" + bob8, 562, 466);
		g.drawString("Profit Per Hour:" + bob7, 560, 511);
		g.drawString("Time Running:" + hours + ":" + minutes + ":" + seconds,
				560, 310);
	}

}
