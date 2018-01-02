import org.junit.Test;

import stargame.android.util.Position;

import static org.junit.Assert.*;

public class PositionTest
{
    @Test
    public void positionEquals() throws Exception
    {
        Position p1 = new Position( 0, 0 );
        Position p2 = new Position( 1, 1 );
        assertTrue( p1.Equals( p1 ) );
        assertFalse( p2.Equals( p1 ) );
        assertFalse( p1.Equals( p2 ) );
    }

    @Test
    public void positionCopyConstructor() throws Exception
    {
        Position p1 = new Position( 0, 0 );
        Position p2 = new Position( p1 );
        assertTrue( p2.Equals( p1 ) );
        assertTrue( p1.Equals( p2 ) );
    }

    @Test
    public void positionSetPos() throws Exception
    {
        Position p1 = new Position( 0, 0 );
        Position p2 = new Position( p1 );

        p2.SetPos( 1, 1 );
        assertFalse( p2.Equals( p1 ) );
        assertFalse( p1.Equals( p2 ) );
    }

    @Test
    public void positionIsInside() throws Exception
    {
        Position p1 = new Position( 0, 0 );

        assertFalse( p1.IsInside( 1, 1, 2, -1 ) );
        assertFalse( p1.IsInside( -2, 1, -1, -1 ) );

        assertTrue( p1.IsInside( 0, 0, 1, 1 ) );
    }
}