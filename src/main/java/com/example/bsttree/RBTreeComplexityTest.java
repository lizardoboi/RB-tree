package com.example.bsttree;

import java.util.Random;

public class RBTreeComplexityTest {
    private static final int NUM_TESTS = 50; // Количество тестов
    private static final int MAX_SIZE = 100; // Максимальный размер дерева

    public static void main(String[] args) {
        // Тестирование случайного RB-дерева
        System.out.println("Testing Random RB Tree:");
        testRandomRBTree();

        // Тестирование вырожденного RB-дерева
        System.out.println("\nTesting Degenerate RB Tree:");
        testDegenerateRBTree();
    }

    // Метод для генерации случайного RB-дерева заданного размера
    private static RBTree<Integer, Integer> generateRandomRBTree(int size) {
        RBTree<Integer, Integer> rbTree = new RBTree<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            rbTree.insert(random.nextInt(), i);
            rbTree.search(random.nextInt());
            rbTree.delete(random.nextInt());
        }
        return rbTree;
    }

    // Метод для генерации вырожденного RB-дерева заданного размера
    private static RBTree<Integer, Integer> generateDegenerateRBTree(int size) {
        RBTree<Integer, Integer> rbTree = new RBTree<>();
        for (int i = 0; i < size; i++) {
            rbTree.insert(i, i);
            rbTree.search(i);
            rbTree.delete(i);
        }
        return rbTree;
    }

    // Метод для измерения времени выполнения операции поиска в дереве
    private static long measureSearchTime(RBTree<Integer, Integer> rbTree, int key) {
        long startTime = System.nanoTime();
        rbTree.search(key);
        return System.nanoTime() - startTime;
    }

    // Метод для измерения времени выполнения операции вставки в дерево
    private static long measureInsertionTime(RBTree<Integer, Integer> rbTree, int key, int value) {
        long startTime = System.nanoTime();
        rbTree.insert(key, value);
        return System.nanoTime() - startTime;
    }

    // Метод для измерения времени выполнения операции удаления из дерева
    private static long measureDeletionTime(RBTree<Integer, Integer> rbTree, int key) {
        long startTime = System.nanoTime();
        rbTree.delete(key);
        return System.nanoTime() - startTime;
    }

    // Метод для тестирования случайного RB-дерева
    private static void testRandomRBTree() {
        for (int i = 1; i <= NUM_TESTS; i++) {
            int size = i * (MAX_SIZE / NUM_TESTS); // Размер дерева для текущего теста
            RBTree<Integer, Integer> rbTree = generateRandomRBTree(size);

            // Измеряем время выполнения операции поиска
            int randomKey = new Random().nextInt(); // Случайный ключ для поиска
            long searchTime = measureSearchTime(rbTree, randomKey);

            int searchCount = rbTree.getSearchCount(); // Получаем количество операций поиска
            System.out.println("Search Count: " + searchCount);

            // Измеряем время выполнения операции вставки
            int insertKey = new Random().nextInt(); // Случайный ключ для вставки
            int insertValue = new Random().nextInt(); // Случайное значение для вставки
            long insertionTime = measureInsertionTime(rbTree, insertKey, insertValue);

            int insertionCount = rbTree.getInsertionCount(); // Получаем количество операций вставки
            System.out.println("Insertion Count: " + insertionCount);

            // Измеряем время выполнения операции удаления
            int deleteKey = new Random().nextInt(); // Случайный ключ для удаления
            long deletionTime = measureDeletionTime(rbTree, deleteKey);

            int deletionCount = rbTree.getDeletionCount(); // Получаем количество операций удаления
            System.out.println("Deletion Count: " + deletionCount);

            // Вывод результатов
            System.out.println("Test " + i + ": Size=" + size + ", Search Time=" + searchTime + " ns, Insertion Time="
                    + insertionTime + " ns, Deletion Time=" + deletionTime + " ns");
        }
    }

    // Метод для тестирования вырожденного RB-дерева
    private static void testDegenerateRBTree() {
        for (int i = 1; i <= NUM_TESTS; i++) {
            int size = i * (MAX_SIZE / NUM_TESTS); // Размер дерева для текущего теста
            RBTree<Integer, Integer> rbTree = generateDegenerateRBTree(size);

            // Измеряем время выполнения операции поиска
            int randomKey = new Random().nextInt(); // Случайный ключ для поиска
            long searchTime = measureSearchTime(rbTree, randomKey);

            int searchCount = rbTree.getSearchCount(); // Получаем количество операций поиска
            System.out.println("Search Count: " + searchCount);

            // Измеряем время выполнения операции вставки
            int insertKey = new Random().nextInt(); // Случайный ключ для вставки
            int insertValue = new Random().nextInt(); // Случайное значение для вставки
            long insertionTime = measureInsertionTime(rbTree, insertKey, insertValue);

            int insertionCount = rbTree.getInsertionCount(); // Получаем количество операций вставки
            System.out.println("Insertion Count: " + insertionCount);

            // Измеряем время выполнения операции удаления
            int deleteKey = new Random().nextInt(); // Случайный ключ для удаления
            long deletionTime = measureDeletionTime(rbTree, deleteKey);

            int deletionCount = rbTree.getDeletionCount(); // Получаем количество операций удаления
            System.out.println("Deletion Count: " + deletionCount);

            // Вывод результатов
            System.out.println("Test " + i + ": Size=" + size + ", Search Time=" + searchTime + " ns, Insertion Time="
                    + insertionTime + " ns, Deletion Time=" + deletionTime + " ns");
        }
    }
}