package com.holub.life;

import java.awt.*;
import javax.swing.*;

import com.holub.ui.ColorTheme;
import com.holub.ui.Colors;	// Contains constants specifying various
import com.holub.ui.Theme;
// colors not defined in java.awt.Color.
import com.holub.life.Cell;
import com.holub.life.Storable;
import com.holub.life.Direction;
import com.holub.life.Neighborhood;
import com.holub.life.Universe;

/*** ****************************************************************
 * The Resident class implements a single cell---a "resident" of a
 * block.
 *
 * @include /etc/license.txt
 */

public final class Resident implements Cell
{
	private boolean amAlive 	= false;
	private boolean willBeAlive	= false;

	private boolean isStable(){return amAlive == willBeAlive; }

	/** figure the next state.
	 *  @return true if the cell is not stable (will change state on the
	 *  next transition().
	 *  현재 상태와 다음 상태가 다르다면 isStable()은 false, figureNextState()은 바뀌어야하므로 true 반환
	 *  현재 상태와 다음 상태가 같다면 isStable()은 true, figureNextState()는 바뀔 필요 없으므로 false 반환

	 */
	public boolean figureNextState(
							Cell north, 	Cell south,
							Cell east, 		Cell west,
							Cell northeast, Cell northwest,
							Cell southeast, Cell southwest )
	{
		verify( north, 		"north"		);
		verify( south, 		"south"		);
		verify( east, 		"east"		);
		verify( west, 		"west"		);
		verify( northeast,	"northeast"	);
		verify( northwest,	"northwest" );
		verify( southeast,	"southeast" );
		verify( southwest,	"southwest" );

		int neighbors = 0;

		if( north.	  isAlive()) ++neighbors;
		if( south.	  isAlive()) ++neighbors;
		if( east. 	  isAlive()) ++neighbors;
		if( west. 	  isAlive()) ++neighbors;
		if( northeast.isAlive()) ++neighbors;
		if( northwest.isAlive()) ++neighbors;
		if( southeast.isAlive()) ++neighbors;
		if( southwest.isAlive()) ++neighbors;

		willBeAlive = (neighbors==3 || (amAlive && neighbors==2));
		return !isStable();
	}

	private void verify( Cell c, String direction )
	{	assert (c instanceof Resident) || (c == Cell.DUMMY)
				: "incorrect type for " + direction +  ": " +
				   c.getClass().getName();
	}

	/** This cell is monetary, so it's at every edge of itself. It's
	 *  an internal error for any position except for (0,0) to be
	 *  requsted since the width is 1.
	 */
	public Cell	edge(int row, int column)
	{	assert row==0 && column==0;
		return this;
	}

	public boolean transition()
	{	boolean changed = isStable();
		amAlive = willBeAlive;
		return changed;
	}

	public void redraw(Graphics g, Rectangle here, boolean drawAll)
    {   g = g.create();
		g.setColor(amAlive ? ColorTheme.theme.LIVE_COLOR : ColorTheme.theme.DEAD_COLOR );
		g.fillRect(here.x+1, here.y+1, here.width-1, here.height-1);

		// Doesn't draw a line on the far right and bottom of the
		// grid, but that's life, so to speak. It's not worth the
		// code for the special case.

		g.setColor( ColorTheme.theme.BORDER_COLOR );
		g.drawLine( here.x, here.y, here.x, here.y + here.height );
		g.drawLine( here.x, here.y, here.x + here.width, here.y  );
		g.dispose();
	}

	public void userClicked(Point here, Rectangle surface)
	{	amAlive = !amAlive;
	}

	public void	   clear()			{amAlive = willBeAlive = false; }
	public boolean isAlive()		{return amAlive;			    }
	public Cell    create()			{return new Resident();			}
	public int 	   widthInCells()	{return 1;}

	public Direction isDisruptiveTo()
	{	return isStable() ? Direction.NONE : Direction.ALL ;
	}

	public boolean transfer(Storable blob,Point upperLeft,boolean doLoad)
	{
		Memento memento = (Memento)blob;
		if( doLoad )
		{	if( amAlive = willBeAlive = memento.isAlive(upperLeft) )
				return true;
		}
		else if( amAlive )  					// store only live cells
			memento.markAsAlive( upperLeft );

		return false;
	}

	/** Mementos must be created by Neighborhood objects. Throw an
	 *  exception if anybody tries to do it here.
	 */
	public Storable createMemento()
	{	throw new UnsupportedOperationException(
					"May not create memento of a unitary cell");
	}
}
