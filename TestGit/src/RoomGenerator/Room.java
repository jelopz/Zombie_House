package RoomGenerator;

public class Room {

	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private int width;
	private int height;
	private int centerX;
	private int centerY;
	
	private boolean isConnected;

	public Room(int x, int y, int w, int h) {
		startX = x;
		startY = y;
		width = w;
		height = h;
		endX = x + h;
		endY = y + h;
		
		isConnected = false;

		centerX = (startX + endX) / 2;
		centerY = (startY + endY) / 2;
	}

	public void printCoordinates() {
		System.out.println("startX:  " + startX + "  startY:  " + startY + "  endX: " + endX + "   endY: " + endY);
		System.out.println("width: " + width + "   height:   " + height);
	}

	public boolean intersects(Room room) {
		return (startX < (room.startX + room.width)) && ((startX + width) > room.startX)
				&& (startY < (room.startY + room.height))
				&& ((startY + height) > room.startY);
	}

	public int getWidth() {
		return width;
	}

	public int getStartY() {
		return startY;
	}

	public int getHeight() {
		return height;
	}

	public int getStartX() {
		return startX;
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public int getCenterX() {
		return centerX;
	}
	public int getCenterY() {
		return centerY;
	}

}
