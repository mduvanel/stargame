package stargame.android.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class TreeIterator< E > implements Iterator< E >
{
    protected static class StackStruct< E >
    {
        public TreeNode< E > mNode;
        public int mIndex;

        public StackStruct( TreeNode< E > oNode, int iIndex )
        {
            mNode = oNode;
            mIndex = iIndex;
        }
    }

    private final Tree< E > mTree;
    private final Stack< StackStruct< E > > mChildLocations;

    private int mCurrentNodePosition;
    private TreeNode< E > mCurrentNode;

    public TreeIterator( Tree< E > oTree )
    {
        mTree = oTree;
        mChildLocations = new Stack< StackStruct< E > >();
        mCurrentNode = mTree.GetRoot();
        mCurrentNodePosition = -1;
    }

    public boolean hasNext()
    {
        return mCurrentNode != null;
    }

    public E next()
    {
        E oNext = null;

        while ( oNext == null )
        {
            if ( mCurrentNodePosition == -1 )
            {
                if ( mCurrentNode.mLeaf != null )
                {
                    // Found!
                    oNext = mCurrentNode.mLeaf;

                    // It's a leaf, go up once
                    GoUp();
                }
                else
                {
                    // Not a leaf, go down to first child
                    mChildLocations.push( new StackStruct< E >( mCurrentNode, 0 ) );
                    mCurrentNode = mCurrentNode.mSubtree.get( 0 );
                    mCurrentNodePosition = -1;
                }
            }
            else
            {
                if ( mCurrentNodePosition < mCurrentNode.mSubtree.size() )
                {
                    // Go down next child
                    mChildLocations.push(
                            new StackStruct< E >( mCurrentNode, mCurrentNodePosition ) );
                    mCurrentNode = mCurrentNode.mSubtree.get( mCurrentNodePosition );
                    mCurrentNodePosition = -1;
                }
                else
                {
                    // End of children, go back up
                    if ( !GoUp() )
                    {
                        throw new NoSuchElementException();
                    }
                }
            }
        }

        // We found the value to return, check if there is one next.
        while ( mCurrentNodePosition == mCurrentNode.mSubtree.size() )
        {
            if ( !GoUp() )
            {
                break;
            }
        }

        return oNext;
    }

    protected boolean GoUp()
    {
        if ( mChildLocations.empty() )
        {
            mCurrentNode = null;
            return false;
        }
        else
        {
            StackStruct< E > oParent = mChildLocations.pop();
            mCurrentNode = oParent.mNode;
            mCurrentNodePosition = oParent.mIndex + 1;
            return true;
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
