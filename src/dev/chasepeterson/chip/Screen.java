package dev.chasepeterson.chip;

public class Screen {

	public boolean[] pixels = new boolean[64 * 32];

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = false;
		}
	}

	public void fill() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = true;
		}
	}

	public boolean drawByte(short vx, short vy, ChipByte b) {

		boolean res = false;
		
		for (int i = 0; i < 8; i++) {
			
			int pixel = vx + (vy * 64) + i;
			
			if (pixel < 64 * 32) {
				
				if (b.getBit(i)) {
					
					pixels[vx + (vy * 64) + i] = !pixels[vx + (vy * 64) + i];
					
					if (!pixels[vx + (vy * 64) + i]) res = true;
					
				}
			
			} else {
				
				continue;
				
			}
			
		}

		return res;

	}

}
