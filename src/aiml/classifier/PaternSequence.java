/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package aiml.classifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.util.PeekIterator;

/**
 * <p>
 * This class keeps an ordered collection of context definitions ("patterns")
 * that represent a single category. In AIML, this is often referred to as the
 * "context of the category" (in opposition to a single context like the input,
 * or topic).
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class PaternSequence {
  /** An internal linked list to represent the priority queue */
  private LinkedList<Pattern> contextQueue = new LinkedList<Pattern>();

  /** An internal stack of previous sequence states (used for loading) */
  private LinkedList<LinkedList<Pattern>> historyStack = new LinkedList<LinkedList<Pattern>>();

  private ContextInfo contextInfo;

  /**
   * A wrapper for a context pattern.
   * 
   * @author Kim Sullivan
   * @version 1.0
   */
  public static class Pattern {
    /** This pattern's context */
    private Context<? extends Object> context;

    /** The actual pattern */
    private String pattern;

    /**
     * Creates a new Pattern and sets it's context and value
     * 
     * @param context
     *          the context
     * @param pattern
     *          the pattern
     */
    public Pattern(Context<? extends Object> context, String pattern) {
      this.context = context;
      this.pattern = pattern;
    }

    /**
     * Gets the context of the pattern
     * 
     * @return the context of the pattern
     */
    public Context<? extends Object> getContext() {
      return context;
    }

    /**
     * Gets the pattern
     * 
     * @return the pattern
     */
    public String getPattern() {
      return pattern;
    }

    /**
     * Returns a string representation of the Pattern object. The resulting
     * string contains info about the pattern, and the context.
     * 
     * @return a string representation of this Pattern object
     */
    public String toString() {
      return context.getName() + "=\"" + pattern + "\"";
    }
  }

  /**
   * Represents a <code>PeekIterator&lt;Pattern&gt;</code>.
   * 
   * @author Kim Sullivan
   * 
   */
  public static class PatternIterator extends PeekIterator<Pattern> {
    public PatternIterator(Iterable<Pattern> iterable) {
      super(iterable);
    }
  }

  /**
   * The default constructor that creates a new empty sequence. Other overloaded
   * constructors are not provided, because in real-world situations the
   * sequence will be built incrementally as the &lt;context&gt; tags will be
   * read.
   * 
   * @param contextInfo
   *          TODO
   */
  public PaternSequence(ContextInfo contextInfo) {
    this.contextInfo = contextInfo;
  }

  /**
   * <p>
   * Adds a whole bunch of patterns to the sequence.
   * </p>
   * <p>
   * The array takes the form of:
   * <code>new String[][] {{"contextname","pattern"},{"othercontext","differentpattern"},...}</code>
   * </p>
   * 
   * @param contexts
   *          an array of name-pattern pairs
   * @throws MultipleContextsException
   */
  public void add(String[][] contexts) throws MultipleContextsException {
    for (int i = 0; i < contexts.length; i++) {
      add(contexts[i][0], contexts[i][1]);
    }
  }

  /**
   * Adds a single context pattern to the sequence.
   * 
   * @param context
   *          the name of a context
   * @param pattern
   *          the pattern
   * @throws MultipleContextsException
   * 
   * @throws MultipleContextsException
   */
  public void add(String context, String pattern)
      throws MultipleContextsException {
    //if (context.equals("input") && pattern.equals("64 F *"))
    //  System.out.println("["+context+"]\""+pattern+"\"");
    Context<? extends Object> c = contextInfo.getContext(context);
    Pattern p = new Pattern(c, pattern);
    ListIterator<Pattern> i = contextQueue.listIterator();
    if (!i.hasNext()) {
      i.add(p);
    } else {
      while (i.hasNext()) {
        Pattern pi = i.next();
        if (pi.context.compareTo(p.context) == 0) {
          throw new MultipleContextsException(c.getName());
        }
        if (pi.context.compareTo(p.context) > 0) {
          i.previous();
          i.add(p);
          return;
        }
      }
      i.add(p);
    }

  }

  /**
   * Saves the current pattern sequence onto a stack (used for loading nested
   * context groups)
   */
  @SuppressWarnings("unchecked")
  public void save() {
    historyStack.addFirst((LinkedList<Pattern>) contextQueue.clone());
  }

  /**
   * Restores the previous pattern sequence.
   * 
   * @throws NoSuchElementException
   */
  public void restore() {
    contextQueue = historyStack.removeFirst();
  }

  /**
   * Returns a string representation of this sequence.
   * 
   * @return a string representation of this sequence
   */
  public String toString() {
    return contextQueue.toString();
  }

  /**
   * Returns an unmodifiable iterator of the priority queue.
   * 
   * @return the unmodifiable iterator
   */
  public PatternIterator iterator() {
    return new PatternIterator(Collections.unmodifiableList(contextQueue));
  }

  /**
   * Returns the length of the sequence - the number of patterns.
   * 
   * @return the length of the sequence
   */
  public int getLength() {
    return contextQueue.size();
  }
}
