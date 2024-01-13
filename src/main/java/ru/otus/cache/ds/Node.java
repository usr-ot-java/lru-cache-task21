package ru.otus.cache.ds;

import lombok.*;

/**
 * Node class of a linked list
 * This is needed since entire linked list is a collection
 * of nodes connected to each other through links
 *
 * <br> We are keeping it generic so that it can be used with
 * Integer, String or something else </br>
 *
 * <br> Each node contains a data item, a pointer to next node
 * and pointer to previous node.
 * Since this is a Doubly linked list and each node points in
 * both directions i.e forward and backward.
 * We maintain two pointers, one to next node and one to previous node </br>
 *
 * @param <T>
 * @author Deepak
 */
@AllArgsConstructor
@EqualsAndHashCode
public class Node<T> {

    /* Data item in the node */
    @Getter
    @Setter(value = AccessLevel.PACKAGE)
    T item;

    /* Pointer to next node */
    Node<T> next;

    /* Pointer to previous node */
    Node<T> prev;

    /* Constructor to create a node */
    public Node(T item) {
        this.item = item;
    }

    /* toString implementation to print just the data */
    @Override
    public String toString() {
        return String.valueOf(item);
    }

}
