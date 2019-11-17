package com.vladsch.flexmark.util.collection.iteration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class PositionListTest {
    @Rule public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getList() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);
        assertSame(list, positions.getList());
    }

    @Test
    public void testPosPreviousDelete1() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.remove(1, 3);
        assertEquals(1, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testPosPreviousDelete2() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.remove(1, 4);
        assertEquals(0, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testPosPreviousDelete3() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.remove(3, 4);
        assertEquals(2, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testPosPreviousDelete4() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.remove(4, 5);
        assertEquals(3, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testPosPreviousInsert1() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.addAll(3, Arrays.asList(-1, -2));
        assertEquals(3, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testPosPreviousInsert2() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.addAll(2, Arrays.asList(-1, -2));
        assertEquals(2, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testPosPreviousInsert3() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.addAll(1, Arrays.asList(-1, -2));
        assertEquals(5, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testPosPreviousInsert4() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(3, PositionAnchor.PREVIOUS);
        positions.addAll(0, Arrays.asList(-1, -2));
        assertEquals(5, position.getIndex());
        assertTrue(position.isValid());
    }

    @Test
    public void testMaxOffset() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        PositionList<Integer> positions = new PositionList<>(list);

        int iMax = list.size();
        for (int i = 0; i < iMax; i++) {
            Position<Integer> position = positions.getPosition(i, PositionAnchor.PREVIOUS);
            assertEquals(iMax - i, position.maxOffset());
        }
    }

    @Test
    public void testRelease() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position1 = positions.getPosition(8, PositionAnchor.CURRENT);
        Position<Integer> position2 = position1.getPosition(2);

        assertEquals(2, positions.trackedPositions());
        position1 = null;
        System.gc();
        assertEquals(1, positions.trackedPositions());
        position2 = null;
        System.gc();
        assertEquals(0, positions.trackedPositions());
    }

    @Test
    public void testListenerAdd() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getPosition(5, PositionAnchor.CURRENT);
        StringBuilder message = new StringBuilder();

        IPositionListener listener = new IPositionListener() {
            @Override
            public void inserted(int index, int count) {
                message.append("Inserted: [").append(index).append(", ").append(index + count).append(")");
            }

            @Override
            public void deleted(int index, int count) {
                message.append("Deleted: [").append(index).append(", ").append(index + count).append(")");
            }
        };

        assertEquals(1, positions.trackedPositions());
        positions.addPositionListener(listener);

        assertEquals(2, positions.trackedPositions());
        position.set(-1);
        assertEquals("", message.toString());

        message.delete(0, message.length());
        position.remove(-1);
        assertEquals("Deleted: [4, 5)", message.toString());

        message.delete(0, message.length());
        position.add(1, -2);
        assertEquals("Inserted: [5, 6)", message.toString());

        positions.removePositionListener(listener);
        assertEquals(1, positions.trackedPositions());

        positions.addPositionListener(listener);
        assertEquals(2, positions.trackedPositions());

        listener = null;
        System.gc();
        assertEquals(1, positions.trackedPositions());

        position = null;
        System.gc();
        assertEquals(0, positions.trackedPositions());
    }

    @Test
    public void get() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        assertEquals((Integer) 9, positions.get(0));
        assertEquals((Integer) 0, positions.get(9));
    }

    @Test
    public void getOrNull() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        assertEquals((Integer) 9, positions.getOrNull(0));
        assertEquals((Integer) 9, positions.getPosition(0).getOrNull());
        assertNull(positions.getOrNull(-1));
        assertNull(positions.getOrNull(10));
        assertNull(positions.getOrNull(11));

        assertNull(positions.getOrNull(-1, Integer.class));
        assertNull(positions.getOrNull(10, Integer.class));
        assertNull(positions.getOrNull(11, Integer.class));
    }

    @Test
    public void testEquals() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions1 = new PositionList<>(list);
        PositionList<Integer> positions2 = new PositionList<>(list);

        assertEquals(positions1, positions2);
        assertEquals(positions2, positions1);

        positions1.remove(0, 2);
        positions2.remove(0, 2);

        assertEquals(positions1, positions2);
        assertEquals(positions2, positions1);
    }

    @Test
    public void testHashCode() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions1 = new PositionList<>(list);
        PositionList<Integer> positions2 = new PositionList<>(list);

        assertEquals(positions1.hashCode(), positions2.hashCode());
        assertEquals(positions2.hashCode(), positions1.hashCode());

        positions1.remove(0, 2);
        positions2.remove(0, 2);

        assertEquals(positions1.hashCode(), positions2.hashCode());
        assertEquals(positions2.hashCode(), positions1.hashCode());
    }

    @Test
    public void testPosEquals() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> positions1 = positions.getPosition(0);
        Position<Integer> positions2 = positions.getPosition(0);

        assertNotEquals(positions1, positions2);
        assertNotEquals(positions2, positions1);
    }

    @Test
    public void tesPostHashCode() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> positions1 = positions.getPosition(0);
        Position<Integer> positions2 = positions.getPosition(0);

        assertNotEquals(positions1.hashCode(), positions2.hashCode());
        assertNotEquals(positions2.hashCode(), positions1.hashCode());
    }

    @Test
    public void set() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        positions.set(0, -1);
        List<Integer> expected = Arrays.asList(-1, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        assertEquals(expected, list);

        Position<Integer> position = positions.getPosition(10);
        assertEquals(10, position.getIndex());

        positions.set(10, -2);
        List<Integer> expected2 = Arrays.asList(-1, 8, 7, 6, 5, 4, 3, 2, 1, 0, -2);
        assertEquals(expected2, list);

        // set past last element does not affect positions
        assertEquals(10, position.getIndex());
        assertTrue(position.isValidElement());
    }

    @Test
    public void iterator() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Iterator<Position<Integer>> iterator = positions.iterator();
        while (iterator.hasNext()) {
            Position<Integer> position = iterator.next();
            list2.add(position.get());
        }
        assertEquals(list, list2);
    }

    @Test
    public void iteratorForwards() {
        List<Integer> input = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Iterator<Position<Integer>> iterator = positions.getPosition(0).forwards().iterator();
        while (iterator.hasNext()) {
            Position<Integer> position = iterator.next();
            list2.add(position.get());
        }
        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorForwards3() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(4);
        Iterator<Position<Integer>> iterator = position.forwards().iterator();
        int nextIndex = position.getIndex();

        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(nextIndex, position.getIndex());

            nextIndex = position.nextIndex();

            list2.add(position.get());
        }

        assertEquals(list.size(), position.nextIndex());

        List<Integer> expected = Arrays.asList(5, 4, 3, 2, 1, 0);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorForwards3a() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(4);
        Iterator<Position<Integer>> iterator = position.nextForwards().iterator();
        int nextIndex = position.nextIndex();

        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(nextIndex, position.getIndex());

            nextIndex = position.nextIndex();

            list2.add(position.get());
        }

        assertEquals(list.size(), position.nextIndex());

        List<Integer> expected = Arrays.asList(4, 3, 2, 1, 0);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorForwards4() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(9);
        Iterator<Position<Integer>> iterator = position.nextForwards().iterator();
        int nextIndex = position.getIndex();

        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(nextIndex, position.getIndex());

            nextIndex = position.nextIndex();

            list2.add(position.get());
        }

        assertEquals(list.size(), position.nextIndex());

        assertEquals(emptyList(), list2);
    }

    @Test
    public void iteratorForwards4a() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(9);
        Iterator<Position<Integer>> iterator = position.nextForwards().iterator();
        int nextIndex = position.nextIndex();

        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(nextIndex, position.getIndex());

            nextIndex = position.nextIndex();

            list2.add(position.get());
        }

        assertEquals(list.size(), position.nextIndex());

        assertEquals(emptyList(), list2);
    }

    @Test
    public void iteratorNext1() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> fakeIterator = positions.getPosition(0, PositionAnchor.NEXT);

        while (fakeIterator.isValidElement()) {
            Position<Integer> position = fakeIterator.withAnchor(PositionAnchor.CURRENT);
            assertSame(position, position.withAnchor(PositionAnchor.CURRENT));
            list2.add(position.get());

            boolean hadNext = fakeIterator.hasNext();
            fakeIterator = fakeIterator.next();

            assertEquals(hadNext, fakeIterator.isValidElement());
        }

        List<Integer> expected = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorBackwards() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Iterator<Position<Integer>> iterator = positions.getPosition(10).backwards().iterator();
        while (iterator.hasNext()) {
            Position<Integer> position = iterator.next();
            list2.add(position.get());
        }
        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorBackwards2() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Iterator<Position<Integer>> iterator = positions.getPosition(9).backwards().iterator();
        while (iterator.hasNext()) {
            Position<Integer> position = iterator.next();
            list2.add(position.get());
        }
        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorBackwards3() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(4);
        Iterator<Position<Integer>> iterator = position.backwards().iterator();
        int index = position.getIndex();
        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(index, position.getIndex());
            index = position.previousIndex();

            list2.add(position.get());
        }

        assertEquals(-1, position.previousIndex());

        List<Integer> expected = Arrays.asList(5, 6, 7, 8, 9);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorBackwards3a() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(4);
        Iterator<Position<Integer>> iterator = position.previousBackwards().iterator();
        int index = position.previousIndex();
        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(index, position.getIndex());
            index = position.previousIndex();

            list2.add(position.get());
        }

        assertEquals(-1, position.previousIndex());

        List<Integer> expected = Arrays.asList(6, 7, 8, 9);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorBackwards4() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(0);
        Iterator<Position<Integer>> iterator = position.backwards().iterator();
        int index = position.getIndex();
        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(index, position.getIndex());
            index = position.previousIndex();

            list2.add(position.get());
        }

        assertEquals(-1, position.previousIndex());

        List<Integer> expected = singletonList(9);
        assertEquals(expected, list2);
    }

    @Test
    public void iteratorBackwards5() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> position = positions.getPosition(0);
        Iterator<Position<Integer>> iterator = position.previousBackwards().iterator();
        int index = position.previousIndex();
        while (iterator.hasNext()) {
            position = iterator.next();

            assertEquals(index, position.getIndex());
            index = position.previousIndex();

            list2.add(position.get());
        }

        assertEquals(-1, position.previousIndex());

        assertEquals(emptyList(), list2);
    }

    @Test
    public void iteratorPrev1() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        Position<Integer> iterator = positions.getPosition(10, PositionAnchor.PREVIOUS);

        while (iterator.hasPrevious()) {
            iterator = iterator.previous();

            Position<Integer> position = iterator.withAnchor(PositionAnchor.CURRENT);
            list2.add(position.get());
        }
        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(expected, list2);
    }

    @Test
    public void iterator2() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        for (Position<Integer> position : positions) {
            list2.add(position.get());
        }
        assertEquals(list, list2);
    }

    @Test
    public void iterator_AddBefore() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions) {
            position.add(-(i++));
            list2.add(position.get());
        }
        List<Integer> expected = Arrays.asList(-1, 9, -2, 8, -3, 7, -4, 6, -5, 5, -6, 4, -7, 3, -8, 2, -9, 1, -10, 0);
        assertEquals(input, list2);
        assertEquals(expected, list);
    }

    @Test
    public void iterator_AddBeforeReversed() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions.reversed()) {
            position.add(Math.max(position.minOffset(), -1), -(i++));
            list2.add(position.get());
        }

        List<Integer> expected = Arrays.asList(0, -1, -2, -3, -4, -5, -6, -7, -8, -9);
        assertEquals(expected, list2);
        List<Integer> expected2 = Arrays.asList(-10, -9, 9, -8, 8, -7, 7, -6, 6, -5, 5, -4, 4, -3, 3, -2, 2, -1, 1, 0);
        assertEquals(expected2, list);
    }

    @Test
    public void iterator_AddAfter() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions) {
            position.add(1, -(i++));
            list2.add(position.get());
        }

        List<Integer> expected = Arrays.asList(9, -1, 8, -2, 7, -3, 6, -4, 5, -5, 4, -6, 3, -7, 2, -8, 1, -9, 0, -10);
        assertEquals(input, list2);
        assertEquals(expected, list);
    }

    @Test
    public void iterator_AddAfterReversed() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions) {
            position.add(1, -(i++));
            list2.add(position.get());
        }

        List<Integer> expected = Arrays.asList(9, -1, 8, -2, 7, -3, 6, -4, 5, -5, 4, -6, 3, -7, 2, -8, 1, -9, 0, -10);
        assertEquals(input, list2);
        assertEquals(expected, list);
    }

    @Test
    public void iterator_Remove() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        Iterator<Position<Integer>> iterator = positions.iterator();
        while (iterator.hasNext()) {
            Position<Integer> position = iterator.next();
            list2.add(position.get());
            iterator.remove();
        }

        List<Integer> expected = Arrays.asList(9, -1, 8, -2, 7, -3, 6, -4, 5, -5, 4, -6, 3, -7, 2, -8, 1, -9, 0, -10);
        assertEquals(input, list2);
        assertEquals(emptyList(), list);
    }

    @Test
    public void iterator_RemoveBeforeNext() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        thrown.expect(IllegalStateException.class);

        int i = 1;
        Iterator<Position<Integer>> iterator = positions.iterator();
        iterator.remove();
    }

    @Test
    public void detached() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        thrown.expect(IllegalStateException.class);

        Position<Integer> position = positions.getPosition(0);
        position.detachListener();

        boolean i = position.hasNext();
    }

    @Test
    public void testToString() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        Position<Integer> position = positions.getPosition(0);
        assertEquals("Position{anchor=CURRENT, index=0, valid=true}", position.toString());
        assertEquals("Position{anchor=NEXT, index=0, valid=true}", position.withAnchor(PositionAnchor.NEXT).toString());
        assertEquals("Position{anchor=PREVIOUS, index=0, valid=true}", position.withAnchor(PositionAnchor.PREVIOUS).toString());
    }

    @Test
    public void invalidRemoveRange() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        thrown.expect(IllegalArgumentException.class);

        Position<Integer> position = positions.getPosition(0);
        position.remove(5, 4);
    }

    @Test
    public void iterator_UseThenRemove() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions) {
            list2.add(position.get());
            position.remove();
        }

        List<Integer> expected = Arrays.asList(9, -1, 8, -2, 7, -3, 6, -4, 5, -5, 4, -6, 3, -7, 2, -8, 1, -9, 0, -10);
        assertEquals(input, list2);
        assertEquals(emptyList(), list);
    }

    @Test
    public void iterator_UseThenRemoveReverse() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions.reversed()) {
            list2.add(position.get());
            position.remove();
        }

        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(expected, list2);
        assertEquals(emptyList(), list);
    }

    @Test
    public void iterator_UseThenRemove2() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions) {
            list2.add(position.get());
            position.remove(0, 2);
        }

        List<Integer> expected = Arrays.asList(9, 7, 5, 3, 1);
        assertEquals(expected, list2);
        assertEquals(emptyList(), list);
    }

    @Test
    public void iterator_UseThenRemove2Reverse() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = 1;
        for (Position<Integer> position : positions.reversed()) {
            list2.add(position.get());
            assertEquals(-position.getIndex(), position.minOffset());
            position.remove(Math.max(position.minOffset(), -1), 1);
        }

        List<Integer> expected = Arrays.asList(0, 2, 4, 6, 8);
        assertEquals(expected, list2);
        assertEquals(emptyList(), list);
    }

    @Test
    public void iterator_RemoveThenUse() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        thrown.expect(IllegalStateException.class);

        int i = 1;
        for (Position<Integer> position : positions) {
            position.remove();
            list2.add(position.get());
        }
    }

    @Test
    public void get_outOfRange1() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);

        Position<Integer> pos = positions.getPosition(-1);
    }

    @Test
    public void get_outOfRange2() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);

        Position<Integer> pos = positions.getPosition(positions.size() + 1);
    }

    @Test
    public void get_valid() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);
        int iMax = list.size();
        for (int i = 0; i <= iMax; i++) {
            Position<Integer> position = positions.getPosition(i);
            assertEquals("" + i, i, position.getIndex());
            assertTrue("" + i, position.isValidIndex());

            assertEquals(i < iMax, position.isValidElement());
            if (i < iMax) {
                assertEquals("" + i, 9 - i, (int) position.get());
                assertEquals("" + i, 9 - i, (int) position.get(0));
            }
        }
    }

    @Test
    public void get_invalid() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);
        int iMax = list.size();
        for (int i = 0; i <= iMax; i++) {
            Position<Integer> position = positions.getPosition(i);
            position.invalidate();
            assertEquals("" + i, i, position.getIndex());
            assertTrue("" + i, position.isValidIndex());
            assertFalse("" + i, position.isValid());

            position = position.next();
            assertEquals("" + i, i < iMax, position.isValidElement());
            if (i < iMax) {
                assertEquals("" + i, 9 - i, (int) position.get());
                assertEquals("" + i, 9 - i, (int) position.get(0));
            }
        }
    }

    @Test
    public void clear() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Position<Integer>> listPositions = new ArrayList<>();

        int iMax = list.size();
        for (int i = 0; i <= iMax; i++) {
            Position<Integer> position = positions.getPosition(i);
            assertTrue("" + i, position.isValid());
            listPositions.add(position);
        }

        positions.clear();
        assertEquals(0, list.size());

        for (int i = 0; i <= iMax; i++) {
            Position<Integer> position = listPositions.get(i);
            assertFalse("" + i, position.isValid());
        }
    }

    @Test
    public void inserted1() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();

        for (int j = 0; j < jMax; j++) {
            ArrayList<Integer> list = new ArrayList<>(input);
            PositionList<Integer> positions = new PositionList<>(list);
            ArrayList<Position<Integer>> listPositions = new ArrayList<>();

            int iMax = list.size();
            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = positions.getPosition(i);
                assertTrue("" + i, position.isValid());
                listPositions.add(position);
            }

            list.add(j, -1);
            positions.inserted(j, 1);

            assertEquals(input.size() + 1, list.size());

            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = listPositions.get(i);
                assertEquals(i < j ? i : i + 1, position.getIndex());
                assertTrue("" + i, position.isValidIndex());
                assertTrue("" + i, position.isValid());

                assertEquals("" + i, i < iMax, position.isValidElement());
                if (i < iMax) {
                    assertEquals("" + i, list.get(i < j ? i : i + 1), position.get());
                    assertEquals("" + i, list.get(i < j ? i : i + 1), position.get(0));
                }
            }
        }
    }

    @Test
    public void deleted() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();

        for (int j = 0; j < jMax; j++) {
            ArrayList<Integer> list = new ArrayList<>(input);
            PositionList<Integer> positions = new PositionList<>(list);
            ArrayList<Position<Integer>> listPositions = new ArrayList<>();

            int iMax = list.size();
            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = positions.getPosition(i);
                assertTrue("" + i, position.isValid());
                listPositions.add(position);
            }

            list.remove(j);
            positions.deleted(j, 1);

            assertEquals(input.size() - 1, list.size());

            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = listPositions.get(i);
                assertEquals("" + i, i <= j ? i : i - 1, position.getIndex());
                assertTrue("" + i, position.isValidIndex());
                assertEquals("" + i, i != j, position.isValid());

                assertEquals("" + i, i != j && i < iMax, position.isValidElement());
                if (i != j && i < iMax) {
                    assertEquals("" + i, list.get(i < j ? i : i - 1), position.get());
                    assertEquals("" + i, list.get(i < j ? i : i - 1), position.get(0));
                }
            }
        }
    }

    @Test
    public void add_Indexed() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();

        for (int j = 0; j <= jMax; j++) {
            ArrayList<Integer> list = new ArrayList<>(input);
            PositionList<Integer> positions = new PositionList<>(list);
            ArrayList<Position<Integer>> listPositions = new ArrayList<>();

            int iMax = list.size();
            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = positions.getPosition(i);
                assertTrue("" + i, position.isValid());
                listPositions.add(position);
            }

            positions.add(j, -1);
            assertEquals(input.size() + 1, list.size());

            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = listPositions.get(i);
                assertEquals("" + i, i < j ? i : i + 1, position.getIndex());
                assertTrue("" + i, position.isValidIndex());
                assertTrue("" + i, position.isValid());

                assertEquals("" + i, i < iMax, position.isValidElement());
                if (i < iMax) {
                    assertEquals("" + i, list.get(i < j ? i : i + 1), position.get());
                    assertEquals("" + i, list.get(i < j ? i : i + 1), position.get(0));
                }
            }
        }
    }

    @Test
    public void add() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();
        int j = jMax;

        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Position<Integer>> listPositions = new ArrayList<>();

        int iMax = list.size();
        for (int i = 0; i <= iMax; i++) {
            Position<Integer> position = positions.getPosition(i);
            assertTrue("" + i, position.isValid());
            listPositions.add(position);
        }

        positions.add(-1);

        assertEquals(input.size() + 1, list.size());

        for (int i = 0; i <= iMax; i++) {
            Position<Integer> position = listPositions.get(i);
            assertEquals("" + i, i < j ? i : i + 1, position.getIndex());
            assertTrue("" + i, position.isValidIndex());
            assertTrue("" + i, position.isValid());

            assertEquals("" + i, i < iMax, position.isValidElement());
            if (i < iMax) {
                assertEquals("" + i, list.get(i < j ? i : i + 1), position.get());
                assertEquals("" + i, list.get(i < j ? i : i + 1), position.get(0));
            }
        }
    }

    @Test
    public void addAll() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();

        for (int j = 0; j <= jMax; j++) {
            ArrayList<Integer> list = new ArrayList<>(input);
            PositionList<Integer> positions = new PositionList<>(list);
            ArrayList<Position<Integer>> listPositions = new ArrayList<>();

            int iMax = list.size();
            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = positions.getPosition(i);
                assertTrue("" + i, position.isValid());
                listPositions.add(position);
            }

            if (j == jMax) {
                positions.addAll(Arrays.asList(-2, -1));
            } else {
                positions.addAll(j, Arrays.asList(-2, -1));
            }

            assertEquals(input.size() + 2, list.size());

            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = listPositions.get(i);
                assertEquals("" + i, i < j ? i : i + 2, position.getIndex());
                assertTrue("" + i, position.isValidIndex());
                assertTrue("" + i, position.isValid());

                assertEquals("" + i, i < iMax, position.isValidElement());
                if (i < iMax) {
                    assertEquals("" + i, list.get(i < j ? i : i + 2), position.get());
                    assertEquals("" + i, list.get(i < j ? i : i + 2), position.get(0));
                }
            }
        }
    }

    @Test
    public void addAll2() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();

        for (int j = 0; j <= jMax; j++) {
            ArrayList<Integer> list = new ArrayList<>(input);
            PositionList<Integer> positions = new PositionList<>(list);
            ArrayList<Position<Integer>> listPositions = new ArrayList<>();

            int iMax = list.size();
            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = positions.getPosition(i);
                assertTrue("" + i, position.isValid());
                listPositions.add(position);
            }

            if (j == jMax) {
                positions.addAll(new PositionList<>(Arrays.asList(-2, -1)));
            } else {
                positions.addAll(j, new PositionList<>(Arrays.asList(-2, -1)));
            }
            assertEquals(input.size() + 2, list.size());

            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = listPositions.get(i);
                assertEquals("" + i, i < j ? i : i + 2, position.getIndex());
                assertTrue("" + i, position.isValidIndex());
                assertTrue("" + i, position.isValid());

                assertEquals("" + i, i < iMax, position.isValidElement());
                if (i < iMax) {
                    assertEquals("" + i, list.get(i < j ? i : i + 2), position.get());
                    assertEquals("" + i, list.get(i < j ? i : i + 2), position.get(0));
                }
            }
        }
    }

    @Test
    public void remove() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();

        for (int j = 0; j < jMax; j++) {
            ArrayList<Integer> list = new ArrayList<>(input);
            PositionList<Integer> positions = new PositionList<>(list);
            ArrayList<Position<Integer>> listPositions = new ArrayList<>();

            int iMax = list.size();
            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = positions.getPosition(i);
                assertTrue("" + i, position.isValid());
                listPositions.add(position);
            }

            positions.remove(j);

            assertEquals(input.size() - 1, list.size());

            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = listPositions.get(i);
                assertEquals("" + i, i <= j ? i : i - 1, position.getIndex());
                assertTrue("" + i, position.isValidIndex());
                assertEquals("" + i, i != j, position.isValid());

                assertEquals("" + i, i != j && i < iMax, position.isValidElement());
                if (i != j && i < iMax) {
                    assertEquals("" + i, list.get(i < j ? i : i - 1), position.get());
                    assertEquals("" + i, list.get(i < j ? i : i - 1), position.get(0));
                }
            }
        }
    }

    @Test
    public void testRemove() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        int jMax = input.size();

        for (int j = 0; j < jMax; j++) {
            ArrayList<Integer> list = new ArrayList<>(input);
            PositionList<Integer> positions = new PositionList<>(list);
            ArrayList<Position<Integer>> listPositions = new ArrayList<>();

            int iMax = list.size();
            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = positions.getPosition(i);
                assertTrue("" + i, position.isValid());
                listPositions.add(position);
            }

            positions.remove(j, j + 1);

            assertEquals(input.size() - 1, list.size());

            for (int i = 0; i <= iMax; i++) {
                Position<Integer> position = listPositions.get(i);
                assertEquals("" + i, i <= j ? i : i - 1, position.getIndex());
                assertTrue("" + i, position.isValidIndex());
                assertEquals("" + i, i != j, position.isValid());

                assertEquals("" + i, i != j && i < iMax, position.isValidElement());
                if (i != j && i < iMax) {
                    assertEquals("" + i, list.get(i < j ? i : i - 1), position.get());
                    assertEquals("" + i, list.get(i < j ? i : i - 1), position.get(0));
                }
            }
        }
    }

    @Test
    public void test_posValidation1() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);
        Position<Integer> position = positions.getPosition(-1);
    }

    @Test
    public void test_posValidation2() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);
        Position<Integer> position = positions.getPosition(11);
    }

    @Test
    public void test_posValidation3() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);
        Position<Integer> position = positions.getPosition(0);
        position.get(-1);
    }

    @Test
    public void test_posValidation4() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);
        Position<Integer> position = positions.getPosition(0);
        position.get(11);
    }

    @Test
    public void test_posValidation5() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);
        Position<Integer> position = positions.getPosition(0);
        position.previous();
    }

    @Test
    public void test_posValidation6() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IllegalStateException.class);
        Position<Integer> position0 = positions.getPosition(0);
        Position<Integer> position1 = position0.getPosition(1);
        position0.remove(1);
        position1.get();
    }

    @Test
    public void test_posValidation7() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);
        Position<Integer> position = positions.getPosition(0);
        position.remove(0, 11);
    }

    @Test
    public void test_posValidation8() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        thrown.expect(IndexOutOfBoundsException.class);
        Position<Integer> position = positions.getPosition(0);
        position.remove(10, 0);
    }

    @Test
    public void posGetPosition() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        PositionList<Integer> positions = new PositionList<>(list);

        Position<Integer> position0 = positions.getPosition(1);
        assertTrue(position0.hasPrevious());
        Position<Integer> position1 = position0.previous();
        assertEquals(9, (int) position1.get());
        assertFalse(position1.hasPrevious());
    }

    @Test
    public void posSet() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = -1;
        for (Position<Integer> position : positions) {
            list2.add(position.get());
            position.set(i--);
        }

        List<Integer> expected = Arrays.asList(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10);
        assertEquals(expected, list);
        assertEquals(input, list2);
    }

    @Test
    public void posSet1() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        ArrayList<Integer> list2 = new ArrayList<>();

        int i = -1;
        for (Position<Integer> position : positions) {
            list2.add(position.get());
            position.set(1, i--);
        }

        List<Integer> expected = Arrays.asList(9, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10);
        assertEquals(expected, list);
        List<Integer> expected1 = Arrays.asList(9, -1, -2, -3, -4, -5, -6, -7, -8, -9);
        assertEquals(expected1, list2);
    }

    @Test
    public void test_getFirst() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getFirst();

        assertEquals(0, position.getIndex());
        assertTrue(position.isValidElement());
    }

    @Test
    public void test_getFirstEmpty() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(emptyList());
        Position<Integer> position = positions.getFirst();

        assertEquals(0, position.getIndex());
        assertFalse(position.isValidElement());
    }

    @Test
    public void test_getLast() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getLast();

        assertEquals(list.size() - 1, position.getIndex());
        assertTrue(position.isValidElement());
    }

    @Test
    public void test_getLastEmpty() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(emptyList());
        Position<Integer> position = positions.getLast();

        assertEquals(0, position.getIndex());
        assertFalse(position.isValidElement());
    }

    @Test
    public void test_getEnd() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);
        Position<Integer> position = positions.getEnd();

        assertEquals(list.size(), position.getIndex());
        assertTrue(position.isValidIndex());
        assertFalse(position.isValidElement());
    }

    @Test
    public void test_getEndEmpty() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(emptyList());
        Position<Integer> position = positions.getEnd();

        assertEquals(0, position.getIndex());
        assertFalse(position.isValidElement());
    }

    @Test
    public void posSetEnd() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        Position<Integer> position = positions.getLast().next();
        assertEquals(list.size(), position.getIndex());

        position.set(-1);

        List<Integer> expected = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0, -1);
        assertEquals(expected, list);
    }

    @Test
    public void posGetOrNull() {
        List<Object> input = Arrays.asList(9, "8", 7, "6", 5, "4", 3, "2", 1, "0");
        ArrayList<Object> list = new ArrayList<>(input);
        PositionList<Object> positions = new PositionList<>(list);

        Position<Object> position = positions.getPosition(0);
        Integer i = position.getOrNull(Integer.class);
        assertEquals(9, (int) i);

        i = position.getOrNull(1, Integer.class);
        assertNull(i);

        String s = position.getOrNull(String.class);
        assertNull(s);

        s = position.getOrNull(1, String.class);
        assertEquals("8", s);
    }

    @Test
    public void posAddAll() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        Position<Integer> position = positions.getPosition(5);
        Position<Integer> position1 = position.getPosition(3);

        position.addAll(Arrays.asList(-1, -2, -3));
        position1.addAll(Arrays.asList(-10, -20));
        assertEquals(input.size() + 5, position.size());
        assertEquals(input.size() + 5, position1.size());
        assertEquals(input.size() + 5, positions.size());

        List<Integer> expected = Arrays.asList(9, 8, 7, 6, 5, -1, -2, -3, 4, 3, 2, -10, -20, 1, 0);
        assertEquals(expected, list);

        assertFalse(position.isEmpty());
        positions.clear();
        assertTrue(position.isEmpty());
    }

    @Test
    public void posAppend() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        Position<Integer> position = positions.getPosition(5);
        Position<Integer> position1 = positions.getEnd();

        position.append(-1);

        assertEquals(input.size() + 1, position1.getIndex());
        assertFalse(position1.isValidElement());

        assertEquals(input.size() + 1, positions.size());

        List<Integer> expected = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0, -1);
        assertEquals(expected, list);
    }

    @Test
    public void posSize() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        Position<Integer> position = positions.getPosition(5);
        Position<Integer> position1 = position.getPosition(3);

        assertEquals(input.size(), position.size());
        assertEquals(input.size(), position1.size());
        assertEquals(input.size(), positions.size());
    }

    @Test
    public void posIsEmpty() {
        List<Integer> input = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        ArrayList<Integer> list = new ArrayList<>(input);
        PositionList<Integer> positions = new PositionList<>(list);

        Position<Integer> position = positions.getPosition(5);
        Position<Integer> position1 = position.getPosition(3);

        assertEquals(input.size(), position.size());
        assertEquals(input.size(), position1.size());
        assertEquals(input.size(), positions.size());

        assertFalse(position.isEmpty());
        positions.clear();
        assertTrue(position.isEmpty());
    }

    @Test
    public void posIndexOf1() {
        List<Object> input = Arrays.asList(0, "1", 2, "3", 4, "5", 6, "7", 8, "9", 0, "1", 2, "3", 4, "5", 6, "7", 8, "9");
        ArrayList<Object> list = new ArrayList<>(input);
        PositionList<Object> positions = new PositionList<>(list);

        Position<Object> position = positions.getPosition(0);
        Position<Object> index = position.indexOf("5");
        assertTrue(index.isValidElement());
        assertEquals(5, index.getIndex());
        assertEquals("5", index.getOrNull(String.class));

        index = position.indexOf(6, "5");
        assertTrue(index.isValidElement());
        assertEquals(15, index.getIndex());
        assertEquals("5", index.getOrNull(String.class));

        index = position.indexOf(6);
        assertTrue(index.isValidElement());
        assertEquals(6, index.getIndex());
        assertEquals((Integer) 6, index.getOrNull(Integer.class));

        index = position.indexOf(7, 6);
        assertTrue(index.isValidElement());
        assertEquals(16, index.getIndex());
        assertEquals((Integer) 6, index.getOrNull(Integer.class));

        // predicate search
        index = position.indexOf(p -> p.get() == (Integer) 6);
        assertTrue(index.isValidElement());
        assertEquals(6, index.getIndex());
        assertEquals((Integer) 6, index.getOrNull(Integer.class));

        index = position.indexOf(7, p -> p.get() == (Integer) 6);
        assertTrue(index.isValidElement());
        assertEquals(16, index.getIndex());
        assertEquals((Integer) 6, index.getOrNull(Integer.class));

        index = position.indexOf(17, p -> p.get() == (Integer) 6);
        assertFalse(index.isValidElement());
    }

    @Test
    public void posLastIndexOf1() {
        List<Object> input = Arrays.asList(0, "1", 2, "3", 4, "5", 6, "7", 8, "9", 0, "1", 2, "3", 4, "5", 6, "7", 8, "9");
        ArrayList<Object> list = new ArrayList<>(input);
        PositionList<Object> positions = new PositionList<>(list);

        Position<Object> position = positions.getPosition(20);
        Position<Object> index = position.lastIndexOf(6);
        assertTrue(index.isValidElement());
        assertEquals(16, index.getIndex());
        assertEquals((Integer) 6, index.getOrNull(Integer.class));

        index = position.lastIndexOf(-4, 6);
        assertTrue(index.isValidElement());
        assertEquals(6, index.getIndex());
        assertEquals((Integer) 6, index.getOrNull(Integer.class));

        index = position.lastIndexOf("5");
        assertTrue(index.isValidElement());
        assertEquals(15, index.getIndex());
        assertEquals("5", index.getOrNull(String.class));

        index = position.lastIndexOf(-5, "5");
        assertTrue(index.isValidElement());
        assertEquals(5, index.getIndex());
        assertEquals("5", index.getOrNull(String.class));

        // predicate search
        index = position.lastIndexOf(p -> p.get() == "7");
        assertTrue(index.isValidElement());
        assertEquals(17, index.getIndex());
        assertEquals("7", index.getOrNull(String.class));

        index = position.lastIndexOf(-3, p -> p.get() == "7");
        assertTrue(index.isValidElement());
        assertEquals(7, index.getIndex());
        assertEquals("7", index.getOrNull(String.class));

        index = position.lastIndexOf(-13, p -> p.get() == "7");
        assertFalse(index.isValidElement());
    }
}
