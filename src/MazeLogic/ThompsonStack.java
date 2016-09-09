package MazeLogic;

public class ThompsonStack<T>
{
  private Node headNode;
  private class Node
  {
    private T data;
    private Node nextNode;
    public Node(T c)
    {
      data = c;
      nextNode = null;
    }
  }
  public ThompsonStack()
  {
    headNode = null;
  }
  public boolean isEmpty()
  {
    if (headNode == null)
      return true;
    return false;
  }
  public T peek()
  {
      return headNode.data;
  }
  public T pop()
  {
    Node tempNode = headNode;
    headNode = headNode.nextNode;
    return tempNode.data;
  }
  public void push(T data)
  {
    Node newNode = new Node(data);
    newNode.nextNode = headNode;
    headNode = newNode;
  }
}