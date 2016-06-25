package stargame.android.storage;


import android.os.Bundle;

import java.util.Collection;

import stargame.android.util.Logger;
import stargame.android.util.Tree;
import stargame.android.util.TreeNode;

/**
 * Helper class to facilitate saving ISavable items.
 * Inside the main Bundle, every ISavable-derived class has its own
 * sub-bundle in which all class instances will be saved in their
 * own bundle:
 * <p/>
 * Base Bundle
 * - Class A Bundle
 * -- instance1 Bundle
 * -- instance2 Bundle
 * - Class B Bundle
 * -- instance1 Bundle
 * ...
 *
 * @author Duduche
 */
public class SavableHelper
{
    public static Bundle getClassBundle( ISavable oSavable, Bundle oMap )
    {
        // check if the class bundle exists already
        String strClassKey = oSavable.getClass().getName();
        Bundle oClassBundle = oMap.getBundle( strClassKey );
        if ( oClassBundle == null )
        {
            // Create it
            oClassBundle = new Bundle();
            oMap.putBundle( strClassKey, oClassBundle );
        }

        return oClassBundle;
    }

    public static String saveInMap( ISavable oSavable, Bundle oGlobalMap )
    {
        if ( null == oSavable )
        {
            return "";
        }

        Bundle oClassBundle = getClassBundle( oSavable, oGlobalMap );

        // check if the object is already saved into the map
        String strObjKey = oSavable.toString();
        if ( !oClassBundle.containsKey( strObjKey ) )
        {
            // first store the bundle (in case there is a reentrant call)
            Bundle oItemMap = new Bundle();
            oClassBundle.putBundle( strObjKey, oItemMap );

            // then save the object
            oSavable.saveState( oItemMap, oGlobalMap );
        }

        return strObjKey;
    }

    public static < T extends ISavable > String[] saveCollectionInMap( Collection< T > colSavable,
                                                                       Bundle oGlobalMap )
    {
        String[] astrIds = new String[ colSavable.size() ];
        int iCounter = 0;

        for ( ISavable oSavable : colSavable )
        {
            astrIds[ iCounter++ ] = saveInMap( oSavable, oGlobalMap );
        }

        return astrIds;
    }

    @SuppressWarnings( "unchecked" )
    public static < T extends ISavable > void loadCollectionFromMap( Collection< T > colSavable,
                                                                     String[] astrIds,
                                                                     Bundle oGlobalMap,
                                                                     T oTmpInstance )
    {
        colSavable.clear();

        for ( int i = 0; i < astrIds.length; ++i )
        {
            T oObject = null;
            if ( astrIds[ i ].length() > 0 )
            {
                // Ugly cast...
                oObject = ( T ) oTmpInstance.createInstance( oGlobalMap, astrIds[ i ] );
            }
            colSavable.add( oObject );
        }
    }

    public static < T extends ISavable > String[] saveArrayInMap( ISavable[] aSavables,
                                                                  Bundle oGlobalMap )
    {
        String[] astrIds = new String[ aSavables.length ];

        for ( int i = 0; i < aSavables.length; ++i )
        {
            astrIds[ i ] = saveInMap( aSavables[ i ], oGlobalMap );
        }

        return astrIds;
    }

    private static final String M_ROW_IDS = "Row";

    private static String GetRowName( int iIndex )
    {
        return String.format( "%s_%d", M_ROW_IDS, iIndex );
    }

    public static < T extends ISavable > Bundle saveBidimensionalArrayInMap( ISavable[][] aSavables,
                                                                             Bundle oGlobalMap )
    {
        Bundle oBundle = new Bundle();

        for ( int i = 0; i < aSavables.length; ++i )
        {
            String[] astrIds = saveArrayInMap( aSavables[ i ], oGlobalMap );
            oBundle.putStringArray( GetRowName( i ), astrIds );
        }

        return oBundle;
    }

    @SuppressWarnings( "unchecked" )
    public static < T extends ISavable > void loadBidimensionalArrayFromMap( T[][] aSavables,
                                                                             Bundle oObjectMap,
                                                                             Bundle oGlobalMap,
                                                                             T oTmpInstance )
    {
        for ( int i = 0; i < aSavables.length; ++i )
        {
            String[] astrIds = oObjectMap.getStringArray( GetRowName( i ) );

            for ( int j = 0; j < astrIds.length; ++j )
            {
                // Ugly cast...
                aSavables[ i ][ j ] = ( T ) oTmpInstance.createInstance( oGlobalMap, astrIds[ j ] );
            }
        }
    }

    private static final String M_TREE_DATA = "Data";
    private static final String M_TREE_CHILDREN = "Children";

    private static String GetChildName( int iIndex )
    {
        return String.format( "%s_%d", M_TREE_CHILDREN, iIndex );
    }

    public static < T extends ISavable > Bundle saveTreeInMap( Tree< T > oSavableTree,
                                                               Bundle oGlobalMap )
    {
        TreeNode< T > oNode = oSavableTree.GetRoot();
        Bundle oBundle = new Bundle();
        saveTreeNodeInMap( oNode, oBundle, oGlobalMap );
        return oBundle;
    }

    private static < T extends ISavable > void saveTreeNodeInMap( TreeNode< T > oNode,
                                                                  Bundle oObjectMap,
                                                                  Bundle oGlobalMap )
    {
        if ( oNode.GetData() != null )
        {
            String strKey = saveInMap( oNode.GetData(), oGlobalMap );
            oObjectMap.putString( M_TREE_DATA, strKey );
        }
        else
        {
            int iCount = 0;
            for ( TreeNode< T > oChildNode : oNode.GetChildren() )
            {
                Bundle oChildBundle = new Bundle();
                saveTreeNodeInMap( oChildNode, oChildBundle, oGlobalMap );
                oObjectMap.putBundle( GetChildName( iCount++ ), oChildBundle );
            }
        }
    }

    public static < T extends ISavable > Tree< T > loadTreeFromMap( Bundle oObjectMap,
                                                                    Bundle oGlobalMap,
                                                                    T oTmpInstance )
    {
        Tree< T > oSavableTree = new Tree< T >();

        TreeNode< T > oRootNode = loadTreeNodeFromMap( oObjectMap, oGlobalMap, oTmpInstance );

        for ( TreeNode< T > oChildNode : oRootNode.GetChildren() )
        {
            oSavableTree.GetRoot().AddChild( oChildNode );
        }

        return oSavableTree;
    }

    @SuppressWarnings( "unchecked" )
    private static < T extends ISavable > TreeNode< T > loadTreeNodeFromMap( Bundle oObjectMap,
                                                                             Bundle oGlobalMap,
                                                                             T oTmpInstance )
    {
        if ( oObjectMap.containsKey( M_TREE_DATA ) )
        {
            String strObjKey = oObjectMap.getString( M_TREE_DATA );
            // Ugly cast...
            return new TreeNode< T >( ( T ) oTmpInstance.createInstance( oGlobalMap, strObjKey ) );
        }
        else
        {
            TreeNode< T > oParentNode = new TreeNode< T >();
            int iCount = 0;
            boolean bContinue = true;

            do
            {
                String strChildName = GetChildName( iCount++ );
                Bundle oChildBundle = oObjectMap.getBundle( strChildName );
                if ( oChildBundle != null )
                {
                    TreeNode< T > oChildNode = loadTreeNodeFromMap( oChildBundle, oGlobalMap,
                                                                    oTmpInstance );
                    oParentNode.AddChild( oChildNode );
                }
                else
                {
                    bContinue = false;
                }
            } while ( bContinue );

            return oParentNode;
        }
    }

    public static Bundle retrieveBundle( Bundle oMap, String strObjKey, String strClassName )
    {
        Bundle oClassBundle = oMap.getBundle( strClassName );
        if ( oClassBundle != null )
        {
            return oClassBundle.getBundle( strObjKey );
        }

        Logger.e( String.format( "Failed to find bundle for instance %s of class %s", strObjKey,
                                 strClassName ) );
        return null;
    }
}
