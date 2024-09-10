package dev.chasepeterson.chip;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

	public boolean[] pressed = new boolean[16];
	
	public int[] keycodes = new int[16];
	
	public Keyboard() {
		super();
		
		keycodes[0] = KeyEvent.VK_X;
		keycodes[1] = KeyEvent.VK_1;
		keycodes[2] = KeyEvent.VK_1;
		keycodes[3] = KeyEvent.VK_1;
		keycodes[4] = KeyEvent.VK_Q;
		keycodes[5] = KeyEvent.VK_W;
		keycodes[6] = KeyEvent.VK_E;
		keycodes[7] = KeyEvent.VK_A;
		keycodes[8] = KeyEvent.VK_S;
		keycodes[9] = KeyEvent.VK_D;
		keycodes[10] = KeyEvent.VK_Z;
		keycodes[11] = KeyEvent.VK_C;
		keycodes[12] = KeyEvent.VK_4;
		keycodes[13] = KeyEvent.VK_R;
		keycodes[14] = KeyEvent.VK_F;
		keycodes[15] = KeyEvent.VK_V;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		for (int i = 0; i < 16; i++) {
			
			if (e.getKeyCode() == keycodes[i]) pressed[i] = true;
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (int i = 0; i < 16; i++) {
			
			if (e.getKeyCode() == keycodes[i]) pressed[i] = false;
			
		}
	}

}
