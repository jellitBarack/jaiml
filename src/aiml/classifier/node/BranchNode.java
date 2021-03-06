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

package aiml.classifier.node;

import graphviz.Graphviz;
import aiml.classifier.MatchState;
import aiml.classifier.Pattern;
import aiml.classifier.PatternContextNode;

/**
 * A branch in the pattern matching tree. This pure branching node implements
 * the "pattern order" principle of AIML - first it triest to match an
 * underscore, then it tires to find an exact match and finally it tries to get
 * a match on the star wildcard. As with all pattern nodes, the branch node
 * applies only to the current depth.
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class BranchNode extends PatternNode {
  /**
   * The star (*) wildcard subtree
   */
  private PatternNode star;

  /**
   * The exact string match subtree
   */
  private PatternNode string;

  /**
   * The underscore (_) wildcard subtree
   */
  private PatternNode underscore;

  /**
   * Creates a new, empty branch node
   */
  public BranchNode(PatternContextNode parent) {
    super(parent);
  }

  /**
   * Creates a new branch node, with one subtree (wildcard or string). The type
   * of the subtree (star, string, underscore) is determined at run-time.
   * 
   * @param node
   *          the first subtree
   */
  public BranchNode(PatternContextNode parent, PatternNode node) {
    super(parent);
    switch (node.getType()) {
    case PatternNode.UNDERSCORE:
      underscore = node;
      break;
    case PatternNode.STAR:
      star = node;
      break;
    case PatternNode.STRING:
      string = node;
      break;
    default:
      throw new UnsupportedOperationException(
          "Can't add unknown node types to a branch");
    }
  }

  /**
   * Add the pattern to itself. For the branch node, this means it must
   * determine the current pattern type, and place it into the appropriate
   * subtree.
   */
  public PatternNode.AddResult add(int depth, String pattern) {
    AddResult result;
    PatternNodeFactory patternNodeFactory = parentContext.getPNF();
    //this is a really ugly method... should I put the subnodes in an Array? is it OK to use
    //static final ints as hardcoded indexes?
    int t = Pattern.getType(depth, pattern);
    if (t == PatternNode.OTHER) {
      throw new UnsupportedOperationException(
          "Can't add nodes of type OTHER to a branch");
    }
    if (t == PatternNode.UNDERSCORE) {
      if (underscore == null) {
        underscore = patternNodeFactory.getInstance(parentContext, depth,
            pattern);
      }
      result = underscore.add(depth, pattern);
      underscore = result.root;
      result.root = this;
      return result;
    }
    if (t == PatternNode.STAR) {
      if (star == null) {
        star = patternNodeFactory.getInstance(parentContext, depth, pattern);
      }
      result = star.add(depth, pattern);
      star = result.root;
      result.root = this;
      return result;
    }
    if (t == PatternNode.STRING) {
      if (string == null) {
        string = patternNodeFactory.getInstance(parentContext, depth, pattern);
      }
      result = string.add(depth, pattern);
      string = result.root;
      result.root = this;
      return result;
    }
    throw new UnsupportedOperationException(
        "Can't add unknown node types to a branch");
  }

  /**
   * Try to match the current match state. This node implements the AIML pattern
   * ordering principle. First it tries to match the state to an underscore
   * wildcard pattern, then an exact string, and finally a star wildcard.
   */
  public boolean match(MatchState match) {
    if (underscore != null) {
      if (underscore.match(match)) {
        return true;
      }
    }
    if (string != null) {
      if (string.match(match)) {
        return true;
      }
    }
    if (star != null) {
      if (star.match(match)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    if (underscore != null) {
      sb.append("_[");
      sb.append(underscore.toString());
      sb.append("] ");
    }
    if (string != null) {
      sb.append("s[");
      sb.append(string.toString());
      sb.append("] ");
    }
    if (star != null) {
      sb.append("*[");
      sb.append(star.toString());
      sb.append("] ");
    }
    sb.append('}');
    return sb.toString();
  }

  @Override
  public String gvNodeLabel() {
    return "";
  }

  public void gvInternalGraph(Graphviz graph) {
    graph.connectGraph(this, underscore, Graphviz.EPSILON);
    graph.connectGraph(this, string, Graphviz.EPSILON);
    graph.connectGraph(this, star, Graphviz.EPSILON);
  }

}
