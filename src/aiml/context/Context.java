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

package aiml.context;

import aiml.classifier.Classifier;
import aiml.classifier.ContextNode;
import aiml.classifier.PaternSequence;
import aiml.context.behaviour.MatchingBehaviour;
import aiml.context.data.DataSource;
import aiml.environment.Environment;

/**
 * This class represents information about a single context. Apart from the
 * context name and context order, a context has an associated
 * {@link aiml.context.DataSource DataSource} and a behaviour that determines
 * the kind of {@link aiml.classifier.ContextNode} used for matching this
 * context.
 * 
 * @author Kim Sullivan
 * @version 1.0
 * @param <V>
 *          The type of values this context stores
 */

public class Context<V> implements Comparable {
  /** The name of this context */
  private String name;

  /**
   * The order (or position) of this context. This is a WORM value (write once
   * read many).
   */
  private int order = -1;

  /** The data source */
  private DataSource<V> dataSource;
  /** The behaviour */
  private MatchingBehaviour matchingBehaviour;

  /**
   * Creates a new context. The order can't be specified in the constructor,
   * because it is dependent on the order of insertion in the ContextInfo class.
   * 
   * <p>
   * <i>Note to self:</i>Would it be better if the order was set automatically
   * at creation time with the use of a static counter?
   * </p>
   * 
   * @param name
   *          The name of this context.
   */
  public Context(String name, DataSource<V> dataSource,
      MatchingBehaviour matchingBehaviour) {
    this.name = name;
    this.dataSource = dataSource;
    this.matchingBehaviour = matchingBehaviour;
  }

  /**
   * Gets the name of the context.
   * 
   * @return the name of this context
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the order (position) of the context.
   * 
   * @return the order of this context
   */
  public int getOrder() {
    // Maybe this should throw some kind of exception if the order is -1, and
    // the Context hasn't been registered with ContextInfo yet...
    return order;
  }

  /**
   * Set the order of a context (non-negative value). Works only once, further
   * calls to setOrder() do not have an effect.
   * 
   * @param value
   *          the order
   */
  public void setOrder(int value) {
    if (value >= 0) {
      if (order == -1) {
        order = value;
      }
    }
  }

  /**
   * Compares Context objects according to their order.
   * 
   * @param c
   *          Object
   * @return -1 if this context has a lower order; 0 if they have the same
   *         order; 1 if this context has a bigger order
   */
  public int compareTo(Object c) {
    int corder = ((Context) c).getOrder();
    return (getOrder() < corder ? -1 : (getOrder() == corder ? 0 : 1));
  }

  /**
   * Get the value currently associated with the context in the specified
   * environment. This method must be overriden in inherited classes to provide
   * meaningfull behaviour.
   * 
   * @return The value currently associated with the context.
   */
  public V getValue(Environment e) {
    return dataSource.getValue(e);
  }
  
  public DataSource<V> getDataSource() {
    return dataSource;
  }
  
  public MatchingBehaviour getBehaviour() {
    return matchingBehaviour;
  }

  /**
   * Returns a new ContextNode specific to this Context type.
   * 
   * @param classifier
   *          the classifier where the node is to be added
   * @param patterns
   *          a pattern sequence
   * @param o
   *          the object to add to the leaf node
   * @param next
   *          the next context node to try if this newly created context fails
   *          to match (if present, otherwise may be <code>null</code>)
   * @return a new ContextNode instance
   */
  public ContextNode createClassifierNode(Classifier classifier,
      PaternSequence.PatternIterator patterns, ContextNode next, Object o) {
    return matchingBehaviour.createClassifierNode(classifier, patterns, next, o);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + order;
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Context))
      return false;
    Context other = (Context) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (order != other.order)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return String.valueOf(order);
  }
}
