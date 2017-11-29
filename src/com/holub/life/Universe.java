package com.holub.life;

import com.holub.io.Files;
import com.holub.ui.MenuSite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Universe is a mediator that sits between the Swing
 * event model and the Life classes. It is also a singleton,
 * accessed via Universe.instance(). It handles all
 * Swing events and translates them into requests to the
 * outermost Neighborhood. It also creates the Composite
 * Neighborhood.
 *
 * @include /etc/license.txt
 */

public class Universe extends JPanel
{	private 		final Cell  	outermostCell;
	private static	final Universe 	theInstance = new Universe();
	private List mementos = new ArrayList<Cell.Memento>();

	/** The default height and width of a Neighborhood in cells.
	 *  If it's too big, you'll run too slowly because
	 *  you have to update the entire block as a unit, so there's more
	 *  to do. If it's too small, you have too many blocks to check.
	 *  I've found that 8 is a good compromise.
	 */
	private static final int  DEFAULT_GRID_SIZE = 8;

	/** The size of the smallest "atomic" cell---a Resident object.
	 *  This size is extrinsic to a Resident (It's passed into the
	 *  Resident's "draw yourself" method.
	 */
	private static final int  DEFAULT_CELL_SIZE = 8;

	// The constructor is private so that the universe can be created
	// only by an outer-class method [Neighborhood.createUniverse()].

	private Universe()
	{	// Create the nested Cells that comprise the "universe." A bug
		// in the current implementation causes the program to fail
		// miserably if the overall size of the grid is too big to fit
		// on the screen.

		outermostCell = new Neighborhood
						(	DEFAULT_GRID_SIZE,
							// prototype: neighborhood
							new Neighborhood
							(	DEFAULT_GRID_SIZE,
								// prototype: resident
								new Resident()
							)
						);

		// 실제 pixel 사이즈
		final Dimension PREFERRED_SIZE =
						new Dimension
						(  outermostCell.widthInCells() * DEFAULT_CELL_SIZE,
						   outermostCell.widthInCells() * DEFAULT_CELL_SIZE
						);

		// 컴포넌트 이벤트를 받는청취자 인터페이스
        // 관련 메소드만을 오버라이드
		addComponentListener
		(	new ComponentAdapter()
			{	// componentHidden(ComponentEvent e)
                // componentMoved(ComponentEvent e)
                // componentShown(ComponentEvent e)
                // 얘들도 있음. 아래 Resized만 override 한 것
			    public void componentResized(ComponentEvent e)
				{
					// Make sure that the cells fit evenly into the
					// total grid size so that each cell will be the
					// same size. For example, in a 64x64 grid, the
					// total size must be an even multiple of 63.

					Rectangle bounds = getBounds();
					bounds.height /= outermostCell.widthInCells();
					bounds.height *= outermostCell.widthInCells();
					bounds.width  =  bounds.height;
					setBounds( bounds );
				}
			}
		);

		setBackground	( Color.white	 );
		setPreferredSize( PREFERRED_SIZE );
		setMaximumSize	( PREFERRED_SIZE );
		setMinimumSize	( PREFERRED_SIZE );
		setOpaque		( true			 );

		addMouseListener					//{=Universe.mouse}
		(	new MouseAdapter()
			{	public void mousePressed(MouseEvent e)
				{
				    // getBounds는 Ractangle을 돌려줌 좌상(x,y)와 우하(width, height)를 가지고 있음
				    Rectangle bounds = getBounds();
					bounds.x = 0;
					bounds.y = 0;
					// 마우스가 눌리면 해당 포인트와 bound 전달.
                    // outermostCell은 Neighborhood임
					outermostCell.userClicked(e.getPoint(),bounds); // == Neighborhood.userClicked
					repaint();
				}
			}
		);

		MenuSite.addLine( this, "Grid", "Clear",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
				{	outermostCell.clear();
					repaint();
				}
			}
		);

		MenuSite.addLine			// {=Universe.load.setup}
		(	this, "Grid", "Load",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
				{	doLoad();
				}
			}
		);

		MenuSite.addLine
		(	this, "Grid", "Store",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
				{	doStore();
				}
			}
		);

		MenuSite.addLine
		(	this, "Grid", "Exit",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
		        {	System.exit(0);
		        }
			}
		);

		MenuSite.addLine
				(this, "Grid", "Resotre Last State",
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								doLoadLocal();
							}
						}
				);

		/**
         * Clock이 TimerTask를 돌면서 run() -> tick()
         * tick()에서, 퍼블리셔가 퍼블리싱
         * 퍼블리싱 -> deliverTo 동작
         * deliverTo에서 구독자의 tick()동작시킴
         * 조건에 따라 refreshNow()동작
         * */
		Clock.instance().addClockListener //{=Universe.clock.subscribe}
		(	new Clock.Listener()
			{	public void tick()
				{	if( outermostCell.figureNextState
						   ( Cell.DUMMY,Cell.DUMMY,Cell.DUMMY,Cell.DUMMY,
							 Cell.DUMMY,Cell.DUMMY,Cell.DUMMY,Cell.DUMMY
						   )
					  )
					{	if( outermostCell.transition() )
							refreshNow();
					}
				}
			}
		);
	}

	/** Singleton Accessor. The Universe object itself is manufactured
	 *  in Neighborhood.createUniverse()
	 *  그런데 이런것은 없음. 주석에서만 존재하고있음
	 */

	public static Universe instance()
	{
	    // public static final theInstance = new Universe();
	    return theInstance;
	}

	private void doLoad()
	{	try
		{
			FileInputStream in = new FileInputStream(
			   Files.userSelected(".",".life","Life File","Load"));

			Clock.instance().stop();		// stop the game and
			outermostCell.clear();			// clear the board.

			Storable memento = outermostCell.createMemento();
			memento.load( in );
			outermostCell.transfer( memento, new Point(0,0), Cell.LOAD );

			in.close();
		}
		catch( IOException theException )
		{	JOptionPane.showMessageDialog( null, "Read Failed!",
					"The Game of Life", JOptionPane.ERROR_MESSAGE);
		}
		repaint();
	}

	private void doStore()
	{	try
		{
			FileOutputStream out = new FileOutputStream(
				  Files.userSelected(".",".life","Life File","Write"));

			Clock.instance().stop();		// stop the game

			Storable memento = outermostCell.createMemento();
			outermostCell.transfer( memento, new Point(0,0), Cell.STORE );
			memento.flush(out);

			out.close();
		}
		catch( IOException theException )
		{	JOptionPane.showMessageDialog( null, "Write Failed!",
					"The Game of Life", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 *  popMemento()
	 * */
	private void doLoadLocal() {
			if(mementos.size() > 0) {
				Clock.instance().stop();        // stop the game and
				outermostCell.clear();            // clear the board.

//				Storable memento = outermostCell.createMemento();	//
//				memento.load((Cell.Memento) mementos.remove(mementos.size()-1));
//				outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

				outermostCell.transfer((Cell.Memento) mementos.remove(mementos.size()-1), new Point(0, 0), Cell.LOAD);
			}
		repaint();
	}

	/**
	 * pushMemento()
	 * */
	public void doStoreLocal() {
			Clock.instance().stop();        // stop the game

			Storable memento = outermostCell.createMemento();
			outermostCell.transfer(memento, new Point(0, 0), Cell.STORE);
			mementos.add(memento);
	}

	/** Override paint to ask the outermost Neighborhood
	 *  (and any subcells) to draw themselves recursively.
	 *  All knowledge of screen size is also encapsulated.
	 *  (The size is passed into the outermost <code>Cell</code>.)
	 */

	public void paint(Graphics g)
	{
		Rectangle panelBounds = getBounds();
		Rectangle clipBounds  = g.getClipBounds();

		// The panel bounds is relative to the upper-left
		// corner of the screen. Pretend that it's at (0,0)
		panelBounds.x = 0;
		panelBounds.y = 0;
		outermostCell.redraw(g, panelBounds, true);		//{=Universe.redraw1}
	}

	public void repaintNow() {
		repaint();
	}
	/** Force a screen refresh by queing a request on
	 *  the Swing event queue. This is an example of the
	 *  Active Object pattern (not covered by the Gang of Four).
	 *  This method is called on every clock tick. Note that
	 *  the redraw() method on a given <code>Cell</code>
	 *  does nothing if the <code>Cell</code> doesn't
	 *  have to be refreshed.
	 */

	private void refreshNow()
	{	SwingUtilities.invokeLater
		(	new Runnable()
			{	public void run()
				{	Graphics g = getGraphics();
					if( g == null )		// Universe not displayable
						return;
					try
					{
						Rectangle panelBounds = getBounds();
						panelBounds.x = 0;
						panelBounds.y = 0;
						outermostCell.redraw(g, panelBounds, false); //{=Universe.redraw2}
					}
					finally
					{	g.dispose();
					}
				}
			}
		);
	}
}
