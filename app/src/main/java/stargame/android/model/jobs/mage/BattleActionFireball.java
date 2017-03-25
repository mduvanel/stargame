package stargame.android.model.jobs.mage;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleCell;
import stargame.android.model.BattleDialog;
import stargame.android.model.BattleUnit;
import stargame.android.model.RangeBattleAction;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Logger;
import stargame.android.util.RandomGenerator;


/**
 * Class BattleActionFireball implements the Fireball action for mages
 *
 * @author Duduche
 */
public class BattleActionFireball extends RangeBattleAction implements ISavable
{
    private int mDamage;

    private static final String M_DAMAGE = "Damage";

    public int GetDamage()
    {
        return mDamage;
    }

    private BattleActionFireball()
    {
        super();
        mDamage = -1;
    }

    BattleActionFireball( Battle oBattle, BattleUnit oUnit )
    {
        super( oBattle, oUnit );
        mActionType = R.string.fireball_action;
        mDamage = 0;

        InitValidArray();
    }

    @Override
    public boolean SetTarget( BattleCell oCell )
    {
        if ( IsValidTarget( oCell.GetPos() ) &&
                oCell.GetUnit() != null &&
                oCell.GetUnit().IsTargetable() ) // invisible units can't be attacked
        {
            mVecTargets.set( 0, new ActionTarget( oCell ) );
            return true;
        }

        return false;
    }

    public boolean CanExecuteAction()
    {
        return ( mVecTargets.get( 0 ) != null );
    }

    @Override
    public void ExecuteAction()
    {
        // Get the target
        ActionTarget oTarget = mVecTargets.get( 0 );

        // Check if attack lands: compute chance of hitting the target
        if ( RandomGenerator.GetInstance().GetRandom( 1, 100 ) <=
                mSourceUnit.GetMagicHitChance( oTarget.mCell.GetUnit() ) )
        {
            // Get fireball damage amount
            double dFinalDamage = Math.max(
                    1, mSourceUnit.GetUnit().GetResultingAttributes().GetMagicPower() -
                            oTarget.mCell.GetUnit().GetUnit().GetResultingAttributes().GetMagicDef() );

            // Check for critical damage
            if ( RandomGenerator.GetInstance().GetRandom( 1, 100 ) <=
                    mCritChance )
            {
                dFinalDamage *= 1.5;
                oTarget.mActionStatus = ActionStatus.STATUS_CRITICAL;
            }
            else
            {
                oTarget.mActionStatus = ActionStatus.STATUS_SUCCESS;
            }

            // Add random portion to damage (10% of base damage)
            dFinalDamage = RandomGenerator.GetInstance().GetRandom(
                    dFinalDamage - ( dFinalDamage / 10.0 ),
                    dFinalDamage + ( dFinalDamage / 10.0 ) );

            mDamage = oTarget.mCell.GetUnit().ApplyDamage( dFinalDamage );
        }
        else
        {
            mDamage = 0;
            oTarget.mActionStatus = ActionStatus.STATUS_MISS;
        }

        BattleDialog oDialog = new BattleDialog();
        oDialog.AddDialog( R.string.incantation_fireball, mSourceUnit );
        try
        {
            mBattle.SetCurrentDialog( oDialog );
        }
        catch ( Exception e )
        {
            Logger.e( "Attempt to set new dialog while another one is still running!" );
        }

        NotifyActionUpdate();
        ResetValidArray();
        mSourceUnit.SetActionPerformed();
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectStore, oGlobalStore );

        oObjectStore.putInt( M_DAMAGE, mDamage );
    }

    public static BattleActionFireball loadState( IStorage oGlobalStore,
                                                  String strObjKey )
    {
        IStorage oObjectBundle = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, BattleActionFireball.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        BattleActionFireball oAction = new BattleActionFireball();

        // Load parent info
        oAction.LoadBattleActionState( oObjectBundle, oGlobalStore );

        oAction.mDamage = oObjectBundle.getInt( M_DAMAGE );

        return oAction;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
