package com.example.bsttree;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RBTree<K extends Comparable<K>, V> extends Application {
    private Node<K, V> root;
    private int insertionCount;
    private int searchCount;
    private int deletionCount;

    // Конструктор
    public RBTree() {
        root = null;
        insertionCount = 0;
        searchCount = 0;
        deletionCount = 0;
    }

    public int getInsertionCount() {
        return insertionCount;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public int getDeletionCount() {
        return deletionCount;
    }

    // Конструктор копирования
    public RBTree(RBTree<K, V> other) {
        root = copyNode(other.root);
    }

    private Node<K, V> copyNode(Node<K, V> node) {
        if (node == null) return null;
        Node<K, V> newNode = new Node<>(node.key, node.value, node.isRed);
        newNode.left = copyNode(node.left);
        newNode.right = copyNode(node.right);
        return newNode;
    }

    // Метод поиска узла по ключу (итеративная реализация)
    private Node<K, V> findNode(K key) {
        Node<K, V> current = root;
        while (current != null && !current.key.equals(key)) {
            if (key.compareTo(current.key) < 0)
                current = current.left;
            else
                current = current.right;
        }
        return current;
    }

    public void insert(K key, V value) {
        root = insertIterative(root, key, value);
        insertionCount++;
        root.isRed = false; // Корень всегда должен быть черным
    }
    public V search(K key) {
        Node<K, V> node = findNode(key);
        return node != null ? node.value : null;
    }

    public void clear() {
        root = null;
    }
    private Node<K, V> insertRecursive(Node<K, V> node, K key, V value, Node<K, V> parent) {
        if (node == null) {
            // Вставляем новый красный узел
            Node<K, V> newNode = new Node<>(key, value, true);
            newNode.parent = parent; // Устанавливаем ссылку на родителя
            return newNode;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRecursive(node.left, key, value, node);
        } else if (cmp > 0) {
            node.right = insertRecursive(node.right, key, value, node);
        } else {
            // Ключ уже существует, обновляем значение
            node.value = value;
        }

        // Проверяем и исправляем нарушения свойств красно-черного дерева
        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }

        return node;
    }

    private boolean isRed(Node<K, V> node) {
        return node != null && node.isRed;
    }

    private Node<K, V> rotateLeft(Node<K, V> node) {
        Node<K, V> x = node.right;
        node.right = x.left;
        x.left = node;
        x.isRed = node.isRed;
        node.isRed = true;
        return x;
    }

    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> x = node.left;
        node.left = x.right;
        x.right = node;
        x.isRed = node.isRed;
        node.isRed = true;
        return x;
    }

    private void flipColors(Node<K, V> node) {
        node.isRed = true;
        node.left.isRed = false;
        node.right.isRed = false;
    }

    public void delete(K key) {
        if (findNode(key) == null) return; // Если узел с таким ключом не найден, ничего не делаем
        if (!isRed(root.left) && !isRed(root.right)) {
            root.isRed = true; // Если оба потомка корня черные, делаем корень красным
        }
        root = deleteIterative(root, key);
        if (root != null) root.isRed = false; // Корень всегда должен быть черным
    }

    private Node<K, V> deleteRecursive(Node<K, V> node, K key) {
        if (key.compareTo(node.key) < 0) {
            if (!isRed(node.left) && !isRed(node.left.left)) {
                node = moveRedLeft(node); // Сначала переносим красную нить влево
            }
            node.left = deleteRecursive(node.left, key);
        } else {
            if (isRed(node.left)) {
                node = rotateRight(node); // Если левый потомок красный, делаем правый поворот
            }
            if (key.compareTo(node.key) == 0 && node.right == null) {
                return null; // Удаляемый узел найден, обнуляем ссылку
            }
            if (!isRed(node.right) && !isRed(node.right.left)) {
                node = moveRedRight(node); // Если правый потомок красный, но не его левый потомок, переносим красную нить вправо
            }
            if (key.compareTo(node.key) == 0) {
                Node<K, V> minNode = findMin(node.right); // Находим узел с минимальным ключом в правом поддереве
                node.key = minNode.key; // Заменяем ключ удаляемого узла на ключ минимального узла в правом поддереве
                node.value = minNode.value; // Заменяем значение удаляемого узла на значение минимального узла в правом поддереве
                node.right = deleteMin(node.right); // Удаляем минимальный узел из правого поддерева
            } else {
                node.right = deleteRecursive(node.right, key);
            }
        }
        return balanceDelete(node); // Восстанавливаем баланс дерева после удаления
    }

    // Метод для удаления минимального узла в поддереве с корнем node
    private Node<K, V> deleteMin(Node<K, V> node) {
        if (node.left == null) return null; // Если узел без левого потомка, возвращаем null
        if (!isRed(node.left) && !isRed(node.left.left)) {
            node = moveRedLeft(node); // Если у левого потомка нет красного потомка слева, переносим красную нить влево
        }
        node.left = deleteMin(node.left); // Рекурсивно вызываем deleteMin для левого поддерева
        return balance(node); // Восстанавливаем баланс дерева после удаления
    }

    // Метод для нахождения узла с минимальным ключом в поддереве с корнем node
    private Node<K, V> findMin(Node<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node<K, V> balance(Node<K, V> node) {
        if (node == null) return null; // Добавим базовый случай для обработки пустого узла

        // Проверяем, является ли правый потомок красным
        if (isRed(node.right)) {
            // Выполняем левый поворот
            node = rotateLeft(node);
        }

        // Проверяем, является ли левый потомок красным и имеет ли он красного левого потомка
        if (isRed(node.left) && isRed(node.left.left)) {
            // Выполняем правый поворот
            node = rotateRight(node);
        }

        // Проверяем, являются ли оба потомка красными
        if (isRed(node.left) && isRed(node.right)) {
            // Меняем цвета узлов
            flipColors(node);
        }

        return node;
    }
    private Node<K, V> balanceDelete(Node<K, V> node) {
        if (isRed(node.right)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }
        return node;
    }
    private Node<K, V> insertIterative(Node<K, V> node, K key, V value) {
        Node<K, V> parent = null;
        Node<K, V> current = node;

        // Находим место для вставки нового узла
        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Ключ уже существует, обновляем значение
                current.value = value;
                return node;
            }
        }

        // Создаем новый красный узел
        Node<K, V> newNode = new Node<>(key, value, true);
        newNode.parent = parent;

        // Вставляем новый узел
        if (parent == null) {
            // Дерево было пустым, новый узел становится корнем
            return newNode;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        // Проверяем и исправляем нарушения свойств красно-черного дерева
        while (newNode != node && isRed(newNode.parent)) {
            if (newNode.parent == newNode.parent.parent.left) {
                Node<K, V> y = newNode.parent.parent.right;
                if (isRed(y)) {
                    newNode.parent.isRed = false;
                    y.isRed = false;
                    newNode.parent.parent.isRed = true;
                    newNode = newNode.parent.parent;
                } else {
                    if (newNode == newNode.parent.right) {
                        newNode = newNode.parent;
                        node = rotateLeft(node);
                    }
                    newNode.parent.isRed = false;
                    newNode.parent.parent.isRed = true;
                    node = rotateRight(node);
                }
            } else {
                Node<K, V> y = newNode.parent.parent.left;
                if (isRed(y)) {
                    newNode.parent.isRed = false;
                    y.isRed = false;
                    newNode.parent.parent.isRed = true;
                    newNode = newNode.parent.parent;
                } else {
                    if (newNode == newNode.parent.left) {
                        newNode = newNode.parent;
                        node = rotateRight(node);
                    }
                    newNode.parent.isRed = false;
                    newNode.parent.parent.isRed = true;
                    node = rotateLeft(node);
                }
            }
        }

        node.isRed = false;
        return node;
    }

    private Node<K, V> deleteIterative(Node<K, V> node, K key) {
        Node<K, V> current = node;

        // Находим узел для удаления
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                break;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (current == null) {
            // Узел для удаления не найден
            return node;
        }

        // Узел для удаления найден, выполняем удаление
        Node<K, V> parent = current.parent;
        Node<K, V> replacement = current;
        boolean isRed = replacement.isRed;

        if (current.left != null && current.right != null) {
            // У узла есть оба потомка, находим преемника
            replacement = current.right;
            while (replacement.left != null) {
                replacement = replacement.left;
            }
            isRed = replacement.isRed;
            current.key = replacement.key;
            current.value = replacement.value;
        } else if (current.left != null) {
            replacement = current.left;
            replaceNode(node, current, current.left);
        } else if (current.right != null) {
            replacement = current.right;
            replaceNode(node, current, current.right);
        } else {
            replaceNode(node, current, null);
        }
        return node;
    }

    // Заменяем узел oldNode на узел newNode
    private void replaceNode(Node<K, V> root, Node<K, V> oldNode, Node<K, V> newNode) {
        if (oldNode.parent == null) {
            root = newNode;
        } else if (oldNode == oldNode.parent.left) {
            oldNode.parent.left = newNode;
        } else {
            oldNode.parent.right = newNode;
        }
        if (newNode != null) {
            newNode.parent = oldNode.parent;
        }
    }

    // Метод для перемещения красной нити влево
    private Node<K, V> moveRedLeft(Node<K, V> node) {
        flipColors(node);
        if (isRed(node.right.left)) {
            node.right = rotateRight(node.right);
            node = rotateLeft(node);
            flipColors(node);
        }
        return node;
    }

    // Метод для перемещения красной нити вправо
    private Node<K, V> moveRedRight(Node<K, V> node) {
        flipColors(node);
        if (isRed(node.left.left)) {
            node = rotateRight(node);
            flipColors(node);
        }
        return node;
    }
    @Override
    public void start(Stage primaryStage) {
        TextField keyField = new TextField();
        TextField valueField = new TextField();
        Button insertButton = new Button("Insert");
        Button deleteButton = new Button("Delete");
        Button searchButton = new Button("Search");
        Button isEmptyButton = new Button("Is Empty"); // Кнопка для проверки на пустоту
        Button clearButton = new Button("Clear Tree"); // Кнопка для полной очистки дерева
        Button sizeButton = new Button("Size"); // Кнопка для проверки размера дерева
        Button getButton = new Button("Get"); // Кнопка для получения значения по ключу
        Button setButton = new Button("Set"); // Кнопка для установки значения по ключу
        Button putButton = new Button("Put"); // Кнопка для включения данных с заданным ключом
        Button removeButton = new Button("Remove"); // Кнопка для удаления данных с заданным ключом
        TextArea outputArea = new TextArea();
        Group graph = new Group();

        insertButton.setOnAction(event -> {
            try {
                K key = (K) keyField.getText();
                V value = (V) valueField.getText();
                insert(key, value);
                outputArea.clear();
                tLrTraversal(root, outputArea);

                // Перерисовываем дерево
                graph.getChildren().clear();
                drawTree(root, graph, 400, 20, 0);
            } catch (Exception e) {
                outputArea.setText("Invalid input!");
            }
        });

        deleteButton.setOnAction(event -> {
            K key = (K) keyField.getText();
            delete(key);
            outputArea.clear();
            tLrTraversal(root, outputArea);

            // Перерисовываем дерево после удаления узла
            graph.getChildren().clear();
            drawTree(root, graph, 400, 20, 0);
        });

        searchButton.setOnAction(event -> {
            K key = (K) keyField.getText();
            outputArea.setText("Key " + key + " found: " + search(key));
        });

        // Добавление обработчиков для новых кнопок
        getButton.setOnAction(event -> {
            K key = (K) keyField.getText();
            V value = get(key);
            outputArea.setText("Value for key " + key + ": " + value);
        });

        setButton.setOnAction(event -> {
            K key = (K) keyField.getText();
            V value = (V) valueField.getText();
            set(key, value);
        });

        putButton.setOnAction(event -> {
            K key = (K) keyField.getText();
            V value = (V) valueField.getText();
            put(key, value);
        });

        removeButton.setOnAction(event -> {
            K key = (K) keyField.getText();
            remove(key);
        });

        isEmptyButton.setOnAction(event -> {
            outputArea.setText("Tree is empty: " + isEmpty());
        });

        clearButton.setOnAction(event -> {
            clear();
            outputArea.clear();
            graph.getChildren().clear();
        });

        sizeButton.setOnAction(event -> {
            outputArea.setText("Tree size: " + size());
        });

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(keyField, valueField, insertButton, deleteButton, searchButton,
                isEmptyButton, clearButton, sizeButton, getButton, setButton, putButton, removeButton);
        root.getChildren().addAll(inputBox, outputArea, graph);

        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.setTitle("RB Tree");
        primaryStage.show();
    }

    // Реализация методов для красно-черного дерева

    private V get(K key) {
        Node<K, V> node = findNode(key);
        return node != null ? node.value : null;
    }

    private void set(K key, V value) {
        Node<K, V> node = findNode(key);
        if (node != null) {
            node.value = value;
        }
    }

    private void put(K key, V value) {
        if (findNode(key) == null) {
            insert(key, value);
        } else {
            set(key, value);
        }
    }

    private void remove(K key) {
        // Implement remove method
    }

    private boolean isEmpty() {
        return root == null;
    }

    private int size() {
        return size(root);
    }

    private int size(Node<K, V> node) {
        if (node == null) return 0;
        return 1 + size(node.left) + size(node.right);
    }

    // Вспомогательный метод для обхода дерева в прямом порядке (tLrTraversal)
    private void tLrTraversal(Node<K, V> node, TextArea outputArea) {
        if (node != null) {
            outputArea.appendText(node.key.toString() + ":" + node.value.toString() + " ");
            tLrTraversal(node.left, outputArea);
            tLrTraversal(node.right, outputArea);
        }
    }

    private double nodeSpacing = 100; // Расстояние между узлами
    private double verticalSpacing = 150; // Вертикальное расстояние между уровнями дерева

    private void drawNode(Node<K, V> node, VBox treeView) {
        if (node != null) {
            Circle circle = new Circle(20);
            if (node.isRed) {
                circle.setFill(Color.RED); // Красный узел
            } else {
                circle.setFill(Color.BLACK); // Черный узел
            }
            circle.setStroke(Color.BLACK);
            Text text = new Text(node.key.toString());
            text.setFill(Color.WHITE); // Белый цвет шрифта
            VBox nodeView = new VBox(circle, text);
            treeView.getChildren().add(nodeView);

            double x = nodeView.getBoundsInParent().getMinX() + 20;
            double y = nodeView.getBoundsInParent().getMinY() + 20;

            if (node.left != null) {
                Line lineLeft = new Line(x, y, x - nodeSpacing, y + verticalSpacing);
                Pane leftPane = new Pane();
                leftPane.getChildren().addAll(lineLeft);
                treeView.getChildren().add(leftPane);
            }

            if (node.right != null) {
                Line lineRight = new Line(x, y, x + nodeSpacing, y + verticalSpacing);
                Pane rightPane = new Pane();
                rightPane.getChildren().addAll(lineRight);
                treeView.getChildren().add(rightPane);
            }

            // Рекурсивно вызываем drawNode для левого и правого поддеревьев
            drawNode(node.left, treeView);
            drawNode(node.right, treeView);
        }
    }

    private void drawTree(Node<K, V> node, Group graph, double x, double y, int level) {
        if (node != null) {
            Circle circle = new Circle(x, y, 30); // Увеличиваем радиус круга до 30
            if (node.isRed) {
                circle.setFill(Color.RED); // Красный узел
            } else {
                circle.setFill(Color.BLACK); // Черный узел
            }
            circle.setStroke(Color.BLACK);
            Text text = new Text(x - 15, y + 5, node.key.toString() + ":" + node.value.toString());
            text.setStyle("-fx-font-weight: bold;"); // Делаем текст жирным
            text.setFill(Color.WHITE); // Белый цвет шрифта
            graph.getChildren().addAll(circle, text);

            double verticalSpacing = 100; // Увеличиваем расстояние между уровнями
            double childrenY = y + verticalSpacing;

            if (node.left != null) {
                double leftX = x - nodeSpacing / Math.pow(2, level);
                drawTree(node.left, graph, leftX, childrenY, level + 1);
                Line lineLeft = new Line(x, y, leftX, childrenY);
                graph.getChildren().add(lineLeft);
            }

            if (node.right != null) {
                double rightX = x + nodeSpacing / Math.pow(2, level);
                drawTree(node.right, graph, rightX, childrenY, level + 1);
                Line lineRight = new Line(x, y, rightX, childrenY);
                graph.getChildren().add(lineRight);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}