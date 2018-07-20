package com.squarespace.interview.findpoint;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

@SuppressWarnings("WeakerAccess")
public class FindPointSolution {

  /**
   * Given a root {@link com.squarespace.interview.findpoint.Node} of a view hierarchy, provide an ordered
   * list of {@link Node#id} representing the path through the hierarchy where the given {@link java.awt.Point}
   * can be located.
   *
   * @param rootNode Top-level node of the view hierarchy.
   * @param toFind An x,y coordinate to be located.
   * @return An ordered list of {@link Node#id}
   */
  @SuppressWarnings("UnusedParameters")
  public static List<String> findPathToNode(Node rootNode, Point toFind) {

    // Initialize return variable
    List<String> pathToPoint = new LinkedList<>();

    if (rootNode == null || !AbsoluteCoordinateNode.contains(rootNode, toFind)) {
      return pathToPoint;
    }

    // Initialize stack for a non-recursive, pre-order, depth first search.
    Stack<Node> stack = new Stack();
    stack.push(rootNode);

    // Traverse the tree of Nodes starting with the root.
    while(!stack.isEmpty()) {
      Node node = stack.pop();

      // Record node id pre-order
      pathToPoint.add(node.getId());

      // Visit first node that contains toFind. Highest Z-order first.
      Node childNode = findChildNode(node, toFind);
      // If Node is valid, continue traversing.
      if (childNode != null) {
        stack.push(childNode);
      }
    }

    return pathToPoint;

  }

  /**
   * Search for the first child node to contain a point.
   * This search is completed by highest Z-order first i.e. reverse order.
   *
   * @param parent
   * @param toFind
   * @return
   */
  private static Node findChildNode(Node parent, Point toFind) {
    // Iterate backwards when searching for a valid child node, z-order matters.
    for (int i = parent.getChildren().size() - 1; i > -1; i--) {
      AbsoluteCoordinateNode normalizedChild = new AbsoluteCoordinateNode(parent, parent.getChildren().get(i));
      if (normalizedChild.contains(toFind)) {
        return normalizedChild;
      }
    }
    return null;
  }

  /**
   * AbsoluteCoordinateNode is a utility class derived from Node that will convert
   * relative coordinate to absolute coordinate. This is required to find if an absolute point
   * exists in a node.
   *
   * Another solution was to convert a point to relative coordinates, but this seemed slightly
   * more reliable and simpler at the possible expense of performance.
   */
  private static class AbsoluteCoordinateNode extends Node {

    /**
     * Base Constructor
     *
     * @param id
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private AbsoluteCoordinateNode(String id, int x, int y, int width, int height) {
      super(id, x, y, width, height);
    }

    /**
     * Constructor to normalize relative coordinates of a node to absolute coordinates.
     *
     * @param parent
     * @param node
     */
    public AbsoluteCoordinateNode(Node parent, Node node) {
      this(node.getId(),
              node.getLeft() + parent.getLeft(),
              node.getTop() + parent.getTop(),
              node.getWidth(),
              node.getHeight());

      for (Node child : node.getChildren()) {
          addChild(child);
      }
    }

    /**
     * Check if a Point is contained in this node.
     *
     * @param toFind
     * @return
     */
    public boolean contains(Point toFind) {
      return AbsoluteCoordinateNode.contains(this, toFind);
    }

    /**
     * Check if a Point is contained in a node.
     *
     * @param toFind
     * @return
     */
    public static boolean contains(Node node, Point toFind) {
      // Using Rectangle.contains() method. No use in recreating the wheel.
      Rectangle rect = new Rectangle(node.getLeft(), node.getTop(), node.getWidth(), node.getHeight());
      return rect.contains(toFind);
    }

  }

}
