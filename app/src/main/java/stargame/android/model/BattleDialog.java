package stargame.android.model;

import android.os.Bundle;

import java.util.Vector;

import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;

/**
 * This class holds the information about a particular dialog going on.
 *
 * @author Duduche
 */
public class BattleDialog implements ISavable
{
    protected static class Dialog implements ISavable
    {
        public int mTextID;
        private static final String M_TEXT_ID = "Text";

        public BattleUnit mTalkingUnit;
        private static final String M_UNIT = "Unit";

        public Dialog()
        {
            mTextID = -1;
            mTalkingUnit = null;
        }

        public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
        {
            oObjectMap.putInt( M_TEXT_ID, mTextID );

            String strObjKey = SavableHelper.saveInMap( mTalkingUnit, oGlobalMap );
            oObjectMap.putString( M_UNIT, strObjKey );
        }

        public static Dialog loadState( Bundle oGlobalMap, String strObjKey )
        {
            Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                                 Dialog.class.getName() );

            if ( oObjectBundle == null )
            {
                return null;
            }

            Dialog oDialog = new Dialog();

            oDialog.mTextID = oObjectBundle.getInt( M_TEXT_ID );

            String strKey = oObjectBundle.getString( M_UNIT );
            oDialog.mTalkingUnit = BattleUnit.loadState( oGlobalMap, strKey );

            return oDialog;
        }

        public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
        {
            return loadState( oGlobalMap, strObjKey );
        }
    }

    /**
     * List of Dialogs
     */
    private Vector< Dialog > mVecDialogs;

    private static final String M_VEC_DIALOGS = "Dialogs";

    /**
     * The currently active dialog
     */
    private int mCurrentDialog;

    private static final String M_CURRENT_DIALOG = "CurrentDialog";

    /**
     * Flag that tells if the object changed since last time the flag was cleared
     */
    private boolean mChanged;

    private static final String M_CHANGED = "Changed";

    public boolean HasChanged()
    {
        return mChanged;
    }

    public void ClearChanged()
    {
        mChanged = false;
    }

    public BattleDialog()
    {
        mVecDialogs = new Vector< Dialog >();
        mCurrentDialog = 0;
        mChanged = false;
    }

    public void AddDialog( int iTextID, BattleUnit oUnit )
    {
        Dialog oNewDialog = new Dialog();
        oNewDialog.mTextID = iTextID;
        oNewDialog.mTalkingUnit = oUnit;
        mVecDialogs.add( oNewDialog );
        mChanged = true;
    }

    public boolean IsDialogFinished()
    {
        return ( mCurrentDialog == mVecDialogs.size() );
    }

    /**
     * Advance to next dialog.
     * Returns true if there is a next dialog, false otherwise.
     *
     * @return
     */
    public boolean NextDialog()
    {
        if ( mCurrentDialog < mVecDialogs.size() )
        {
            ++mCurrentDialog;
            mChanged = true;
            return true;
        }
        return false;
    }

    public int GetCurrentDialogID()
    {
        if ( mCurrentDialog < mVecDialogs.size() )
        {
            return mVecDialogs.get( mCurrentDialog ).mTextID;
        }
        return -1;
    }

    public BattleUnit GetCurrentDialogUnit()
    {
        if ( mCurrentDialog < mVecDialogs.size() )
        {
            return mVecDialogs.get( mCurrentDialog ).mTalkingUnit;
        }
        return null;
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        String[] astrIds = SavableHelper.saveCollectionInMap( mVecDialogs, oGlobalMap );
        oObjectMap.putStringArray( M_VEC_DIALOGS, astrIds );

        oObjectMap.putInt( M_CURRENT_DIALOG, mCurrentDialog );
        oObjectMap.putBoolean( M_CHANGED, mChanged );
    }

    public static BattleDialog loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             BattleDialog.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        BattleDialog oDialog = new BattleDialog();

        oDialog.mChanged = oObjectBundle.getBoolean( M_CHANGED );
        oDialog.mCurrentDialog = oObjectBundle.getInt( M_CURRENT_DIALOG );

        String[] astrIds = oObjectBundle.getStringArray( M_VEC_DIALOGS );
        SavableHelper.loadCollectionFromMap( oDialog.mVecDialogs, astrIds, oGlobalMap,
                                             new Dialog() );

        return oDialog;
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }
}
