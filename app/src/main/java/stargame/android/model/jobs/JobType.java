package stargame.android.model.jobs;

public enum JobType
{
    TYPE_BARBARIAN,
    TYPE_ARCHER,
    TYPE_SOLDIER,
    TYPE_MAGE,
    TYPE_PRIEST,
    TYPE_ORACLE,
    TYPE_ROGUE;

    public String toString()
    {
        switch ( this )
        {
            case TYPE_BARBARIAN:
                return "barbarian";
            case TYPE_ARCHER:
                return "archer";
            case TYPE_SOLDIER:
                return "soldier";
            case TYPE_MAGE:
                return "mage";
            case TYPE_ROGUE:
                return "rogue";
            case TYPE_ORACLE:
                return "oracle";
            case TYPE_PRIEST:
                return "priest";
            default:
                return "";
        }
    }
}
