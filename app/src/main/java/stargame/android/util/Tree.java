package stargame.android.util;

import java.util.Iterator;

public class Tree< E > implements Iterable< E >
{
    TreeNode< E > mRoot;

    public Tree()
    {
        mRoot = new TreeNode< E >();
    }

    public TreeNode< E > GetRoot()
    {
        return mRoot;
    }

    public int GetNbElements()
    {
        return mRoot.GetNbElements();
    }

    public E GetElement( int iIndex )
    {
        if ( iIndex < 0 || iIndex > GetNbElements() - 1 )
        {
            return null;
        }

        Iterator< E > oIterator = iterator();
        E oObj = null;
        for ( int i = 0; i < iIndex; ++i )
        {
            oObj = oIterator.next();
        }
        return oObj;
    }

    public Iterator< E > iterator()
    {
        return new TreeIterator< E >( this );
    }
}
