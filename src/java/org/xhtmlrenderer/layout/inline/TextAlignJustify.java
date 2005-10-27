/*
 * TextAlignJustify.java
 * Copyright (c) 2004, 2005 Torbj�rn Gannholm
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
 *
 */
package org.xhtmlrenderer.layout.inline;

import org.xhtmlrenderer.layout.FontUtil;
import org.xhtmlrenderer.render.InlineBox;
import org.xhtmlrenderer.render.InlineTextBox;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.RenderingContext;

import java.util.ArrayList;
import java.util.List;


/**
 * Description of the Class
 *
 * @author Torbj�rn Gannholm
 */
public class TextAlignJustify {

    /**
     * Description of the Method
     *
     * @param line  PARAM
     * @param width PARAM
     */
    public static void justifyLine(RenderingContext c, LineBox line, int width) {
        if (line.contentWidth > width) {
            return;
        }

        // split each inline box into words
        List temp_list = new ArrayList();
        for (int i = 0; i < line.getChildCount(); i++) {
            splitInlineBox(c, (InlineBox) line.getChild(i), temp_list);
        }
        // clear out all inlines
        line.removeAllChildren();
        // add in all of the new inlines
        line.contentWidth = 0;
        for (int i = 0; i < temp_list.size(); i++) {
            InlineBox box = (InlineBox) temp_list.get(i);
            line.contentWidth += box.getWidth();
            line.addChild(box);
        }

        int extra = width - line.contentWidth;
        int spaces = (line.getChildCount() - 1);
        float spacer = extra / (float) spaces;
        // now realign them
        int total_lengths = 0;
        for (int i = 0; i < line.getChildCount(); i++) {
            InlineBox box = (InlineBox) line.getChild(i);
            box.x = total_lengths + (int) (spacer * (float) i);
            total_lengths += box.getWidth();
        }
    }

    /**
     * Gets the justified attribute of the TextAlignJustify class
     *
     * @param style  PARAM
     * @return       The justified value
     */
    /*public static boolean isJustified( CalculatedStyle style ) {
        if ( style.isIdent( CSSName.TEXT_ALIGN, IdentValue.JUSTIFY ) ) {
            return true;
        }
        return false;
    }*/

    /**
     * Description of the Method
     *
     * @param c         PARAM
     * @param ibox      PARAM
     * @param temp_list PARAM
     */
    private static void splitInlineBox(RenderingContext c, InlineBox ibox, List temp_list) {
        if (!(ibox instanceof InlineTextBox)) {
            temp_list.add(ibox.copy());
            return;
        }
        InlineTextBox box = (InlineTextBox) ibox;
        String[] words = words(box);
        // don't split if only one word
        if (words.length < 2) {
            temp_list.add(box);
            return;
        }
        int currentWordPosition = box.start_index + box.getSubstring().indexOf(words[0]);
        for (int i = 0; i < words.length; i++) {
            InlineTextBox copy = (InlineTextBox) box.copy();
            copy.setSubstring(currentWordPosition, currentWordPosition + words[i].length());//was: (words[i]);
            currentWordPosition = currentWordPosition + words[i].length() + 1;//skip the space too
            copy.contentWidth = FontUtil.len(c, copy, c.getTextRenderer(), c.getGraphics());
            temp_list.add(copy);
        }
    }

    /**
     * Description of the Method
     *
     * @param box PARAM
     * @return Returns
     */
    private static String[] words(InlineTextBox box) {
        String text = box.getSubstring();
        String[] words = text.split("\\s");
        return words;
    }
}

