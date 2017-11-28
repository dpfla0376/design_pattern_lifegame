package com.holub.life;

import java.awt.*;
import javax.swing.*;

import com.holub.ui.ColorTheme;
import com.holub.ui.MenuSite;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame
{	
	private static JComponent universe;	// 안쓰는 변수

	public static void main( String[] arguments )
	{	new Life();
	}

	private Life()

	{	super( "The Game of Life. "
					+"(c)2003 Allen I. Holub <http://www.holub.com>");

		// Must establish the MenuSite very early in case
		// a subcomponent puts menus on it.
		MenuSite.establish( this );		//{=life.java.establish}
		ColorTheme.getInstance();
		setDefaultCloseOperation	( EXIT_ON_CLOSE 		);
		getContentPane().setLayout	( new BorderLayout()	);
		getContentPane().add( Universe.instance(), BorderLayout.CENTER); //{=life.java.install}

		pack();

		setVisible( true );	

	}
}
