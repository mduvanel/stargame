package stargame.android.model;

import java.util.Vector;

import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

/**
 * This class holds the information about a particular dialog going on.
 *
 * @author Duduche
 */
public class BattleDialog implements ISavable
{
    private static class Dialog implements ISavable
    {
        int mTextID;
        private static final String M_TEXT_ID = "Text";

        BattleUnit mTalkingUnit;
        private static final String M_UNIT = "Unit";

        Dialog()
        {
            mTextID = -1;
            mTalkingUnit = null;
        }

        public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
        {
            oObjectStore.putInt( M_TEXT_ID, mTextID );

            String strObjKey = SavableHelper.saveInStore( mTalkingUnit,
                                                          oGlobalStore );
            oObjectStore.putString( M_UNIT, strObjKey );
        }

        public static Dialog loadState( IStorage oGlobalStore, String strObjKey )
        {
            IStorage oObjectStore = SavableHelper.retrieveStore(
                    oGlobalStore, strObjKey,
                    Dialog.class.getName() );

            if ( oObjectStore == null )
            {
                return null;
            }

            Dialog oDialog = new Dialog();

            oDialog.mTextID = oObjectStore.getInt( M_TEXT_ID );

            String strKey = oObjectStore.getString( M_UNIT );
            oDialog.mTalkingUnit = BattleUnit.loadState( oGlobalStore, strKey );

            return oDialog;
        }

        public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
        {
            return loadState( oGlobalStore, strObjKey );
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
     *
     * @return true if there is a next dialog, false otherwise.
     */
    boolean NextDialog()
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

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        String[] astrIds = SavableHelper.saveCollectionInStore( mVecDialogs,
                                                                oGlobalStore );
        oObjectStore.putStringArray( M_VEC_DIALOGS, astrIds );

        oObjectStore.putInt( M_CURRENT_DIALOG, mCurrentDialog );
        oObjectStore.putBoolean( M_CHANGED, mChanged );
    }

    public static BattleDialog loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey,
                BattleDialog.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        BattleDialog oDialog = new BattleDialog();

        oDialog.mChanged = oObjectStore.getBoolean( M_CHANGED );
        oDialog.mCurrentDialog = oObjectStore.getInt( M_CURRENT_DIALOG );

        String[] astrIds = oObjectStore.getStringArray( M_VEC_DIALOGS );
        SavableHelper.loadCollectionFromStore( oDialog.mVecDialogs, astrIds,
                                               oGlobalStore,
                                               new Dialog() );

        return oDialog;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
