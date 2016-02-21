package RoomGenerator;

public class Hall {
//	private static final int HALL_WIDTH = 2;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private int width;
	private int height;
	private boolean isVertical;

	public Hall(int x1, int y1, int targetX, int targetY, boolean isVertical) {
		startX = x1;
		startY = y1;
		this.isVertical = isVertical;
		makeHall(targetX, targetY);
	}

	private void makeHall(int targetX, int targetY) {
		if (isVertical) {
			endY = targetY;
			endX = getStartX();
			width = 1;
			height = startY - endY;
		} else {
			endX = targetX;
			endY = getStartY();
			width = getStartX() - endX;
			height = 1;
		}
	}

	public void printCoordinates() {
		System.out.println(
				"startX:  " + startX + "  startY:  " + startY + "  endX: " + endX + "   endY: " + endY);
		System.out.println("WIDTH:  " + (startX - endX) + "    HEIGHT : " + (startY - endY));
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
	
	public int getEndX(){
		return endX;
	}
	
	public int getEndY(){
		return endY;
	}

	public int getWidth() {
		return width;
	}
}
