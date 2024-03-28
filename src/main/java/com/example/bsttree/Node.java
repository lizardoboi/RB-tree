package com.example.bsttree;

class Node<K, V> {
    K key;
    V value;
    Node<K, V> left;
    Node<K, V> right;
    Node<K, V> parent; // Добавляем поле parent
    boolean isRed;

    // Конструктор
    public Node(K key, V value, boolean isRed) {
        this.key = key;
        this.value = value;
        this.isRed = isRed;
        this.left = null;
        this.right = null;
        this.parent = null; // Инициализируем parent как null
    }
}
