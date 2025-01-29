package de.uni_passau.fim.se2.sbse.suite_generation.instrumentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;
import java.util.Set;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.objectweb.asm.Opcodes;





class BranchTracerTest {

    private BranchTracer tracer;
    private MethodVisitor methodVisitorMock;
    private Branch.Decision trueBranchMock;
    private Branch.Decision falseBranchMock;
    private Branch.Entry rootBranchMock;
    private Branch.Node nodeMock;

   @BeforeEach
    void setUp() {
        // Get a fresh instance of BranchTracer and clear any previous data
        tracer = BranchTracer.getInstance();
        tracer.clear();

        // Mock dependencies
        methodVisitorMock = mock(MethodVisitor.class);
        trueBranchMock = mock(Branch.Decision.class);
        falseBranchMock = mock(Branch.Decision.class);
        rootBranchMock = mock(Branch.Entry.class);
        nodeMock = mock(Branch.Node.class);

        // Set up consistent return values for mocks
        when(trueBranchMock.getNode()).thenReturn(nodeMock);
        when(falseBranchMock.getNode()).thenReturn(nodeMock);
        when(rootBranchMock.getId()).thenReturn(42);
        when(trueBranchMock.getId()).thenReturn(1);
        when(falseBranchMock.getId()).thenReturn(2);

        // Ensure method visitor behavior is neutralized for all tests
        doNothing().when(methodVisitorMock).visitMethodInsn(anyInt(), anyString(), anyString(), anyString(), anyBoolean());
    }


    @Test
    void testSingletonInstance() {
        BranchTracer instance1 = BranchTracer.getInstance();
        BranchTracer instance2 = BranchTracer.getInstance();
        assertSame(instance1, instance2, "Instances should be the same (Singleton pattern)");
    }

    @Test
    void testInstrumentBranchNodeUnaryComparison() {
        tracer.instrumentBranchNode(methodVisitorMock, trueBranchMock, falseBranchMock, org.objectweb.asm.Opcodes.IFEQ);
        verify(methodVisitorMock, atLeastOnce()).visitMethodInsn(anyInt(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testInstrumentBranchNodeBinaryComparison() {
        tracer.instrumentBranchNode(methodVisitorMock, trueBranchMock, falseBranchMock, org.objectweb.asm.Opcodes.IF_ICMPEQ);
        verify(methodVisitorMock, atLeastOnce()).visitMethodInsn(anyInt(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testInstrumentBranchNodeThrowsExceptionForDifferentNodes() {
        when(falseBranchMock.getNode()).thenReturn(mock(Branch.Node.class));
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            tracer.instrumentBranchNode(methodVisitorMock, trueBranchMock, falseBranchMock, org.objectweb.asm.Opcodes.IFEQ)
        );
        assertEquals("branches have different origins", exception.getMessage());
    }

    @Test
    void testPassedBranchIntComparisonIFEQ() {
        tracer.passedBranch(0, org.objectweb.asm.Opcodes.IFEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }

    @Test
    void testPassedBranchIntComparisonIFGT() {
        tracer.passedBranch(5, org.objectweb.asm.Opcodes.IFGT, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1), "True branch should have a distance of 0.0 when i > 0.");
        assertEquals(5.0, distances.get(2), "False branch should have a distance of 5.0.");
    }

    @Test
    void testPassedBranchReferenceComparison() {
        Object obj = new Object();
        tracer.passedBranch(obj, org.objectweb.asm.Opcodes.IFNULL, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(1.0, distances.get(1));
        assertEquals(0.0, distances.get(2));
    }

    @Test
    void testPassedBranchObjectComparisonIF_ACMPEQ() {
        Object obj1 = new Object();
        tracer.passedBranch(obj1, obj1, org.objectweb.asm.Opcodes.IF_ACMPEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }

    @Test
    void testPassedBranchRootMethodInvocation() {
        tracer.passedBranch(99);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(99));
    }
    
    @Test
    void testClearDistances() {
        tracer.passedBranch(1);
        assertFalse(tracer.getDistances().isEmpty());
        tracer.clear();
        assertTrue(tracer.getDistances().isEmpty());
    }
    @Test
    void testGetBranchById() {
        // Simulate adding a branch manually
        Branch.Entry mockEntry = mock(Branch.Entry.class);
        when(mockEntry.getId()).thenReturn(1);
        
        tracer.instrumentMethodEntry(methodVisitorMock, mockEntry);
    
        // Now retrieve and assert
        IBranch branch = tracer.getBranchById(1);
        assertNotNull(branch, "Branch with ID 1 should not be null after being added.");
        assertEquals(1, branch.getId(), "Branch ID should match the expected value.");
    }

    @Test
    void testInvalidOpcodeForObjectsThrowsException() {
        Object obj = new Object();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            tracer.passedBranch(obj, 999, 1, 2)
        );
        assertEquals("Unsupported opcode: 999", exception.getMessage());
    }

    @Test
    void testInvalidOpcodeForTwoObjectsThrowsException() {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            tracer.passedBranch(obj1, obj2, 999, 1, 2)
        );
        assertEquals("Unsupported opcode: 999", exception.getMessage());
    }
    @Test
    void testInstrumentBranchNodeIFGT() {
        tracer.instrumentBranchNode(methodVisitorMock, trueBranchMock, falseBranchMock, org.objectweb.asm.Opcodes.IFGT);
        verify(methodVisitorMock, atLeastOnce()).visitMethodInsn(anyInt(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testInstrumentBranchNodeIF_ICMPLE() {
        tracer.instrumentBranchNode(methodVisitorMock, trueBranchMock, falseBranchMock, org.objectweb.asm.Opcodes.IF_ICMPLE);
        verify(methodVisitorMock, atLeastOnce()).visitMethodInsn(anyInt(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testPassedBranchIntComparisonIFLT() {
        tracer.passedBranch(-1, org.objectweb.asm.Opcodes.IFLT, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2)); // Expected distance for false branch
    }


    @Test
    void testPassedBranchIntComparisonIFGE() {
        tracer.passedBranch(0, org.objectweb.asm.Opcodes.IFGE, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }

    @Test
    void testPassedBranchNullComparisonIFNONNULL() {
        Object obj = new Object();
        tracer.passedBranch(obj, org.objectweb.asm.Opcodes.IFNONNULL, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }

    @Test
    void testBranchTracerHandlesMultipleBranches() {
        tracer.passedBranch(10, org.objectweb.asm.Opcodes.IFEQ, 1, 2);
        tracer.passedBranch(20, org.objectweb.asm.Opcodes.IFNE, 3, 4);
        tracer.passedBranch(30, org.objectweb.asm.Opcodes.IFGT, 5, 6);
        
        assertEquals(6, tracer.getDistances().size());
    }

    @Test
    void testGetBranchByIdWithNonExistentId() {
        assertNull(tracer.getBranchById(99), "Branch with non-existent ID should return null.");
    }

    @Test
    void testPassedBranchWithLargeNumbers() {
        tracer.passedBranch(1000000, org.objectweb.asm.Opcodes.IFGE, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1000001.0, distances.get(2));
    }

    @Test
    void testExceptionHandlingForNullMethodVisitor() {
        assertThrows(NullPointerException.class, () ->
            tracer.instrumentMethodEntry(null, rootBranchMock)
        );
    }

    @Test
    void testPassedBranchWithIntegerMaxValue() {
        tracer.passedBranch(Integer.MAX_VALUE, org.objectweb.asm.Opcodes.IFEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals((double) Integer.MAX_VALUE, distances.get(1));
        assertEquals(0.0, distances.get(2));
    }

    @Test
    void testPassedBranchWithIntegerMinValue() {
        tracer.passedBranch(Integer.MIN_VALUE, org.objectweb.asm.Opcodes.IFLT, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals((double) Integer.MIN_VALUE, distances.get(2));
    }

    @Test
    void testPassedBranchWithNullReference() {
        tracer.passedBranch(null, org.objectweb.asm.Opcodes.IFNONNULL, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(1.0, distances.get(1));
        assertEquals(0.0, distances.get(2));
    }
    @Test
    void testPassedBranchThrowsExceptionForInvalidOpcode() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            tracer.passedBranch(10, 10, 999, 1, 2)
        );
        assertEquals("Unsupported opcode: 999", exception.getMessage());
    }

    @Test
    void testPassedBranchWithNullReferenceThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            tracer.passedBranch(null, null, 999, 1, 2)
        );
        assertEquals("Unsupported opcode: 999", exception.getMessage());
    }

    @Test
    void testGetBranchByIdReturnsNullForUnknownId() {
        assertNull(tracer.getBranchById(99), "Should return null for non-existent branch ID.");
    }

    @Test
    void testGetDistancesForNonExistentBranch() {
        assertNull(tracer.getDistances().get(999), "Should return null for unknown branch ID.");
    }
    @Test
    void testSingletonInstanceThreadSafety() throws InterruptedException {
        final BranchTracer[] instances = new BranchTracer[2];

        Thread t1 = new Thread(() -> instances[0] = BranchTracer.getInstance());
        Thread t2 = new Thread(() -> instances[1] = BranchTracer.getInstance());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertSame(instances[0], instances[1], "Singleton should return the same instance across threads.");
    }
    @Test
    void testPassedBranchIntComparisonIFLTBoundary() {
        tracer.passedBranch(0, Opcodes.IFLT, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(1.0, distances.get(1), "Distance should be 1 when i == 0.");
        assertEquals(0.0, distances.get(2), "Distance should be 0 when i == 0.");
    }

    @Test
    void testPassedBranchWithLargeNegativeValue() {
        tracer.passedBranch(-1000000, Opcodes.IFGE, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(1000000.0, distances.get(1), "Distance should match the absolute negative value.");
        assertEquals(0.0, distances.get(2), "False branch should have zero distance.");
    }
    @Test
    void testPassedBranchWithNullReferenceForEquality() {
        tracer.passedBranch(null, Opcodes.IFNULL, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1), "Distance should be zero for null reference.");
        assertEquals(1.0, distances.get(2), "Distance should be one for non-null case.");
    }
    @Test
    void testSingletonPattern() {
        BranchTracer instance1 = BranchTracer.getInstance();
        BranchTracer instance2 = BranchTracer.getInstance();
        assertSame(instance1, instance2, "BranchTracer should follow the singleton pattern.");
    }
    @Test
    void testClearRemovesAllBranchesAndDistances() {
        tracer.passedBranch(10, Opcodes.IFEQ, 1, 2);
        assertFalse(tracer.getDistances().isEmpty(), "Distances should not be empty before clearing.");

        tracer.clear();

        assertTrue(tracer.getDistances().isEmpty(), "Distances should be empty after clearing.");
    }
    @Test
    void testPassedBranchIntComparisonIFGEZeroValue() {
        tracer.passedBranch(0, Opcodes.IFGE, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }
    @Test
    void testPassedBranchIntComparisonIF_ICMPEQMaxValues() {
        tracer.passedBranch(Integer.MAX_VALUE, Integer.MAX_VALUE, Opcodes.IF_ICMPEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1), "Distances should be zero when values are equal.");
        assertEquals(1.0, distances.get(2), "Distance should be one when condition is false.");
    }
    @Test
    void testInstrumentationCalls() {
        tracer.instrumentBranchNode(methodVisitorMock, trueBranchMock, falseBranchMock, Opcodes.IFEQ);
        verify(methodVisitorMock, atLeastOnce()).visitMethodInsn(anyInt(), anyString(), anyString(), anyString(), anyBoolean());
    }
    @Test
    void testInvalidOpcodeThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            tracer.passedBranch(10, 20, 999, 1, 2)
        );
    }
    @Test
    void testMultipleBranchDistanceUpdates() {
        tracer.passedBranch(5, Opcodes.IFEQ, 1, 2);
        tracer.passedBranch(10, Opcodes.IFNE, 3, 4);

        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(5.0, distances.get(1));
        assertEquals(0.0, distances.get(2));
        assertEquals(0.0, distances.get(3));
        assertEquals(10.0, distances.get(4));
    }
    @Test
    void testPassedBranchIntComparisonIFGEWithNegativeValue() {
        tracer.passedBranch(-1, Opcodes.IFGE, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(1.0, distances.get(1));
        assertEquals(0.0, distances.get(2));
    }

    @Test
    void testPassedBranchWithNullReferenceIFNULL() {
        tracer.passedBranch(null, Opcodes.IFNULL, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1), "Distance should be zero when object is null.");
        assertEquals(1.0, distances.get(2), "Distance should be one for non-null case.");
    }

    @Test
    void testPassedBranchWithNonNullReferenceIFNONNULL() {
        tracer.passedBranch(new Object(), Opcodes.IFNONNULL, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1), "Distance should be zero when object is not null.");
        assertEquals(1.0, distances.get(2), "Distance should be one for null case.");
    }

    @Test
    void testPassedBranchWithSameObjectReferences() {
        Object obj = new Object();
        tracer.passedBranch(obj, obj, Opcodes.IF_ACMPEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }

    @Test
    void testPassedBranchWithDifferentObjectReferences() {
        Object obj1 = new Object();
        Object obj2 = new Object();
        tracer.passedBranch(obj1, obj2, Opcodes.IF_ACMPNE, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }

    @Test
    void testPassedBranchWithZeroDistanceCalculation() {
        tracer.passedBranch(0, Opcodes.IFEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1), "Distance should be zero when condition is met.");
        assertEquals(1.0, distances.get(2), "Distance should be one when condition is not met.");
    }

    @Test
    void testInstrumentBranchNodeBoundaryCase() {
        tracer.instrumentBranchNode(methodVisitorMock, trueBranchMock, falseBranchMock, Opcodes.IF_ICMPLE);
        verify(methodVisitorMock, atLeastOnce()).visitMethodInsn(anyInt(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testSingletonInstanceConcurrentAccess() throws InterruptedException {
        final BranchTracer[] instances = new BranchTracer[2];

        Thread t1 = new Thread(() -> instances[0] = BranchTracer.getInstance());
        Thread t2 = new Thread(() -> instances[1] = BranchTracer.getInstance());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertSame(instances[0], instances[1], "Singleton should return the same instance across threads.");
    }

    @Test
    void testConcurrentDistanceUpdates() throws InterruptedException {
        Thread t1 = new Thread(() -> tracer.passedBranch(10, Opcodes.IFEQ, 1, 2));
        Thread t2 = new Thread(() -> tracer.passedBranch(20, Opcodes.IFNE, 3, 4));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        Map<Integer, Double> distances = tracer.getDistances();
        assertTrue(distances.size() >= 2, "Concurrent updates should not overwrite values.");
    }
    @Test
    void testPassedBranchWithByteComparison() {
        byte b = 5;
        tracer.passedBranch(b, Opcodes.IFEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(5.0, distances.get(1), "Expected distance for byte value");
        assertEquals(0.0, distances.get(2));
    }

    @Test
    void testPassedBranchWithCharComparison() {
        char c = 'A';
        tracer.passedBranch(c, Opcodes.IFNE, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(65.0, distances.get(2), "Expected ASCII value distance");
    }

    @Test
    void testConcurrentPassedBranchUpdates() throws InterruptedException {
        Thread t1 = new Thread(() -> tracer.passedBranch(5, Opcodes.IFGE, 1, 2));
        Thread t2 = new Thread(() -> tracer.passedBranch(10, Opcodes.IFLE, 3, 4));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals(4, tracer.getDistances().size());
    }

    @Test
    void testPassedBranchWithIntegerMinMaxValues() {
        tracer.passedBranch(Integer.MIN_VALUE, Opcodes.IFEQ, 1, 2);
        tracer.passedBranch(Integer.MAX_VALUE, Opcodes.IFNE, 3, 4);
        
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals((double) Integer.MIN_VALUE, distances.get(1));
        assertEquals(0.0, distances.get(2));
        assertEquals(0.0, distances.get(3));
        assertEquals((double) Integer.MAX_VALUE, distances.get(4));
    }
    @Test
    void testPassedBranchWithInvalidOpcode() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            tracer.passedBranch(10, 20, 9999, 1, 2)
        );
        assertEquals("Unsupported opcode: 9999", exception.getMessage());
    }

    @Test
    void testTraceBranchDistanceMerging() {
        tracer.passedBranch(5, Opcodes.IFEQ, 1, 2);
        tracer.passedBranch(3, Opcodes.IFEQ, 1, 2);  // Should update to the minimum distance
        
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(3.0, distances.get(1));
        assertEquals(0.0, distances.get(2));
    }
    @Test
    void testBranchTracerSingletonUnderConcurrency() throws InterruptedException {
        final BranchTracer[] instances = new BranchTracer[2];

        Thread t1 = new Thread(() -> instances[0] = BranchTracer.getInstance());
        Thread t2 = new Thread(() -> instances[1] = BranchTracer.getInstance());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertSame(instances[0], instances[1], "Singleton should return the same instance across threads.");
    }
    @Test
    void testPassedBranchWithEqualValues() {
        tracer.passedBranch(50, 50, Opcodes.IF_ICMPEQ, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1));
        assertEquals(1.0, distances.get(2));
    }
    @Test
    void testClearResetsAllData() {
        tracer.passedBranch(100, Opcodes.IFGE, 1, 2);
        assertFalse(tracer.getDistances().isEmpty(), "Distances should not be empty before clearing.");
        
        tracer.clear();
        
        assertTrue(tracer.getDistances().isEmpty(), "Distances should be empty after clearing.");
    }
    @Test
    void testUnexpectedInputHandling() {
        assertThrows(IllegalArgumentException.class, () -> 
            tracer.passedBranch(null, Opcodes.IF_ACMPEQ, 1, 2)
        );
    }
    @Test
    void testLazyHolderInstanceCreation() {
        BranchTracer instance1 = BranchTracer.getInstance();
        BranchTracer instance2 = BranchTracer.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testPassedBranchWithNullObject() {
        tracer.passedBranch(null, Opcodes.IFNULL, 1, 2);
        Map<Integer, Double> distances = tracer.getDistances();
        assertEquals(0.0, distances.get(1), "Null object should produce correct branch distance.");
        assertEquals(1.0, distances.get(2));
    }
    @ParameterizedTest
    @CsvSource({
        "10, 153, 1, 2",  // Opcodes.IFEQ = 153
        "20, 154, 3, 4"   // Opcodes.IFNE = 154
    })

    void testPassedBranchParameterized(int input, int opcode, int trueBranch, int falseBranch) {
        tracer.passedBranch(input, opcode, trueBranch, falseBranch);
        Map<Integer, Double> distances = tracer.getDistances();
        assertNotNull(distances);
    }
    @Test
    void testDistanceMapSizeAfterBranchPassage() {
        tracer.passedBranch(10, Opcodes.IFEQ, 1, 2);
        assertTrue(tracer.getDistances().containsKey(1), "True branch should be recorded.");
        assertTrue(tracer.getDistances().containsKey(2), "False branch should be recorded.");
    }
    
    @Test
    void testPassedBranchWithRandomValues() {
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            int randomValue = rand.nextInt();
            tracer.passedBranch(randomValue, Opcodes.IFEQ, 1, 2);
        }
        assertNotNull(tracer.getDistances());
    }
    @Test
    void testConcurrentBranchUpdates() throws InterruptedException {
        Thread t1 = new Thread(() -> tracer.passedBranch(10, Opcodes.IFGE, 1, 2));
        Thread t2 = new Thread(() -> tracer.passedBranch(-10, Opcodes.IFLE, 3, 4));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals(4, tracer.getDistances().size(), "Concurrent updates should record all branches.");
    }
    @Test
    void testGetBranchesReturnsExpectedBranches() {
        BranchTracer tracer = BranchTracer.getInstance();
        tracer.clear();  // Ensure fresh start

        // Providing the required parameters for Entry constructor
        Branch.Entry mockEntry1 = new Branch.Entry(1, "TestClass", "testMethod1", "()V");
        Branch.Entry mockEntry2 = new Branch.Entry(2, "TestClass", "testMethod2", "()V");

        // Mocking the MethodVisitor object required by the method
        MethodVisitor mvMock = mock(MethodVisitor.class);

        tracer.instrumentMethodEntry(mvMock, mockEntry1);
        tracer.instrumentMethodEntry(mvMock, mockEntry2);

        Set<IBranch> branches = tracer.getBranches();

        // Assertions
        assertNotNull(branches, "Expected non-null branch set");
        assertEquals(2, branches.size(), "Expected 2 branches to be recorded");
        assertTrue(branches.stream().anyMatch(b -> b.getId() == 1), "Branch with ID 1 should exist");
        assertTrue(branches.stream().anyMatch(b -> b.getId() == 2), "Branch with ID 2 should exist");
    }

    @Test
    void testPassedBranch_IFEQ() {
        BranchTracer tracer = BranchTracer.getInstance();
        
        // i == 0 (true), i != 0 (false)
        tracer.passedBranch(0, Opcodes.IFEQ, 1, 2);
        
        assertEquals(0.0, tracer.getBranchDistance(1), "Expected true branch distance to be 0 for IFEQ");
        assertEquals(1.0, tracer.getBranchDistance(2), "Expected false branch distance to be 1 for IFEQ");
    }
    @Test
    void testPassedBranch_IFNE() {
        BranchTracer tracer = BranchTracer.getInstance();
        
        // i != 0 (true), i == 0 (false)
        tracer.passedBranch(5, Opcodes.IFNE, 1, 2);

        assertEquals(0.0, tracer.getBranchDistance(1), "Expected true branch distance to be 0 for IFNE");
        assertEquals(5.0, tracer.getBranchDistance(2), "Expected false branch distance to be 5 for IFNE");
    }
    @Test
    void testPassedBranch_IFLT() {
        BranchTracer tracer = BranchTracer.getInstance();
        
        tracer.passedBranch(-3, Opcodes.IFLT, 1, 2);

        assertEquals(0.0, tracer.getBranchDistance(1), "Expected true branch distance to be 0 for IFLT");
        assertEquals(3.0, tracer.getBranchDistance(2), "Expected false branch distance to be 3 for IFLT");

        tracer.passedBranch(3, Opcodes.IFLT, 3, 4);

        assertEquals(4.0, tracer.getBranchDistance(3), "Expected true branch distance to be 4 for IFLT");
        assertEquals(0.0, tracer.getBranchDistance(4), "Expected false branch distance to be 0 for IFLT");
    }
    @Test
    void testPassedBranch_IFLE() {
        BranchTracer tracer = BranchTracer.getInstance();

        tracer.passedBranch(-2, Opcodes.IFLE, 1, 2);

        assertEquals(0.0, tracer.getBranchDistance(1), "Expected true branch distance to be 0 for IFLE");
        assertEquals(3.0, tracer.getBranchDistance(2), "Expected false branch distance to be 3 for IFLE");
    }
    @Test
    void testPassedBranch_IFGT() {
        BranchTracer tracer = BranchTracer.getInstance();

        tracer.passedBranch(10, Opcodes.IFGT, 1, 2);

        assertEquals(0.0, tracer.getBranchDistance(1), "Expected true branch distance to be 0 for IFGT");
        assertEquals(10.0, tracer.getBranchDistance(2), "Expected false branch distance to be 10 for IFGT");

        tracer.passedBranch(-1, Opcodes.IFGT, 3, 4);

        assertEquals(2.0, tracer.getBranchDistance(3), "Expected true branch distance to be 2 for IFGT");
        assertEquals(0.0, tracer.getBranchDistance(4), "Expected false branch distance to be 0 for IFGT");
    }
    @Test
    void testPassedBranch_IFGE() {
        BranchTracer tracer = BranchTracer.getInstance();

        tracer.passedBranch(0, Opcodes.IFGE, 1, 2);

        assertEquals(0.0, tracer.getBranchDistance(1), "Expected true branch distance to be 0 for IFGE");
        assertEquals(1.0, tracer.getBranchDistance(2), "Expected false branch distance to be 1 for IFGE");

        tracer.passedBranch(-1, Opcodes.IFGE, 3, 4);

        assertEquals(1.0, tracer.getBranchDistance(3), "Expected true branch distance to be 1 for IFGE");
        assertEquals(0.0, tracer.getBranchDistance(4), "Expected false branch distance to be 0 for IFGE");
    }
    @Test
    void testPassedBranch_EdgeCases() {
        BranchTracer tracer = BranchTracer.getInstance();

        // Test with Integer.MAX_VALUE and Integer.MIN_VALUE
        tracer.passedBranch(Integer.MAX_VALUE, Opcodes.IFGT, 1, 2);
        assertEquals(0.0, tracer.getBranchDistance(1), "Expected true branch distance for max int");

        tracer.passedBranch(Integer.MIN_VALUE, Opcodes.IFLT, 3, 4);
        assertEquals(0.0, tracer.getBranchDistance(3), "Expected true branch distance for min int");
    }
    @Test
    void testPassedBranch_IF_ICMPEQ() throws Exception {
        BranchTracer tracer = BranchTracer.getInstance();
        invokePassedBranch(tracer, 5, 5, Opcodes.IF_ICMPEQ, 1, 2);
        assertBranchDistance(tracer, 1, 0.0);
        assertBranchDistance(tracer, 2, 1.0);
    }

    @Test
    void testPassedBranch_IF_ICMPNE() throws Exception {
        BranchTracer tracer = BranchTracer.getInstance();
        invokePassedBranch(tracer, 5, 3, Opcodes.IF_ICMPNE, 3, 4);
        assertBranchDistance(tracer, 3, 0.0);
        assertBranchDistance(tracer, 4, 2.0);
    }

    @Test
    void testPassedBranch_IF_ICMPLT() throws Exception {
        BranchTracer tracer = BranchTracer.getInstance();
        invokePassedBranch(tracer, 2, 5, Opcodes.IF_ICMPLT, 5, 6);
        assertBranchDistance(tracer, 5, 0.0);
        assertBranchDistance(tracer, 6, 3.0);
    }

    @Test
    void testPassedBranch_IF_ICMPLE() throws Exception {
        BranchTracer tracer = BranchTracer.getInstance();
        invokePassedBranch(tracer, 5, 5, Opcodes.IF_ICMPLE, 7, 8);
        assertBranchDistance(tracer, 7, 0.0);
        assertBranchDistance(tracer, 8, 1.0);
    }

    @Test
    void testPassedBranch_IF_ICMPGT() throws Exception {
        BranchTracer tracer = BranchTracer.getInstance();
        invokePassedBranch(tracer, 7, 3, Opcodes.IF_ICMPGT, 9, 10);
        assertBranchDistance(tracer, 9, 0.0);
        assertBranchDistance(tracer, 10, 4.0);
    }

    @Test
    void testPassedBranch_IF_ICMPGE() throws Exception {
        BranchTracer tracer = BranchTracer.getInstance();
        invokePassedBranch(tracer, 7, 7, Opcodes.IF_ICMPGE, 11, 12);
        assertBranchDistance(tracer, 11, 0.0);
        assertBranchDistance(tracer, 12, 1.0);
    }

    // Utility method to call passedBranch using reflection if the method is private
    private void invokePassedBranch(BranchTracer tracer, int i, int j, int opcode, int trueBranch, int falseBranch) throws Exception {
        Method method = BranchTracer.class.getDeclaredMethod("passedBranch", int.class, int.class, int.class, int.class, int.class);
        method.setAccessible(true);
        method.invoke(tracer, i, j, opcode, trueBranch, falseBranch);
    }

    // Utility method to check recorded branch distances using reflection
    private void assertBranchDistance(BranchTracer tracer, int branch, double expectedDistance) throws Exception {
        Field distancesField = BranchTracer.class.getDeclaredField("distances");
        distancesField.setAccessible(true);
        Map<Integer, Double> distances = (Map<Integer, Double>) distancesField.get(tracer);
        assertEquals(expectedDistance, distances.get(branch), 0.01, "Distance mismatch for branch " + branch);
    }
}
