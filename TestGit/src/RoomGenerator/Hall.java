package RoomGenerator;

public class Hall {
	// private static final int HALL_WIDTH = 2;
	private Hall connectingHall;
	private int startX;
	private int startY;
	private int endX;
	private int endY;

	private boolean isVertical;

	public Hall(int x1, int y1, int targetX, int targetY, boolean isVertical) {
		startX = x1;
		startY = y1;
		this.isVertical = isVertical;
		makeHall(targetX, targetY);
		makeConnectingHall(targetX, targetY);
	}

	private Hall(int x1, int y1, int endX, int endY, boolean isVertical, Hall neighbor) {
		startX = x1;
		startY = y1;
		this.endX = endX;
		this.endY = endY;
		this.isVertical = isVertical;
		connectingHall = neighbor;
	}

	/*
	 * Creates the neighboring hall that is the opposite type of the first one
	 * made. If first hall is vertical, the neighbor is horizontal, and vice
	 * versa.
	 */
	private void makeConnectingHall(int targetX, int targetY) {
		connectingHall = new Hall(this.endX, this.endY, targetX, targetY, !isVertical, this);
	}

	private void makeHall(int targetX, int targetY) {
		if (isVertical) {
			endX = startX;
			endY = targetY;
		} else {
			endX = targetX;
			endY = startY;
		}
	}

	public void printCoordinates() {
		System.out.println("startX: " + startX + " startY: " + startY + " endX: " + endX + " endY: " + endY);
	}

	public int getStartY() {
		return startY;
	}

	public int getStartX() {
		return startX;
	}

	public boolean isVertical() {
		return isVertical;
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public Hall getNeighbor() {
		return connectingHall;
	}
}
