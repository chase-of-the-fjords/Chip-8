package dev.chasepeterson.chip;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.swing.JFrame;

public class Emulator extends Canvas implements Runnable {
	
	public JFrame frame;
	public Thread thread;
	
	public BufferedImage image;
	public int[] pixels;
	
	public Keyboard keyboard;
	
	public CPU cpu = new CPU();
	public Clock clock = new Clock(500);
	
	public Screen screen = new Screen();
	
	public synchronized void start() {
		
		try {
			File file = new File(getClass().getResource("/roms/Tron.ch8").toURI());
			byte[] bytes = Files.readAllBytes(file.toPath());
			for (int i = 0; i < bytes.length; i += 1) {
				cpu.setMemoryValue((short) (0x200 + i), String.format("%2s", Integer.toHexString(bytes[i] & 0xFF)).replace(' ', '0'));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		setPreferredSize(new Dimension(64 * 15, 32 * 15));
		
		frame = new JFrame();
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		frame.setTitle("CHIP-8 Emulator");
		frame.setVisible(true);
		
		image = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
		
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		keyboard = new Keyboard();
		
		addKeyListener(keyboard);

		cpu.passScreen(screen);
		cpu.passKeyboard(keyboard);
		
		clock.start();
		
		thread = new Thread(this, "CHIP-8 Emulator");
		thread.start();
		
	}
	
	public void run() {
		
		while (true) {
			render();
			while (clock.iterate()) {
				cpu.runIteration();
			}
		}
		
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for (int i = 0; i < screen.pixels.length; i++) {
			if (screen.pixels[i] == true) pixels[i] = 0xFFFFFF;
			else pixels[i] = 0x000000;
		}
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		
		bs.show();
	}
	
	public void update() {
		
	}
	
	public static void main(String[] args) {
		
		Emulator emulator = new Emulator();
		emulator.start();
		
	}
	
}
