package dev.chasepeterson.chip;

import java.awt.Toolkit;
import java.util.Stack;

public class CPU {
	
	public short PC = 0x200;
	public short I = 0x0;
	
	public short[] V = new short[16];
	
	public ChipByte[] memory = new ChipByte[4096];
	
	public Screen screen;
	public Keyboard keyboard;
	
	public Timer delay = new Timer(60);
	
	public Stack<Short> subroutines = new Stack<Short>();
	
	public CPU() {
		
		for (int i = 0; i < 4096; i++) {
			
			memory[i] = new ChipByte();
			
		}
		
		this.insertMemory((short) (0x050 + 0), new String[] {"f0", "90", "90", "90", "f0"}); // 0
		this.insertMemory((short) (0x050 + 5), new String[] {"20", "60", "20", "20", "70"}); // 1
		this.insertMemory((short) (0x050 + 10), new String[] {"f0", "10", "f0", "80", "f0"}); // 2
		this.insertMemory((short) (0x050 + 15), new String[] {"f0", "10", "f0", "10", "f0"}); // 3
		this.insertMemory((short) (0x050 + 20), new String[] {"90", "90", "f0", "10", "10"}); // 4
		this.insertMemory((short) (0x050 + 25), new String[] {"f0", "80", "f0", "10", "f0"}); // 5
		this.insertMemory((short) (0x050 + 30), new String[] {"f0", "80", "f0", "90", "f0"}); // 6
		this.insertMemory((short) (0x050 + 35), new String[] {"f0", "10", "20", "40", "40"}); // 7
		this.insertMemory((short) (0x050 + 40), new String[] {"f0", "90", "f0", "90", "f0"}); // 8
		this.insertMemory((short) (0x050 + 45), new String[] {"f0", "90", "f0", "10", "f0"}); // 9
		this.insertMemory((short) (0x050 + 50), new String[] {"f0", "90", "f0", "90", "90"}); // A
		this.insertMemory((short) (0x050 + 55), new String[] {"e0", "90", "e0", "90", "e0"}); // B
		this.insertMemory((short) (0x050 + 60), new String[] {"f0", "80", "80", "80", "f0"}); // C
		this.insertMemory((short) (0x050 + 65), new String[] {"e0", "90", "90", "90", "e0"}); // D
		this.insertMemory((short) (0x050 + 70), new String[] {"f0", "80", "f0", "80", "f0"}); // E
		this.insertMemory((short) (0x050 + 75), new String[] {"f0", "80", "f0", "80", "80"}); // F
		
	}
	
	public void passScreen(Screen screen) {
		this.screen = screen;
	}
	
	public void passKeyboard(Keyboard keyboard) {
		this.keyboard = keyboard;
	}
	
	public void runIteration() {
		Instruction instruction = new Instruction(memory[PC], memory[PC + 1]);
//		System.out.println(Integer.toHexString(PC) + " - " + instruction.toString());
		
		if (delay.active) delay.iterate();

		// 00E0 - CLEAR
		if (instruction.matches("00E0")) {
			
			screen.clear();
			PC += 2;
			
			return;
			
		}

		// 00EE - RETURN (end a subroutine)
		if (instruction.matches("00EE")) {
			
			PC = subroutines.pop();
			
			return;
			
		}

		// 1NNN - JUMP to NNN
		if (instruction.matches("1NNN")) {
			
			PC = instruction.getValue(1, 4);
			
			return;
			
		}

		// 2NNN - Call Subroutine
		if (instruction.matches("2NNN")) {
			
			subroutines.add((short) (PC + 2));
			PC = instruction.getValue(1, 4);
			
			return;
			
		}
		
		// 3XNN - If vx != NN then
		if (instruction.matches("3XNN")) {
			
			short register = instruction.getValue(1, 2);
			
			if (V[register] != instruction.getValue(2, 4)) {
				
				PC += 2;
				
			} else {
				
				PC += 4;
				
			}
			
			return;
			
		}
		
		// 4XNN - If vx == NN then
		if (instruction.matches("4XNN")) {
			
			short register = instruction.getValue(1, 2);
			
			if (V[register] == instruction.getValue(2, 4)) {
				
				PC += 2;
				
			} else {
				
				PC += 4;
				
			}
			
			return;
			
		}
		
		// 5XY0 - If vx != vy then
		if (instruction.matches("5XY0")) {
			
			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);
			
			if (V[register1] != V[register2]) {
				
				PC += 2;
				
			} else {
				
				PC += 4;
				
			}
			
			return;
			
		}

		// 6XNN - vx := NN
		if (instruction.matches("6XNN")) {
			
			short register = instruction.getValue(1, 2);
			
			V[register] = instruction.getValue(2, 4);
			
			PC += 2;
			
			return;
			
		}
		
		// 7XNN - vx += NN
		if (instruction.matches("7XNN")) {
			
			short register = instruction.getValue(1, 2);
			
			V[register] += instruction.getValue(2, 4);
			while (V[register] >= 0x100) V[register] -= 0x100;
			
			PC += 2;
			
			return;
			
		}
		
		// 8XY0 - vx := vy
		if (instruction.matches("8XY0")) {
			
			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);
			
			V[register1] = V[register2];
			
			PC += 2;
			
			return;
			
		}
		
		// 8XY1 - vx |= vy
		if (instruction.matches("8XY1")) {
			
			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);
			
			V[register1] |= V[register2];
			
			PC += 2;
			
			return;
			
		}
		
		// 8XY2 - vx &= vy
		if (instruction.matches("8XY2")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			V[register1] &= V[register2];

			PC += 2;
			
			return;

		}
		
		// 8XY3 - vx ^= vy
		if (instruction.matches("8XY3")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			V[register1] ^= V[register2];

			PC += 2;

			return;

		}
		
		// 8XY4 - vx += vy
		if (instruction.matches("8XY4")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			V[register1] += V[register2];
			
			V[0xf] = 0;
			
			if (V[register1] > 0xff) {
				V[0xf] = 1;
				V[register1] %= 0x100;
			}

			PC += 2;

			return;

		}
		
		// 8XY5
		if (instruction.matches("8XY5")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			V[register1] -= V[register2];
			
			V[0xf] = 1;
			
			while (V[register1] < 0) {
				V[0xf] = 0;
				V[register1] += 0x100;
			}

			PC += 2;

			return;

		}
		
		// 8XY6
		if (instruction.matches("8XY6")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			V[0xf] = (short) (V[register2] % 2);
			
			V[register1] = (short) (V[register2] >> 1);
			
			PC += 2;

			return;

		}
		
		// 8XY7
		if (instruction.matches("8XY7")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			V[0xf] = V[register2] < V[register1] ? (short) 1 : 0;
			
			V[register1] = (short) (V[register2] - V[register1]);
			
			while (V[register1] < 0) {
				
				V[register1] += 0x100;
				
			}
			
			PC += 2;

			return;

		}
		
		// 8XYE
		if (instruction.matches("8XYE")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			V[0xf] = V[register2] > 0x800 ? (short) 1 : 0;
			
			V[register1] = (short) (V[register2] << 1);
			
			PC += 2;

			return;

		}
		
		// 9XY0 - If vx == vy then
		if (instruction.matches("9XY0")) {

			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);

			if (V[register1] == V[register2]) {

				PC += 2;

			} else {

				PC += 4;

			}

			return;

		}
		
		// ANNN
		if (instruction.matches("ANNN")) {
			
			I = instruction.getValue(1, 4);
			
			PC += 2;
			
			return;
			
		}
		
		// BNNN
		if (instruction.matches("BNNN")) {

			PC = (short) (instruction.getValue(1, 4) + V[0]);

			return;

		}

		// CXNN
		if (instruction.matches("CXNN")) {

			short register = instruction.getValue(1, 2);

			V[register] = (short) (((short) Math.floor(Math.random() * 256)) & instruction.getValue(2, 4));
			
			PC += 2;

			return;

		}

		// DXYN - Draw Sprite
		if (instruction.matches("DXYN")) {
			
			short register1 = instruction.getValue(1, 2);
			short register2 = instruction.getValue(2, 3);
			
			short vx = V[register1];
			short vy = V[register2];
			
			short height = instruction.getValue(3, 4);
			
			boolean changed = false;
			for (int i = 0; i < height; i++) {
				if (screen.drawByte(vx, (short) (vy + i), memory[I + i])) changed = true;
			}
			
			if (changed) V[0xf] = 1;
			else V[0xf] = 0;
			
			PC += 2;
			
			return;
			
		}
		
		// EX9E
		if (instruction.matches("EX9E")) {
			
			short register = instruction.getValue(1, 2);
			
			if (!keyboard.pressed[V[register]]) {
				
				PC += 2;
				
			} else {
				
				PC += 4;
				
			}
			
			return;
			
		}
		
		// EXA1
		if (instruction.matches("EXA1")) {
			
			short register = instruction.getValue(1, 2);
			
			if (keyboard.pressed[V[register]]) {
				
				PC += 2;
				
			} else {
				
				PC += 4;
				
			}
			
			return;
			
		}
		
		// FX07 - Set register to timer
		if (instruction.matches("FX07")) {
			
			short register = instruction.getValue(1, 2);
			
			V[register] = delay.getValue();
			
			PC += 2;
			
			return;
			
		}
		
		// FX0A
		if (instruction.matches("FX0A")) {
			
			short register = instruction.getValue(1, 2);
			
			for (int i = 0; i < 16; i++) {
				
				if (keyboard.pressed[i]) {
					
					V[register] = (short) i;
					
					PC += 2;
					
					return;
					
				}
				
			}
			
			return;
			
		}
		
		// FX15 - delay := vx
		if (instruction.matches("FX15")) {
			
			short register = instruction.getValue(1, 2);
			
			delay.start(V[register]);
			
			PC += 2;
			
			return;
			
		}
		
		// FX18
		if (instruction.matches("FX18")) {
			
			System.out.println("We're not doing sounds right now.");
			
			PC += 2;
			
			return;
			
		}
		
		// FX1E
		if (instruction.matches("FX1E")) {

			short register = instruction.getValue(1, 2);
			
			I += V[register];

			PC += 2;

			return;

		}
		
		// FX29
		if (instruction.matches("FX29")) {

			short register = instruction.getValue(1, 2);
			
			I = (short) (0x050 + (V[register] * 5));

			PC += 2;

			return;

		}

		// FX33
		if (instruction.matches("FX33")) {
			
			short register = instruction.getValue(1, 2);
			
			short value = V[register];
			
			short digit0 = (short) Math.floor(value / 100);
			short digit1 = (short) Math.floor((value % 100) / 10);
			short digit2 = (short) (value % 10);
			
			memory[I] = new ChipByte(digit0);
			memory[I + 1] = new ChipByte(digit1);
			memory[I + 2] = new ChipByte(digit2);
			
			PC += 2;
			
			return;
			
		}
		
		// FX55
		if (instruction.matches("FX55")) {
			
			short register = instruction.getValue(1, 2);
			
			for (int i = 0; i <= register; i++) {
				
				memory[I + i] = new ChipByte(V[i]);
				
			}
			
			PC += 2;
			
			return;
			
		}
		
		// FX65
		if (instruction.matches("FX65")) {
			
			short register = instruction.getValue(1, 2);
			
			for (int i = 0; i <= register; i++) {
				
				V[i] = memory[I + i].getValue();
				
			}
			
			PC += 2;
			
			return;
			
		}
		
		System.out.println("Instruction " + instruction.toString() + " not found.");
		
		PC += 2;
		
	}
	
	public void setMemoryValue(short address, String b) {
		
		memory[address] = new ChipByte(b.charAt(0), b.charAt(1));
		
	}
	
	public void insertMemory(short address, String[] values) {
		
		for (int i = 0; i < values.length; i++) {
			
			memory[address + i] = new ChipByte(values[i].charAt(0), values[i].charAt(1));
			
		}
		
	}
	
}

class ChipByte {
	
	byte v1, v2;
	
	public ChipByte() {
		this.v1 = 0;
		this.v2 = 0;
	}
	
	public ChipByte(short value) {
		this.v1 = (byte) (value >> 4);
		this.v2 = (byte) (value % 16);
	}
	
	public ChipByte(char v1, char v2) {
		this.v1 = getByte(v1);
		this.v2 = getByte(v2);
	}
	
	public static byte getByte(char b) {
		if (b == '0') return 0x0;
		if (b == '1') return 0x1;
		if (b == '2') return 0x2;
		if (b == '3') return 0x3;
		if (b == '4') return 0x4;
		if (b == '5') return 0x5;
		if (b == '6') return 0x6;
		if (b == '7') return 0x7;
		if (b == '8') return 0x8;
		if (b == '9') return 0x9;
		if (b == 'A' || b == 'a') return 0xA;
		if (b == 'B' || b == 'b') return 0xB;
		if (b == 'C' || b == 'c') return 0xC;
		if (b == 'D' || b == 'd') return 0xD;
		if (b == 'E' || b == 'e') return 0xE;
		if (b == 'F' || b == 'f') return 0xF;
		return 0x0;
	}
	
	public boolean getBit(int bit) {
		
		if (bit >= 0 && bit < 4) {
			
			return ((v1 >> (3 - bit)) & 1) == 1;
			
		}
		
		if (bit >= 4 && bit < 8) {
			
			return ((v2 >> (7 - bit)) & 1) == 1;
			
		}
		
		return false;
		
	}
	
	public short getValue() {
		return (short) ((v1 * 16) + v2);
	}
	
	public String toString() {
		String b1String = "", b2String = "";
		
		if (v1 == 0) b1String = "0";
		else if (v1 == 1) b1String = "1";
		else if (v1 == 2) b1String = "2";
		else if (v1 == 3) b1String = "3";
		else if (v1 == 4) b1String = "4";
		else if (v1 == 5) b1String = "5";
		else if (v1 == 6) b1String = "6";
		else if (v1 == 7) b1String = "7";
		else if (v1 == 8) b1String = "8";
		else if (v1 == 9) b1String = "9";
		else if (v1 == 10) b1String = "A";
		else if (v1 == 11) b1String = "B";
		else if (v1 == 12) b1String = "C";
		else if (v1 == 13) b1String = "D";
		else if (v1 == 14) b1String = "E";
		else if (v1 == 15) b1String = "F";
		
		if (v2 == 0) b2String = "0";
		else if (v2 == 1) b2String = "1";
		else if (v2 == 2) b2String = "2";
		else if (v2 == 3) b2String = "3";
		else if (v2 == 4) b2String = "4";
		else if (v2 == 5) b2String = "5";
		else if (v2 == 6) b2String = "6";
		else if (v2 == 7) b2String = "7";
		else if (v2 == 8) b2String = "8";
		else if (v2 == 9) b2String = "9";
		else if (v2 == 10) b2String = "A";
		else if (v2 == 11) b2String = "B";
		else if (v2 == 12) b2String = "C";
		else if (v2 == 13) b2String = "D";
		else if (v2 == 14) b2String = "E";
		else if (v2 == 15) b2String = "F";
		
		return b1String + b2String;
	}
	
}

class Instruction {
	
	ChipByte b1, b2;
	
	public Instruction(ChipByte b1, ChipByte b2) {
		this.b1 = b1;
		this.b2 = b2;
	}
	
	public byte getDigit(int digit) {
		if (digit == 0) {
			return b1.v1;
		} else if (digit == 1) {
			return b1.v2;
		} else if (digit == 2) {
			return b2.v1;
		} else if (digit == 3) {
			return b2.v2;
		}
		return 0;
	}
	
	public short getValue(int start, int end) {
		short result = 0;
		for (int i = start; i < end; i++) {
			result *= 16;
			result += getDigit(i);
		}
		return result;
	}
	
	public boolean matches(String input) {
		for (int i = 0; i < 4; i++) {
			char character = input.charAt(i);
			if (character == '*' || character == 'X' || character == 'Y' || character == 'N') continue;
			byte digit = ChipByte.getByte(character);
			if (getDigit(i) != digit) return false;
		}
		return true;
	}
	
	public String toString() {
		return b1.toString() + b2.toString();
	}
	
}
