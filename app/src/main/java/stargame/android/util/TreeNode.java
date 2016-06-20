package stargame.android.util;

import java.util.ArrayList;
import java.util.List;

public class TreeNode< E >
{
	protected List< TreeNode< E > > mSubtree;
	
	protected E mLeaf;

	public TreeNode()
	{
		mLeaf = null;
		mSubtree = null;
	}

	public TreeNode( E oValue )
	{
		mLeaf = oValue;
		mSubtree = null;
	}

	public List< TreeNode< E > > GetChildren()
	{
        if ( mSubtree == null )
        {
            return new ArrayList< TreeNode< E > >();
        }
		return mSubtree;
	}

	public void AddChild( TreeNode< E > oChild )
	{
        if ( mSubtree == null )
        {
        	mSubtree = new ArrayList< TreeNode< E > >();
        }
        mSubtree.add( oChild );
    }

    public E GetData()
    {
        return mLeaf;
    }

    public void SetData( E oData )
    {
        mLeaf = oData;
    }

    public int GetNbElements()
    {
    	if ( mLeaf != null )
    	{
    		return 1;
    	}
    	else
    	{
    		int iSize = 0;
    		for ( TreeNode< E > oElement : mSubtree )
    		{
    			iSize += oElement.GetNbElements();
    		}
    		return iSize;
    	}
    }
}
