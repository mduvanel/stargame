package stargame.android.controller;

/**
 * IDPadPeeker returns a DPadZone hint about what direction is
 * currently indicated by the user
 */
public interface IDPadPeeker
{
    enum DPadZone
    {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        CENTER,
        NONE
    }

    ;

    public DPadZone PeekZone();
}
