package ru.otus.cache.ds;

/**
 * Double linked list that returns the nodes.
 * */
public class DoubleLinkedList<E> {

    /* Head is needed to keep track of first node */
    private Node<E> head;

    /* Tail is needed to keep track of last node */
    private Node<E> tail;

    /* Size to keep track of number of elements in list.
     * This should be increased by 1 when a element is added
     * and should be reduced by 1 when a element is deleted */
    private int size = 0;

    /**
     * Inserts a element into a linked list at head position.
     * This does not require traversal through entire list.
     *
     * <br> Complexity :
     * Since there is no traversal involved here, and insertion
     * always happens at the head, this can be done in constant
     * time. Hence, complexity comes out to be O(1)
     * </br>
     *
     * @param value
     */
    public Node<E> insertAtHead(E value) {
        Node<E> newNode = new Node<E>(value);
        if (null == head) {
            /* If list is empty */
            newNode.next = null;
            newNode.prev = null;
            head = newNode;
            tail = newNode;
            size++;
        } else {
            newNode.next = head;
            newNode.prev = null;
            head.prev = newNode;
            head = newNode;
            size++;
        }
        return newNode;
    }

    /**
     * Inserts a element into a linked list at tail position.
     * This does not needs traversal through entire list before insertion happens.
     *
     * <br> Complexity :
     * Since, traversal through entire list is NOT involved here before
     * new node gets inserted, and let's assume list has n elements,
     * so insertion at tail will take O(1) time
     * </br>
     *
     * @param value
     */
    public Node<E> insertAtTail(E value) {
        Node<E> newNode = new Node<E>(value);
        if (null == tail) {
            /* If list is empty */
            newNode.next = null;
            newNode.prev = null;
            head = newNode;
            tail = newNode;
            size++;
        } else {
            tail.next = newNode;
            newNode.next = null;
            newNode.prev = tail;
            tail = newNode;
            size++;
        }
        return newNode;
    }

    /**
     * Inserts a element into a linked list at a given position.
     * This needs traversal through the linked list till the given position.
     *
     * <br> Complexity :
     * This insertion can possibly happen at last node, means we will have complexity
     * as O(1) as explained above.
     * we may have to traverse entire linked list. On an average case with
     * linked list having n elements, this will take n/2 time and after ignoring
     * the constant term, complexity comes out to be O(n)
     * </br>
     *
     * @param value
     * @param position
     */
    public Node<E> insertAtPosition(E value, int position) {
        if (position < 0 || position > size) {
            throw new IllegalArgumentException("Position is Invalid");
        }
        /* Conditions check passed, let's insert the node */
        if (position == 0) {
            /* Insertion should happen at head */
            return insertAtHead(value);
        } else if (position == size - 1) {
            /* Insertion should happen at tail */
            return insertAtTail(value);
        } else {
            /* Insertion is happening somewhere in middle */
            Node<E> currentNode = head;
            for (int i = 0; i < position; i++) {
                currentNode = currentNode.next;
            }
            Node<E> previousNode = currentNode.prev;
            /* Insertion of new node will happen in
             * between previous node and current node */
            Node<E> newNode = new Node<E>(value);
            newNode.next = currentNode;
            newNode.prev = previousNode;
            previousNode.next = newNode;
            currentNode.prev = newNode;
            size++;
            return newNode;
        }
    }

    public Node<E> updateNodeValueAndMoveToBeginning(Node<E> node, E value) {
        node.setItem(value);
        return moveNodeToBeginning(node);
    }

    public Node<E> moveNodeToBeginning(Node<E> node) {
        if (node.next == null && node.prev != null) {
            tail = node.prev;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        node.next = head;
        node.prev = null;
        head.prev = node;
        head = node;
        return node;
    }

    /**
     * Traverse the linked list in forward direction and print the items
     */
    public void traverseForward() {
        Node<E> temp = head;
        while (temp != null) {
            System.out.println(temp.item);
            temp = temp.next;
        }
    }

    /**
     * Traverse the linked list in backward direction and print the items
     */
    public void traverseBackward() {
        Node<E> temp = tail;
        while (temp != null) {
            System.out.println(temp.item);
            temp = temp.prev;
        }
    }

    /**
     * Returns the size of the linked list
     *
     * @return {@link int}
     */
    public int size() {
        return size;
    }

    /**
     * Returns true, if linked list is empty
     *
     * @return {@link boolean}
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the Node containing data item after searching
     * for a given index. If invalid index is passed, proper
     * exception is thrown.
     *
     * @param index
     * @return {@link Node<E>}
     */
    public Node<E> searchByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index passed while searching for a value");
        }
        /* Validation passed, let's search for value using the index */
        Node<E> temp = head;
        for (int i = 0; i < index; i++) {
            /* Start from 0 and go till one less then index
             * because we are jumping to next node inside the loop */
            temp = temp.next;
        }
        return temp;
    }

    /**
     * Returns the node containing data item after searching
     * for a given value. If there are multiple same values
     * in linked list, first one will be returned.
     *
     * @param value
     * @return {@link Node<E>}
     */
    public Node<E> searchByValue(E value) {
        /* Traverse through each node until this value is found */
        Node<E> temp = head;
        while (null != temp.next && temp.item != value) {
            temp = temp.next;
        }
        if (temp.item == value) {
            return temp;
        }
        return null;
    }

    /**
     * Delete's the element present at head node
     */
    public void deleteFromHead() {
        /* If list is empty, return */
        if (null == head) {
            return;
        }
        Node<E> temp = head;
        head = temp.next;
        head.prev = null;
        size--;
    }

    /**
     * Delete's the element present at tail node
     */
    public Node<E> deleteFromTail() {
        /* If list is empty, return */
        if (null == tail) {
            return null;
        }
        Node<E> temp = tail;
        tail = temp.prev;
        tail.next = null;
        size--;
        return temp;
    }

    /**
     * Delete's the element present at given position
     *
     * @param position
     */
    public void deleteFromPosition(int position) {
        if (position < 0 || position >= size) {
            throw new IllegalArgumentException("Position is Invalid");
        }
        /* Conditions check passed, let's delete the node */
        Node<E> nodeToBeDeleted = head;
        for (int i = 0; i < position; i++) {
            nodeToBeDeleted = nodeToBeDeleted.next;
        }
        Node<E> previousNode = nodeToBeDeleted.prev;
        Node<E> nextNode = nodeToBeDeleted.next;
        previousNode.next = nextNode;
        nextNode.prev = previousNode;
        size--;
    }

    /**
     * Returns a array containing each element
     * from the list from start to end
     *
     * @return
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = head; x != null; x = x.next) {
            result[i++] = x.item;
        }
        return result;
    }
}

