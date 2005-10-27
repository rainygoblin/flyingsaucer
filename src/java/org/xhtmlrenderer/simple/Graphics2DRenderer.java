/*
 * {{{ header & license
 * Copyright (c) 2004, 2005 Joshua Marinacci
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package org.xhtmlrenderer.simple;

import org.w3c.dom.Document;
import org.xhtmlrenderer.layout.SharedContext;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


/**
 * <p/>
 * Graphics2DRenderer supports headless rendering of XHTML documents, and is useful
 * for rendering documents directly to images.</p>
 * <p/>
 * <p>Graphics2DRenderer supports the {@link XHTMLPanel#setDocument(Document)},
 * {@link XHTMLPanel#layout()}, and {@link XHTMLPanel#render()} methods from
 * {@link XHTMLPanel}, as well as easy-to-use static utility methods.
 * For example, to render a document in an image that is 600 pixels wide use the
 * {@link #renderToImage(String, int)} method like this:</p>
 * <pre>
 * BufferedImage img = Graphics2DRenderer.renderToImage( "test.xhtml", width);
 * </pre>
 * <p/>
 * <p/>
 * <p/>
 * </p>
 *
 * @author Joshua Marinacci
 */

public class Graphics2DRenderer {
    /**
     * Description of the Field
     */
    protected XHTMLPanel panel;
    /**
     * Description of the Field
     */
    protected Dimension dim;

    /**
     * Constructor for the Graphics2DRenderer object
     */
    public Graphics2DRenderer() {
        panel = new XHTMLPanel(false);
        panel.setInteractive(false);
    }

    // ASK maybe we could change the graphics2d to be a font rendering context?
    /**
     * lay out the document with the desired size. don't paint though.
     *
     * @param g2  PARAM
     * @param dim PARAM
     */
    public void layout(Graphics2D g2, Dimension dim) {
        this.dim = dim;
        panel.setSize(dim);
        panel.doActualLayout(g2);
    }


    /**
     * actually draw the
     *
     * @param g2 PARAM
     */
    public void render(Graphics2D g2) {
        if (g2.getClip() == null) {
            g2.setClip(getMinimumSize());
        }
        panel.paintComponent(g2);
    }


    /**
     * set the document to be rendered.
     *
     * @param url The new document value
     */
    public void setDocument(String url) {
        panel.setDocument(url);
    }

    /**
     * Sets the document attribute of the Graphics2DRenderer object
     *
     * @param doc      The new document value
     * @param base_url The new document value
     */
    public void setDocument(Document doc, String base_url) {
        panel.setDocument(doc, base_url);
    }

    /**
     * Sets the renderingContext attribute of the Graphics2DRenderer object
     *
     * @param ctx The new renderingContext value
     */
    public void setSharedContext(SharedContext ctx) {
        panel.setSharedContext(ctx);
    }

    /**
     * return the size image needed to render the document without anything
     * going off the side. *could be different than the dimensions passed into
     * layout because of text that couldn't break or a table that's set to be
     * too big
     *
     * @return The minimumSize value
     */
    public Rectangle getMinimumSize() {
        if (panel.getIntrinsicSize() != null) {
            return new Rectangle(0, 0,
                    (int) panel.getIntrinsicSize().getWidth(),
                    (int) panel.getIntrinsicSize().getHeight());
        } else {
            return new Rectangle(0, 0, panel.getWidth(), panel.getHeight());
        }
    }

    /**
     * Gets the renderingContext attribute of the Graphics2DRenderer object
     *
     * @return The renderingContext value
     */
    public SharedContext getSharedContext() {
        return panel.getSharedContext();
    }

    public XHTMLPanel getPanel() {
        return panel;
    }

    /**
     * a static utility method to automatically create an image the right size,
     * then layout and render the document
     *
     * @param url    PARAM
     * @param width  PARAM
     * @param height PARAM
     * @return Returns
     */
    public static BufferedImage renderToImage(String url, int width, int height) {
        Graphics2DRenderer g2r = new Graphics2DRenderer();
        g2r.setDocument(url);
        Dimension dim = new Dimension(width, height);
        BufferedImage buff = new BufferedImage((int) dim.getWidth(), (int) dim.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) buff.getGraphics();
        g2r.layout(g, dim);
        g2r.render(g);
        g.dispose();
        return buff;
    }

    /**
     * Description of the Method
     *
     * @param url   PARAM
     * @param width PARAM
     * @return Returns
     */
    public static BufferedImage renderToImage(String url, int width) {
        Graphics2DRenderer g2r = new Graphics2DRenderer();
        g2r.setDocument(url);
        Dimension dim = new Dimension(width, 1000);

        // do layout with temp buffer
        BufferedImage buff = new BufferedImage((int) dim.getWidth(), (int) dim.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) buff.getGraphics();
        g2r.layout(g, new Dimension(width, 1000));
        g.dispose();

        // get size
        Rectangle rect = g2r.getMinimumSize();

        // render into real buffer
        buff = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        g = (Graphics2D) buff.getGraphics();
        g2r.render(g);
        g.dispose();

        // return real buffer
        return buff;
    }

}

/*
 * $Id$
 *
 * $Log$
 * Revision 1.15  2005/10/27 00:09:07  tobega
 * Sorted out Context into RenderingContext and LayoutContext
 *
 * Revision 1.14  2005/10/26 17:01:44  peterbrant
 * Allow the "use threads" config property to be set on individual instances of
 * XHTMLPanel.
 *
 * Revision 1.13  2005/09/29 06:15:06  tobega
 * Patch from Peter Brant:
 * List of changes:
 *  - Fix extents height calculation
 *  - Small refactoring to Boxing to combine a method
 *  - Make render and layout threads interruptible and add
 * RootPanel.shutdown() method to shut them down in an orderly manner
 *  - Fix NPE in Graphics2DRenderer.  It looks like
 * BasicPanel.intrinsic_size will always be null anyway?
 *  - Fix NPE in RootPanel when enclosingScrollPane is null.
 *  - Both RenderLoop.collapseRepaintEvents and
 * LayoutLoop.collapseLayoutEvents will go into an infinite loop if the
 * next event isn't collapsible.  I added a common implementation to
 * RenderQueue which doesn't have this problem.
 *
 * Revision 1.12  2005/09/28 00:25:16  joshy
 * a bit more cleanup
 * Issue number:
 * Obtained from:
 * Submitted by:
 * Reviewed by:
 *
 * Revision 1.11  2005/09/26 22:40:21  tobega
 * Applied patch from Peter Brant concerning margin collapsing
 *
 * Revision 1.10  2005/06/16 07:24:52  tobega
 * Fixed background image bug.
 * Caching images in browser.
 * Enhanced LinkListener.
 * Some house-cleaning, playing with Idea's code inspection utility.
 *
 * Revision 1.9  2005/06/15 10:56:14  tobega
 * cleaned up a bit of URL mess, centralizing URI-resolution and loading to UserAgentCallback
 *
 * Revision 1.8  2005/06/01 21:36:43  tobega
 * Got image scaling working, and did some refactoring along the way
 *
 * Revision 1.7  2005/03/24 23:11:41  pdoubleya
 * Comments.
 *
 *
 */
