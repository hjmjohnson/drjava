/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is a part of DrJava. Current versions of this project are available
 * at http://sourceforge.net/projects/drjava
 *
 * Copyright (C) 2001-2002 JavaPLT group at Rice University (javaplt@rice.edu)
 * 
 * DrJava is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * DrJava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * or see http://www.gnu.org/licenses/gpl.html
 *
 * In addition, as a special exception, the JavaPLT group at Rice University
 * (javaplt@rice.edu) gives permission to link the code of DrJava with
 * the classes in the gj.util package, even if they are provided in binary-only
 * form, and distribute linked combinations including the DrJava and the
 * gj.util package. You must obey the GNU General Public License in all
 * respects for all of the code used other than these classes in the gj.util
 * package: Dictionary, HashtableEntry, ValueEnumerator, Enumeration,
 * KeyEnumerator, Vector, Hashtable, Stack, VectorEnumerator.
 *
 * If you modify this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version. (However, the
 * present version of DrJava depends on these classes, so you'd want to
 * remove the dependency first!)
 *
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;

import java.lang.reflect.Field;

import java.util.LinkedList;

// Importing the Java 1.3 / JSR-14 v1.0 Compiler classes
import com.sun.tools.javac.v8.JavaCompiler;
import com.sun.tools.javac.v8.util.Name;
import com.sun.tools.javac.v8.util.Position;
import com.sun.tools.javac.v8.util.Hashtable;
import com.sun.tools.javac.v8.util.List;
import com.sun.tools.javac.v8.util.Log;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.FileOps;

/**
 * An implementation of the CompilerInterface that supports compiling with
 * the JSR-14 prototype compiler.
 * It adds the collections classes signature to the bootclasspath
 * as requested by {@link Configuration}.
 * 
 * This class can only be compiled using the JSR-14 v1.0 compiler,
 * since the JSR-14 v1.2 compiler has incompatible classes.
 *
 * @version $Id$
 */
public class JSR14v10Compiler extends JavacGJCompiler {
  
  private File _collectionsPath;
  
  /** Singleton instance. */
  public static final CompilerInterface ONLY = new JSR14v10Compiler();

  protected JSR14v10Compiler() {
    super();
  }
  
  public boolean isAvailable() {
    try {
      // Main$10 exists in JSR14 v1.0 but not JDK 1.3
      Class.forName("com.sun.tools.javac.v8.Main$10");
      return super.isAvailable();
    }
    catch (Throwable t) {
      return false;
    }
  }
      
  
  protected void updateBootClassPath() {

    // add collections path to the bootclasspath
    // Yes, we are mutating some other class's public variable.
    // But the docs for ClassReader say it's OK for others to mutate it!
    // And this way, we don't need to specify the entire bootclasspath,
    // just what we want to add on to it.
    
    if (_collectionsPath != null) {
      String ccp = _collectionsPath.getAbsolutePath();
    
      if (ccp != null && ccp.length() > 0) {
        compiler.syms.reader.bootClassPath = ccp +
          System.getProperty("path.separator")+
          compiler.syms.reader.bootClassPath;
  
      }
    }
  }
    
  protected void initCompiler(File[] sourceRoots) {
    super.initCompiler(sourceRoots);
    updateBootClassPath();
  }

  /**
   * This method allows us to set the JSR14 collections path across a class loader.
   * (cannot cast a loaded class to a subclass, so all compiler interfaces must have this method)
   */ 
  public void addToBootClassPath( File cp) {
    _collectionsPath = cp;
  }
  
  public String getName() { return "JSR-14 v1.0"; }


}
