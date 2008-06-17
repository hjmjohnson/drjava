/*BEGIN_COPYRIGHT_BLOCK
 *
 * Copyright (c) 2001-2008, JavaPLT group at Rice University (drjava@rice.edu)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the names of DrJava, the JavaPLT group, Rice University, nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software is Open Source Initiative approved Open Source Software.
 * Open Source Initative Approved is a trademark of the Open Source Initiative.
 * 
 * This file is part of DrJava.  Download the current version of this project
 * from http://www.drjava.org/ or http://sourceforge.net/projects/drjava/
 * 
 * END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.BraceInfo;

/** Aligns indentation of the current line to the character that opened the enclosing block or expression list.  
  * Optional additional whitespaces can be passed through the constructor.
  * @version $Id$
  */
public class ActionBracePlus extends IndentRuleAction {
  /** int holding the number of additional blanks to be inserted. */
  private int _suffixCt;

  /** @param suffix The additional whitespaces to be inserted. */
  public ActionBracePlus(int ct) { _suffixCt = ct; }

  /** Properly indents the line that the caret is currently on.  Replaces all whitespace characters at the beginning of
    * the line with the appropriate spacing or characters.<p>
    * Preconditions: must be inside a brace.
    * @param doc AbstractDJDocument containing the line to be indented.
    * @return true if the caller should update the current location, false if the indenter has already done this.
    */
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    boolean supResult = super.indentLine(doc, reason);
    int here = doc.getCurrentLocation();
    int startLine = doc._getLineStartPos(here);
    doc.setCurrentLocation(startLine);  // Is this necessary?  _getLineEnclosingBrace only depends on current LINE
    BraceInfo info = doc._getLineEnclosingBrace();
    int dist = info.distance();

    // Check preconditions
    if (info.braceType().equals("") || dist < 0) {  // Should use interned Strings here
      // Can't find brace, so do nothing.
      return supResult;
    }

    // Find length to brace
    int bracePos = startLine - dist;
    // Get distance to start of line from enclosing brace
    int braceNewline = doc._getLineStartPos(bracePos);
    int braceIndent = bracePos - braceNewline;

    // Create tab string
    final int tab = _suffixCt + braceIndent;

    if (here > doc.getLength()) here = doc.getLength() - 1;
    doc.setCurrentLocation(here);

    doc.setTab(tab, here);
    
    return supResult;
  }
}
