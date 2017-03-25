package stargame.android.storage;


import java.util.Collection;
import stargame.android.util.Logger;
import stargame.android.util.Tree;
import stargame.android.util.TreeNode;

/**
 * Helper class to facilitate saving ISavable items.
 * Inside the main IStorage, every ISavable-derived class has its own
 * sub-store in which all class instances will be saved in their
 * own store:
 *
 * Base IStorage
 * - Class A IStorage
 * -- instance1 IStorage
 * -- instance2 IStorage
 * - Class B IStorage
 * -- instance1 IStorage
 * ...
 *
 * @author Duduche
 */
public class SavableHelper
{
    private static IStorageFactory mFactory;

    public static void setIStorageFactory(IStorageFactory oFactory)
    {
        mFactory = oFactory;
    }

    public static IStorage buildStore()
    {
        return mFactory.buildStorage();
    }

    private static IStorage getClassStore( ISavable oSavable, IStorage oStore )
    {
        // check if the class bundle exists already
        String strClassKey = oSavable.getClass().getName();
        IStorage oClassStore = oStore.getStore( strClassKey );
        if ( oClassStore == null )
        {
            // Create it
            oClassStore = buildStore();
            oStore.putStore( strClassKey, oClassStore );
        }

        return oClassStore;
    }

    public static String saveInStore( ISavable oSavable,
                                      IStorage oGlobalStore )
    {
        if ( null == oSavable )
        {
            return "";
        }

        IStorage oClassStore = getClassStore( oSavable, oGlobalStore );

        // check if the object is already saved into the map
        String strObjKey = oSavable.toString();
        if ( !oClassStore.containsKey( strObjKey ) )
        {
            // first store the bundle (in case there is a reentrant call)
            IStorage oItemStore = buildStore();
            oClassStore.putStore( strObjKey, oItemStore );

            // then save the object
            oSavable.saveState( oItemStore, oGlobalStore );
        }

        return strObjKey;
    }

    public static < T extends ISavable > String[] saveCollectionInStore(
            Collection< T > colSavable,
            IStorage oGlobalStore )
    {
        String[] astrIds = new String[ colSavable.size() ];
        int iCounter = 0;

        for ( ISavable oSavable : colSavable )
        {
            astrIds[ iCounter++ ] = saveInStore( oSavable, oGlobalStore );
        }

        return astrIds;
    }

    @SuppressWarnings( "unchecked" )
    public static < T extends ISavable > void loadCollectionFromStore(
            Collection< T > colSavable,
            String[] astrIds,
            IStorage oGlobalStore,
            T oTmpInstance )
    {
        colSavable.clear();

        for ( String strVal : astrIds )
        {
            T oObject = null;
            if ( strVal.length() > 0 )
            {
                // Ugly cast...
                oObject = ( T ) oTmpInstance.createInstance( oGlobalStore,
                                                             strVal );
            }
            colSavable.add( oObject );
        }
    }

    private static String[] saveArrayInStore(
            ISavable[] aSavables,
            IStorage oGlobalStore )
    {
        String[] astrIds = new String[ aSavables.length ];

        for ( int i = 0; i < aSavables.length; ++i )
        {
            astrIds[ i ] = saveInStore( aSavables[ i ], oGlobalStore );
        }

        return astrIds;
    }

    private static final String M_ROW_IDS = "Row";

    private static String GetRowName( int iIndex )
    {
        return String.format( "%s_%d", M_ROW_IDS, iIndex );
    }

    public static IStorage saveBidimensionalArrayInStore(
            ISavable[][] aSavables,
            IStorage oGlobalStore )
    {
        IStorage oStore = buildStore();

        for ( int i = 0; i < aSavables.length; ++i )
        {
            String[] astrIds = saveArrayInStore( aSavables[ i ], oGlobalStore );
            oStore.putStringArray( GetRowName( i ), astrIds );
        }

        return oStore;
    }

    @SuppressWarnings( "unchecked" )
    public static < T extends ISavable > void loadBidimensionalArrayFromStore(
            T[][] aSavables,
            IStorage oObjectStore,
            IStorage oGlobalStore,
            T oTmpInstance )
    {
        for ( int i = 0; i < aSavables.length; ++i )
        {
            String[] astrIds = oObjectStore.getStringArray( GetRowName( i ) );

            for ( int j = 0; j < astrIds.length; ++j )
            {
                // Ugly cast...
                aSavables[ i ][ j ] = ( T ) oTmpInstance.createInstance(
                        oGlobalStore, astrIds[ j ] );
            }
        }
    }

    private static final String M_TREE_DATA = "Data";
    private static final String M_TREE_CHILDREN = "Children";

    private static String GetChildName( int iIndex )
    {
        return String.format( "%s_%d", M_TREE_CHILDREN, iIndex );
    }

    public static < T extends ISavable > IStorage saveTreeInStore(
            Tree< T > oSavableTree,
            IStorage oGlobalStore )
    {
        TreeNode< T > oNode = oSavableTree.GetRoot();
        IStorage oStore = buildStore();
        saveTreeNodeInStore( oNode, oStore, oGlobalStore );
        return oStore;
    }

    private static < T extends ISavable > void saveTreeNodeInStore(
            TreeNode< T > oNode,
            IStorage oObjectStore,
            IStorage oGlobalStore )
    {
        if ( oNode.GetData() != null )
        {
            String strKey = saveInStore( oNode.GetData(), oGlobalStore );
            oObjectStore.putString( M_TREE_DATA, strKey );
        }
        else
        {
            int iCount = 0;
            for ( TreeNode< T > oChildNode : oNode.GetChildren() )
            {
                IStorage oChildStore = buildStore();
                saveTreeNodeInStore( oChildNode, oChildStore, oGlobalStore );
                oObjectStore.putStore( GetChildName( iCount++ ), oChildStore );
            }
        }
    }

    public static < T extends ISavable > Tree< T > loadTreeFromStore(
            IStorage oObjectStore,
            IStorage oGlobalStore,
            T oTmpInstance )
    {
        Tree< T > oSavableTree = new Tree< T >();

        TreeNode< T > oRootNode = loadTreeNodeFromStore( oObjectStore,
                                                         oGlobalStore,
                                                         oTmpInstance );

        for ( TreeNode< T > oChildNode : oRootNode.GetChildren() )
        {
            oSavableTree.GetRoot().AddChild( oChildNode );
        }

        return oSavableTree;
    }

    @SuppressWarnings( "unchecked" )
    private static < T extends ISavable > TreeNode< T > loadTreeNodeFromStore(
            IStorage oObjectStore,
            IStorage oGlobalStore,
            T oTmpInstance )
    {
        if ( oObjectStore.containsKey( M_TREE_DATA ) )
        {
            String strObjKey = oObjectStore.getString( M_TREE_DATA );
            // Ugly cast...
            return new TreeNode< T >( ( T ) oTmpInstance.createInstance(
                    oGlobalStore, strObjKey ) );
        }
        else
        {
            TreeNode< T > oParentNode = new TreeNode< T >();
            int iCount = 0;
            boolean bContinue = true;

            do
            {
                String strChildName = GetChildName( iCount++ );
                IStorage oChildStore = oObjectStore.getStore( strChildName );
                if ( oChildStore != null )
                {
                    TreeNode< T > oChildNode = loadTreeNodeFromStore(
                            oChildStore,
                            oGlobalStore,
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

    public static IStorage retrieveStore( IStorage oStore, String strObjKey,
                                          String strClassName )
    {
        IStorage oClassStore = oStore.getStore( strClassName );
        if ( oClassStore != null )
        {
            return oClassStore.getStore( strObjKey );
        }

        Logger.e( String.format(
                "Failed to find bundle for instance %s of class %s",
                strObjKey,
                strClassName ) );
        return null;
    }
}
