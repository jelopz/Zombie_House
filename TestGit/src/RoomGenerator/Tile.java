package RoomGenerator;

public class Tile {
	private char tileType;
	
	public Tile(char c)
	{
		tileType = c;
	}
	
	public char getTileType()
	{
		return tileType;
	}
	
	public void setTileType(char c)
	{
		tileType = c;
	}
}
