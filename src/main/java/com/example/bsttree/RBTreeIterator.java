package com.example.bsttree;

import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;

public abstract class RBTreeIterator<K extends Comparable<K>, V> implements Iterator<K> {
    private Stack<Node<K, V>> stack;
    private Node<K, V> currentNode; // Текущий узел

    public RBTreeIterator(Node<K, V> root, boolean forward) {
        stack = new Stack<>();
        Node<K, V> current = root;
        while (current != null) {
            stack.push(current);
            current = forward ? current.left : current.right;
        }
    }

    // Операция доступа по чтению к данным текущего узла
    public V getCurrentNodeValue() {
        if (currentNode != null) {
            return currentNode.value;
        } else {
            return null; // Если текущий узел не задан, возвращаем null
        }
    }

    // Операция доступа по записи к данным текущего узла
    public void setCurrentNodeValue(V value) {
        if (currentNode != null) {
            currentNode.value = value;
        }
    }

    // Операция перехода к следующему по ключу узлу в дереве
    public void moveToNextNode(K key) {
        if (currentNode == null) return;

        // Поиск узла по ключу
        Node<K, V> current = currentNode;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                // Узел найден
                return;
            } else if (cmp < 0) {
                // Переход к левому поддереву
                if (current.left != null) {
                    currentNode = current.left;
                    return;
                } else {
                    // Узел с заданным ключом не найден, оставляем текущий узел без изменений
                    return;
                }
            } else {
                // Переход к правому поддереву
                if (current.right != null) {
                    current = current.right;
                } else {
                    // Узел с заданным ключом не найден, оставляем текущий узел без изменений
                    return;
                }
            }
        }
    }

    // Операция перехода к предыдущему по ключу узлу в дереве
    public void moveToPreviousNode(K key) {
        if (currentNode == null) return;

        // Поиск узла по ключу
        Node<K, V> current = currentNode;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                // Узел найден
                return;
            } else if (cmp < 0) {
                // Переход к правому поддереву
                if (current.right != null) {
                    currentNode = current.right;
                    return;
                } else {
                    // Узел с заданным ключом не найден, оставляем текущий узел без изменений
                    return;
                }
            } else {
                // Переход к левому поддереву
                if (current.left != null) {
                    current = current.left;
                } else {
                    // Узел с заданным ключом не найден, оставляем текущий узел без изменений
                    return;
                }
            }
        }
    }

    // Проверка равенства однотипных итераторов
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Ссылки указывают на один и тот же объект
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Проверка на null и сравнение классов
        }
        // Приведение объекта к типу RBTreeIterator
        RBTreeIterator<?, ?> iterator = (RBTreeIterator<?, ?>) obj;
        // Проверка равенства стеков
        return Objects.equals(stack, iterator.stack) && Objects.equals(currentNode, iterator.currentNode);
    }

    // Проверка неравенства однотипных итераторов
    public boolean notEquals(Object obj) {
        return !equals(obj);
    }
}
